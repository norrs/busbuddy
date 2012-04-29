package no.norrs.busbuddy.pub.api.model;

import no.norrs.busbuddy.pub.api.model.answer.MetaAnswer;
import org.joda.time.DateTime;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

/**
 * @author Håvard Slettvold
 */


public class MetaAnswerTest {

    DateTime dt = new DateTime();

    MetaAnswer metaAnswer1 = new MetaAnswer("Fra","Til","1337", Arrays.asList(new DateTime[]{dt}),5);
    MetaAnswer metaAnswer2 = new MetaAnswer("Fra","Til","1337", Arrays.asList(new DateTime[]{dt}),5);

    @Test
    public void testGetters() {
        assertEquals("Fra", metaAnswer1.getStart());
        assertEquals("Til", metaAnswer1.getDestination());
        assertEquals("1337", metaAnswer1.getBusRoute());
        assertEquals("["+dt.toString()+"]", metaAnswer1.getTimes().toString());
        assertEquals(5, metaAnswer1.getDuration());
    }

    @Test
    public void testCompareTo() {
        assertTrue(metaAnswer1.compareTo(metaAnswer2));
        assertFalse(metaAnswer1.equals(metaAnswer2));
    }

    @Test
    public void testToString() {
        assertEquals(metaAnswer1.toString(), metaAnswer2.toString());
        assertFalse(metaAnswer1.equals(metaAnswer2));
    }

    @Test
    public void testInaccuracy() {
        /* Both of these durations will produce an average of 5, but should be different when compared. */
        metaAnswer1.setInaccurate("3-7");
        metaAnswer2.setInaccurate("2-8");

        assertFalse(metaAnswer1.compareTo(metaAnswer2));
    }

}
