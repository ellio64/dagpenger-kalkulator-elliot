package no.nav.saksbehandler;

import no.nav.dagpenger.Spesialisering;
import no.nav.resultatregister.Resultat;
import no.nav.resultatregister.ResultatRegister;

import java.util.List;

public class Saksbehandler {

    private final Spesialisering spesialisering;
    private final ResultatRegister resultatRegister;

    public Saksbehandler(Spesialisering spesialisering, ResultatRegister resultatRegister) {
        this.spesialisering = spesialisering;
        this.resultatRegister = resultatRegister;
    }

    public List<Resultat> hentUbehandledeResultater() {
        return resultatRegister.hentUbehandledeResultaterForSpesialisering(this.spesialisering);
    }

    public void godkjenn(Resultat resultat) {
        resultat.godkjenn();
    }

    public void avslå(Resultat resultat) {
        resultat.avslå();
    }
}
