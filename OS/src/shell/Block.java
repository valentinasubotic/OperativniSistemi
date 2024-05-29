package shell;

public class Block {
    private int startAddress;
    private int sizeInMB;
    private boolean allocated;
    private Block buddy;
    private Block parent;
    // Konstruktor za inicijalizaciju bloka memorije sa početnom adresom i veličinom
    public Block(int startAddress, int sizeInMB) {
        this.startAddress = startAddress;
        this.sizeInMB = sizeInMB;
        this.allocated = false;  // Blok je inicijalno slobodan
        this.buddy = null;
        this.parent = null;
    }

    // Getter za početnu adresu bloka
    public int getStartAddress() {
        return startAddress;
    }

    // Getter za veličinu bloka u MB
    public int getSizeInMB() {
        return sizeInMB;
    }

    // Metoda koja provjerava da li je blok alociran
    public boolean isAllocated() {
        return allocated;
    }

    // Metoda za alokaciju bloka
    public void allocate() {
        allocated = true;
    }

    // Metoda za dealokaciju bloka
    public void deallocate() {
        allocated = false;
    }

    // Getter za buddy blok
    public Block getBuddy() {
        return buddy;
    }

    // Setter za buddy blok
    public void setBuddy(Block buddy) {
        this.buddy = buddy;
    }

    // Getter za roditeljski blok
    public Block getParent() {
        return parent;
    }

    // Setter za roditeljski blok
    public void setParent(Block parent) {
        this.parent = parent;
    }


}


