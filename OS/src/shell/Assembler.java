package shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Assembler {
    private Map<String, Integer> symbolTable;
    private Map<String, String> opcodeTable;

    public Assembler() {
        symbolTable = new HashMap<>();
        opcodeTable = new HashMap<>();
        initializeOpcodeTable();
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

    // Glavna funkcija za pokretanje komandne linije
    public static void main(String[] args) {
        Assembler assembler = new Assembler();
        CommandLineInterface cli = new CommandLineInterface();
        cli.run();
    }
}

class CommandLineInterface {
    private String currentDirectory = System.getProperty("user.dir");
    private boolean running = true;

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (running) {
            System.out.print(currentDirectory + " > ");
            String command = scanner.nextLine().trim();
            processCommand(command);
        }

        scanner.close();
    }

    private void processCommand(String command) {
        String[] parts = command.split("\\s+");
        String cmd = parts[0];

        switch (cmd) {
            case "cd":
                if (parts.length > 1) {
                    changeDirectory(parts[1]);
                } else {
                    System.out.println("Nedostaje argument za cd komandu.");
                }
                break;
            case "dir":
                listDirectory();
                break;
            case "ps":
                listProcesses();
                break;
            case "mkdir":
                if (parts.length > 1) {
                    createDirectory(parts[1]);
                } else {
                    System.out.println("Nedostaje argument za mkdir komandu.");
                }
                break;
            case "run":
                if (parts.length > 1) {
                    runProcess(parts[1]);
                } else {
                    System.out.println("Nedostaje argument za run komandu.");
                }
                break;
            case "mem":
                showMemoryUsage();
                break;
            case "exit":
                exitOS();
                break;
            case "rm":
                if (parts.length > 1) {
                    removeFileOrDirectory(parts[1]);
                } else {
                    System.out.println("Nedostaje argument za rm komandu.");
                }
                break;
            default:
                System.out.println("Nepoznata komanda: " + cmd);
                break;
        }
    }

    private void changeDirectory(String path) {
        File dir = new File(currentDirectory, path);
        if (dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();
        } else {
            System.out.println("Direktorijum ne postoji: " + path);
        }
    }

    private void listDirectory() {
        File dir = new File(currentDirectory);
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                System.out.println(file.getName());
            }
        }
    }

    private void listProcesses() {
        // Ispisivanje lažnih informacija o procesima kao primer
        System.out.println("PID\tInstrukcija\tRAM\tIzvršene Instrukcije");
        System.out.println("1\tLOAD\t\t1024KB\t100");
        System.out.println("2\tADD\t\t512KB\t50");
        System.out.println("3\tSTORE\t\t2048KB\t200");
    }

    private void createDirectory(String name) {
        File dir = new File(currentDirectory, name);
        if (dir.mkdir()) {
            System.out.println("Direktorijum je napravljen: " + name);
        } else {
            System.out.println("Neuspešno pravljenje direktorijuma: " + name);
        }
    }

    private void runProcess(String program) {
        // Ova metoda bi pokretala proces, ovde je samo kao primer
        System.out.println("Pokretanje procesa: " + program);
    }

    private void showMemoryUsage() {
        // Ispisivanje lažnih podataka o RAM memoriji kao primer
        System.out.println("Ukupna RAM: 8192KB");
        System.out.println("Zauzeta RAM: 4096KB");
        System.out.println("Slobodna RAM: 4096KB");
    }

    private void exitOS() {
        running = false;
        System.out.println("Gašenje OS-a...");
    }

    private void removeFileOrDirectory(String name) {
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
                System.out.println("Uspješno uklonjen: " + name);
            } catch (IOException e) {
                System.out.println("Greška pri uklanjanju: " + name);
            }
        } else {
            System.out.println("Fajl ili direktorijum ne postoji: " + name);
        }
    }
}
