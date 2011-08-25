package ac.technion.iem.ontobuilder.io.utils.xml.schema;

import java.util.ArrayList;

/**
 * <p>Title: XMLSchema</p>
 */
public class XMLSchema
{

    private String name;
    private ArrayList<XSDElement> elements = new ArrayList<XSDElement>();
    private int currentElementIndex = 0;

    /**
     * @return Returns the elements.
     */
    public ArrayList<XSDElement> getElements()
    {
        return elements;
    }

    /**
     * @param elements The elements to set.
     */
    public void setElements(ArrayList<XSDElement> elements)
    {
        this.elements = elements;
    }

    /**
     * @return Returns the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    public void addXSDElement(XSDElement e)
    {
        elements.add(currentElementIndex++, e);
    }

    public XSDElementsIterator iterator()
    {
        return new XSDElementsIterator(elements);
    }
}
