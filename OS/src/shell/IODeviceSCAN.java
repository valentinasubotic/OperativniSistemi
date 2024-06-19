package shell;

import java.util.ArrayList;
import java.util.List;

public class IODeviceSCAN {
    // Lista zahtjeva koje treba obraditi
    private List<Integer> requests;
    // Trenutna pozicija glave uređaja
    private int currentPosition;
    // Promjenljiva koja označava da li se glava uređaja kreće ka gore (true) ili ka dole (false)
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
        System.out.println("Starting SCAN algorithm simulation...");
        // Petlja se izvršava sve dok ima zahtjeva koji nisu obrađeni
        while (!requests.isEmpty()) {
            // Provjerava da li trenutna pozicija sadrži zahtjev i, ako da, obrađuje ga
            if (requests.contains(currentPosition)) {
                System.out.println("Exectued request on position: " + currentPosition);
                requests.remove(Integer.valueOf(currentPosition));
            }

            // Pomjeranje trenutne pozicije glave uređaja u zavisnosti od smjera kretanja
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
        System.out.println("All requests are executed.");
    }
}
