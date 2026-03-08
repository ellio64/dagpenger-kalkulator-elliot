package no.nav.dagpenger;

import no.nav.grunnbeløp.GrunnbeløpVerktøy;
import no.nav.årslønn.Årslønn;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.io.IOException;
import java.lang.InterruptedException;

/**
 * Kalkulator for å beregne hvor mye dagpenger en person har rett på i Norge basert på dagens grunnbeløp (1G).
 * For at en person skal ha rett på dagpenger, må en av de to følgene kravene være møtt:
 *      De siste 3 årene må gjennomsnitslønnen være høyere enn 3G.
 *      Tjent mer det siste året enn 1.5G.
 * Hvis en person har rett på dagpenger, må følgende ting vurderes for å kalkulere dagsatsen:
 *      Hva er størst av gjennomsnittlig årslønn de 3 siste årene og siste årslønn.
 *      Hvis siste årslønn er størst, er årslønnen høyere enn 6G.
 * Antall årlige arbeidsdager i Norge er satt til å være 260, så ved beregning av dagsats må 260 dager
 * brukes og ikke 365.
 *
 * @author Emil Elton Nilsen
 * @version 1.0
 */
public class DagpengerKalkulator {

    private final GrunnbeløpVerktøy grunnbeløpVerktøy;

    private final List<Årslønn> årslønner;

    private final static int ANTALL_ÅRSLØNNER = 3;

    private final static int ARBEIDSDAGER_I_ÅRET = 260;

    public enum BeregningsMetode {
        MAKS_ÅRLIG_DAGPENGERGRUNNLAG,
        SISTE_ÅRSLØNN,
        GJENNOMSNITTET_AV_TRE_ÅR
    }

    public DagpengerKalkulator() throws IOException, InterruptedException {
        try {
            this.grunnbeløpVerktøy = new GrunnbeløpVerktøy();
        } catch (IOException exception) {
            throw new IOException("Kunne ikke hente grunnbeløp fra API", exception);
        }
        this.årslønner = new ArrayList<>();
    }

    /**
     * Hvis en person har rett på dagpenger, vil den kalkulere dagsatsen en person har rett på.
     * Hvis ikke en person har rett på dagpenger, vil metoden returnere 0kr som dagsats, som en antagelse på at det
     * er det samme som å ikke ha rett på dagpenger.
     * @return dagsatsen en person har rett på.
     */
    public double kalkulerDagsats() throws IllegalStateException {
        double dagsats = 0;

        if (harRettigheterTilDagpenger()) {

            switch(velgBeregningsMetode()){
                case GJENNOMSNITTET_AV_TRE_ÅR:
                    return Math.ceil((summerNyligeÅrslønner(ANTALL_ÅRSLØNNER) / ANTALL_ÅRSLØNNER) / ARBEIDSDAGER_I_ÅRET);
                case SISTE_ÅRSLØNN:
                    return Math.ceil(hentÅrslønnVedIndeks(0).hentÅrslønn() / ARBEIDSDAGER_I_ÅRET);
                case MAKS_ÅRLIG_DAGPENGERGRUNNLAG:
                    return Math.ceil(grunnbeløpVerktøy.hentMaksÅrligDagpengegrunnlag() / ARBEIDSDAGER_I_ÅRET);
                default:
                    throw new IllegalStateException("Ukjent beregningsmetode");
            }
        }

        return dagsats;
    }

    /**
     * Sjekker om en person har rettighet til dagpenger eller ikke.
     * @return om personen har rett på dagpenger.
     */
    public boolean harRettigheterTilDagpenger() {
        return summerNyligeÅrslønner(ANTALL_ÅRSLØNNER) >= grunnbeløpVerktøy.hentTotaltGrunnbeløpForGittAntallÅr(ANTALL_ÅRSLØNNER)
            || hentÅrslønnVedIndeks(0).hentÅrslønn() >= grunnbeløpVerktøy.hentMinimumÅrslønnForRettPåDagpenger();
    }

    /**
     * Velger hva som skal være beregnings metode for dagsats ut ifra en person sine årslønner.
     * @return beregnings metode for dagsats.
     */
    public BeregningsMetode velgBeregningsMetode() {
        double årslønn = hentÅrslønnVedIndeks(0).hentÅrslønn();

        if (årslønn > (summerNyligeÅrslønner(ANTALL_ÅRSLØNNER) / ANTALL_ÅRSLØNNER)) {
            if (årslønn > grunnbeløpVerktøy.hentMaksÅrligDagpengegrunnlag()) {
                return BeregningsMetode.MAKS_ÅRLIG_DAGPENGERGRUNNLAG;
            }
            return BeregningsMetode.SISTE_ÅRSLØNN;
        } else {
            return BeregningsMetode.GJENNOMSNITTET_AV_TRE_ÅR;
        }
    }

    public void leggTilÅrslønn(Årslønn årslønn) {
        this.årslønner.add(årslønn);
        this.sorterÅrslønnerBasertPåNyesteÅrslønn();
    }

    /**
     * Henter årslønnen i registeret basert på dens posisjon i registeret ved gitt indeks.
     * @param indeks posisjonen til årslønnen.
     * @return årslønnen ved gitt indeks.
     */
    private Årslønn hentÅrslønnVedIndeks(int indeks) {
        if (this.årslønner.isEmpty()) return new Årslønn(0, 0);

        return this.årslønner.get(indeks);
    }

    /**
     * Summemer sammen antall årslønner basert på gitt parameter.
     * @param antallÅrÅSummere antall år med årslønner vi vil summere.
     * @return summen av årslønner.
     */
    private double summerNyligeÅrslønner(int antallÅrÅSummere) {
        double sumAvNyligeÅrslønner = 0;
        int antall = Math.min(antallÅrÅSummere, this.årslønner.size());

        for (Årslønn årslønn : this.årslønner.subList(0, antall)) {
            sumAvNyligeÅrslønner += årslønn.hentÅrslønn();
        }

        return sumAvNyligeÅrslønner;
    }

    /**
     * Sorterer registeret slik at den nyligste årslønnen er det først elementet i registeret.
     */
    private void sorterÅrslønnerBasertPåNyesteÅrslønn() {
        this.årslønner.sort(Comparator.comparingInt(Årslønn::hentÅretForLønn).reversed());
    }
}
