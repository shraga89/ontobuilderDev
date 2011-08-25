package ac.technion.iem.ontobuilder.io.utils.xml.wsdl;

import java.util.Iterator;
import java.util.Vector;

import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Term;

/**
 * <p>Title: EEE05Challenge</p>
 */
public class EEE05Challenge
{

    public static final byte DISCOVERY = 1;
    public static final byte COMPOSITION = 2;

    private Vector<?> provided;
    private Vector<?> resultant;
    private String discoveredInputWSDL;
    private String discoveredOutputWSDL;
    private Ontology inputOntology;
    private Ontology outputOntology;
    private double inputDiscoverScore;
    private double outputDiscoverScore;

    public EEE05Challenge(byte type, Vector<?> provided, Vector<?> resultant)
    {
        this.provided = provided;
        this.resultant = resultant;
        generateOntologies();
    }

    private void generateOntologies()
    {
        inputOntology = new Ontology();
        inputOntology.setName("challenge");
        inputOntology.setTitle("challenge");
        outputOntology = new Ontology();
        outputOntology.setName("challenge");
        outputOntology.setTitle("challenge");
        Iterator<?> itr = provided.iterator();
        while (itr.hasNext())
        {
            inputOntology.addTerm(new Term((String) itr.next()));
        }
        itr = resultant.iterator();
        while (itr.hasNext())
        {
            outputOntology.addTerm(new Term((String) itr.next()));
        }
    }

    /**
     * @return Returns the discoveredInputWSDL.
     */
    public String getDiscoveredInputWSDL()
    {
        return discoveredInputWSDL;
    }

    /**
     * @param discoveredInputWSDL The discoveredInputWSDL to set.
     */
    public void setDiscoveredInputWSDL(String discoveredWSDL)
    {
        this.discoveredInputWSDL = discoveredWSDL;
    }

    /**
     * @return Returns the inputOntology.
     */
    public Ontology getInputOntology()
    {
        return inputOntology;
    }

    /**
     * @param inputOntology The inputOntology to set.
     */
    public void setInputOntology(Ontology inputOntology)
    {
        this.inputOntology = inputOntology;
    }

    /**
     * @return Returns the outputOntology.
     */
    public Ontology getOutputOntology()
    {
        return outputOntology;
    }

    /**
     * @param outputOntology The outputOntology to set.
     */
    public void setOutputOntology(Ontology outputOntology)
    {
        this.outputOntology = outputOntology;
    }

    /**
     * @return Returns the provided.
     */
    public Vector<?> getProvided()
    {
        return provided;
    }

    /**
     * @param provided The provided to set.
     */
    public void setProvided(Vector<?> provided)
    {
        this.provided = provided;
    }

    /**
     * @return Returns the resultant.
     */
    public Vector<?> getResultant()
    {
        return resultant;
    }

    /**
     * @param resultant The resultant to set.
     */
    public void setResultant(Vector<?> resultant)
    {
        this.resultant = resultant;
    }

    /**
     * @return Returns the inputDiscoverScore.
     */
    public double getInputDiscoverScore()
    {
        return inputDiscoverScore;
    }

    /**
     * @param inputDiscoverScore The inputDiscoverScore to set.
     */
    public void setInputDiscoverScore(double inputDiscoverScore)
    {
        this.inputDiscoverScore = inputDiscoverScore;
    }

    /**
     * @return Returns the outputDiscoverScore.
     */
    public double getOutputDiscoverScore()
    {
        return outputDiscoverScore;
    }

    /**
     * @param outputDiscoverScore The outputDiscoverScore to set.
     */
    public void setOutputDiscoverScore(double outputDiscoverScore)
    {
        this.outputDiscoverScore = outputDiscoverScore;
    }

    /**
     * @return Returns the discoveredOutputWSDL.
     */
    public String getDiscoveredOutputWSDL()
    {
        return discoveredOutputWSDL;
    }

    /**
     * @param discoveredOutputWSDL The discoveredOutputWSDL to set.
     */
    public void setDiscoveredOutputWSDL(String discoveredOutputWSDL)
    {
        this.discoveredOutputWSDL = discoveredOutputWSDL;
    }
}
