package shell;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Assembler {
    private Map<String, Integer> symbolTable;
    private Map<String, String> opcodeTable;

    FileSystem fileSystem;
    FileSystemOrganization root;
    // Dodajemo promenljivu za čuvanje trenutnog radnog direktorijuma
    FileSystemOrganization currentDirectory;
    ProcessScheduler scheduler = new ProcessScheduler();
    private BuddyAllocator buddyAllocator;
    static Memory memory = null;
    private static int totalMemory = 2048;



    public Assembler() {
        symbolTable = new HashMap<>();
        opcodeTable = new HashMap<>();
        initializeOpcodeTable();
        fileSystem = new FileSystem();
        // Postavljamo početni direktorijum na radni direktorijum korisnika
        root = fileSystem.getRoot();
        currentDirectory = fileSystem.getRoot();
        buddyAllocator = new BuddyAllocator(1024);



          memory = new Memory(buddyAllocator);

        try {
            Thread schedulerThread = new Thread(() -> scheduler.runScheduler());
            schedulerThread.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    private void initializeOpcodeTable() {
        // Inicijalizacija opcode tablice za jednoadresne instrukcije
        opcodeTable.put("LOAD", "01");
        opcodeTable.put("STORE", "02");
        opcodeTable.put("ADD", "03");
        opcodeTable.put("SUB", "04");
        opcodeTable.put("JUMP", "05");
        opcodeTable.put("JZ", "06");  // Jump if Zero
        opcodeTable.put("JN", "07");  // Jump if Negative
        opcodeTable.put("HALT", "FF");  // Halt
    }

    public String assemble(String sourceCode) {
        String[] lines = sourceCode.split("\n");
        StringBuilder machineCode = new StringBuilder();

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith(";")) {
                continue;  // Ignoriše prazne linije i komentare
            }

            String[] tokens = trimmedLine.split("\\s+");
            String instruction = tokens[0].toUpperCase();
            String address = tokens.length > 1 ? tokens[1] : "";

            String opcode = opcodeTable.get(instruction);
            if (opcode == null) {
                throw new IllegalArgumentException("Nepoznata instrukcija: " + instruction);
            }

            if (instruction.equals("HALT")) {
                machineCode.append(opcode).append("\n");
            } else {
                Integer addressValue = resolveAddress(address);
                machineCode.append(opcode).append(String.format("%02X", addressValue)).append("\n");
            }
        }

        return machineCode.toString();
    }

    private Integer resolveAddress(String address) {
        if (address.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(address);
        } catch (NumberFormatException e) {
            // Simboličke adrese
            if (!symbolTable.containsKey(address)) {
                symbolTable.put(address, symbolTable.size() + 1);  // Jednostavna implementacija tablice simbola
            }
            return symbolTable.get(address);
        }
    }

    // Metoda za procesiranje korisničkih komandi
    public  String processCommand(String command) {
        String[] parts = command.split("\\s+");
        String action = parts[0].toLowerCase();
        StringBuilder output = new StringBuilder();

        switch (action) {
            case "cd":
                if (parts.length > 1) {
                    String directoryName = parts[1];
                    if (directoryName.equals("..")) {
                        if (currentDirectory.getParent() != null) {
                            currentDirectory = currentDirectory.getParent();
                            output.append("Promenjen direktorijum na: ").append(currentDirectory.getName());
                        } else {
                            output.append("Već ste u korenskom direktorijumu.");
                        }
                    } else {
                        FileSystemOrganization subdir = currentDirectory.changeToSubdirectory(directoryName);
                        if (subdir != null) {
                            currentDirectory = subdir;
                            output.append("Promenjen direktorijum na: ").append(currentDirectory.getName());
                        } else {
                            output.append("Direktorijum ne postoji: ").append(directoryName);
                        }
                    }
                } else {
                    output.append("Nedostaje argument za cd komandu.");
                }
                break;
            case "dir":
                output.append("Subdirectories:\n");
                for (FileSystemOrganization subdir: currentDirectory.getSubdirectories()){
                    output.append(subdir.getName() + "\n");
                }
                break;


            case "ps":
                List<Process> processesInQueue = scheduler.getProcessesInQueue();

                output.append("-----------\n");

                for (Process process : processesInQueue) {
                    output.append("Process " + process.getName() + " in state: " + process.getState()
                            + ", " + process.getExecutionTime() + "s\n");
                }

                Process currentProcess = scheduler.getCurrentRunningProcess();

                if (currentProcess != null) {
                    output.append("Process " + currentProcess.getName() + " in state: "
                            + currentProcess.getState() + ", " + currentProcess.getExecutionTime() + "s\n");
                }

                List<Process> processesFinished = scheduler.getCompletedProcesses();

                for (Process process : processesFinished) {
                    output.append("Process " + process.getName() + " in state: " + process.getState()
                            + ", " + process.getExecutionTime() + "s\n");
                }

                output.append("-----------\n");
                break;

            case "run":
                if (parts.length == 3) {

                    String processName = parts[1];
                    int memorySize = Integer.parseInt(parts[2]);

                    boolean processExists = false;
                    for (Memory.MemorySegment segment : memory.getMemorySegments()) {
                        Process existingProcess = segment.getProcess();
                        if (existingProcess != null && existingProcess.getName().equals(processName)) {
                            processExists = true;
                            System.out.println(
                                    "A process with name " + processName + " is already in memory. Ignored.");
                            break;
                        }
                    }

                    if (memory.getNumAvailableSegments() > 0 && !processExists) {
                        Process p = new Process(processName, 10, memorySize);

                        List<Memory.MemorySegment> allocatedSegments = memory.allocateMemory(p, memorySize);

                        for (Memory.MemorySegment segment : allocatedSegments) {
                            segment.setProcess(p);
                        }

                        output.append("-----------\n");

                        if (allocatedSegments.size() > 0) {
                            scheduler.addProcess(p);

                            output.append("Process " + p.getName() + ", " + p.getState() + ", time: "
                                    + p.getExecutionTime() + "s\n");
                        } else {
                            output.append("Not enough memory for process.\n");
                        }

                        output.append("-----------\n");
                    } else {
                        System.out.println("Not enough available memory segments to allocate for the process.");
                    }


                    System.out.println("Memory allocation status:");
                    List<Memory.MemorySegment> memorySegments = memory.getMemorySegments();

                    for (Memory.MemorySegment segment : memorySegments) {
                        Process process = segment.getProcess();
                        processName = (process != null) ? process.getName() : "Unallocated";
                        System.out
                                .println("Buddy block  Size: " + segment.getSize() + " Process: " + processName);
                    }
                } else {
                    output.append("Invalid command format.");
                }
                break;

            case "mkdir":
                if (parts.length == 2) {

                        String directoryName = parts[1];
                        // Provera da li direktorijum već postoji
                        boolean directoryExists = false;
                        for (FileSystemOrganization dir : currentDirectory.getSubdirectories()) {
                            if (directoryName.equals(dir.getName())) {
                                directoryExists = true;
                                break;
                            }
                        }
                        if (directoryExists) {
                            output.append("Existing folder.\n");
                        } else {
                            // Kreiranje novog direktorijuma
                            FileSystemOrganization newDir = currentDirectory.createDirectory(directoryName);
                            output.append("New directory created: " + newDir.getName() + "\n");
                        }
                } else {
                    output.append("Invalid command.");
                }
                break;
            case "touch":
                if (parts.length == 3) {
                    String fileName = parts[1];
                    int fileSizeInMB = Integer.parseInt(parts[2]);

                    if (!fileName.contains(".")) {
                        output.append("-----------\n");
                        output.append("File extension not defined properly.\n");
                        output.append("-----------\n");
                        output.append("Current Directory: " + currentDirectory + "\n");
                        output.append("Enter command, '..' to go back:\n");
                        break;
                    }

                    // Provera da li fajl već postoji u trenutnom direktorijumu
                    boolean fileExists = currentDirectory.containsFile(fileName);

                    if (!fileExists) {
                        // Alokacija memorije za novi fajl
                        List<Block> allocatedBlocks = buddyAllocator.allocate(fileSizeInMB);

                        if (!allocatedBlocks.isEmpty()) {
                            // Kreiranje fajla u trenutnom direktorijumu
                            currentDirectory.createFile(fileName, fileSizeInMB, allocatedBlocks);
                            output.append("---------------------------------\n");
                            output.append("New file created: ").append(fileName).append(" Size: ").append(fileSizeInMB).append("MB\n");
                            output.append("---------------------------------\n");
                            output.append("Current directory: ").append(currentDirectory).append("\n");
                        } else {
                            output.append("---------------------------------\n");
                            output.append("Failed to allocate memory for file: ").append(fileName).append(" Size: ").append(fileSizeInMB).append("MB\n");
                            output.append("Not enough free memory available.\n");
                            output.append("---------------------------------\n");
                            output.append("Current directory: ").append(currentDirectory).append("\n");
                        }
                    } else {
                        output.append("---------------------------------\n");
                        output.append("File ").append(fileName).append(" already exists.\n");
                        output.append("---------------------------------\n");
                        output.append("Current directory: ").append(currentDirectory).append("\n");
                    }
                } else {
                    output.append("Invalid command. Usage: touch <filename> <sizeInMB>\n");
                }
                break;
            case "rm":
                if (parts.length == 2){
                    output.append("---------------------------------\n");
                    for (FileSystemOrganization d : currentDirectory.getSubdirectories()) {
                        if (parts[1].equals(d.toString())) {
                            currentDirectory.deleteDirectory(d.getName());
                            output.append("Directory" + d.toString()+" deleted.\n");
                            break;
                        }
                    }
                    for (File f : currentDirectory.getFiles()) {
                        if (parts[1].equals(f.toString())) {
                            currentDirectory.deleteFile(f.getName());
                            for (Block block : f.getAllocatedBlocks()) {
                                buddyAllocator.deallocate(block);
                            }
                            output.append("File deleted and memory deallocated.\n");
                            break;
                        }
                    }
                }else {
                    output.append("Invalid command. \n");
                }
                break;
            case "mem":
                int freeMemory = buddyAllocator.getFreeMemory();
                output.append("---------------------------------\n");
                output.append("Free memory: ").append(freeMemory).append("MB\n");
                output.append("---------------------------------\n");
                break;
            case "exit":
                System.exit(0);
                output.append("Exiting OS...");
                break;
            default:
                output.append("Unknown command: ").append(action);
                break;
        }
        return output.toString();
    }

    private Block findBlockByAddress(int address) {
        for (Block block : buddyAllocator.getFreeBlocks()) {
            if (block.getStartAddress() == address && block.isAllocated()) {
                return block;
            }
        }
        return null;
    }
    /*
    private String changeDirectory(String path) {
        File dir = new File(currentDirectory, path);
        if (dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();
            return "Promenjen direktorijum na: " + currentDirectory;
        } else {
            return "Direktorijum ne postoji: " + path;
        }
    }
    */
    /*
    private String listDirectory() {
        File dir = new File(currentDirectory);
        File[] files = dir.listFiles();
        StringBuilder output = new StringBuilder();

        if (files != null) {
            for (File file : files) {
                output.append(file.getName()).append("\n");
            }
        }
        return output.toString();
    }

    private String listProcesses() {
        // Ispisivanje lažnih informacija o procesima kao primer
        StringBuilder output = new StringBuilder();
        //  output.append("PID\tInstrukcija\tRAM\tIzvršene Instrukcije\n");
        //  output.append("1\tLOAD\t\t1024KB\t100\n");
        //  output.append("2\tADD\t\t512KB\t50\n");
        //  output.append("3\tSTORE\t\t2048KB\t200\n");
        return output.toString();
    }

    private String createDirectory(String name) {
        File dir = new File(currentDirectory, name);
        if (dir.mkdir()) {
            return "Direktorijum je napravljen: " + name;
        } else {
            return "Neuspešno pravljenje direktorijuma: " + name;
        }
    }

    private String runProcess(String program) {
        // Ova metoda bi pokretala proces, ovde je samo kao primer
        return "Pokretanje procesa: " + program;
    }

    private String showMemoryUsage() {
        // Ispisivanje lažnih podataka o RAM memoriji kao primer
        StringBuilder output = new StringBuilder();
        output.append("Ukupna RAM: 8192KB\n");
        output.append("Zauzeta RAM: 4096KB\n");
        output.append("Slobodna RAM: 4096KB\n");
        return output.toString();
    }

    private String exitOS() {
        System.exit(0);
        return "Gašenje OS-a...";
    }


    private String removeFileOrDirectory(String name) {
        File file = new File(currentDirectory, name);
        if (file.exists()) {
            try {
                if (file.isDirectory()) {
                    Files.walk(Paths.get(file.getAbsolutePath()))
                            .map(java.nio.file.Path::toFile)
                            .sorted((o1, o2) -> -o1.compareTo(o2))
                            .forEach(File::delete);
                } else {
                    file.delete();
                }
                return "Uspješno uklonjen: " + name;
            } catch (IOException e) {
                return "Greška pri uklanjanju: " + name;
            }
        } else {
            return "Fajl ili direktorijum ne postoji: " + name;
        }
    }
     */
}
