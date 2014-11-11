

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

/**
 *
 * @author herc
 */
public class Article implements Comparable {

    public String link;
    public String title;
    public Date pubdate;
    public String content;

    public Article(String title, String link, String pubdate, SimpleDateFormat sdf) throws ParseException {
        this.link = link;
        this.title = title;
        GregorianCalendar cal = new GregorianCalendar();
//        cal.setTime(sdf.parse(pubdate));
        try {
            this.pubdate = sdf.parse(pubdate);
            if (sdf.toPattern().indexOf("yy") < 0) {
                this.pubdate.setYear(cal.get(GregorianCalendar.YEAR) - 1900);
            }
        } catch (Exception ex) {
            System.err.println("Error in date format:\t" + sdf.toPattern() + " for date string:\t" + pubdate);
            this.pubdate = new Date();
            this.pubdate.setYear(cal.get(GregorianCalendar.YEAR) - 1900);
            this.pubdate.setMonth(cal.get(GregorianCalendar.MONTH) + 1);
            this.pubdate.setDate(cal.get(GregorianCalendar.DAY_OF_MONTH));
        }

    }

    public void fetchBody(Source source) {
        try {
            HtmlCleaner cleaner = new HtmlCleaner();
            CleanerProperties props = cleaner.getProperties();
            props.setCharset(source.encoding);
//            InputStream aa=new URL(this.link).openStream();
            TagNode node = cleaner.clean(new URL(this.link));
            Object[] bodyNodes = node.evaluateXPath(source.xpathbody);
            int maxLength = 0;
            String body = "";
            for (Object o : bodyNodes) {
                TagNode t = (TagNode) o;
                for (String tagToRemove : source.tagsToRemove) {
                    Object[] othernodes = t.evaluateXPath("/" + tagToRemove);
                    for (Object x : othernodes) {
                        ((TagNode)(x)).removeFromTree();
//                        t.removeChild(x);
                    }
                }
                String html = "<" + t.getName() + ">" + cleaner.getInnerHtml(t) + "</" + t.getName() + ">";
                html = html.replaceAll("<br />", ". ");
                t = cleaner.clean(html);

                String b = t.getText().toString().replaceAll("\n", " ");
                if (b.length() > maxLength) {
                    maxLength = b.length();
                }
                body = b;
            }
            this.content = body;

//             System.out.println(content);
        } catch (XPatherException ex) {
            Logger.getLogger(Article.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Article.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Article.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public int compareTo(Object o) {
        Article a = (Article) o;
        if (a.pubdate.before(pubdate)) {
            return 1;
        } else if (a.pubdate.after(pubdate)) {
            return -1;
        } else {
            return 0;
        }
    }
}
