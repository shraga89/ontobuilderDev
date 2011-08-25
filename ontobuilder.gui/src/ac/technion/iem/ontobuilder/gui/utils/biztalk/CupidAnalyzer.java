package ac.technion.iem.ontobuilder.gui.utils.biztalk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTable;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import ac.technion.iem.ontobuilder.core.biztalk.Link;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;
import ac.technion.iem.ontobuilder.gui.utils.StringUtilitiesGui;

/**
 * <p>Title: CupidAnalyzer</p>
 */
public class CupidAnalyzer
{
    private static JTable linksTable;
    private static JTable tentativeLinksTable;

    public static void main(String args[])
    {
        if (args.length != 1)
        {
            printUsage();
            System.exit(1);
        }
        try
        {
            File file = new File(args[0]);
            createLinksTable(file);
            String linksTableString = StringUtilitiesGui.getJTableStringRepresentation(linksTable);
            String tentativeLinksTableString = StringUtilitiesGui
                .getJTableStringRepresentation(tentativeLinksTable);
            System.out.println("Cupid results for '" + file + "'\n\n\n");
            System.out.println("Links\n");
            System.out.println(linksTableString);
            System.out.println("\n\nTentative Links\n");
            System.out.println(tentativeLinksTableString);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void printUsage()
    {
        System.out.println("Usage: java cac.technion.iem.ontobuilder.gui.utils.biztalk.CupidAnalyzer <file>");
    }

    private static void createLinksTable(File file) throws IOException, JDOMException
    {
        ArrayList<Link> links = new ArrayList<Link>();
        ArrayList<Link> tentativeLinks = new ArrayList<Link>();

        BufferedReader reader = new BufferedReader(new FileReader(file));
        SAXBuilder builder = new SAXBuilder(false);
        Document doc = builder.build(reader);

        Namespace b = Namespace.getNamespace("b", "urn:schemas-microsoft-com:BizTalkServer");
        // Namespace d=Namespace.getNamespace("d","urn:schemas-microsoft-com:datatypes");
        Namespace def = Namespace.getNamespace("urn:schemas-microsoft-com:xml-data");

        Element mapSourceElement = doc.getRootElement();
        String src = mapSourceElement.getChild("srctree").getChild("Schema", def)
            .getAttributeValue("root_reference", b);
        String dst = mapSourceElement.getChild("sinktree").getChild("Schema", def)
            .getAttributeValue("root_reference", b);
        Element linksElement = mapSourceElement.getChild("links");
        List<?> linksList = linksElement.getChildren("link");
        extractLinks(links, linksList);
        List<?> tentativeLinksList = linksElement.getChild("tentative-links").getChildren("link");
        extractLinks(tentativeLinks, tentativeLinksList);

        linksTable = createLinksTable(links, src, dst);
        tentativeLinksTable = createTentativeLinksTable(tentativeLinks, src, dst);
    }

    private static void extractLinks(ArrayList<Link> links, List<?> linksList)
    {
        for (Iterator<?> i = linksList.iterator(); i.hasNext();)
        {
            Element linkElement = (Element) i.next();
            Link link = new Link();
            String id = linkElement.getAttributeValue("linkid");
            if (id != null)
            {
                try
                {
                    link.setId(new Long(id));
                }
                catch (NumberFormatException e)
                {
                }
            }
            String from = linkElement.getAttributeValue("linkfrom");
            if (from != null)
                link.setFrom(from);
            String to = linkElement.getAttributeValue("linkto");
            if (to != null)
                link.setTo(to);
            String similarity = linkElement.getAttributeValue("similarity");
            if (similarity != null)
            {
                try
                {
                    link.setSimilarity(new Double(similarity));
                }
                catch (NumberFormatException e)
                {
                }
            }
            links.add(link);
        }
    }

    private static JTable createLinksTable(ArrayList<Link> links, String src, String dst)
    {
        String columnNames[] =
        {
            "Link ID", "From (" + src + ")", "To (" + dst + ")"
        };
        Object matchTable[][] = new Object[links.size()][3];

        for (int i = 0; i < links.size(); i++)
        {
            Link link = (Link) links.get(i);
            matchTable[i][0] = link.getId();
            matchTable[i][1] = link.getFrom();
            matchTable[i][2] = link.getTo();
        }
        return new JTable(new PropertiesTableModel(columnNames, 3, matchTable));
    }

    private static JTable createTentativeLinksTable(ArrayList<Link> links, String src, String dst)
    {
        String columnNames[] =
        {
            "From (" + src + ")", "To (" + dst + ")", "Similarity"
        };
        Object matchTable[][] = new Object[links.size()][3];

        for (int i = 0; i < links.size(); i++)
        {
            Link link = (Link) links.get(i);
            matchTable[i][0] = link.getFrom();
            matchTable[i][1] = link.getTo();
            matchTable[i][2] = link.getSimilarity();
        }
        return new JTable(new PropertiesTableModel(columnNames, 3, matchTable));
    }
}
