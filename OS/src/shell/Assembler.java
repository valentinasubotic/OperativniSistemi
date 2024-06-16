package shell;

import java.util.HashMap;
import java.util.Map;

public class Assembler {
    // Tablica simbola za čuvanje adresa simbola
    private Map<String, Integer> symbolTable;
    // Tablica opcode-ova za čuvanje kodova instrukcija
    private Map<String, String> opcodeTable;

    public Assembler() {
        symbolTable = new HashMap<>();
        opcodeTable = new HashMap<>();
        initializeOpcodeTable();  // Inicijalizacija tablice opcode-ova
    }

    // Inicijalizacija opcode tablice za jednoadresne instrukcije
    private void initializeOpcodeTable() {
        opcodeTable.put("LOAD", "01");
        opcodeTable.put("STORE", "02");
        opcodeTable.put("ADD", "03");
        opcodeTable.put("SUB", "04");
        opcodeTable.put("JUMP", "05");
        opcodeTable.put("JZ", "06");  // Jump if Zero
        opcodeTable.put("JN", "07");  // Jump if Negative
        opcodeTable.put("HALT", "FF");  // Halt
    }

    // Glavna funkcija za sastavljanje asembler koda u mašinski kod
    public String assemble(String sourceCode) {
        String[] lines = sourceCode.split("\n");
        StringBuilder machineCode = new StringBuilder();

        for (String line : lines) {
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty() || trimmedLine.startsWith(";")) {
                continue;  // Ignoriše prazne linije i komentare
            }

            // Parsiranje linije u tokene
            String[] tokens = trimmedLine.split("\\s+");
            String instruction = tokens[0].toUpperCase();
            String address = tokens.length > 1 ? tokens[1] : "";

            // Dobijanje opcode-a za instrukciju
            String opcode = opcodeTable.get(instruction);
            if (opcode == null) {
                throw new IllegalArgumentException("Nepoznata instrukcija: " + instruction);
            }

            // Specijalni slučaj za HALT instrukciju
            if (instruction.equals("HALT")) {
                machineCode.append(opcode).append("\n");
            } else {
                // Rezolucija adrese i sastavljanje mašinskog koda
                Integer addressValue = resolveAddress(address);
                machineCode.append(opcode).append(String.format("%02X", addressValue)).append("\n");
            }
        }

        return machineCode.toString();
    }

    // Funkcija za rezoluciju adrese
    private Integer resolveAddress(String address) {
        if (address.isEmpty()) {
            return 0;
        }

        try {
            return Integer.parseInt(address);  // Direktna adresa
        } catch (NumberFormatException e) {
            // Simboličke adrese
            if (!symbolTable.containsKey(address)) {
                symbolTable.put(address, symbolTable.size() + 1);  // Jednostavna implementacija tablice simbola
            }
            return symbolTable.get(address);
        }
    }

}

