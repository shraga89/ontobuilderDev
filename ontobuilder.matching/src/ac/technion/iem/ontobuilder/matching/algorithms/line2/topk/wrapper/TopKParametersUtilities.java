package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.wrapper;


/**
 * <p>Title: TopKParametersUtilities</p>
 * <p>Description: Class to parse input parameters</p>
 */
public class TopKParametersUtilities
{

    private String[] params;
    private boolean isValidated = true;
    private String candidateOntologyXMLFilePath;
    private String targetOntologyXMLFilePath;
    private String topKOutputXMLFilePath;
    private String errorString = "";
    private int matchAlgorithm;

    /**
     * Constructs a TopKParametersUtilities
     * 
     * @param parameters the parameters
     * @throws TopKException
     */
    public TopKParametersUtilities(String[] parameters) throws TopKException
    {
        try
        {
            this.params = parameters;
            boolean[] checkFlags = new boolean[4];
            // int coFlagParamIndex = 0;
            // int toFlagParamIndex = 0;
            // int outFlagParamIndex = 0;

            if (params.length != 8)
                isValidated = false;
            if (isValidated)
            {
                for (int i = 0; i < params.length; i++)
                {
                    if (params[i].equalsIgnoreCase("-co"))
                    {
                        checkFlags[0] = true;
                        candidateOntologyXMLFilePath = params[i + 1];
                        // coFlagParamIndex = i;
                        continue;
                    }
                    if (params[i].equalsIgnoreCase("-to"))
                    {
                        checkFlags[1] = true;
                        targetOntologyXMLFilePath = params[i + 1];
                        // toFlagParamIndex = i;
                        continue;
                    }
                    if (params[i].equalsIgnoreCase("-out"))
                    {
                        checkFlags[2] = true;
                        topKOutputXMLFilePath = params[i + 1];
                        // outFlagParamIndex = i;
                        continue;
                    }
                    if (params[i].equalsIgnoreCase("-alg"))
                    {
                        checkFlags[3] = true;
                        matchAlgorithm = Integer.parseInt(params[i + 1]);
                        if (matchAlgorithm < 0 || matchAlgorithm > 5)
                            isValidated = false;
                        // outFlagParamIndex = i;
                        continue;
                    }
                }
                if (!checkFlags[0])
                    isValidated = false;
                if (!checkFlags[1] && isValidated)
                    isValidated = false;
                if (!checkFlags[2] && isValidated)
                    isValidated = false;
                if (!checkFlags[3] && isValidated)
                    isValidated = false;
            }
            if (!isValidated)
                errorString += "\nError in Top K paramaters, syntax:\n"
                    + "TopK -co <candidate Ontology XML file path> -to <target Ontology XML file path> -out <output XML file name> -alg \n 0:= Term match |\n 1:= Value Match |\n 2:= Term and Value match|\n 3:= Combined Match|\n 4:= Precedence Match|\n 5:= Graph Match\n";
        }
        catch (Throwable e)
        {
            isValidated = false;
        }
    }

    public boolean isValidCommandLine()
    {
        return isValidated;
    }

    public String getErrorString()
    {
        return errorString;
    }

    public String getCandidateOntologyXMLFilePath()
    {
        return candidateOntologyXMLFilePath;
    }

    public String getTargetOntologyXMLFilePath()
    {
        return targetOntologyXMLFilePath;
    }

    public String getTopKOutputXMLFilePath()
    {
        return topKOutputXMLFilePath;
    }

    public int getMatchAlgorithm()
    {
        return matchAlgorithm;
    }

}