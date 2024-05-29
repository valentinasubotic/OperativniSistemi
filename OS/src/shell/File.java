package shell;

import java.util.List;

public class File {
    // Ime fajla
    private String name;
    // Veličina fajla u megabajtima
    private int sizeInMB;
    // Lista blokova koje je fajl zauzeo u memoriji
    private List<Block> allocatedBlocks;

    // Konstruktor
    public File(String name, int sizeInMB) {
        this.name = name;
        this.sizeInMB = sizeInMB;
        this.allocatedBlocks = null; // Inicijalizacija alokovanih blokova kao null
    }

    // Getter za ime fajla
    public String getName() {
        return name;
    }

    // Setter za ime fajla
    public void setName(String name) {
        this.name = name;
    }

    // Getter za veličinu fajla u megabajtima
    public int getSizeInMB() {
        return sizeInMB;
    }

    // Setter za veličinu fajla u megabajtima
    public void setSizeInMB(int sizeInMB) {
        this.sizeInMB = sizeInMB;
    }

    // Getter za listu alokovanih blokova
    public List<Block> getAllocatedBlocks() {
        return allocatedBlocks;
    }

    // Setter za listu alokovanih blokova
    public void setAllocatedBlocks(List<Block> allocatedBlocks) {
        this.allocatedBlocks = allocatedBlocks;
    }

    // Metoda za prikaz imena fajla
    @Override
    public String toString() {
        return name;
    }
}
