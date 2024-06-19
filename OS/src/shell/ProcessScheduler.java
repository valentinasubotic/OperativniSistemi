package shell;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessScheduler {
    // Red spremnih procesa
    private Queue<Process> readyQueue;
    // Lista završenih procesa
    private List<Process> completedProcesses;
    // Trenutno izvršavani proces
    private Process currentRunningProcess;

    // Konstruktor koji inicijalizuje red spremnih procesa i listu završenih procesa
    public ProcessScheduler() {
        readyQueue = new ConcurrentLinkedQueue<>();
        completedProcesses = new ArrayList<>();
    }

    // Metoda za dodavanje procesa u red spremnih procesa
    public void addProcess(Process process) {
        readyQueue.add(process);
    }

    // Metoda koja vraća listu procesa u redu spremnih procesa
    public List<Process> getProcessesInQueue() {
        return new ArrayList<>(readyQueue);
    }

    // Metoda koja vraća trenutno izvršavani proces
    public Process getCurrentRunningProcess() {
        return currentRunningProcess;
    }

    // Metoda koja pokreće raspoređivač procesa
    public void runScheduler() {
        while (true) {  // Beskonačna petlja za stalno pokretanje raspoređivača
            if (!readyQueue.isEmpty()) {    // Provjerava da li red spremnih procesa nije prazan
                Process currentProcess = readyQueue.poll(); // Uklanja i dobija prvi proces iz reda
                currentRunningProcess = currentProcess; // Postavlja trenutni proces kao trenutno izvršavani

                while (!currentProcess.isCompleted()) { // Dok se trenutni proces ne završi
                    currentProcess.execute(1);  // Izvršava proces za 1 vremenski jedinicu
                    System.out.println(currentProcess.getName());   // Ispisuje ime trenutnog procesa

                    try {
                        Thread.sleep(1000); // Pauzira izvršenje na 1 sekundu da simulira vrijeme izvršenja
                    } catch (InterruptedException e) {
                        e.printStackTrace();    // Štampa stack trace u slučaju izuzetka
                    }
                }

                System.out.println("Process " + currentProcess.getName() + " DONE"); // Ispisuje da je proces završen

                currentRunningProcess.setState(Process.State.DONE);
                completedProcesses.add(currentRunningProcess);
                currentRunningProcess = null;
            }
        }
    }

    // Metoda koja vraća listu završenih procesa
    public List<Process> getCompletedProcesses() {
        return completedProcesses;
    }
}
