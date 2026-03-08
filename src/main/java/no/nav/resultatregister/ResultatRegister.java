package no.nav.resultatregister;

import java.util.ArrayList;
import java.util.List;
import no.nav.dagpenger.Spesialisering;

public class ResultatRegister {
    private final List<Resultat> register;

    public ResultatRegister() {
        this.register = new ArrayList<>();
    }

    public void leggTilResultat(Resultat resultat) {
        this.register.add(resultat);
    }

    public List<Resultat> hentUbehandledeResultaterForSpesialisering(Spesialisering spesialisering) {
        List<Resultat> ubehandlede = new ArrayList<>();
        for (Resultat resultat : this.register) {
            if (resultat.getSpesialisering() == spesialisering && !resultat.erBehandlet()) {
                ubehandlede.add(resultat);
            }
        }
        return ubehandlede;
    }
}
