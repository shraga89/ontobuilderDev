package ac.technion.iem.ontobuilder.gui.match;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.SwingConstants;

import ac.technion.iem.ontobuilder.core.ontology.OntologyUtilities;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.core.utils.properties.PropertiesHandler;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.ontology.OntologyGui;
import ac.technion.iem.ontobuilder.gui.utils.StringUtilitiesGui;
import ac.technion.iem.ontobuilder.gui.utils.graphs.GraphUtilities;
import ac.technion.iem.ontobuilder.matching.match.Match;
import ac.technion.iem.ontobuilder.matching.match.MatchInformation;
import ac.technion.iem.ontobuilder.matching.match.MatchInformationFormatTypesEnum;
import ac.technion.iem.ontobuilder.matching.match.Mismatch;
import ac.technion.iem.ontobuilder.matching.meta.match.MatchedAttributePair;
import ac.technion.iem.ontobuilder.matching.utils.SchemaMatchingsUtilities;
import ac.technion.iem.ontobuilder.matching.utils.SchemaTranslator;

import com.jgraph.JGraph;
import com.jgraph.graph.ConnectionSet;
import com.jgraph.graph.DefaultEdge;
import com.jgraph.graph.DefaultGraphCell;
import com.jgraph.graph.DefaultGraphModel;
import com.jgraph.graph.DefaultPort;

/**
 * <p>
 * Title: MatchInformationGui
 * </p>
 * <p>
 * Description: Implements the methods of the MatchInformation used by the GUI
 * </p>
 */
public class MatchInformationGui
{
    private MatchInformation _matchInformation;

    public MatchInformationGui(MatchInformation matchInformation)
    {
        _matchInformation = matchInformation;
    }

    /**
     * Creates a JTABLE object containing the matches (target term,candidate term effectiveness)
     * 
     * @return {@link JTable}
     */
    public JTable getMatchTable()
    {
        Object data[][] = getTableModelData();
        Object columnNames[] =
        {
            _matchInformation.getTargetOntology().getName(),
            _matchInformation.getCandidateOntology().getName(),
            ApplicationUtilities.getResourceString("ontology.match.effectiveness")
        };
        return new JTable(new MatchTableModel(columnNames, data));
    }

    /**
     * Converts matches to an object array
     * 
     * @return Object[][] - each row holds target term, candidate term, effectiveness
     */
    public Object[][] getTableModelData()
    {
        ArrayList<Match> matches = _matchInformation.getMatches();
        Object data[][] = new Object[matches.size()][3];
        NumberFormat nf = NumberFormat.getInstance();
        for (int i = 0; i < matches.size(); i++)
        {
            Match match = (Match) matches.get(i);
            data[i][0] = match.getTargetTerm();
            data[i][1] = match.getCandidateTerm();
            data[i][2] = (match.getEffectiveness() >= 0 ? nf.format(match.getEffectiveness() * 100) +
                "%" : "n/a");
        }
        return data;
    }

    /**
     * Creates a JTABLE object containing the target mismatches (target terms)
     * 
     * @return {@link JTable}
     */
    public JTable getMismatchTargetOntologyTable()
    {
        ArrayList<Mismatch> mismatchesTargetOntology = _matchInformation
            .getMismatchesTargetOntology();
        Object data[][] = new Object[mismatchesTargetOntology.size()][1];
        Object columnNames[] =
        {
            _matchInformation.getTargetOntology().getName()
        };
        for (int i = 0; i < mismatchesTargetOntology.size(); i++)
        {
            Term term = ((Mismatch) mismatchesTargetOntology.get(i)).getTerm();
            data[i][0] = term;
        }
        return new JTable(new MatchTableModel(columnNames, data));
    }

    /**
     * Creates a JTABLE object containing the candidate mismatches (candidate terms)
     * 
     * @return {@link JTable}
     */
    JTable getMismatchCandidateOntologyTable()
    {
        ArrayList<Mismatch> mismatchesCandidateOntology = _matchInformation
            .getMismatchesCandidateOntology();
        Object data[][] = new Object[mismatchesCandidateOntology.size()][1];
        Object columnNames[] =
        {
            _matchInformation.getCandidateOntology().getName()
        };
        for (int i = 0; i < mismatchesCandidateOntology.size(); i++)
        {
            Term term = ((Mismatch) mismatchesCandidateOntology.get(i)).getTerm();
            data[i][0] = term;
        }
        return new JTable(new MatchTableModel(columnNames, data));
    }

