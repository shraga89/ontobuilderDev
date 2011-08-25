package ac.technion.iem.ontobuilder.extraction.webform.utils.files.html;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import ac.technion.iem.ontobuilder.core.utils.graphs.GraphCell;

/*
 * Fix: default FROM::method to be GET
 */
/**
 * <p>Title: FORMElement</p>
 * Extends {@link HTMLElement}
 */
public class FORMElement extends HTMLElement
{

    static final Character EQUALS = new Character('=');

    static final String POST = "post";
    static final String GET = "get";

    private URL action;
    private String method;
    private String name;
    private ArrayList<INPUTElement> inputs;

    private String lastQueryString = "";
    private int lastRes = -1;

    public FORMElement()
    {
        super(HTMLElement.FORM);
        inputs = new ArrayList<INPUTElement>();
    }

    public FORMElement(String name, URL action, String method)
    {
        super(HTMLElement.FORM);
        this.name = name;
        this.action = action;
        this.method = method;
        inputs = new ArrayList<INPUTElement>();
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setMethod(String methods)
    {
        this.method = methods;
    }

    public String getMethod()
    {
        return method;
    }

    public void setAction(URL action)
    {
        this.action = action;
    }

    public URL getAction()
    {
        return action;
    }

    public int getInputsCount()
    {
        return inputs.size();
    }

    public String toString()
    {
        return name;
    }

    public void setInputs(ArrayList<INPUTElement> inputs)
    {
        this.inputs = inputs;
        for (Iterator<INPUTElement> i = this.inputs.iterator(); i.hasNext();)
            ((INPUTElement) i.next()).setForm(this);
    }

    public INPUTElement getInput(int index)
    {
        if (index < 0 || index >= inputs.size())
            return null;
        return (INPUTElement) inputs.get(index);
    }

    public void addInput(INPUTElement input)
    {
        if (input == null)
            return;
        input.setForm(this);
        inputs.add(input);
    }

    public void flat()
    {
        flatRadios();
        flatCheckboxes();
    }

    private void flatRadios()
    {
        HashMap<String, RadioINPUTElement> hm = new HashMap<String, RadioINPUTElement>();
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElement input = (INPUTElement) i.next();
            if (input.getInputType().equals(INPUTElement.RADIO))
            {
                RadioINPUTElement radioInput = (RadioINPUTElement) input;
                String name = radioInput.getName();
                if (hm.containsKey(name))
                {
                    RadioINPUTElement radio = (RadioINPUTElement) hm.get(input.getName());
                    for (int j = 0; j < radioInput.getOptionsCount(); j++)
                        radio.addOption(radioInput.getOption(j));
                    i.remove();
                }
                else
                    hm.put(name, radioInput);
            }
        }
    }

    private void flatCheckboxes()
    {
        HashMap<String, CheckboxINPUTElement> hm = new HashMap<String, CheckboxINPUTElement>();
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElement input = (INPUTElement) i.next();
            if (input.getInputType().equals(INPUTElement.CHECKBOX))
            {
                CheckboxINPUTElement checkboxInput = (CheckboxINPUTElement) input;
                String name = checkboxInput.getName();
                if (hm.containsKey(name))
                {
                    CheckboxINPUTElement checkbox = (CheckboxINPUTElement) hm.get(input.getName());
                    for (int j = 0; j < checkboxInput.getOptionsCount(); j++)
                        checkbox.addOption(checkboxInput.getOption(j));
                    i.remove();
                }
                else
                    hm.put(name, checkboxInput);
            }
        }
    }

    public InputStream submit() throws IOException
    {
        return submit(new StringBuffer());
    }

    public InputStream submit(StringBuffer info) throws IOException
    {
        // Build the query string
        info.append("Query string:\n");
        StringBuffer queryString = new StringBuffer();
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElement input = (INPUTElement) i.next();
            if (input.canSubmit())
            {
                String param = input.paramString();
                queryString.append(param).append("&");
                info.append("\t- " + param + "\n");
            }
        }
        String queryStringText = queryString.toString();
        // Remove last &
        if (queryStringText.length() > 0)
            queryStringText = queryStringText.substring(0, queryStringText.length() - 1);
        URL url = action;

        // fix for default GET (HTTP specifications) .
        // amir 08/2004
        if (method.length() == 0)
            method = GET;

        if (method.equalsIgnoreCase(GET))
        {
            try
            {

                // added by haggai 5/1/04
                lastQueryString = url.getFile() + "?" + queryStringText;
                //

                url = new URL(url, url.getFile() + "?" + queryStringText);
            }
            catch (MalformedURLException e)
            {
                return null;
            }
        }
        try
        {
            HttpURLConnection.setFollowRedirects(true);// was false
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("user-agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0; Q312461)");
            // those headers were commented out
            // connection.setRequestProperty("connection","Keep-Alive");
            // connection.setRequestProperty("accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
            // connection.setRequestProperty("accept-language","en-us");
            // connection.setRequestProperty("host","OntoBuilder");

            // Get information about headers
            info.append("Headers:\n");
            Map<?, ?> headers = connection.getRequestProperties();
            Set<?> headersSet = headers.keySet();
            for (Iterator<?> i = headersSet.iterator(); i.hasNext();)
            {
                String key = (String) i.next();
                info.append("\t- " + key + "=" + headers.get(key) + "\n");
            }

            if (method.equalsIgnoreCase(POST))
            {
                connection.setDoOutput(true);
                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(queryStringText);
                out.close();
            }

            // Handle server redirections
            /*
             * just printouts to check the connection print("URL is "+ url.getProtocol()
             * +";"+url.getHost()+";"+url.getPort()+";"+url.getFile()); print("Properties = " +
             * connection.getRequestProperties().toString()); print("Headers = " +
             * connection.getHeaderFields().toString()); print("Length = " +
             * connection.getContentLength()); print("Use Caches = " +
             * connection.getDefaultUseCaches()); print("Expiration = " +
             * connection.getExpiration()); print("Type = " + connection.getContentType());
             * print("Encoding =  " + connection.getContentEncoding()); // print("Content = " +
             * connection.getContent());
             */

            // Handle server redirections
            if (connection instanceof HttpURLConnection)
            {
                URL redirectURL = null;
                int res = ((HttpURLConnection) connection).getResponseCode();
                lastRes = res;

                while (res > 299 && res < 400)
                {
                    String location = connection.getHeaderField("Location");
                    try
                    {
                        redirectURL = new URL(URLDecoder.decode(location, "UTF-8"));
                    }
                    catch (MalformedURLException e)
                    {
                        break;
                    }
                    connection = redirectURL.openConnection();
                    res = ((HttpURLConnection) connection).getResponseCode();
                    lastRes = res;

                }
            }

            return connection.getInputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public int getLastRes()
    {
        return lastRes;
    }

    public String getLastQueryString()
    {
        return lastQueryString;
    }

    public void reset()
    {
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
            ((INPUTElement) i.next()).reset();
    }

    public void clearPressed()
    {
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElement input = (INPUTElement) i.next();
            if (input.getInputType().equals(INPUTElement.IMAGE))
                ((ImageINPUTElement) input).clearPressed();
            else if (input.getInputType().equals(INPUTElement.SUBMIT))
                ((SubmitINPUTElement) input).clearPressed();
        }
    }
    
    public GraphCell getTreeBranch() 
    {
    	GraphCell node = new GraphCell(this);
        for (Iterator<INPUTElement> i = inputs.iterator(); i.hasNext();)
            node.addChild(((INPUTElement) i.next()).getTreeBranch());
        return node;
    }
    

}