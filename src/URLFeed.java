

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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

    public void fetch() {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            InputStream inputStream;

            HtmlCleaner cleaner = new HtmlCleaner();
            CleanerProperties props = cleaner.getProperties();
            props.setCharset(source.encoding);
//            InputStream aa=new URL(this.link).openStream();
            TagNode node = cleaner.clean(new URL(source.url));
            Object[] articleNodes = node.evaluateXPath(source.xpathnode);
            for (Object o : articleNodes) {
                TagNode aNode = (TagNode) o;
                String articleLink = (String) aNode.evaluateXPath(source.xpathlink)[0];
                TagNode dateNode = (TagNode) aNode.evaluateXPath(source.xpathdate)[0];
                TagNode titleNode = (TagNode) aNode.evaluateXPath(source.xpathtitle)[0];
                String date = dateNode.getText().toString();
                String title = titleNode.getText().toString();
                SimpleDateFormat sdf = new SimpleDateFormat(source.dateFormat);
//                Date articleDate=sdf.parse(d);
                articleLink= StaticURLUtilities.fix(articleLink, source.getURL());
                Article f = new Article(title, articleLink, date, sdf);
                this.articles.add(f);
            }
//                this.content = body.getText().toString().replaceAll("\n", " ");

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
