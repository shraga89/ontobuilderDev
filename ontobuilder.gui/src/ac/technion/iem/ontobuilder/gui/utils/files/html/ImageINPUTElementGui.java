package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ImageINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/**
 * <p>Title: ImageINPUTElement</p>
 * Extends {@link INPUTElementGui}
 */
public class ImageINPUTElementGui extends INPUTElementGui
{

    protected ImageINPUTElement imageINPUTElement;
    protected JButton image;
    
    public ImageINPUTElementGui(final ImageINPUTElement imageINPUTElement)
    {
    	super(imageINPUTElement);
    	this.imageINPUTElement = imageINPUTElement;
        image = new JButton();
        image.setBorder(null);
        image.setRequestFocusEnabled(false);
        image.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                imageINPUTElement.setX(e.getX());
                imageINPUTElement.setY(e.getY());
                if (form != null)
                    form.clearPressed();
                imageINPUTElement.setPressed(true);
            }
        });
        image.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public ImageINPUTElementGui()
    {
        this(new ImageINPUTElement());
    }

    public ImageINPUTElementGui(String name, URL src, String alt)
    {
        this(new ImageINPUTElement(name,src,alt));
    }

    public void setSrc(URL src)
    {
        this.imageINPUTElement.setSrc(src);
        image.setIcon(new ImageIcon(src));
    }

    public URL getSrc()
    {
        return this.imageINPUTElement.getSrc();
    }

    public void setAlt(String alt)
    {
    	this.imageINPUTElement.setAlt(alt);
    }

    public String getAlt()
    {
        return this.imageINPUTElement.getAlt();
    }

    public void setDisabled(boolean b)
    {
        super.setDisabled(b);
        this.imageINPUTElement.setDisabled(b);
        image.setEnabled(!b);
    }

    public JTable getProperties()
    {
        String columnNames[] =
        {
            ApplicationUtilities.getResourceString("properties.attribute"),
            ApplicationUtilities.getResourceString("properties.value")
        };
        Object data[][] =
        {
            {
                ApplicationUtilities.getResourceString("html.input.type"), getInputType()
            },
            {
                ApplicationUtilities.getResourceString("html.input.image.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.input.image.src"), getSrc()
            },
            {
                ApplicationUtilities.getResourceString("html.input.image.alt"), getAlt()
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 4, data));
    }

    public int getX()
    {
        return this.imageINPUTElement.getX();
    }

    public int getY()
    {
        return this.imageINPUTElement.getY();
    }

    public String paramString()
    {
        return this.imageINPUTElement.paramString();
    }

    public Component getComponent()
    {
        return image;
    }

    public void clearPressed()
    {
    	this.imageINPUTElement.clearPressed();
    }

    public boolean canSubmit()
    {
        return this.imageINPUTElement.canSubmit();
    }
}