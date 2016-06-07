

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author herc
 */
public class NonSyndicatedSource extends Source {

    String url;
    String xpathnode;
    String xpathlink;
    String xpathdate;
    String xpathtitle;

    public NonSyndicatedSource(String javascript, String url, String xpathnode, String xpathlink, String xpathdate, String xpathtitle, String name, String rssFeed, String encoding, String xpathbody, String dateFormat, String tagsToRemove, String bodyremoveregex) {
        super(javascript, name, rssFeed, encoding, xpathbody, dateFormat, tagsToRemove, bodyremoveregex);
        this.url = url;
        this.xpathnode = xpathnode;
        this.xpathlink = xpathlink;
        this.xpathdate = xpathdate;
        this.xpathtitle = xpathtitle;
    }
    public String getURL(){
        return url;
    }
}
