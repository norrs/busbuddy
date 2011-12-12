package no.norrs.busbuddy.pub.api.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.List;

/**
 * @author Håvard Slettvold
 */


public class MetaAnswerFactoryTest {

    @Test
    public void testSimpleNorwegianAnswer() {
        String answer = "Buss 5 går fra Ila kl. 1055 til Dragvoll kl. 1114";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);

        assertEquals(1, metaAnswers.size());

        MetaAnswer ma = metaAnswers.get(0);

        assertEquals(5, ma.getBusRoute());
        assertEquals("Ila", ma.getStart());
        assertEquals("Dragvoll", ma.getDestination());
    }

    @Test
    public void testComplicatedNorwegianAnswer() {
        String answer = "Holdeplassen nærmest Gløshaugen er Gløshaugen Syd. Buss 5 passerer  Dronningens gate D3 kl. 1617  og  kommer til Gløshaugen Syd, 8 minutter senere."+
                "Buss 52 passerer  Munkegata M3 kl. 1625  og  kommer til  Gløshaugen Syd,  7 minutter senere. " +
                "Buss 52 passerer  Torget kl. 1626  og  kommer til  Gløshaugen Syd,  6 minutter senere.  " +
                "Tidene angir tidligste passeringer av holdeplassene.";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);
        MetaAnswer ma;

        assertEquals(3,metaAnswers.size());

        ma = metaAnswers.get(0);
        assertEquals(5, ma.getBusRoute());
        assertEquals("Dronningens gate D3", ma.getStart());
        assertEquals("Gløshaugen Syd", ma.getDestination());

        ma = metaAnswers.get(1);
        assertEquals(52, ma.getBusRoute());
        assertEquals("Munkegata M3", ma.getStart());
        assertEquals("Gløshaugen Syd", ma.getDestination());

        ma = metaAnswers.get(2);
        assertEquals(52, ma.getBusRoute());
        assertEquals("Torget", ma.getStart());
        assertEquals("Gløshaugen Syd", ma.getDestination());
    }

    @Test
    public void testEnglishAnswer() {
        String answer = "Bus 7 goes from Reppe at 3.43 pm to Strandveien at 4.07 pm and " +
                "bus 4 goes from Strandveien at 4.25 pm to Lade allé 80 at 4.40 pm. The hours indicate the earliest passing times.";
        List<MetaAnswer> metaAnswers = MetaAnswerFactory.getMetaAnswers(answer);
        MetaAnswer ma;

        assertEquals(2, metaAnswers.size());

        ma = metaAnswers.get(0);
        assertEquals(7, ma.getBusRoute());
        assertEquals("Reppe", ma.getStart());
        assertEquals("Strandveien", ma.getDestination());

        ma = metaAnswers.get(1);
        assertEquals(4, ma.getBusRoute());
        assertEquals("Strandveien", ma.getStart());
        assertEquals("Lade allé 80", ma.getDestination());
    }

}
