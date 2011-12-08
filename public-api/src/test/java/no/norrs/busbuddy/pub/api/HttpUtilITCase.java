package no.norrs.busbuddy.pub.api;


import no.norrs.busbuddy.pub.api.model.OracleServiceEnum;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class HttpUtilITCase {
    @Before
    public void setUp() {

    }

    @Test
    public void testOracleAtbEndpoint() throws IOException {
        HttpResponse response = HttpUtil.GET(String.format(OracleServiceEnum.ATB.getEndpoint(), "LOL"));
        assertEquals("LAWL", HttpUtil.readString(response.getEntity().getContent()).trim());
    }
}
