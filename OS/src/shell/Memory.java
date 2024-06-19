package shell;

import java.util.ArrayList;
import java.util.List;

public class Memory {
    private BuddyAllocator buddyAllocator;
    private List<MemorySegment> allocatedSegments;

    public Memory(BuddyAllocator buddyAllocator) {
        this.buddyAllocator = buddyAllocator;
        this.allocatedSegments = new ArrayList<>();
    }

    public List<MemorySegment> allocateMemory(Process process, int memorySize) {
        if (memorySize <= 0) {
            throw new IllegalArgumentException("Invalid memory size for allocation");
        }

        List<Block> allocatedBlocks = buddyAllocator.allocate(memorySize);
        if (allocatedBlocks.isEmpty()) {
            System.out.println("Requested memory size exceeds available memory. Allocation failed.");
            return new ArrayList<>();
        }

        List<MemorySegment> allocatedMemorySegments = new ArrayList<>();
        for (Block block : allocatedBlocks) {
            MemorySegment segment = new MemorySegment(process, block);
            allocatedMemorySegments.add(segment);
            allocatedSegments.add(segment);
        }

        return allocatedMemorySegments;
    }

    public void deallocateDoneProcesses() {
        List<MemorySegment> segmentsToDeallocate = new ArrayList<>();

        for (MemorySegment segment : allocatedSegments) {
            Process process = segment.getProcess();
            if (process != null && process.isCompleted()) {
                segmentsToDeallocate.add(segment);
                buddyAllocator.deallocate(segment.getBlock());
            }
        }

        allocatedSegments.removeAll(segmentsToDeallocate);
    }

    public int getNumAvailableSegments() {
        return buddyAllocator.getFreeBlocks().size();
    }

    public List<MemorySegment> getMemorySegments() {
        return allocatedSegments;
    }

    public static class MemorySegment {
        private Process process;
        private Block block;

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
            return block.getSizeInMB();
        }

        public Block getBlock() {
            return block;
        }
    }
}
