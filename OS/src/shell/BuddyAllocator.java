package shell;

import java.util.ArrayList;
import java.util.List;

public class BuddyAllocator {
    private int allocatedMemory;
    private final int totalMemorySize;  // Ukupna veličina memorije kojom se upravlja
    private Block root;  // Root blok koji pokriva cijelu memoriju
    private List<Block> freeList;  // Lista slobodnih blokova memorije

    // Konstruktor za inicijalizaciju alokatora sa ukupnom veličinom memorije
    public BuddyAllocator(int totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
        this.root = new Block(0, totalMemorySize);  // Inicijalizacija root bloka
        this.freeList = new ArrayList<>();  // Inicijalizacija liste slobodnih blokova
        this.freeList.add(root);  // Dodavanje root bloka u listu slobodnih blokova
    }
    /*

    // Metoda za alokaciju bloka memorije određene veličine
    public Block allocate(int sizeInMB) {
        Block block = findFreeBlock(sizeInMB);  // Pronalazak slobodnog bloka odgovarajuće veličine
        if (block != null) {
            split(block, sizeInMB);  // Dijeljenje bloka na manje dok ne dobijemo odgovarajuću veličinu
            block.allocate();  // Alokacija bloka
            allocatedMemory += sizeInMB;
        }
        return block;  // Vraćanje alociranog bloka (ili null ako nije moguće)
    }


     */

    public List<Block> allocate(int sizeInMB) {
        List<Block> allocatedBlocks = new ArrayList<>();
        int remainingSize = sizeInMB;

        while (remainingSize > 0) {
            Block block = findFreeBlock(remainingSize);
            if (block != null) {
                split(block, remainingSize);
                block.allocate();
                allocatedBlocks.add(block);
                remainingSize -= block.getSizeInMB();
                allocatedMemory += block.getSizeInMB();
            } else {
                // Dealociraj sve prethodno alocirane blokove u slučaju neuspeha
                for (Block b : allocatedBlocks) {
                    b.deallocate();
                }
                allocatedBlocks.clear();
                break;
            }
        }
        return allocatedBlocks;
    }
    // Metoda za dealokaciju bloka memorije
    public void deallocate(Block block) {
        block.deallocate();  // Postavljanje bloka kao slobodnog
        merge(block);  // Pokušaj spajanja bloka sa njegovim buddy blokom
        allocatedMemory -= block.getSizeInMB(); // Ažurirajte alociranu memoriju
    }

    // Privatna metoda za pronalazak slobodnog bloka odgovarajuće veličine
    private Block findFreeBlock(int sizeInMB) {
        for (Block block : freeList) {
            if (!block.isAllocated() && block.getSizeInMB() >= sizeInMB) {
                return block;
            }
        }
        return null;  // Nema odgovarajućeg slobodnog bloka
    }

    // Privatna metoda za dijeljenje bloka na manje blokove
    private void split(Block block, int sizeInMB) {
        while (block.getSizeInMB() / 2 >= sizeInMB) {
            int newSize = block.getSizeInMB() / 2;
            Block buddy = new Block(block.getStartAddress() + newSize, newSize);  // Kreiranje buddy bloka
            buddy.setBuddy(block);
            block.setBuddy(buddy);

            // Kreiramo novi blok sa smanjenom veličinom umesto da menjamo postojeći
            Block parent = new Block(block.getStartAddress(), newSize);
            parent.setBuddy(buddy);
            buddy.setParent(parent);
            parent.setParent(block.getParent());

            // Ažuriramo buddy blokove tako da ukazuju na novi parent blok
            freeList.add(buddy);  // Dodavanje buddy bloka u listu slobodnih blokova
            freeList.remove(block);
            freeList.add(parent);

            block = parent;  // Ažuriramo trenutni blok na manji blok
        }
        freeList.remove(block);  // Uklanjanje dijeljenog bloka iz liste slobodnih blokova
    }

    // Privatna metoda za spajanje bloka sa njegovim buddy blokom
    private void merge(Block block) {
        Block buddy = block.getBuddy();
        if (buddy != null && !buddy.isAllocated()) {
            Block parent = block.getParent();
            if (parent != null) {
                freeList.remove(buddy);  // Uklanjanje buddy bloka iz liste slobodnih blokova
                freeList.add(parent);  // Dodavanje roditeljskog bloka u listu slobodnih blokova
                parent.setBuddy(null);  // Resetovanje buddy odnosa
                merge(parent);  // Rekurzivno spajanje roditeljskog bloka
            }
        } else {
            freeList.add(block);  // Dodavanje bloka u listu slobodnih blokova ako se ne može spojiti
        }
    }

    // Metoda za dobijanje liste slobodnih blokova
    public List<Block> getFreeBlocks() {
        return new ArrayList<>(freeList);
    }

    public int getFreeMemory() {
        int freeMemory = 0;
        for (Block block : freeList) {
            if (!block.isAllocated()) {
                freeMemory += block.getSizeInMB();
            }
        }
        return freeMemory;
    }
}
