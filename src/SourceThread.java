

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author herc
 */
public class SourceThread extends Thread {

    public Source source;
    public HashMap<String, Article> cachedArticles;
    public GregorianCalendar startDate;

    public SourceThread(Source source) {
        this.source = source;
        this.cachedArticles = new HashMap<String, Article>();
        this.startDate = new GregorianCalendar();
    }

    public void run() {
        long start = System.currentTimeMillis();
        if (source instanceof NonSyndicatedSource) {
            URLFeed r = new URLFeed((NonSyndicatedSource) source);
            
            r.fetch();
            process(r);
            long end = System.currentTimeMillis();
//            System.out.println("DONE in " + ((end - start) / 1000));
            GregorianCalendar currDate = new GregorianCalendar();
            if (cachedArticles.size() >= NeoProject.minArticles || startDate.get(GregorianCalendar.DAY_OF_MONTH) != currDate.get(GregorianCalendar.DAY_OF_MONTH)
                    || (startDate.get(GregorianCalendar.DAY_OF_MONTH) == currDate.get(GregorianCalendar.DAY_OF_MONTH) && currDate.get(GregorianCalendar.HOUR_OF_DAY)%6==0 && currDate.get(GregorianCalendar.MINUTE)<20)) {
                flush();
            }
        } else if (source instanceof Source) {
            RssFeed r = new RssFeed(source.rssFeed, source.dateFormat);
//        RssFeed r = new RssFeed("sample.xml");
            r.fetch();
            process(r);
            long end = System.currentTimeMillis();
//            System.out.println("DONE in " + ((end - start) / 1000));
            GregorianCalendar currDate = new GregorianCalendar();
            if (cachedArticles.size() >= NeoProject.minArticles || startDate.get(GregorianCalendar.DAY_OF_MONTH) != currDate.get(GregorianCalendar.DAY_OF_MONTH)
                    || (startDate.get(GregorianCalendar.DAY_OF_MONTH) == currDate.get(GregorianCalendar.DAY_OF_MONTH) && currDate.get(GregorianCalendar.HOUR_OF_DAY)%6==0 && currDate.get(GregorianCalendar.MINUTE)<20)) {
                flush();
            }
        }

    }

    public void process(RssFeed r) {
        int newitems = 0;
        for (Article a : r.articles) {
            if (source.fetched.containsKey(a.link)) {
                if (source.fetched.get(a.link).before(a.pubdate)) {
                    System.err.println("Refetch: was:\t" + source.fetched.get(a.link) + "\tis:\t" + a.pubdate);
                    a.fetchBody(source);
                    cachedArticles.put(a.link, a);
                }
            } else {
                a.fetchBody(source);
                source.fetched.put(a.link, a.pubdate);
                cachedArticles.put(a.link, a);
                newitems++;
            }
        }
        System.out.println("Added " + newitems + " new items from " + r.feedURL);
    }

    public void process(URLFeed r) {
        int newitems = 0;
        for (Article a : r.articles) {
            if (source.fetched.containsKey(a.link)) {
                if (source.fetched.get(a.link).before(a.pubdate)) {
                    System.err.println("Refetch: was:\t" + source.fetched.get(a.link) + "\tis:\t" + a.pubdate);
                    a.fetchBody(source);
                    cachedArticles.put(a.link, a);
                }
            } else {
                a.fetchBody(source);
                source.fetched.put(a.link, a.pubdate);
                cachedArticles.put(a.link, a);
                newitems++;
                if (newitems % 10 == 0) {
                    System.out.println(newitems);
                }
            }
        }
        System.out.println("Added " + newitems + " new items from " + r.source.url);
    }

    public void flush() {
        PrintStream ps = null;
        try {
            Date d = new Date();
            String fname = "files/" + this.source.name + "_" + d + ".xml";
            fname = fname.replaceAll(":", "-");
            ps = new PrintStream(new FileOutputStream(new File(fname)), true, this.source.encoding);

            XStream xstream = new XStream(new DomDriver());
            ps.println("<?xml version=\"1.0\" encoding=\"" + this.source.encoding + "\"?>");
            ps.println("<root date=\"" + d + "\">");
            for (Article a : cachedArticles.values()) {
                String xml = xstream.toXML(a);
                ps.println(xml);
            }
            ps.println("</root>");
            ps.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SourceThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SourceThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ps.close();
        }
        source.fetched.clear();
        cachedArticles.clear();
        startDate = new GregorianCalendar();
    }
}
