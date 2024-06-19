package shell;
public class FileSystem {
    // Korijenski organizacioni sistem fajlova
    private FileSystemOrganization root;

    // Konstruktor koji kreira novi sistem fajlova sa korijenskim direktorijumom
    public FileSystem() {
        root = new FileSystemOrganization("Root");
    }
    // Metoda koja vraća korijenski organizacioni sistem fajlova
    public FileSystemOrganization getRoot() {
        return root;
    }

}
