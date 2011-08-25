package ac.technion.iem.ontobuilder.gui.ontology;

import java.util.ArrayList;
import java.util.Locale;

import javax.swing.JTable;

import ac.technion.iem.ontobuilder.core.ontology.domain.DomainSimilarity;
import ac.technion.iem.ontobuilder.core.ontology.domain.DomainSimilarityEntry;
import ac.technion.iem.ontobuilder.core.resources.OntoBuilderResources;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.utils.StringUtilitiesGui;

/**
 * <p>
 * Title: DomainGui
 * </p>
  * <p>Description: Implements the methods of the Domain used by the GUI</p>
 */
public class DomainSimilarityGui
{
    private static DomainSimilarity _domainSimilarity;
    
    public DomainSimilarityGui(DomainSimilarity domainSimilarity)
    {
        _domainSimilarity = domainSimilarity;
    }
    
    /**
     * Get a Domain Similarity Table
     *
     * @return a {@link JTable}
     */
    public static JTable getDomainSimilarityTable()
    {
        ArrayList<DomainSimilarityEntry> domainMatrix = _domainSimilarity.getDomainMatrix();
        
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("domain.1"),
            ApplicationUtilities.getResourceString("domain.2"),
            ApplicationUtilities.getResourceString("domain.similarity")
        };
        Object domainTable[][] = new Object[domainMatrix.size()][3];

        for (int index = 0; index < domainMatrix.size(); index++)
        {
            DomainSimilarityEntry dse = (DomainSimilarityEntry) domainMatrix.get(index);
            domainTable[index][0] = dse.getDomain1();
            domainTable[index][1] = dse.getDomain2();
            domainTable[index][2] = new Double(dse.getValue());
        }

        return new JTable(new PropertiesTableModel(columnNames, domainMatrix.size(), domainTable));
    }
    
    public static void main(String args[])
    {
        // Initialize the properties
        try
        {
            PropertiesHandler.initializeProperties(OntoBuilderResources.Config.APPLICATION_PROPERTIES);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        // Initialize the resource bundle
        try
        {
            PropertiesHandler.initializeResources("resources.resources", Locale.getDefault());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }

        DomainSimilarity.buildDomainMatrix(PropertiesHandler.getStringProperty("domain.domainMatrix"));
        System.out.println(StringUtilitiesGui
            .getJTableStringRepresentation(getDomainSimilarityTable()));
        System.out.println(args[0] + " <-> " + args[1] + " = " +
            DomainSimilarity.getDomainSimilarity(args[0], args[1]));
    }

}
