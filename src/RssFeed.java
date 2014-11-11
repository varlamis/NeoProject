

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author herc
 */
public class RssFeed {

    String feedURL;
    String dateFormat;
    ArrayList<Article> articles;

    public RssFeed(String feedURL, String dateFormat) {
        this.feedURL = feedURL;
        this.dateFormat = dateFormat;
        this.articles = new ArrayList<Article>();
    }

    public void fetch() {
        try {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true);
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            InputStream inputStream;
            if (feedURL.startsWith("http")) {
                URL url = new URL(feedURL);
                inputStream = url.openStream();
            } else {
                inputStream = new FileInputStream(feedURL);
            }
//            Reader reader = new InputStreamReader(inputStream, "UTF-8");
//            InputSource inputSource = new InputSource(reader);
            Document doc = builder.parse(inputStream);
            XPath xpath = XPathFactory.newInstance().newXPath();
            XPathExpression exprT = xpath.compile("//rss/channel/item/title/text()");
            XPathExpression exprL = xpath.compile("//rss/channel/item/link/text()");
            XPathExpression exprD = xpath.compile("//rss/channel/item/pubDate/text()");
            NodeList nodesT = (NodeList) exprT.evaluate(doc, XPathConstants.NODESET);
            NodeList nodesL = (NodeList) exprL.evaluate(doc, XPathConstants.NODESET);
            NodeList nodesD = (NodeList) exprD.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0; i < nodesT.getLength(); i++) {
                String title = nodesT.item(i).getNodeValue();
                String link = nodesL.item(i).getNodeValue();
                String date = nodesD.item(i).getNodeValue();
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.ROOT);
                Article f = new Article(title, link, date, sdf);
                this.articles.add(f);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }


}
