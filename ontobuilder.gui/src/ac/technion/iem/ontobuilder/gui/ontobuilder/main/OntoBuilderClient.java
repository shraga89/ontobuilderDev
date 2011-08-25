package ac.technion.iem.ontobuilder.gui.ontobuilder.main;

import java.net.*;
import java.io.*;

/**
 * <p>Title: OntoBuilderClient</p>
 */
public class OntoBuilderClient
{
    private static String host;
    private static int port;
    private static String file;

    public static void main(String args[])
    {
        parseCommandLine(args);
        try
        {
            String message = buildMessage(new File(file));
            String response = request(host, port, message);
            System.out.println(response);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Parses the command line
     *
     * @param args the arguments
     */
    public static void parseCommandLine(String args[])
    {
        if (args.length != 3)
        {
            System.out.println("Syntax: OntoBuilderClient <host> <port> <file>");
            System.exit(1);
        }

        host = args[0];
        file = args[2];
        try
        {
            port = Integer.parseInt(args[1]);
        }
        catch (NumberFormatException e)
        {
            e.printStackTrace();
            System.exit(2);
        }
    }

    /**
     * Builds a message from a file
     *
     * @param file the {@link File} to read from
     * @return a string with the message
     * @throws IOException when cannot read the message from the file
     */
    public static String buildMessage(File file) throws IOException
    {
        FileReader in = new FileReader(file);
        StringBuffer buffer = new StringBuffer();
        int c;
        while ((c = in.read()) != -1)
            buffer.append((char) c);
        in.close();
        return buffer.toString();
    }

    /**
     * Executes a request
     * 
     * @param host the host
     * @param port the port number
     * @param message the request message
     * @return the string of the response
     * @throws IOException
     */
    public static String request(String host, int port, String message) throws IOException
    {
        Socket socket = new Socket(host, port);
        socket.setReceiveBufferSize(1024 * 1024);
        InputStream in = socket.getInputStream();
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
            socket.getOutputStream())));
        out.print(message);
        out.println();
        out.println();
        out.flush();
        StringBuffer response = new StringBuffer();
        int c;
        while ((c = in.read()) != -1)
        {
            System.out.print((char) c);
            response.append((char) c);
        }
        out.close();
        in.close();
        socket.close();
        return response.toString();
    }
}
