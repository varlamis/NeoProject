

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author herc
 */
public class NeoProject {

    public static int refreshInterval = 600;
    public static int minArticles = 40;
    public static ArrayList<SourceThread> threads;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AddShutdownHookSample sample = new AddShutdownHookSample();
        sample.attachShutDownHook();

        ArrayList<Source> sources = new ArrayList<Source>();
        threads = new ArrayList<SourceThread>();
        if (args[0] != null) {
            refreshInterval = new Integer(args[0]).intValue();
        }
        if (args[1] != null) {
            minArticles = new Integer(args[1]).intValue();
        }
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

                String rssf = prop.getProperty("rss");
                String encoding = prop.getProperty("charset");
                String name = prop.getProperty("name");
                String xpathbody = prop.getProperty("xpathbody");
                String dateFormat = prop.getProperty("dateFormat");
                String tagsToRemove = prop.getProperty("tagsToRemove");
                if (rssf != null) {
                    Source s = new Source(name, rssf, encoding, xpathbody, dateFormat, tagsToRemove);
                    sources.add(s);
                } else {
                    String url = prop.getProperty("url");
                    String xpathnode = prop.getProperty("xpathnode");
                    String xpathlink = prop.getProperty("xpathlink");
                    String xpathdate = prop.getProperty("xpathdate");
                    String xpathtitle = prop.getProperty("xpathtitle");
                    Source s = new NonSyndicatedSource(url, xpathnode, xpathlink, xpathdate, xpathtitle, name, rssf, encoding, xpathbody, dateFormat, tagsToRemove);
                    sources.add(s);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        for (Source s : sources) {
            SourceThread st = new SourceThread(s);
            threads.add(st);
        }
        while (true) {
            for (SourceThread st : threads) {
                try {
                    st.run();
                } catch (Exception ex) {
                    System.err.println("Skipping:\t" + st.source.name + ":\t" + ex.getMessage());
                }
            }
            try {
                Thread.sleep(NeoProject.refreshInterval * 1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(NeoProject.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }
}
