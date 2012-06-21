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

    public void searchExperiment() throws ServerConnectionException, FailedLoginException,
            ClientProtocolException, IOException {
        SearchExperimentsByCriteria experimentSearchCriteria = new SearchExperimentsByCriteria();
        CaArrayServer server = new CaArrayServer(BaseProperties.getServerHostname(),
                BaseProperties.getServerJndiPort());
        server.connect();
        /*
         * Verify anonymous search does not return results.
         */
        experimentSearchCriteria.doSearch(server);

        // Emulate a login from caIntegrator through CAS
        String ticket = login();

        /*
        * To properly authenticate users with CAS information for EJB calls the caArray application requires 2 pieces of information:
        * + The service value that was used at authentication time when the ticket was created
        * + The ticket value that was provided by CAS
        */
        server.connect(BaseProperties.getServiceURLforCAS(), ticket);
        experimentSearchCriteria.doSearch(server);

        /*
         * This shows that in addition to being able to authenticate with CAS the application also provides
         * the capability to authenticate against the database in an SSO environment.
         */
        server.connect(BaseProperties.CAS_USERNAME, BaseProperties.CAS_USER_CAARRAY_PASSWORD);
        experimentSearchCriteria.doSearch(server);
    }

    /*
     * Create a Java HTTP Client that will load the CAS Login page then simulate submitting
     * the login form with the Username and Password and returning the Ticket key that is provided
     * by CAS when redirecting back to the application.
     */
    private String login() throws ClientProtocolException, IOException {
        HttpResponse casLoginPageResponse = loadCasLoginPage();
        printCookies("---HTTP--- Cookies after first get:");
        HttpResponse submitLoginResponse = submitCasLogin(casLoginPageResponse);
        printCookies("---HTTP--- Cookies after posting credentials:");
        return getTicketValue(submitLoginResponse);
    }

    private HttpResponse loadCasLoginPage() throws ClientProtocolException, IOException {
        printCookies("---HTTP--- Initial set of cookies:");
        HttpGet httpget = new HttpGet(BaseProperties.CAS_URL + "?service="
                + URLEncoder.encode(BaseProperties.getServiceURLforCAS(), HTTP.UTF_8));
        return httpclient.execute(httpget);
    }

    private void printCookies(String title) {
        System.out.println(title);
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            System.out.println("---COOKIE---     None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                System.out.println("---COOKIE---     " + cookies.get(i).toString());
            }
        }
    }

    private void consumeEntity(HttpResponse response) throws IOException {
        System.out.println("---HTTP--- Response status: " + response.getStatusLine());
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            EntityUtils.consume(entity);
        }
    }

    private HttpResponse submitCasLogin(HttpResponse casLoginPageResponse)
            throws ClientProtocolException, IOException {
        String postURL = BaseProperties.CAS_URL + "/login";
        HttpPost httpPost = new HttpPost(postURL);

        // These 3 input fields are hidden values set in the CAS login page, CAS server expects them to facilitate its workflows,
        // we will be capturing them from the page and adding to username/password combination when posting login
        //      <input type="hidden" name="lt" value="LT-38-TPi7eJ2w3UNbO2dK0qPphKUPGuZJna" />
        //      <input type="hidden" name="execution" value="e1s1" />
        //      <input type="hidden" name="_eventId" value="submit" />
        Document contentDocument = getContentDocument(casLoginPageResponse);
        String loginTicketParam = contentDocument.select("input[name=lt]").attr("value");
        String executionParam = contentDocument.select("input[name=execution]").attr("value");
        String eventIdParam = contentDocument.select("input[name=_eventId]").attr("value");


        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", BaseProperties.CAS_USERNAME));
        nvps.add(new BasicNameValuePair("password", BaseProperties.CAS_PASSWORD));
        nvps.add(new BasicNameValuePair("lt", loginTicketParam));
        nvps.add(new BasicNameValuePair("execution", executionParam));
        nvps.add(new BasicNameValuePair("_eventId", eventIdParam));

        httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        return httpclient.execute(httpPost);
    }

    private Document getContentDocument(HttpResponse response) throws IllegalStateException, IOException {
        HttpEntity entity = response.getEntity();
        String content = IOUtils.toString(entity.getContent());
        consumeEntity(response);
        return Jsoup.parse(content);
    }

    private String getTicketValue(HttpResponse response) throws IOException {
        String result = "";
        Header[] headers = response.getHeaders("Location");
        String serviceTicketURL = headers[0].getValue();
        Pattern p = Pattern.compile(".*ticket=(.*)$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(serviceTicketURL);
        m.find();
        result = m.group(1);
        consumeEntity(response);
        return result;
    }
}
