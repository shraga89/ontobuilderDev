package ac.technion.iem.ontobuilder.gui.ontobuilder.main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;

import ac.technion.iem.ontobuilder.core.utils.properties.ApplicationOptions;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: OntoBuilderAgent</p>
 * Extends a {@link Thread}
 */
public class OntoBuilderAgent extends Thread
{
    public static final String AGENT_PORT_PROPERTY = "agentPort";
    public static final int AGENT_DEFAULT_PORT = 7070;

    ApplicationOptions options;
    int port;
    ServerSocket serverSocket;
    Boolean shutdown = Boolean.FALSE;
    boolean verbose;

    /**
     * Constructs a OntoBuilderAgent
     * 
     * @param options the {@link ApplicationOptions}
     * @param verbose <code>true </code> if is verbose
     */
    public OntoBuilderAgent(ApplicationOptions options, boolean verbose)
    {
        this.options = options;
        this.verbose = verbose;
        try
        {
            port = Integer.parseInt((String) options
                .getOptionValue(OntoBuilderAgent.AGENT_PORT_PROPERTY));
        }
        catch (NumberFormatException e)
        {
            port = AGENT_DEFAULT_PORT;
        }
    }

    /**
     * Shutdown the application
     */
    synchronized public void shutdown()
    {
        shutdown = Boolean.TRUE;
    }

    /**
     * Runs the thread
     */
    public void run()
    {
        try
        {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            System.out.print(MessageFormat.format(
                ApplicationUtilities.getResourceString("error.agent.create"), new Object[]
                {
                    new Integer(port)
                }));
        }

        synchronized (shutdown)
        {
            while (!shutdown.booleanValue())
            {
                try
                {
                    Socket clientSocket = serverSocket.accept();
                    if (verbose)
                        System.out.println(MessageFormat.format(
                            ApplicationUtilities.getResourceString("agent.clientConnected"),
                            clientSocket.getInetAddress().toString()));
                    OntoBuilderAgentClient client = new OntoBuilderAgentClient(options,
                        clientSocket, verbose);
                    client.start();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        try
        {
            serverSocket.close();
        }
        catch (IOException e)
        {
        }
    }
}
