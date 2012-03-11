package caarray.client.examples.java;

import gov.nih.nci.caarray.services.ServerConnectionException;
import gov.nih.nci.caarray.services.external.v1_0.CaArrayServer;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.login.FailedLoginException;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class CASRemoteEJBClient {

    private final DefaultHttpClient httpclient = new DefaultHttpClient();
    
    public static void main(String[] args) {
        CASRemoteEJBClient client = new CASRemoteEJBClient();
        try {
            client.searchExperiment();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void searchExperiment() throws ServerConnectionException, FailedLoginException, ClientProtocolException, IOException {
        SearchExperimentsByCriteria se = new SearchExperimentsByCriteria();
        CaArrayServer server = new CaArrayServer(   
                BaseProperties.getServerHostname(), 
                BaseProperties.getServerJndiPort());
        server.connect();
        se.doSearch( server );
        
        String ticket = login( true ); // Emulating caIntegrator login
        login( false ); // Emulating SSO caArray2 login
        
        server.connect( BaseProperties.SERVICE_URL, ticket );
        se.doSearch( server );
    }

    private String login( boolean bPostingCredentials ) throws ClientProtocolException, IOException {
        String result = "";
        printCookies("---HTTP--- Initial set of cookies:");
        HttpGet httpget = new HttpGet(BaseProperties.SERVICE_URL);
        HttpResponse response = httpclient.execute(httpget);
        printCookies("---HTTP--- Cookies after first get:");
        HttpEntity entity = response.getEntity();
        String content = IOUtils.toString(entity.getContent());
/*
            <input type="hidden" name="lt" value="LT-38-TPi7eJ2w3UNbO2dK0qPphKUPGuZJna" />
            <input type="hidden" name="execution" value="e1s1" />
            <input type="hidden" name="_eventId" value="submit" />
*/
        Document doc = Jsoup.parse(content);
        String loginTicket = doc.select("input[name=lt]").attr("value");
        String execution = doc.select("input[name=execution]").attr("value");
        String eventId = doc.select("input[name=_eventId]").attr("value");
        
        if (entity != null) {
            EntityUtils.consume(entity);
        }
        
        if( bPostingCredentials ) {
            String postURL = BaseProperties.CAS_URL + "/login?" + 
                    URLEncoder.encode( BaseProperties.SERVICE_URL, HTTP.UTF_8 ); 
            HttpPost httpost = new HttpPost( postURL );
    
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair( "username", BaseProperties.USERNAME ));
            nvps.add(new BasicNameValuePair( "password", BaseProperties.PASSWORD ));
            nvps.add(new BasicNameValuePair( "lt", loginTicket ));
            nvps.add(new BasicNameValuePair( "execution", execution ));
            nvps.add(new BasicNameValuePair( "_eventId", eventId ));

            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            response = httpclient.execute(httpost);
            printCookies("---HTTP--- Cookies after posting credentials:");

            Header[] headers = response.getHeaders("Location");
            String serviceTicketURL = headers[0].getValue();
            Pattern p = Pattern.compile( ".*ticket=(.*)$", Pattern.CASE_INSENSITIVE );
            Matcher m = p.matcher(serviceTicketURL);
            m.find();
            result = m.group(1);
            consumeEntity(response);
        }
        System.out.println("");
        
        return result;
    }

    private void consumeEntity(HttpResponse response) throws IOException {
        System.out.println("---HTTP--- Response status: " + response.getStatusLine());
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            EntityUtils.consume(entity);
        }
    }

    private void printCookies( String title ) {
        System.out.println(title);
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            System.out.println("---HTTP---     None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("---HTTP---     " + cookies.get(i).toString());
            }
        }
    }

}
