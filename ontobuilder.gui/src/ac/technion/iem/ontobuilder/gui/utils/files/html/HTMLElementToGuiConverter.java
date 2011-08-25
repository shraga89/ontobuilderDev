package ac.technion.iem.ontobuilder.gui.utils.files.html;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.AElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ButtonINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.CheckboxINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.CheckboxINPUTElementOption;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.FORMElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.FRAMEElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.FileINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.HTMLElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.HTMLTable;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.HiddenINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ImageINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.METAElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.OPTIONElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.PasswordINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.RadioINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.RadioINPUTElementOption;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ResetINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.SELECTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.SubmitINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.TEXTAREAElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.TextINPUTElement;

/**
 * Converter of HTML elements to their GUI wrappers
 *
 * @author Y.A
 */
public class HTMLElementToGuiConverter
{
    
    /**
     * 
     * Convert an HTML element to its GUI wrapper 
     *
     * @param element element to convert
     * @return the new wrapper
     */
    public static HTMLElementGui convert(HTMLElement element)
    {
        HTMLElementGui guiElement;
        if (element.getClass() == AElement.class)
        {
            guiElement = new AElementGui((AElement)element);
        }
        else if (element.getClass() == ButtonINPUTElement.class)
        {
            guiElement = new ButtonINPUTElementGui((ButtonINPUTElement)element);
        } 
        else if (element.getClass() == CheckboxINPUTElement.class)
        {
            guiElement = new CheckboxINPUTElementGui((CheckboxINPUTElement)element);
        } 
        else if (element.getClass() == CheckboxINPUTElementOption.class)
        {
            guiElement = new CheckboxINPUTElementOptionGui((CheckboxINPUTElementOption)element);
        }
        else if (element.getClass() == FileINPUTElement.class)
        {
            guiElement = new FileINPUTElementGui((FileINPUTElement)element);
        }
        else if (element.getClass() == FORMElement.class)
        {
            guiElement = new FORMElementGui((FORMElement)element);
        }
        else if (element.getClass() == FRAMEElement.class)
        {
            guiElement = new FRAMEElementGui((FRAMEElement)element);
        }
        else if (element.getClass() == HiddenINPUTElement.class)
        {
            guiElement = new HiddenINPUTElementGui((HiddenINPUTElement)element);
        }
        else if (element.getClass() == ImageINPUTElement.class)
        {
            guiElement = new ImageINPUTElementGui((ImageINPUTElement)element);
        }
        else if (element.getClass() == METAElement.class)
        {
            guiElement = new METAElementGui((METAElement)element);
        }
        else if (element.getClass() == OPTIONElement.class)
        {
            guiElement = new OPTIONElementGui((OPTIONElement)element);
        }
        else if (element.getClass() == PasswordINPUTElement.class)
        {
            guiElement = new PasswordINPUTElementGui((PasswordINPUTElement)element);
        }
        else if (element.getClass() == RadioINPUTElement.class)
        {
            guiElement = new RadioINPUTElementGui((RadioINPUTElement)element);
        }
        else if (element.getClass() == RadioINPUTElementOption.class)
        {
            guiElement = new RadioINPUTElementOptionGui((RadioINPUTElementOption)element);
        }
        else if (element.getClass() == ResetINPUTElement.class)
        {
            guiElement = new ResetINPUTElementGui((ResetINPUTElement)element);
        }
        else if (element.getClass() == SELECTElement.class)
        {
            guiElement = new SELECTElementGui((SELECTElement)element);
        }
        else if (element.getClass() == SubmitINPUTElement.class)
        {
            guiElement = new SubmitINPUTElementGui((SubmitINPUTElement)element);
        }
        else if (element.getClass() == TEXTAREAElement.class)
        {
            guiElement = new TEXTAREAElementGui((TEXTAREAElement)element);
        }
        else if (element.getClass() == TextINPUTElement.class)
        {
            guiElement = new TextINPUTElementGui((TextINPUTElement)element);
        }
        else
        {
            // TODO: Throw an exception?
            guiElement = null;
        }
        return guiElement;
    }
    
    /**
     * Convert an HTML table element to its GUI wrapper 
     *
     * @param table table to convert
     * @return the new wrapper
     */
    public static HTMLTableGui convert(HTMLTable table)
    {
        HTMLTableGui guiTable = new HTMLTableGui(table);
        return guiTable;
    }
}
