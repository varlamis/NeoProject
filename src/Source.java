

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Date;
import java.util.HashMap;

/**
 *
 * @author herc
 */
public class Source {
    public String javascript;
    public String name;
    public String encoding;
    public String rssFeed;
    public String xpathbody;
    public String dateFormat;
    public String[] tagsToRemove;
    public HashMap<String, Date> fetched;
    public String bodyremoveregex;

    public Source(String javascript,String name, String rssFeed, String encoding, String xpathbody, String dateFormat, String tagsToRemove, String bodyremoveregex) {
        this.javascript = javascript;
        this.name = name;
        this.encoding = encoding;
        this.rssFeed = rssFeed;
        this.xpathbody = xpathbody;
        this.dateFormat = dateFormat;
        this.fetched = new HashMap<String, Date>();
        this.tagsToRemove = tagsToRemove.split(",");
        this.bodyremoveregex = bodyremoveregex;
    }

    public String getURL() {
        return rssFeed;
    }
}
