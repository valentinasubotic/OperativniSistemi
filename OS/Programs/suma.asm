LOAD ZERO               //Učitaj konstantu nula u akumulator
STORE R1                //Postavi R1 na nulu (trenutni broj)
STORE R2                //Postavi R2 na nulu (trenutna suma)
LOAD ONE                //Učitaj konstantu jedan
STORE ONE_TEMP          //Skladišti jedinicu u privremenu promenljivu

LOOP:   LOAD R1         //Učitaj trenutnu vrednost R1 (trenutni broj)
        ADD ONE_TEMP    //Povećaj R1 za 1
        STORE R1        //Skladišti novu vrednost nazad u R1

        LOAD R2         //Učitaj trenutnu sumu (R2)
        ADD R1          //Dodaj trenutni broj (R1) sumi (R2)
        STORE R2        //Skladišti novu sumu nazad u R2

        LOAD R1         //Učitaj trenutnu vrednost R1 (trenutni broj)
        SUB TEN         //Oduzmi 10 od R1
        JZ END          //Ako je rezultat nula, završi petlju
        JUMP LOOP       //Inače, ponovi petlju

END:    LOAD R2         //Učitaj konačnu sumu
        STORE R3        //Skladišti sumu u R3 (rezultat)
        HALT            //Završetak programa

