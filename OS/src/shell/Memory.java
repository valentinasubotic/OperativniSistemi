package shell;

import java.util.ArrayList;
import java.util.List;

public class Memory {
    private BuddyAllocator buddyAllocator;  // Alokator koji koristi buddy sistem
    private List<MemorySegment> allocatedSegments;  // Lista svih alociranih segmenata memorije

    public Memory(BuddyAllocator buddyAllocator) {
        this.buddyAllocator = buddyAllocator;
        this.allocatedSegments = new ArrayList<>(); // Inicijalizacija prazne liste segmenata
    }

    // Metoda za alokaciju memorije za dati proces
    public List<MemorySegment> allocateMemory(Process process, int memorySize) {
        if (memorySize <= 0) {
            throw new IllegalArgumentException("Invalid memory size for allocation");   // Provjera validnosti veličine memorije
        }

        List<Block> allocatedBlocks = buddyAllocator.allocate(memorySize);
        if (allocatedBlocks.isEmpty()) {
            System.out.println("Requested memory size exceeds available memory. Allocation failed.");
            return new ArrayList<>();
        }

        List<MemorySegment> allocatedMemorySegments = new ArrayList<>();
        for (Block block : allocatedBlocks) {
            MemorySegment segment = new MemorySegment(process, block); // Kreiranje novog segmenta memorije
            allocatedMemorySegments.add(segment); // Dodavanje segmenta u listu alociranih segmenata
            allocatedSegments.add(segment); // Dodavanje segmenta u globalnu listu svih alociranih segmenata
        }

        return allocatedMemorySegments; // Vraća listu novokreiranih segmenata
    }

    // Metoda za dealokaciju memorije procesa koji su završeni
    public void deallocateDoneProcesses() {
        List<MemorySegment> segmentsToDeallocate = new ArrayList<>();

        for (MemorySegment segment : allocatedSegments) {
            Process process = segment.getProcess();
            if (process != null && process.isCompleted()) { // Provjera da li je proces završen
                segmentsToDeallocate.add(segment); // Dodavanje segmenta za dealokaciju u listu
                buddyAllocator.deallocate(segment.getBlock()); // Dealokacija bloka
            }
        }

        allocatedSegments.removeAll(segmentsToDeallocate); // Uklanjanje dealociranih segmenata iz globalne liste
    }

    // Metoda koja vraća broj slobodnih segmenata memorije
    public int getNumAvailableSegments() {
        return buddyAllocator.getFreeBlocks().size();
    }

    // Metoda koja vraća listu svih alociranih segmenata memorije
    public List<MemorySegment> getMemorySegments() {
        return allocatedSegments;
    }

    // Unutrašnja klasa koja predstavlja segment memorije
    public static class MemorySegment {
        private Process process; // Proces koji koristi ovaj segment
        private Block block; // Blok memorije koji je alociran

        public MemorySegment(Process process, Block block) {
            this.process = process;
            this.block = block;
        }

        public Process getProcess() {
            return process;
        }

        public void setProcess(Process process) {
            this.process = process;
        }

        public int getSize() {
            return block.getSizeInMB(); // Vraća veličinu segmenta memorije
        }

        public Block getBlock() {
            return block;   // Vraća blok memorije
        }
    }
}
