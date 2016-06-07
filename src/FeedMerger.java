
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author herc
 */
public class FeedMerger {

    public static String infolder = "files/01-january-2014-office/01-january-2014-office";
    public static String outfolder = "mergedFiles/01-january-2014-office";

    public static void main(String[] args) {

        if (args.length >= 2) {
            infolder = args[0];
            outfolder = args[1];
        }
        ArrayList<Source> sources = new ArrayList<Source>();
        File dir = new File("conf");
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".properties");
            }
        });
        for (File f : files) {
            Properties prop = new Properties();
            try {
                //load a properties file
                prop.load(new FileInputStream(f));
                String javascript = prop.getProperty("javascript", "false");
                String rssf = prop.getProperty("rss");
                String encoding = prop.getProperty("charset");
                String name = prop.getProperty("name");
                String xpathbody = prop.getProperty("xpathbody");
                String dateFormat = prop.getProperty("dateFormat");
                String tagsToRemove = prop.getProperty("tagsToRemove");
                String bodyremoveregex = prop.getProperty("bodyremoveregex");
                
                if (rssf != null) {
                    Source s = new Source(javascript, name, rssf, encoding, xpathbody, dateFormat, tagsToRemove,bodyremoveregex);
                    sources.add(s);
                } else {
                    String url = prop.getProperty("url");
                    String xpathnode = prop.getProperty("xpathnode");
                    String xpathlink = prop.getProperty("xpathlink");
                    String xpathdate = prop.getProperty("xpathdate");
                    String xpathtitle = prop.getProperty("xpathtitle");
                    Source s = new NonSyndicatedSource(javascript, url, xpathnode, xpathlink, xpathdate, xpathtitle, name, rssf, encoding, xpathbody, dateFormat, tagsToRemove,bodyremoveregex);
                    sources.add(s);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        for (Source s : sources) {
            PrintStream ps = null;
            ObjectInputStream in = null;
            try {
                Date d = new Date();
                String fname = outfolder + "/" + s.name + "_" + (d.getYear() + 1900) + "-" + (d.getMonth() + 1) + "-" + d.getDate() + ".xml";
                ps = new PrintStream(new FileOutputStream(new File(fname)), true, s.encoding);
                ps.println("<?xml version=\"1.0\" encoding=\"" + s.encoding + "\"?>");
                ps.println("<root date=\"" + d + "\">");
                XStream xstream = new XStream(new DomDriver());
                XStream instream = new XStream(new DomDriver());
                File xmlFilesFolder = new File(infolder);
                File[] xmlfiles = xmlFilesFolder.listFiles(new XMLFilenameFilter(s.name));
                HashMap<String, Article> allArticles = new HashMap<String, Article>();
                Object o = null;
                for (File f : xmlfiles) {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), s.encoding));
                        String line = br.readLine();
                        s.encoding = line.substring(line.indexOf("encoding=") + 10);
                        s.encoding = s.encoding.substring(0, s.encoding.indexOf("\""));
                        br.close();
                        in = instream.createObjectInputStream(new InputStreamReader(new FileInputStream(f), s.encoding));
                        o = in.readObject();

                        while (o != null) {
                            Article a = (Article) o;
                            if (allArticles.containsKey(a.link)) {
                                if (allArticles.get(a.link).pubdate.before(a.pubdate)) {
                                    allArticles.put(a.link, a);
                                }
                            } else {
                                allArticles.put(a.link, a);
                            }
                            try {
                                o = in.readObject();
                            } catch (java.io.EOFException eofex) {
                                o = null;
                            }
                        }
                        in.close();
                    } catch (com.thoughtworks.xstream.mapper.CannotResolveClassException ex) {
                        System.out.println("CannotResolveClassException");
                    } catch (EOFException ex) {
                        System.out.println("Reached EOF");
                    } catch (com.thoughtworks.xstream.io.StreamException ex) {
                        System.out.println("An invalid XML character was found in the element content of the document");
                    }
                }
                ArrayList<Article> all = new ArrayList<Article>();
                all.addAll(allArticles.values());
                Collections.sort(all);
                for (Article a : all) {
                    String xml = xstream.toXML(a);
                    ps.println(xml);
                }
                ps.println("</root>");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(SourceThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(FeedMerger.class.getName()).log(Level.SEVERE, null, ex);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SourceThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FeedMerger.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                ps.close();
            }
        }
    }
}