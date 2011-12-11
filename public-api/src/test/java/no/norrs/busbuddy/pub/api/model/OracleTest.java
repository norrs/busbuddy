package no.norrs.busbuddy.pub.api.model;

/**
 * Thanks to Håvard for providing regex matcher used in Oracle.java and the the main tests provided with it ;-)
 *
 * @author Roy Sindre Norangshol
 * @author Håvard Slettvold
 */


import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class OracleTest {
    private Oracle oracle;


    @Before
    public void setUp() {
        oracle = new Oracle();
    }

    @Test
    public void testNearestFromDestinationMatching() {
        oracle.setAnswer("Holdeplassen nærmest Gløshaugen er Gløshaugen Syd. Buss 5 passerer  Dronningens gate D3 kl. 1617  og  kommer til Gløshaugen Syd, 8 minutter senere. Buss 52 passerer  Munkegata M3 kl. 1625  og  kommer til  Gløshaugen Syd,  7 minutter senere. Buss 52 passerer  Torget kl. 1626  og  kommer til  Gløshaugen Syd,  6 minutter senere.  Tidene angir tidligste passeringer av holdeplassene.");
        System.out.println("--- Test 1 ---");
        assertEquals("Gløshaugen Syd", oracle.getDestinationFrom());
        System.out.println("Holdeplass fra: " + oracle.getDestinationFrom());
    }

    @Test
    public void testPassingByFromDestinationMatching() {
        oracle.setAnswer("Buss 5 passerer Glxshaugen Nord kl. 0931 og kl. 1001 og kommer til Sentrumsterminalen, 5-8 minutter senere. Buss 52 passerer Gløshaugen Nord kl. 1010 og kommer til Munkegata M3, 6 minutter senere. Tidene angir tidligste passeringer av holdeplassene.");
        System.out.println("--- Test 2 ---");
        assertEquals("Glxshaugen Nord", oracle.getDestinationFrom());
        System.out.println("Holdeplass fra: " + oracle.getDestinationFrom());
    }

    @Test
    public void testGoesFromDestinationMatching() {
        oracle.setAnswer("Buss 5 går fra Ila kl. 1055 til Dronningens gate D3 kl. 1100 og buss 9 går fra Torget kl. 1117 til Heimdal sentrum kl. 1135. Tidene angir tidligste passeringer av holdeplassene.");
        System.out.println("--- Test 3 ---");
        assertEquals("Ila", oracle.getDestinationFrom());
        System.out.println("Holdeplass fra: " + oracle.getDestinationFrom());
    }

    @Test
    public void testPassingByInFutureFromDestinationMatching() {
        oracle.setAnswer("17. Des. 2011 er en lørdag. For denne dato gjelder AtB Vinterruter. Buss 52 passerer Nardokrysset kl. 0722 og kl. 0752 og kommer til Munkegata M3, 9 minutter senere. Buss 8 passerer Nardokrysset kl. 0728 og kommer til Sentrumsterminalen, 10-13 minutter senere. Tidene angir tidligste passeringer av holdeplassene.");
        System.out.println("--- Test 4 ---");
        assertEquals("Nardokrysset", oracle.getDestinationFrom());
        System.out.println("Holdeplass fra: " + oracle.getDestinationFrom());
    }

    @Test
    public void testGoesFromEnglishFromDestinationMatching() {
        oracle.setAnswer("Bus 7 goes from Reppe at 3.43 pm to Strandveien at 4.07 pm and bus 4 goes from Strandveien at 4.25 pm to Lade allé 80 at 4.40 pm. The hours indicate the earliest passing times.");
        System.out.println("--- Test 5 ---");
        assertEquals("Reppe", oracle.getDestinationFrom());
        System.out.println("Holdeplass fra: " + oracle.getDestinationFrom());
    }

    @Test
    public void testNullIfNoAnswerIsRetreivedOrNotFound() {
        assertNull(oracle.getDestinationFrom());
        oracle.setAnswer(null);
        assertNull(oracle.getDestinationFrom());
        oracle.setAnswer("");
        assertNull(oracle.getDestinationFrom());
        oracle.setAnswer("Silly text which doesn't match the regex at all");
        assertNull(oracle.getDestinationFrom());
    }

}