

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author herc
 */
public class AuthorIDFetcher {

//    private static WebClient webClient;
    private static String base64Encode(String stringToEncode) {
        return DatatypeConverter.printBase64Binary(stringToEncode.getBytes());
    }

    public static void main(String args[]) {
        try {

//            webClient = new WebClient(BrowserVersion.CHROME);
//            JavaScriptEngine engine = new JavaScriptEngine(webClient);
//            webClient.setJavaScriptEngine(engine);
//            String base64encodedUsernameAndPassword = base64Encode("affiliationId=&txGid=BC646E4994C0A013845A83E47A054EA4.fM4vPBipdL1BpirDq5Cw%3a7");
////            webClient.addRequestHeader("Authorization", "Basic ");
//            webClient.getPage("http://www.scopus.com/results/authorNamesList.url?sort=count-f&src=al&sid=BC646E4994C0A013845A83E47A054EA4.fM4vPBipdL1BpirDq5Cw%3a80&sot=al&sdt=al&sl=46&s=AUTH--LAST--NAME%28Varlamis%29+AND+AUTH--FIRST%28I.%29&st1=Varlamis&st2=I.&selectionPageSearch=anl&reselectAuthor=false&activeFlag=false&showDocument=false&resultsPerPage=20&offset=1&jtp=false&currentPage=1&previousSelectionCount=0&tooManySelections=false&previousResultCount=0&authSubject=LFSC&authSubject=HLSC&authSubject=PHSC&authSubject=SOSC&exactAuthorSearch=false&showFullList=false&authorPreferredName=&origin=AuthorNamesList");
//
////            HTMLElement a=page1.getHtmlElementById("resultsBody");
////            a.toString();
            String scopus = "http://www.scopus.com/results/authorNamesList.url?sort=count-f&src=al&sid=BC646E4994C0A013845A83E47A054EA4.fM4vPBipdL1BpirDq5Cw%3a80&sot=al&sdt=al&sl=46&s=AUTH--LAST--NAME%28Varlamis%29+AND+AUTH--FIRST%28I.%29&st1=Varlamis&st2=I.&selectionPageSearch=anl&reselectAuthor=false&activeFlag=false&showDocument=false&resultsPerPage=20&offset=1&jtp=false&currentPage=1&previousSelectionCount=0&tooManySelections=false&previousResultCount=0&authSubject=LFSC&authSubject=HLSC&authSubject=PHSC&authSubject=SOSC&exactAuthorSearch=false&showFullList=false&authorPreferredName=&origin=AuthorNamesList";
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            InputStream inputStream;

            HtmlCleaner cleaner = new HtmlCleaner();
            CleanerProperties props = cleaner.getProperties();
            List<Cookie> cookies;
//            URL mainu = new URL("http://www.scopus.com");
// HttpURLConnection mainConn = (HttpURLConnection) mainu.openConnection();
// mainConn.connect();
//             TagNode a = cleaner.clean(mainConn.getInputStream());

            HttpClient httpClient = new DefaultHttpClient();
            httpClient.getParams().setParameter("http.useragent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.101 Safari/537.36");
            try {
                // Create a local instance of cookie store
                BasicCookieStore cookieStore = new BasicCookieStore();
                // Create local HTTP context
                HttpContext localContext = new BasicHttpContext();
                // Bind custom cookie store to the local context
                localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

                HttpGet httpget = new HttpGet(scopus);

//                System.out.println("executing request " + httpget.getURI());
                // Pass local context as a parameter
                HttpResponse response = httpClient.execute(httpget, localContext);
                HttpEntity entity = response.getEntity();
//                System.out.println("----------------------------------------");
//                System.out.println(response.getStatusLine());
//                if (entity != null) {
//                    System.out.println("Response content length: " + entity.getContentLength());
//                }
                cookies = cookieStore.getCookies();
//                for (int i = 0; i < cookies.size(); i++) {
//                    System.out.println("Local cookie: " + cookies.get(i));
//                }
                // Consume response content
//                EntityUtils.consume(entity);
                String responseTxt = EntityUtils.toString(entity);
                System.out.println("----------------------------------------");
                TagNode node = cleaner.clean(new ByteArrayInputStream(responseTxt.getBytes("UTF-8")));
                Object[] hrefNode = node.evaluateXPath("//div[@id=\"srchResultsList\"]//li[@class=\"dataCol2\"][1]//a[@href]");
//            Object[] hrefNode = node.evaluateXPath("//div");

            for (Object o : hrefNode) {
                TagNode aNode = (TagNode) o;
                String href=aNode.getAttributeByName("href").toString();
                String part=href.substring(href.indexOf("authorId=")+9);
                String id=part.substring(0,part.indexOf("&"));
                System.out.println("AUTHOR ID:"+id);
            }

            } finally {
                // When HttpClient instance is no longer needed,
                // shut down the connection manager to ensure
                // immediate deallocation of all system resources
                httpClient.getConnectionManager().shutdown();
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
