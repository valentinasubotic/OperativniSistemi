package shell;

import java.util.ArrayList;
import java.util.List;

public class IODeviceSCAN {
    // Lista zahtjeva koje treba obraditi
    private List<Integer> requests;
    // Trenutna pozicija glave uređaja
    private int currentPosition;
    // Promenljiva koja označava da li se glava uređaja kreće ka gore (true) ili ka dole (false)
    private boolean movingUp;

    // Konstruktor klase
    public IODeviceSCAN() {
        // Inicijalizacija liste zahtjeva
        requests = new ArrayList<>();
        // Početna pozicija glave uređaja
        currentPosition = 0;
        // Početno kretanje glave uređaja ka gore
        movingUp = true;
    }

    // Metoda za dodavanje novog zahtjeva
    public void addRequest(int position) {
        requests.add(position);
    }

    // Metoda koja simulira rad uređaja koristeći SCAN algoritam
    public void performScan() {
        System.out.println("Početak simulacije algoritma SCAN...");
        // Petlja se izvršava sve dok ima zahteva koji nisu obrađeni
        while (!requests.isEmpty()) {
            // Proverava da li trenutna pozicija sadrži zahtev i, ako da, obrađuje ga
            if (requests.contains(currentPosition)) {
                System.out.println("Obrađen zahtjev na poziciji: " + currentPosition);
                requests.remove(Integer.valueOf(currentPosition));
            }

            // Pomeranje trenutne pozicije glave uređaja u zavisnosti od smjera kretanja
            if (movingUp) {
                currentPosition++;
            } else {
                currentPosition--;
            }

            // Ako je glava dostigla početak ili kraj diska, mijenja smjer kretanja
            if (currentPosition == 0 || currentPosition == 100) {
                movingUp = !movingUp;
            }
        }
        System.out.println("Svi zahtjevi su obrađeni.");
    }
}
