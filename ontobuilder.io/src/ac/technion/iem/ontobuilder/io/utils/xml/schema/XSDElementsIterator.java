package ac.technion.iem.ontobuilder.io.utils.xml.schema;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * <p>Title: XSDElementsIterator</p>
 */
public class XSDElementsIterator
{

    private ArrayList<XSDElement> elements = null;
    private int currentPossition;

    protected XSDElementsIterator(ArrayList<XSDElement> elements)
    {
        this.elements = elements;
        currentPossition = 0;
    }

    public XSDElement nextElement() throws NoSuchElementException
    {
        if (currentPossition >= elements.size())
            throw new NoSuchElementException();
        return elements.get(currentPossition++);
    }

    public boolean hasNext()
    {
        return currentPossition < elements.size();
    }

    public void reset()
    {
        currentPossition = 0;
    }
}
