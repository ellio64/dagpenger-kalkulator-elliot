package no.nav.resultatregister;

import no.nav.dagpenger.Spesialisering;

public class Resultat {

    public enum Status {
        UBEHANDLET,
        GODKJENT,
        AVSLÅTT
    }

    private final double dagsats;
    private final Spesialisering spesialisering;
    private Status status;

    public Resultat(double dagsats, Spesialisering spesialisering) {
        this.dagsats = dagsats;
        this.spesialisering = spesialisering;
        this.status = Status.UBEHANDLET;
    }

    public double getDagsats() {
        return dagsats;
    }

    public Spesialisering getSpesialisering() {
        return spesialisering;
    }

    public boolean erBehandlet() {
        return this.status != Status.UBEHANDLET;
    }

    public void godkjenn() {
        this.status = Status.GODKJENT;
    }

    public void avslå() {
        this.status = Status.AVSLÅTT;
    }

    public Status getStatus() {
        return status;
    }
}
