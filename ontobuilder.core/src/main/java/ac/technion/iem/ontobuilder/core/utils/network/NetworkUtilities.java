package ac.technion.iem.ontobuilder.core.utils.network;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * <p>Title: NetworkUtilities</p>
 */
public class NetworkUtilities
{
    public final static String DEFAULT_URL_PROTOCOL = "http";

    public static boolean isSameDomain(URL url, String domain)
    {
        return (url.getHost().endsWith(domain));
    }

    public static String getURLDomain(URL url)
    {
        if (url == null)
            return null;
        String domain = url.getHost();
        int index = domain.lastIndexOf(".");
        index = domain.lastIndexOf(".", index - 1);
        if (index < 0)
            return new String("");
        domain = domain.substring(index + 1);
        return domain;
    }

    public static String getURLProtocol(String url)
    {
        int index = url.indexOf(":");
        if (index > 0)
            return url.substring(0, index);
        else
            return null;
    }

    public static URL makeURL(String url) throws MalformedURLException
    {
        if (url == null || url.trim().length() == 0)
            return null;
        if (getURLProtocol(url) == null)
            url = DEFAULT_URL_PROTOCOL + "://" + url;
        return new URL(url);
    }

    public static void initializeHTTPSProtocol()
    {
        // Register security provider to handle https protocols
        System.setProperty("java.protocol.handler.pkgs", "com.sun.net.ssl.internal.www.protocol");
        // Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
    }

    public static String getContentType(URL url)
    {
        if (url == null)
            return null;
        try
        {
            return url.openConnection().getContentType();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    public static URL getAbsoluteURL(String url, URL document, URL base)
        throws MalformedURLException
    {

        if (base != null)
            return new URL(base, url);
        else
            return new URL(document, url);
    }

    public static void setProxy(String proxyServer, String proxyServerPort)
    {
        System.setProperty(NetworkUtilitiesPropertiesEnum.PROXY_SET_PROPERTY.getValue(), "true");
        System.setProperty(NetworkUtilitiesPropertiesEnum.PROXY_HOST_PROPERTY.getValue(), proxyServer);
        System.setProperty(NetworkUtilitiesPropertiesEnum.PROXY_PORT_PROPERTY.getValue(), proxyServerPort);
    }

    public static void disableProxy()
    {
        System.setProperty(NetworkUtilitiesPropertiesEnum.PROXY_SET_PROPERTY.getValue(), "false");
    }

    public static void setConnectionTimeout(long millis)
    {
        System.setProperty("sun.net.client.defaultConnectTimeout", "" + millis);
    }

    public static long getConnectionTimeout()
    {
        String millis = System.getProperty("sun.net.client.defaultConnectTimeout");
        try
        {
            long longMillis = Long.parseLong(millis);
            return longMillis;
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            return -1;
        }
    }

    public static void resetConnectionTimeout()
    {
        setConnectionTimeout(-1);
    }
}