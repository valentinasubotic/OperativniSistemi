package shell;


public class Operations {
/*
    public static final String halt = "0000";
    public static final String mov = "0001";
    public static final String store = "0010";
    public static final String add = "0011";
    public static final String sub = "0100";
    public static final String jmp = "0110";
    public static final String jmpl = "0111";
    public static final String jmpg = "1000";
    public static final String jmpe = "1001";
    public static final String load = "1010";
    public static final String jmpd = "1011";
    public static final String dec = "1100";
    public static final String inc = "1101";
*/
    public static Register R1 = new Register("R1", 0);
    public static Register R2 = new Register("R2",  0);
    public static Register R3 = new Register("R3", 0);
    public static Register R4 = new Register("R4", 0);

    // sabira vrijednosti 1. i 2. registra i cuva je na 1. registru
    public static void add(String reg, String val) {
        Register r = getRegister(reg);
        if (val.length() == 8) { // vrijednost
            if (r != null)
                r.value += Integer.parseInt(val, 2);
        } else if (val.length() == 4) { // registar
            Register r2 = getRegister(val);
            if (r != null && r2 != null)
                r.value += r2.value;
        }
    }
    public static void load(String reg, String val) {
        Register r = getRegister(reg);
        r.value = Integer.parseInt(val, 2);
    }

    // Implementacija STORE instrukcije
    public static void store(String reg, String val) {
        Register r = getRegister(reg);
        Register r2 = getRegister(val);
        r2.value = r.value;
    }
    // Implementacija SUB instrukcije
    public static void sub(String reg, String val) {
        Register r = getRegister(reg);
        if (val.length() == 8) { // vrijednost
            r.value -= Integer.parseInt(val, 2);
        } else if (val.length() == 4) { // registar
            Register r2 = getRegister(val);
            r.value -= r2.value;
        }
    }
    // Implementacija HALT instrukcije
    public static void halt() {
        System.out.println("Execution halted.");
        System.exit(0);
    }
    public static void inc(String reg) {
        Register r = getRegister(reg);
        r.value += 1;
    }

    public static void dec(String reg) {
        Register r = getRegister(reg);
        r.value -= 1;
    }
    // vraca registar na osnovu adrese registra
    private static Register getRegister(String adr) {
        switch (adr) {
            case Constants.R1:
                return R1;
            case Constants.R2:
                return R2;
            case Constants.R3:
                return R3;
            case Constants.R4:
                return R4;
            default:
                return null;
        }
    }
    public static void printRegisters() {
        System.out.println("Registers:");
        System.out.println("R1 value - [ " + R1.value + " ]");
        System.out.println("R2 value - [ " + R2.value + " ]");
        System.out.println("R3 value - [ " + R3.value + " ]");
        System.out.println("R4 value - [ " + R4.value + " ]");
    }

    public static void clearRegisters() {
        R1.value = 0;
        R2.value = 0;
        R3.value = 0;
        R4.value = 0;
    }
}

