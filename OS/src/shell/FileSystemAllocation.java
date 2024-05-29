package shell;
import java.util.ArrayList;
import java.util.List;
public class FileSystemAllocation {

        // Lista blokova u memoriji diska
        static List<Block> discMemoryBlocks = new ArrayList<>();
        // Ukupan broj blokova u memoriji diska
        private static final int TOTAL_DISC_MEMORY_BLOCKS = 256;
        // Veličina bloka u megabajtima
        static final int blockSizeInMB = 4;

        // Metoda za alokaciju blokova memorije
        public List<Block> allocateMemoryBlocks(int fileSizeInMB) {
            // Lista alokovanih blokova
            List<Block> allocatedBlocks = new ArrayList<>();
            // Broj blokova potreban za datu veličinu fajla
            int blocksRequired = (int) Math.ceil((double) fileSizeInMB / blockSizeInMB);

            // Brojač za kontinuirane blokove
            int contiguousCount = 0;

            // Iteracija kroz blokove memorije
            for (Block block : discMemoryBlocks) {
                if (!block.isAllocated()) {
                    contiguousCount++;
                    allocatedBlocks.add(block);

                    // Ako je pronađen dovoljan broj kontinuiranih blokova
                    if (contiguousCount == blocksRequired) {
                        // Alokacija blokova
                        for (Block allocatedBlock : allocatedBlocks) {
                            allocatedBlock.allocate();
                        }
                        // Vraćanje alokovanih blokova
                        return allocatedBlocks;
                    }
                } else {
                    // Resetovanje brojača i liste ako se prekine niz slobodnih blokova
                    contiguousCount = 0;
                    allocatedBlocks.clear();
                }
            }

            // Dealokacija blokova ako nije moguće naći dovoljan broj kontinuiranih blokova
            for (Block block : allocatedBlocks) {
                block.deallocate();
            }

            return null;
        }

        // Metoda za dealokaciju blokova memorije
        public void deallocateMemoryBlocks(List<Block> blocksToDeallocate) {
            for (Block block : blocksToDeallocate) {
                block.deallocate();
            }
        }

        // Metoda za kreiranje blokova memorije
        public static void createMemoryBlocks() {
            int startAddress = 0; // Početna adresa za prvi blok
            for (int i = 0; i < TOTAL_DISC_MEMORY_BLOCKS; i++) {
                discMemoryBlocks.add(new Block(startAddress, blockSizeInMB));
                startAddress += blockSizeInMB; // Povećaj početnu adresu za veličinu bloka
            }
        }
    }