    /**
     * Creates a JTABLE object containing all mismatches
     * 
     * @return {@link JTable}
     */
    public JTable getMismatchTable()
    {
        ArrayList<Mismatch> mismatchesTargetOntology = _matchInformation
            .getMismatchesTargetOntology();
        ArrayList<Mismatch> mismatchesCandidateOntology = _matchInformation
            .getMismatchesCandidateOntology();

        Object data[][] = new Object[Math.max(mismatchesTargetOntology.size(),
            mismatchesCandidateOntology.size())][2];
        Object columnNames[] =
        {
            _matchInformation.getTargetOntology().getName(),
            _matchInformation.getCandidateOntology().getName()
        };
        for (int i = 0; i < mismatchesTargetOntology.size(); i++)
        {
            Term term = ((Mismatch) mismatchesTargetOntology.get(i)).getTerm();
            data[i][0] = term;
        }
        for (int i = 0; i < mismatchesCandidateOntology.size(); i++)
        {
            Term term = ((Mismatch) mismatchesCandidateOntology.get(i)).getTerm();
            data[i][1] = term;
        }
        return new JTable(new MatchTableModel(columnNames, data));
    }

    /**
     * Calculates precision and recall of the match;
     * 
     * @return {@link JTable} holds target,candidate names, algorithm, total count of matches
     * precision and recall
     */
    public JTable getStatistics()
    {
        return getStatistics(null);
    }

    /**
     * Calculates precision and recall of the match;
     * 
     * @return {@link JTable} holds target,candidate names, algorithm, total count of matches
     * precision and recall
     */
    public JTable getStatistics(SchemaTranslator exactMapping)
    {
        double recall = -1;
        double precision = -1;
        if (exactMapping != null)
        {
            recall = _matchInformation.getRecall(exactMapping);
            precision = _matchInformation.getPrecision(exactMapping);
        }
        Object columnNames[] =
        {
            PropertiesHandler.getResourceString("ontology.match.statistics"),
            PropertiesHandler.getResourceString("ontology.match.value")
        };
        NumberFormat nf = NumberFormat.getInstance();
        Object data[][] =
        {
            {
                PropertiesHandler.getResourceString("ontology.match.target"),
                _matchInformation.getTargetOntology().getName()
            },
            {
                PropertiesHandler.getResourceString("ontology.match.candidate"),
                _matchInformation.getCandidateOntology().getName()
            },
            {
                PropertiesHandler.getResourceString("ontology.match.algorithm"),
                _matchInformation.getAlgorithm() != null ? _matchInformation.getAlgorithm()
                    .getName() : _matchInformation.getMetaAlgorithm().getAlgorithmName()
            },
            {
                PropertiesHandler.getResourceString("ontology.match.totalTarget"),
                new Integer(_matchInformation.getTargetOntologyTermsTotal())
            },
            {
                PropertiesHandler.getResourceString("ontology.match.totalCandidate"),
                new Integer(_matchInformation.getCandidateOntologyTermsTotal())
            },
            {
                PropertiesHandler.getResourceString("ontology.match.totalMatches"),
                new Integer(_matchInformation.getMatches().size())
            },
            {
                PropertiesHandler.getResourceString("ontology.match.recall"),
                (recall != -1 ? nf.format(recall * 100) + "%" : "N/A")
            },
            {
                PropertiesHandler.getResourceString("ontology.match.precision"),
                (precision != -1 ? nf.format(precision * 100) + "%" : "N/A")
            },
        // {PropertiesHandler.getResourceString("ontology.match.overall"),nf.format(getOverallMatchConfidence()*100)+"%"}
        };
        return new JTable(new MatchTableModel(columnNames, data));
    }

