package shell;
import java.util.ArrayList;
import java.util.List;
public class FileSystemOrganization {


    // Ime direktorijuma
    private String name;
    // Lista poddirektorijuma
    private List<FileSystemOrganization> subdirectories;
    // Lista fajlova
    private List<File> files;
    // Roditeljski direktorijum
    private FileSystemOrganization parent;

    // Konstruktor
    public FileSystemOrganization(String name) {
        this.name = name;
        subdirectories = new ArrayList<>();
        files = new ArrayList<>();
        parent = null;
    }

        // Dodavanje poddirektorijuma
        public void addDirectory(FileSystemOrganization directory) {
            subdirectories.add(directory);
        }

        // Dodavanje fajla
        public void addFile(File file) {
            files.add(file);
        }

        // Getter za ime direktorijuma
        public String getName() {
            return name;
        }

        // Setter za ime direktorijuma
        public void setName(String name) {
            this.name = name;
        }

        // Getter za listu poddirektorijuma
        public List<FileSystemOrganization> getSubdirectories() {
            return subdirectories;
        }

        // Setter za listu poddirektorijuma
        public void setSubdirectories(List<FileSystemOrganization> subdirectories) {
            this.subdirectories = subdirectories;
        }

        // Getter za listu fajlova
        public List<File> getFiles() {
            return files;
        }

        // Setter za listu fajlova
        public void setFiles(List<File> files) {
            this.files = files;
        }

        // Metoda za prikaz imena direktorijuma
        @Override
        public String toString() {
            return name;
        }

        // Getter za roditeljski direktorijum
        public FileSystemOrganization getParent() {
            return parent;
        }

        // Setter za roditeljski direktorijum
        public void setParent(FileSystemOrganization parent) {
            this.parent = parent;
        }

        // Metoda za kreiranje novog poddirektorijuma
        public FileSystemOrganization createDirectory(String name) {
            FileSystemOrganization newDirectory = new FileSystemOrganization(name);
            subdirectories.add(newDirectory);
            newDirectory.setParent(this);
            return newDirectory;
        }

        // Metoda za kreiranje novog fajla
        public void createFile(String name, int sizeInMB, List<Block> allocatedBlocks) {
            File newFile = new File(name, sizeInMB);
            newFile.setAllocatedBlocks(allocatedBlocks); // Postavljanje alokovanih blokova za fajl
            files.add(newFile);
        }

        // Metoda za pronalaženje poddirektorijuma po imenu
        public FileSystemOrganization getSubdirectoryByName(String name) {
            for (FileSystemOrganization subdir : subdirectories) {
                if (subdir.getName().equals(name)) {
                    return subdir;
                }
            }
            return null;
        }

        // Metoda za promenu trenutnog direktorijuma na poddirektorijum
        public FileSystemOrganization changeToSubdirectory(String name) {
            FileSystemOrganization subdir = getSubdirectoryByName(name);
            if (subdir != null) {
                return subdir;
            } else {
                System.out.println("Directory not found: " + name);
                return this;
            }
        }

        // Metoda za brisanje direktorijuma
        public void deleteDirectory(String name) {
            FileSystemOrganization directoryToDelete = getSubdirectoryByName(name);
            if (directoryToDelete != null) {
                subdirectories.remove(directoryToDelete);
            }
        }

        // Metoda za brisanje fajla
        public void deleteFile(String name) {
            File fileToDelete = getFileByName(name);
            if (fileToDelete != null) {
                files.remove(fileToDelete);
            }
        }

        // Metoda za pronalaženje fajla po imenu
        private File getFileByName(String name) {
            for (File file : files) {
                if (name.equals(file.getName())) {
                    return file;
                }
            }
            return null;
        }
    }


