

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

/**
 *
 * @author herc
 */
public class URLFeed {

    NonSyndicatedSource source;
    ArrayList<Article> articles;

    public URLFeed(NonSyndicatedSource source) {
        this.source = source;
        this.articles = new ArrayList<Article>();
    }

    public void fetch(String bodyremoveregex) {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            InputStream inputStream;

            HtmlCleaner cleaner = new HtmlCleaner();
            CleanerProperties props = cleaner.getProperties();
            props.setCharset(source.encoding);
            TagNode node = null;
            if (source.javascript!=null && source.javascript.equals("true")) {
                java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.SEVERE);
                WebClient webClient = new WebClient(BrowserVersion.CHROME);
                webClient.getOptions().setJavaScriptEnabled(false);
                webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
                webClient.getOptions().setUseInsecureSSL(true);
                webClient.getCookieManager().setCookiesEnabled(true);
                webClient.getOptions().setThrowExceptionOnScriptError(false);
                HtmlPage page = webClient.getPage(source.url);
                node = cleaner.clean(page.asXml());
            } else {
                node = cleaner.clean(new URL(source.url));
            }

            Object[] articleNodes = node.evaluateXPath(source.xpathnode);
            for (Object o : articleNodes) {
                TagNode aNode = (TagNode) o;
                String articleLink = (String) aNode.evaluateXPath(source.xpathlink)[0];
                TagNode dateNode;
                String date = "";
                try {
                    dateNode = (TagNode) aNode.evaluateXPath(source.xpathdate)[0];
                    date = dateNode.getText().toString();
                } catch (ArrayIndexOutOfBoundsException ex) {
                }
                TagNode titleNode = (TagNode) aNode.evaluateXPath(source.xpathtitle)[0];
                String title = titleNode.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat(source.dateFormat);
//                Date articleDate=sdf.parse(d);
                articleLink = StaticURLUtilities.fix(articleLink, source.getURL());
                Article f = new Article(title, articleLink, date, sdf);
                this.articles.add(f);
            }
//                this.content = body.getText().toString().replaceAll("\n", " ");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
