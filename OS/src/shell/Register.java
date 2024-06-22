package shell;

public class Register {
    public final String name; // Ime registra (opcionalno za prikaz)
    public int value; // Vrijednost registra

    public Register(String name, int value){
        this.name = name;
        this.value = value;
    }
}