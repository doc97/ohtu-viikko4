package ohtu.verkkokauppa;

import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 * Created by Daniel Riissanen on 25.11.2018.
 */
public class KauppaTest {

    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikein() {
        String nimi = "pekka";
        String tilinumero = "12345";
        int viitenumero = 42;
        int hintasumma = 5;

        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(viitenumero);

        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu(nimi, tilinumero);

        verify(pankki).tilisiirto(eq(nimi), eq(viitenumero), eq(tilinumero), anyString(), eq(hintasumma));
    }

    @Test
    public void kahdenEriTuotteenOstoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikein() {
        String nimi = "pekka";
        String tilinumero = "12345";
        int viitenumero = 32;
        int hintasumma = 5 + 6;

        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(viitenumero);

        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.saldo(2)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(1, "kaakao", 6));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);     // ostetaan tuotetta numero 1 eli kaakaota
        k.tilimaksu(nimi, tilinumero);

        verify(pankki).tilisiirto(eq(nimi), eq(viitenumero), eq(tilinumero), anyString(), eq(hintasumma));
    }

    @Test
    public void kahdenSamanTuotteenOstoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikein() {
        String nimi = "juha";
        String tilinumero = "12345";
        int viitenumero = 40;
        int hintasumma = 5 + 5;

        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(viitenumero);

        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu(nimi, tilinumero);

        verify(pankki).tilisiirto(eq(nimi), eq(viitenumero), eq(tilinumero), anyString(), eq(hintasumma));
    }

    @Test
    public void loppuOlevanTuotteenOstoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaanOikein() {
        String nimi = "juha";
        String tilinumero = "12345";
        int viitenumero = 24;
        int hintasumma = 5;

        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(viitenumero);

        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.saldo(2)).thenReturn(0);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(1, "kaakao", 6));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);     // ostetaan tuotetta numero 2 eli kaakao
        k.tilimaksu(nimi, tilinumero);

        verify(pankki).tilisiirto(eq(nimi), eq(viitenumero), eq(tilinumero), anyString(), eq(hintasumma));
    }

    @Test
    public void aloitaAsiointiNollaaEdellistenOstoksenTiedot() {
        String nimi = "pelle";
        String tilinumero = "12345";
        int viitenumero = 24;
        int hintasumma = 5;

        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(viitenumero);

        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu(nimi, tilinumero);

        verify(pankki).tilisiirto(eq(nimi), eq(viitenumero), eq(tilinumero), anyString(), eq(hintasumma));
    }

    @Test
    public void kauppaPyytaaUudenViitenumeronJokaiselleTapahtumalle() {
        String nimi = "pelle";
        String tilinumero = "12345";
        int viitenumero = 24;

        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(viitenumero);

        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);

        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu(nimi, tilinumero);

        verify(viite, times(1)).uusi();
    }

    @Test
    public void tuotteenPoistaminenKoristaKutsuuVarastonMetodeja() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);
        k.aloitaAsiointi();
        k.lisaaKoriin(1); // +1 haeTuote()
        k.poistaKorista(1); // +1 haeTuote()

        verify(varasto, times(2)).haeTuote(1);
        verify(varasto, times(1)).palautaVarastoon(any());
    }

    @Test
    public void poistetustaTuotteestaEiMakseta() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "kaakao", 10));

        Kauppa k = new Kauppa(varasto, pankki, viite);
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.poistaKorista(1);
        k.tilimaksu("pekka", "12345");

        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), eq(10));
    }

    @Test
    public void tuotteenPoistoAinoastaanKunTuoteOnKorissa() {
        Pankki pankki = mock(Pankki.class);

        Viitegeneraattori viite = mock(Viitegeneraattori.class);
        when(viite.uusi()).thenReturn(42);

        Varasto varasto = mock(Varasto.class);
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));

        Kauppa k = new Kauppa(varasto, pankki, viite);
        k.aloitaAsiointi();
        k.poistaKorista(1);

        verify(varasto, times(0)).palautaVarastoon(any());
    }
}
