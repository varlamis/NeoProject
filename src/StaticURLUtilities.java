

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.MalformedURLException;
import java.net.URL;

import org.jsoup.Jsoup;

/**
 *
 * @author varlamis
 */
public class StaticURLUtilities {

    public static String fix(String url, String categoryPage) throws MalformedURLException {
        String domain = getDomain(categoryPage);
        String path = getPath(categoryPage);
        String query = getQuery(categoryPage);
//        System.err.println("*" + domain + "\t" + path + "\t" + query);
        if (!(url.startsWith("http:") || url.startsWith("https:"))) {
            if (url.startsWith("/")) {
                url = domain + url;
            } else if (url.startsWith("?")) {
                int questionMarkIndex = categoryPage.indexOf("?");
                if (questionMarkIndex >= 0) {
                    categoryPage = categoryPage.substring(0, questionMarkIndex);
                    url = categoryPage + url;
                } else {
                    url = categoryPage + url;
                }
            } else if (url.contains(".") && path != null && query != null && path.length() > 1 && path.lastIndexOf("/") >= 0
                    && path.lastIndexOf(".") > 0 && path.lastIndexOf("/") < path.lastIndexOf(".")) {
                String newpath = path.substring(0, path.lastIndexOf("/") + 1);
                url = domain + newpath + url;
            } else if (url.contains(".") && path != null && query != null && path.length() > 1 && path.lastIndexOf("/") < 0
                    && path.lastIndexOf(".") > 0 && path.lastIndexOf("/") < path.lastIndexOf(".")) {
                String newpath = "/";
                url = domain + newpath + url;
            } else if (url.toLowerCase().startsWith("javascript")) {
                url = null;

            } else {
                if (categoryPage.endsWith("/")) {
                    url = categoryPage + url;
                } else {
                    url = categoryPage + "/" + url;
                }
            }
        }
        if (url != null) {
            url = Jsoup.parse(url).text();
        }
        return url;
    }

    public static String getDomain(String categoryPage) throws MalformedURLException {
        URL u = new URL(categoryPage);
        String domain = u.getProtocol() + "://" + u.getHost();
        return domain;
    }

    public static String getHost(String categoryPage) throws MalformedURLException {
        URL u = new URL(categoryPage);
        String domain = u.getHost();
        return domain;
    }

    public static String getPath(String categoryPage) throws MalformedURLException {
        URL u = new URL(categoryPage);
        return u.getPath();
    }

    public static String getQuery(String categoryPage) throws MalformedURLException {
        URL u = new URL(categoryPage);
        return u.getQuery();
    }
}