    /**
     * Create a full report containing information about the MatchInformation object (algorithm,
     * threshold,recall, precision, matches and mismatces)
     * 
     * @return String with the full report
     */
    public String getReport()
    {
        NumberFormat nf = NumberFormat.getInstance();
        StringBuffer sb = new StringBuffer("");
        sb.append(PropertiesHandler.getResourceString("ontology.match.target")).append(": ")
            .append(_matchInformation.getTargetOntology().getName()).append("\n");
        sb.append(PropertiesHandler.getResourceString("ontology.match.candidate")).append(": ")
            .append(_matchInformation.getCandidateOntology().getName()).append("\n");
        sb.append("\n");
        sb.append(PropertiesHandler.getResourceString("ontology.match.algorithm")).append(": ")
            .append(_matchInformation.getAlgorithm().getName()).append("\n");
        sb.append(PropertiesHandler.getResourceString("ontology.match.threshold")).append(": ")
            .append(nf.format(_matchInformation.getAlgorithm().getThreshold()) + "%").append("\n");
        sb.append("\n");
        sb.append(PropertiesHandler.getResourceString("ontology.match.totalTarget")).append(": ")
            .append(_matchInformation.getTargetOntologyTermsTotal()).append("\n");
        sb.append(PropertiesHandler.getResourceString("ontology.match.totalCandidate"))
            .append(": ").append(_matchInformation.getCandidateOntologyTermsTotal()).append("\n");
        sb.append(PropertiesHandler.getResourceString("ontology.match.totalMatches")).append(": ")
            .append(_matchInformation.getMatches().size()).append("\n");
        sb.append(PropertiesHandler.getResourceString("ontology.match.recall"))
            .append(": ")
            .append(
                nf.format(_matchInformation.getMatches().size() /
                    (double) _matchInformation.getTargetOntologyTermsTotal() * 100) +
                    "%").append("\n");
        sb.append("\n");
        sb.append(PropertiesHandler.getResourceString("mergewizard.matchInformation.matches"))
            .append("\n");
        sb.append(StringUtilitiesGui.getJTableStringRepresentation(getMatchTable()));
        sb.append("\n\n");
        sb.append(PropertiesHandler.getResourceString("mergewizard.matchInformation.mismatches"))
            .append("\n");
        sb.append(StringUtilitiesGui.getJTableStringRepresentation(getMismatchTable()));
        sb.append("\n\n");
        sb.append(
            PropertiesHandler.getResourceString("mergewizard.matchInformation.mismatchesTarget"))
            .append("\n");
        sb.append(StringUtilitiesGui.getJTableStringRepresentation(getMismatchTargetOntologyTable()));
        sb.append("\n\n");
        sb.append(
            PropertiesHandler.getResourceString("mergewizard.matchInformation.mismatchesCandidate"))
            .append("\n");
        sb.append(StringUtilitiesGui
            .getJTableStringRepresentation(getMismatchCandidateOntologyTable()));
        return sb.toString();
    }

    /**
     * @return {@link JGraph} with the matches
     */
    public JGraph getGraph()
    {
        return getGraph(null);
    }

