/*
 * Copyright 2011 BusBuddy (Roy Sindre Norangshol)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package no.norrs.busbuddy.pub.api;


import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import javax.net.ssl.SSLException;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Roy Sindre Norangshol
 * http://www.roysindre.no
 * <p/>
 * Date: 11/27/10
 * Time: 4:33 PM
 */
public class HttpUtil {

    private static final String USER_AGENT = "BusBuddy/1.2";

    public static HttpResponse GET(String url) throws IOException, SSLException {
        return GET(null, url, null);
    }

    public static HttpResponse GET(String apiKey, String url) throws IOException, SSLException {
        return GET(apiKey, url, null);
    }

    public static HttpResponse GET(String url, UsernamePasswordCredentials credentials) throws IOException, SSLException {
        return GET(null, url, credentials);
    }

    public static HttpResponse GET(String apiKey, String url, UsernamePasswordCredentials credentials) throws IOException, SSLException {


        //int timeoutConnection = 55000*5;
        // Set the default socket timeout (SO_TIMEOUT)
        // in milliseconds which is the timeout for waiting for data.
        //int timeoutSocket = 60000*5;

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", new PlainSocketFactory(), 80));
        //registry.register(new Scheme("https", new LocalTrustSSLSocketFactory(url), 443));

        HttpParams httpParams = new BasicHttpParams();
        //HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
        //HttpConnectionParams.setSoTimeout(httpParams, timeoutSocket);
        //HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        httpParams.setParameter("http.protocol.version", HttpVersion.HTTP_1_1)
        ;


        DefaultHttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, registry), httpParams);


        HttpGet httpGet = new HttpGet(url);

        //  httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("User-Agent", USER_AGENT);
        if (apiKey != null) {
            httpGet.addHeader("X-norrs-busbuddy-apikey", apiKey);
        }
        InputStream response = null;
        HttpResponse httpResponse = httpClient.execute(httpGet);
        return httpResponse;


    }

     public static HttpResponse POST(URL url, String payload) throws IOException, URISyntaxException {

        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(url.toURI());
        post.addHeader("User-Agent", USER_AGENT);

        StringEntity entity = new StringEntity(payload, "UTF-8");
        entity.setContentType("application/json");
        post.setEntity(entity);


        HttpResponse response = httpClient.execute(post);

        return response;
    }


    public static String readString(InputStream inputStream) throws UnsupportedEncodingException,IOException {
        BufferedReader reader = null;
        StringBuilder stringBuilder = new StringBuilder();

            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }


        return stringBuilder.toString();
    }


}