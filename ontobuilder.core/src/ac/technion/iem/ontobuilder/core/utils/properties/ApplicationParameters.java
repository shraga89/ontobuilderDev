package ac.technion.iem.ontobuilder.core.utils.properties;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.text.ParseException;

/**
 * <p>Title: ApplicationParameters</p>
 */
public class ApplicationParameters
{
    public static final String PROPERTIES_FILE = "/application.properties";
    
    public static boolean verbose = false;
    public static boolean result = false;

    public static URL url = null;
    public static boolean generate = false;

    public static boolean match = false;
    public static URL targetURL = null;
    public static URL candidateURL = null;

    public static String outputFile = null;
    public static boolean normalize = false;

    public static boolean agent = false;

    public static String messages[] =
    {
        "Syntax error: ''{0}'' flag specified more than once",
        "Syntax error: The URL ''{0}'' is not correct",
        "Syntax error: The 'agent' and 'url' and 'match' are exclusive flags",
        "Syntax error: The 'generate' flag requires the 'url' and 'output' parameters to be specified",
        "Syntax error: The 'url' parameter requires the 'generate' flag to be specified",
        "Syntax error: The 'match' flag requires the 'targetURL', 'candidateURL', and 'output' parameters to be specified",
        "Syntax error: The 'targetURL' or 'candidateURL' parameters require the 'match' flag to be specified",
        "Syntax error: The 'output' parameter requires either the 'generate' or 'match' flags to be specified",
        "Syntax error: The 'normalize' flag requires either the 'generate' or 'match' flags to be specified",
        "Syntax error: The 'result' and 'verbose' flag cannot be specified with the 'generate' or 'match' flag",
        "Syntax error: No 'agent' flag must be specified alone"
    };

    /**
     * Parses the command line
     *
     * @param args the arguments
     * @throws ParseException
     */
    public static void parseCommandLine(String args[]) throws ParseException
    {
        boolean verboseFlag = false;
        boolean resultFlag = false;

        boolean agentFlag = false;

        boolean generateFlag = false;
        boolean urlFlag = false;

        boolean matchFlag = false;
        boolean targetURLFlag = false;
        boolean candidateURLFlag = false;

        boolean outputFlag = false;
        boolean normalizeFlag = false;

        for (int i = 0; i < args.length; i++)
        {
            String parameter = args[i];

            if (parameter.equalsIgnoreCase("-verbose") || parameter.equalsIgnoreCase("-v"))
            {
                if (verboseFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "verbose"), i);
                verbose = true;
                result = true;
                verboseFlag = true;
            }
            else if (parameter.equalsIgnoreCase("-result") || parameter.equalsIgnoreCase("-r"))
            {
                if (resultFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "result"), i);
                result = true;
                resultFlag = true;
            }
            else if (parameter.equalsIgnoreCase("-agent") || parameter.equalsIgnoreCase("-a"))
            {
                if (agentFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "agent"), i);
                agent = true;
                agentFlag = true;
            }
            else if (parameter.equalsIgnoreCase("-generate") || parameter.equalsIgnoreCase("-g"))
            {
                if (generateFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "generate"), i);
                generate = true;
                generateFlag = true;
            }
            else if (parameter.equalsIgnoreCase("-url"))
            {
                if (urlFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "url"), i);
                String strURL = args[++i];
                try
                {
                    url = new URL(strURL);
                }
                catch (MalformedURLException e)
                {
                    throw new ParseException(MessageFormat.format(messages[1], strURL), i);
                }
                urlFlag = true;
            }
            else if (parameter.equalsIgnoreCase("-match") || parameter.equalsIgnoreCase("-m"))
            {
                if (matchFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "match"), i);
                match = true;
                matchFlag = true;
            }
            else if (parameter.equalsIgnoreCase("-targetURL"))
            {
                if (targetURLFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "targetURL"), i);
                String strURL = args[++i];
                try
                {
                    targetURL = new URL(strURL);
                }
                catch (MalformedURLException e)
                {
                    throw new ParseException(MessageFormat.format(messages[1], strURL), i);
                }
                targetURLFlag = true;
            }
            else if (parameter.equalsIgnoreCase("-candidateURL"))
            {
                if (candidateURLFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "candidateURL"), i);
                String strURL = args[++i];
                try
                {
                    candidateURL = new URL(strURL);
                }
                catch (MalformedURLException e)
                {
                    throw new ParseException(MessageFormat.format(messages[1], strURL), i);
                }
                candidateURLFlag = true;
            }
            else if (parameter.equalsIgnoreCase("-output") || parameter.equalsIgnoreCase("-o"))
            {
                if (outputFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "output"), i);
                outputFile = args[++i];
                outputFlag = true;
            }
            else if (parameter.equalsIgnoreCase("-normalize") || parameter.equalsIgnoreCase("-n"))
            {
                if (normalizeFlag)
                    throw new ParseException(MessageFormat.format(messages[0], "normalize"), i);
                normalize = true;
                normalizeFlag = true;
            }
        }

        if ((agentFlag && (generateFlag || matchFlag)) ||
            (generateFlag && (agentFlag || matchFlag)) ||
            (matchFlag && (agentFlag || generateFlag)))
            throw new ParseException(messages[2], -1);
        if (generateFlag && (url == null || !outputFlag))
            throw new ParseException(messages[3], -1);
        if (url != null && !generateFlag)
            throw new ParseException(messages[4], -1);
        if (matchFlag && (targetURL == null || candidateURL == null || !outputFlag))
            throw new ParseException(messages[5], -1);
        if ((targetURL != null || candidateURL != null) && !matchFlag)
            throw new ParseException(messages[6], -1);
        if (outputFlag && !matchFlag && !generateFlag)
            throw new ParseException(messages[7], -1);
        if (normalizeFlag && !matchFlag && !generateFlag)
            throw new ParseException(messages[8], -1);
        if ((generateFlag || matchFlag) && (resultFlag || verboseFlag))
            throw new ParseException(messages[9], -1);
        if (agentFlag &&
            (verboseFlag || resultFlag || normalizeFlag || urlFlag || targetURLFlag ||
                candidateURLFlag || normalizeFlag || outputFlag))
            throw new ParseException(messages[10], -1);

    }

    /**
     * Get if an interface exists (there's no agent, no generate and no match) 
     *
     * @return <code>true</code> if there is an interface
     */
    public static boolean hasInterface()
    {
        return !agent && !generate && !match;
    }

    /**
     * Prints the configuration
     */
    public static void showConfiguration()
    {
        System.out.println("Configuration:");
        System.out.println("- result: " + result);
        System.out.println("- verbose: " + verbose);
        System.out.println();
        System.out.println("- agent: " + agent);
        System.out.println();
        System.out.println("- generate: " + generate);
        System.out.println("- url: " + url);
        System.out.println();
        System.out.println("- match: " + match);
        System.out.println("- targetURL: " + targetURL);
        System.out.println("- candidateURL: " + candidateURL);
        System.out.println("- normalize: " + normalize);
        System.out.println();
        System.out.println("- output: " + outputFile);
    }

    /**
     * Prints the correct syntax
     */
    public static void showSyntax()
    {
        System.out.println("Syntax: OntoBuilder [-v|-verbose] [-r|-result]");
        System.out.println("Syntax: OntoBuilder -a|-agent");
        System.out
            .println("Syntax: OntoBuilder -g|-generate -url <URL> -o|-output <file> [-n|-normalize]");
        System.out
            .println("Syntax: OntoBuilder -m|-match -targetURL <URL> -candidateURL <URL> -o|-output <file> [-n|-normalize]");
    }
}