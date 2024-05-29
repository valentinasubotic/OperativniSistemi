package shell;

public class Process {
    // Ime procesa
    private String name;
    // Vrijeme potrebno za izvršenje procesa
    private int executionTime;
    // Memorijski zahtjev procesa
    private int memoryRequirement;
    // Stanje procesa
    private State state;

    // Enumeracija za stanja procesa
    public enum State {
        READY,    // Proces je spreman za izvršenje
        RUNNING,  // Proces se trenutno izvršava
        DONE      // Proces je završen
    }

    // Konstruktor koji inicijalizuje proces sa imenom, vremenom izvršenja i memorijskim zahtevom
    public Process(String name, int executionTime, int memoryRequirement) {
        this.name = name;
        this.executionTime = executionTime;
        this.memoryRequirement = memoryRequirement;
        this.state = State.READY; // Inicijalno stanje procesa je READY (spreman)
    }

    // Metoda koja vraća ime procesa
    public String getName() {
        return name;
    }

    // Metoda koja vraća vrijeme potrebno za izvršenje procesa
    public int getExecutionTime() {
        return executionTime;
    }

    // Metoda koja vraća memorijski zahtjev procesa
    public int getMemoryRequirement() {
        return memoryRequirement;
    }

    // Metoda koja vraća trenutno stanje procesa
    public State getState() {
        return state;
    }

    // Metoda koja postavlja stanje procesa
    public void setState(State state) {
        this.state = state;
    }

    // Metoda koja simulira izvršenje procesa za određeno vreme
    public void execute(int time) {
        executionTime -= time; // Smanjuje preostalo vrijeme izvršenja za zadato vrijeme

        // Ako je preostalo vrijeme izvršenja manje ili jednako nuli, proces je završen
        if (executionTime <= 0) {
            state = State.DONE; // Postavlja stanje procesa na DONE
        } else {
            state = State.RUNNING; // Inače, proces prelazi u stanje RUNNING
        }
    }

    // Metoda koja proverava da li je proces završen
    public boolean isCompleted() {
        return state == State.DONE;
    }
}
