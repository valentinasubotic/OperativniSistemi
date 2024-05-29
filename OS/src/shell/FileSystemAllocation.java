package shell;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class FileSystemAllocation {
    // Bitset koji predstavlja prazan prostor
    private static BitSet emptySpace;
    // Veličina bloka u megabajtima
    private static final int blockSizeInMB = 4;
    // Ukupan broj blokova u memoriji diska
    private static final int TOTAL_DISC_MEMORY_BLOCKS = 256;

    // Inicijalizacija praznog prostora
    public static void initializeEmptySpace() {
        emptySpace = new BitSet(TOTAL_DISC_MEMORY_BLOCKS);
        emptySpace.set(0, TOTAL_DISC_MEMORY_BLOCKS); // Postavljanje svih blokova kao slobodne
    }

    // Metoda za alokaciju blokova memorije
    public static List<Block> allocateMemoryBlocks(int fileSizeInMB) {
        List<Block> allocatedBlocks = new ArrayList<>();
        int blocksRequired = (int) Math.ceil((double) fileSizeInMB / blockSizeInMB);

        int startIndex = findContiguousBlocks(blocksRequired);
        if (startIndex != -1) {
            for (int i = startIndex; i < startIndex + blocksRequired; i++) {
                Block block = new Block(i * blockSizeInMB, blockSizeInMB);
                block.allocate();
                allocatedBlocks.add(block);
                emptySpace.clear(i);
            }
            return allocatedBlocks;
        }

        return null;
    }

    // Pronalaženje kontinuiranih blokova u praznom prostoru
    private static int findContiguousBlocks(int blocksRequired) {
        int startIndex = emptySpace.nextClearBit(0);
        int contiguousCount = 0;
        for (int i = startIndex; i < TOTAL_DISC_MEMORY_BLOCKS; i++) {
            if (!emptySpace.get(i)) {
                contiguousCount++;
                if (contiguousCount == blocksRequired) {
                    return i - blocksRequired + 1;
                }
            } else {
                contiguousCount = 0;
            }
        }
        return -1;
    }

    // Metoda za dealokaciju blokova memorije
    public static void deallocateMemoryBlocks(List<Block> blocksToDeallocate) {
        for (Block block : blocksToDeallocate) {
            int blockIndex = block.getStartAddress() / blockSizeInMB;
            emptySpace.set(blockIndex);
            block.deallocate();
        }
    }
}