    /**
     * @return {@link JGraph} with the matches
     */
    public JGraph getGraph(SchemaTranslator exact)
    {
        SchemaTranslator st = new SchemaTranslator(_matchInformation, true, false);
        // List badMatches = SchemaMatchingsUtilities.diffBestMatches(st, exact);
        List<?> goodMatches = (exact != null ? SchemaMatchingsUtilities.intersectMappings(st,
            exact, _matchInformation.getMatrix()) : new ArrayList<Object>());
        List<?> allMatches = st.getMatches();
        JGraph graph = new JGraph(new DefaultGraphModel());
        graph.setEditable(false);
        ArrayList<DefaultGraphCell> cells = new ArrayList<DefaultGraphCell>();
        // ConnectionSet for the Insert method
        ConnectionSet cs = new ConnectionSet();
        // Hashtable for Attributes (Vertex to Map)
        Hashtable<DefaultGraphCell, Map<?, ?>> attributes = new Hashtable<DefaultGraphCell, Map<?, ?>>();

        OntologyGui targetOntologyGui = new OntologyGui(_matchInformation.getTargetOntology());
        OntologyGui candidateOntologyGui = new OntologyGui(_matchInformation.getCandidateOntology());
        DefaultGraphCell targetCell = targetOntologyGui.addToGraph(cells, attributes, cs);
        DefaultGraphCell candidateCell = candidateOntologyGui.addToGraph(cells, attributes, cs);

        NumberFormat nf = NumberFormat.getInstance();

        // Create match edges
        for (Iterator<?> i = allMatches.iterator(); i.hasNext();)
        {
            MatchedAttributePair match = (MatchedAttributePair) i.next();

            MatchedAttributePair pair;
            Term cand, target;
            cand = _matchInformation.getMatrix().getTermByName(match.getAttribute2());
            target = _matchInformation.getMatrix().getTermByName(match.getAttribute1());
            pair = new MatchedAttributePair(OntologyUtilities.oneIdRemoval(target.toStringVs2()),
                OntologyUtilities.oneIdRemoval(cand.toStringVs2()), match.getMatchedPairWeight());

            String targetTerm = match.getAttribute1();
            String candidateTerm = match.getAttribute2();

            DefaultGraphCell targetTermCell = null, candidateTermCell = null;
            boolean targetSelected = false;
            boolean candidateSelected = false;
            for (Iterator<DefaultGraphCell> j = cells.iterator(); j.hasNext();)
            {
                Object object = j.next();
                if (object instanceof DefaultGraphCell)
                {
                    DefaultGraphCell cell = (DefaultGraphCell) object;
                    Object userObject = cell.getUserObject();
                    if (!targetSelected && userObject != null &&
                        OntologyUtilities.oneIdRemoval(userObject.toString()).equals(targetTerm))
                    {
                        targetTermCell = cell;
                        targetSelected = true;
                    }
                    else if (!candidateSelected && userObject != null &&
                        OntologyUtilities.oneIdRemoval(userObject.toString()).equals(candidateTerm))
                    {
                        candidateTermCell = cell;
                        candidateSelected = true;
                    }
                }

                if (candidateSelected && targetSelected)
                    break;
            }
            if (targetTermCell != null && candidateTermCell != null)
            {
                DefaultPort toCandidatePort = new DefaultPort("toCandidate");
                targetTermCell.add(toCandidatePort);
                String portName;
                if (goodMatches.contains(pair))
                {
                    portName = "toGoodTarget";
                }
                else
                {
                    portName = "toBadTarget";
                }
                DefaultPort toTargetPort = new DefaultPort(portName);
                candidateTermCell.add(toTargetPort);
                DefaultEdge edge = new DefaultEdge(nf.format(match.getMatchedPairWeight()));
                cs.connect(edge, toCandidatePort, true);
                cs.connect(edge, toTargetPort, false);
                cells.add(edge);
            }
        }

        // Insert the cells (View stores attributes)
        graph.getModel().insert(cells.toArray(), cs, null, attributes);
        GraphUtilities.alignHierarchy(graph, targetCell, SwingConstants.LEFT, 10, 10);

        // Calculate the offset for the graph right hierarchy
        int width = PropertiesHandler.getIntProperty("graph.cell.width");
        int interspace = PropertiesHandler.getIntProperty("graph.cell.hierarchyInterspace");
        int leftDepth = GraphUtilities.getDepth(targetCell) + 1;
        int rightDepth = GraphUtilities.getDepth(candidateCell) + 1;
        int off = (width / 2 + 10) * (leftDepth + rightDepth - 2) + width * 2 + interspace;
        GraphUtilities.alignHierarchy(graph, candidateCell, SwingConstants.RIGHT, off - 10, 10);

        GraphUtilities.alignMatches(graph);

        return graph;
    }

    /**
     * @param file - a {@link File} to write to
     */
    public void save(File file) throws IOException
    {
        save(file, MatchInformationFormatTypesEnum.TEXT_FORMAT);
    }

    /**
     * @param file - a {@link File} to write to
     * @param format - 1 to XML, 0 for text
     */
    public void save(File file, MatchInformationFormatTypesEnum format) throws IOException
    {
        switch (format)
        {
        case XML_FORMAT:
            _matchInformation.saveToXML(file);
            break;
        case TEXT_FORMAT:
            saveToText(file);
            break;
        default:
            throw new IOException(
                PropertiesHandler.getResourceString("error.matchInformation.fileFormat"));
        }
    }

    /**
     * Writes a report of the the match information
     * 
     * @param file - a {@link File} to write to
     * @throws IOException
     */
    public void saveToText(File file) throws IOException
    {
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(getReport());
        out.flush();
        out.close();
    }
}
