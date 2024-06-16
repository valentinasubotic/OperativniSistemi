package shell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Assembler {
    private Map<String, Integer> symbolTable;
    private Map<String, String> opcodeTable;
    private String currentDirectory;  // Dodajemo promenljivu za čuvanje trenutnog radnog direktorijuma

    public Assembler() {
        symbolTable = new HashMap<>();
        opcodeTable = new HashMap<>();
        initializeOpcodeTable();
        currentDirectory = System.getProperty("user.dir");  // Postavljamo početni direktorijum na radni direktorijum korisnika
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
    public String processCommand(String command) {
        String[] parts = command.split("\\s+");
        String cmd = parts[0];
        StringBuilder output = new StringBuilder();

        switch (cmd) {
            case "cd":
                if (parts.length > 1) {
                    output.append(changeDirectory(parts[1]));
                } else {
                    output.append("Nedostaje argument za cd komandu.");
                }
                break;
            case "dir":
                output.append(listDirectory());
                break;
            case "ps":
                output.append(listProcesses());
                break;
            case "mkdir":
                if (parts.length > 1) {
                    output.append(createDirectory(parts[1]));
                } else {
                    output.append("Nedostaje argument za mkdir komandu.");
                }
                break;
            case "run":
                if (parts.length > 1) {
                    output.append(runProcess(parts[1]));

                } else {
                    output.append("Nedostaje argument za run komandu.");
                }
                break;
            case "mem":
                output.append(showMemoryUsage());
                break;
            case "exit":
                output.append(exitOS());
                break;
            case "rm":
                if (parts.length > 1) {
                    output.append(removeFileOrDirectory(parts[1]));
                } else {
                    output.append("Nedostaje argument za rm komandu.");
                }
                break;
            default:
                output.append("Nepoznata komanda: ").append(cmd);
                break;
        }

        return output.toString();
    }

    private String changeDirectory(String path) {
        File dir = new File(currentDirectory, path);
        if (dir.exists() && dir.isDirectory()) {
            currentDirectory = dir.getAbsolutePath();
            return "Promenjen direktorijum na: " + currentDirectory;
        } else {
            return "Direktorijum ne postoji: " + path;
        }
    }

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
        // output.append("PID\tInstrukcija\tRAM\tIzvršene Instrukcije\n");
        //   output.append("1\tLOAD\t\t1024KB\t100\n");
//output.append("2\tADD\t\t512KB\t50\n");
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
}