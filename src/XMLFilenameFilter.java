


import java.io.File;
import java.io.FilenameFilter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author herc
 */
public class XMLFilenameFilter implements FilenameFilter {
    String sourcename;
    public XMLFilenameFilter (String sourcename){
        this.sourcename=sourcename;
    }
    public boolean accept(File dir, String name) {
        return (name.toLowerCase().endsWith(".xml") && name.toLowerCase().startsWith(sourcename.toLowerCase()));
    }
}
