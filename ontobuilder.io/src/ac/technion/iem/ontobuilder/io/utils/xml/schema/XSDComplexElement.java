package ac.technion.iem.ontobuilder.io.utils.xml.schema;

import java.util.ArrayList;

/**
 * <p>Title: XSDComplexElement</p>
 * Extends {@link XSDSimpleElement}
 */
public class XSDComplexElement extends XSDSimpleElement
{

    private ArrayList<XSDElement> sequence = new ArrayList<XSDElement>();

    /**
     * @return Returns the sequence.
     */
    public ArrayList<XSDElement> getSequence()
    {
        return sequence;
    }

    /**
     * @param sequence The sequence to set.
     */
    public void setSequence(ArrayList<XSDElement> sequence)
    {
        this.sequence = sequence;
    }

    public XSDElementsIterator iterator()
    {
        return new XSDElementsIterator(sequence);
    }
}
