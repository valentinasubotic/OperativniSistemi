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

    public List<Block> allocate(int sizeInMB) {
        List<Block> allocatedBlocks = new ArrayList<>();    // Kreiramo listu blokova koji će biti alocirani
        int remainingSize = sizeInMB;   // Postavljamo preostalu veličinu koja treba biti alocirana

        while (remainingSize > 0) {
            Block block = findFreeBlock(remainingSize);     // Pronalazimo slobodan blok koji je dovoljno velik za preostalu veličinu
            if (block != null) {
                split(block, remainingSize);    // Dijelimo blok ako je potrebno da bi odgovarao preostaloj veličini
                block.allocate();   // Alociramo blok
                allocatedBlocks.add(block);
                remainingSize -= block.getSizeInMB();   // Smanjujemo preostalu veličinu za veličinu upravo alociranog bloka
                allocatedMemory += block.getSizeInMB();
            } else {
                // Ako nema dovoljno velikih slobodnih blokova, dealociramo sve prethodno alocirane blokove
                for (Block b : allocatedBlocks) {
                    b.deallocate();
                }
                allocatedBlocks.clear();    // Praznimo listu alociranih blokova jer alokacija nije uspela
                break;
            }
        }
        return allocatedBlocks;
    }


    // Metoda za dealokaciju bloka memorije
    public void deallocate(Block block) {
        block.deallocate(); // Postavljanje bloka kao slobodnog
        //merge(block); // Pokušaj spajanja bloka sa njegovim buddy blokom
        allocatedMemory -= block.getSizeInMB(); // Ažurirajte alociranu memoriju
    }

    // Privatna metoda za pronalazak slobodnog bloka odgovarajuće veličine
    private Block findFreeBlock(int sizeInMB) {
        for (Block block : freeList) {
            if (!block.isAllocated() && block.getSizeInMB() >= sizeInMB) {
                return block;
            }
        }
        return null;    // Nema odgovarajućeg slobodnog bloka
    }

    // Privatna metoda za dijeljenje bloka na manje blokove
    private void split(Block block, int sizeInMB) {
        while (block.getSizeInMB() / 2 >= sizeInMB) {
            int newSize = block.getSizeInMB() / 2;
            Block buddy = new Block(block.getStartAddress() + newSize, newSize);    // Kreiranje buddy bloka
            buddy.setBuddy(block);
            block.setBuddy(buddy);

            // Kreiramo novi blok sa smanjenom veličinom umjesto da mijenjamo postojeći
            Block parent = new Block(block.getStartAddress(), newSize);
            parent.setBuddy(buddy);
            buddy.setParent(parent);
            parent.setParent(block.getParent());

            // Ažuriramo buddy blokove tako da ukazuju na novi parent blok
            freeList.add(buddy);    // Dodavanje buddy bloka u listu slobodnih blokova
            freeList.remove(block);
            freeList.add(parent);

            block = parent; // Ažuriramo trenutni blok na manji blok
        }
        freeList.remove(block); // Uklanjanje dijeljenog bloka iz liste slobodnih blokova
    }

    // Privatna metoda za spajanje bloka sa njegovim buddy blokom
    private void merge(Block block) {
        Block buddy = block.getBuddy();
        if (buddy != null && !buddy.isAllocated()) {
            // Pronađi roditeljski blok
            Block parent = block.getParent();
            if (parent != null) {
                // Ukloni buddy blok iz liste slobodnih blokova
                freeList.remove(buddy);
                // Dodaj roditeljski blok u listu slobodnih blokova
                freeList.add(parent);
                // Resetuj buddy odnos
                parent.setBuddy(null);
                // Rekurzivno spajanje roditeljskog bloka
                merge(parent);
            }
        } else {
            // Dodaj blok nazad u listu slobodnih blokova ako se ne može spojiti
            freeList.add(block);
        }
    }

    // Metoda za dobijanje liste slobodnih blokova
    public List<Block> getFreeBlocks() {
        return new ArrayList<>(freeList);
    }

    // Metoda za izračunavanje ukupne slobodne memorije u MB.
    public int getFreeMemory() {
        int freeMemory = 0;
        for (Block block : freeList) {
            // Ako blok nije alociran (tj. slobodan je)
            if (!block.isAllocated()) {
                // Dodajemo veličinu slobodnog bloka ukupnoj slobodnoj memoriji
                freeMemory += block.getSizeInMB();
            }
        }
        return freeMemory;
    }
}
