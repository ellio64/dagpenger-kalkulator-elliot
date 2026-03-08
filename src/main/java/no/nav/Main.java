package no.nav;

import java.io.IOException;
import java.util.List;
import no.nav.dagpenger.DagpengerKalkulator;
import no.nav.dagpenger.Spesialisering;
import no.nav.resultatregister.Resultat;
import no.nav.resultatregister.ResultatRegister;
import no.nav.saksbehandler.Saksbehandler;
import no.nav.årslønn.Årslønn;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        ResultatRegister resultatRegister = new ResultatRegister();

        Saksbehandler saksbehandlerInnvilget = new Saksbehandler(Spesialisering.INNVILGET, resultatRegister);
        Saksbehandler saksbehandlerMakssats = new Saksbehandler(Spesialisering.INNVILGET_MED_MAKSSATS, resultatRegister);
        Saksbehandler saksbehandlerAvslag = new Saksbehandler(Spesialisering.AVSLAG_FOR_LAV_INNTEKT, resultatRegister);

        DagpengerKalkulator kalkulator1 = new DagpengerKalkulator();
        kalkulator1.leggTilÅrslønn(new Årslønn(2023, 500000));
        kalkulator1.leggTilÅrslønn(new Årslønn(2022, 450000));
        kalkulator1.leggTilÅrslønn(new Årslønn(2021, 400000));
        resultatRegister.leggTilResultat(kalkulator1.kalkulerDagsats());

        DagpengerKalkulator kalkulator2 = new DagpengerKalkulator();
        kalkulator2.leggTilÅrslønn(new Årslønn(2023, 100000));
        resultatRegister.leggTilResultat(kalkulator2.kalkulerDagsats());

        DagpengerKalkulator kalkulator3 = new DagpengerKalkulator();
        kalkulator3.leggTilÅrslønn(new Årslønn(2023, 900000));
        resultatRegister.leggTilResultat(kalkulator3.kalkulerDagsats());

        System.out.println("Saksbehandler: Innvilget");
        List<Resultat> innvilgede = saksbehandlerInnvilget.hentUbehandledeResultater();
        for (Resultat resultat : innvilgede) {
            saksbehandlerInnvilget.godkjenn(resultat);
            System.out.println("Godkjent dagsats: " + resultat.getDagsats() + " . Status: " + resultat.getStatus());
        }

        System.out.println("Saksbehandler: Innvilget med makssats");
        List<Resultat> makssats = saksbehandlerMakssats.hentUbehandledeResultater();
        for (Resultat resultat : makssats) {
            saksbehandlerMakssats.godkjenn(resultat);
            System.out.println("Godkjent dagsats: " + resultat.getDagsats() + " . Status: " + resultat.getStatus());
        }

        System.out.println("Saksbehandler: Avslag");
        List<Resultat> avslåtte = saksbehandlerAvslag.hentUbehandledeResultater();
        for (Resultat resultat : avslåtte) {
            saksbehandlerAvslag.avslå(resultat);
            System.out.println("Avslått dagsats: " + resultat.getDagsats() + " . Status: " + resultat.getStatus());
        }
    }
}