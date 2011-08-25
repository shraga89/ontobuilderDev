package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.HTMLElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.INPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: ElementsTreeRenderer</p>
 * Extends {@link DefaultTreeCellRenderer}
 */
public class ElementsTreeRenderer extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = 1L;

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
        boolean expanded, boolean leaf, int row, boolean hasFocus)
    {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        Object object = ((DefaultMutableTreeNode) value).getUserObject();
        if (object instanceof String)
        {
            String s = (String) object;
            if (s.equals(ApplicationUtilities.getResourceString("html.elements")))
                setIcon(ApplicationUtilities.getImage("elements.gif"));
            else if (expanded)
            {
                if (s.equals("<meta>"))
                    setIcon(ApplicationUtilities.getImage("metaopened.gif"));
                else if (s.equals("<a>"))
                    setIcon(ApplicationUtilities.getImage("linkopened.gif"));
                else if (s.equals("<frame>"))
                    setIcon(ApplicationUtilities.getImage("frameopened.gif"));
                else if (s.equals("<form>"))
                    setIcon(ApplicationUtilities.getImage("formopened.gif"));
            }
            else
            {
                if (s.equals("<meta>"))
                    setIcon(ApplicationUtilities.getImage("metaclosed.gif"));
                else if (s.equals("<a>"))
                    setIcon(ApplicationUtilities.getImage("linkclosed.gif"));
                else if (s.equals("<frame>"))
                    setIcon(ApplicationUtilities.getImage("frameclosed.gif"));
                else if (s.equals("<form>"))
                    setIcon(ApplicationUtilities.getImage("formclosed.gif"));
            }
            return this;
        }
        else if (object instanceof HTMLElementGui)
        {
            HTMLElementGui node = (HTMLElementGui) object;
            if (node.getType().equals(HTMLElement.A))
                setIcon(ApplicationUtilities.getImage("link.gif"));
            if (node.getType().equals(HTMLElement.META))
                setIcon(ApplicationUtilities.getImage("meta.gif"));
            else if (node.getType().equals(HTMLElement.FRAME))
                setIcon(ApplicationUtilities.getImage("frame.gif"));
            else if (node.getType().equals(HTMLElement.FORM))
                setIcon(ApplicationUtilities.getImage("form.gif"));
            else if (node.getType().equals(HTMLElement.INPUT))
            {
                INPUTElementGui input = (INPUTElementGui) node;
                if (input.getInputType().equals(INPUTElement.HIDDEN))
                    setIcon(ApplicationUtilities.getImage("hidden.gif"));
                else if (input.getInputType().equals(INPUTElement.TEXT))
                    setIcon(ApplicationUtilities.getImage("textbox.gif"));
                else if (input.getInputType().equals(INPUTElement.TEXTAREA))
                    setIcon(ApplicationUtilities.getImage("textarea.gif"));
                else if (input.getInputType().equals(INPUTElement.PASSWORD))
                    setIcon(ApplicationUtilities.getImage("password.gif"));
                else if (input.getInputType().equals(INPUTElement.CHECKBOX))
                    setIcon(ApplicationUtilities.getImage("checkbox.gif"));
                else if (input.getInputType().equals(INPUTElement.CHECKBOXOPTION))
                    setIcon(ApplicationUtilities.getImage("checkboxoption.gif"));
                else if (input.getInputType().equals(INPUTElement.RADIO))
                    setIcon(ApplicationUtilities.getImage("radio.gif"));
                else if (input.getInputType().equals(INPUTElement.RADIOOPTION))
                    setIcon(ApplicationUtilities.getImage("radiooption.gif"));
                else if (input.getInputType().equals(INPUTElement.SUBMIT))
                    setIcon(ApplicationUtilities.getImage("submit.gif"));
                else if (input.getInputType().equals(INPUTElement.RESET))
                    setIcon(ApplicationUtilities.getImage("reset.gif"));
                else if (input.getInputType().equals(INPUTElement.FILE))
                    setIcon(ApplicationUtilities.getImage("file.gif"));
                else if (input.getInputType().equals(INPUTElement.IMAGE))
                    setIcon(ApplicationUtilities.getImage("image.gif"));
                else if (input.getInputType().equals(INPUTElement.BUTTON))
                    setIcon(ApplicationUtilities.getImage("button.gif"));
                else if (input.getInputType().equals(INPUTElement.OPTION))
                    setIcon(ApplicationUtilities.getImage("option.gif"));
                else if (input.getInputType().equals(INPUTElement.SELECT))
                {
                    if (((SELECTElementGui) input).isMultiple())
                        setIcon(ApplicationUtilities.getImage("list.gif"));
                    else
                        setIcon(ApplicationUtilities.getImage("select.gif"));
                }
            }
        }
        return this;
    }
}