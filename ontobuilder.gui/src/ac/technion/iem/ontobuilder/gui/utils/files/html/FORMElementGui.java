package ac.technion.iem.ontobuilder.gui.utils.files.html;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;

import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.FORMElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.INPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.ImageINPUTElement;
import ac.technion.iem.ontobuilder.extraction.webform.utils.files.html.SubmitINPUTElement;
import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;
import ac.technion.iem.ontobuilder.gui.application.PropertiesTableModel;

/*
 * Fix: default FROM::method to be GET
 */
/**
 * <p>Title: FORMElement</p>
 * Extends {@link HTMLElementGui}
 */
public class FORMElementGui extends HTMLElementGui
{
    private ArrayList<INPUTElementGui> inputs;

    private JPanel component;
    protected FORMElement formElement;

    public FORMElementGui(FORMElement formElement)
    {
        super(formElement);
        this.formElement = formElement;
        inputs = new ArrayList<INPUTElementGui>();
    }
    
    public FORMElementGui()
    {
    	this(new FORMElement());
    }

    public FORMElementGui(String name, URL action, String method)
    {
    	this(new FORMElement(name,action,method));
    }
    
    public FORMElement getFORMElement()
    {
    	return formElement;
    }

    public void setName(String name)
    {
        this.formElement.setName(name);
    }

    public String getName()
    {
        return this.formElement.getName();
    }

    public void setMethod(String methods)
    {
    	this.formElement.setMethod(methods);
    }

    public String getMethod()
    {
        return this.formElement.getMethod();
    }

    public void setAction(URL action)
    {
    	this.formElement.setAction(action);
    }

    public URL getAction()
    {
        return this.formElement.getAction();
    }

    public int getInputsCount()
    {
        return inputs.size();
    }

    public String toString()
    {
        return this.formElement.toString();
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
                ApplicationUtilities.getResourceString("html.form.name"), getName()
            },
            {
                ApplicationUtilities.getResourceString("html.form.method"), getMethod()
            },
            {
                ApplicationUtilities.getResourceString("html.form.action"), getAction()
            },
            {
                ApplicationUtilities.getResourceString("html.form.inputs"),
                new Integer(inputs.size())
            }
        };
        return new JTable(new PropertiesTableModel(columnNames, 4, data));
    }

    public DefaultMutableTreeNode getTreeBranch()
    {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(this);
        for (Iterator<INPUTElementGui> i = inputs.iterator(); i.hasNext();)
            node.add(((INPUTElementGui) i.next()).getTreeBranch());
        return node;
    }

    public void setInputs(ArrayList<INPUTElementGui> inputs)
    {
        this.inputs = inputs;
        ArrayList<INPUTElement> inputElements = new ArrayList<INPUTElement>();
        for (INPUTElementGui input : inputs)
        {
            input.setForm(this);
            INPUTElement inputElement = input.getInputElement();
            inputElements.add(inputElement);
            inputElement.setForm(getFORMElement());
        }
        this.formElement.setInputs(inputElements);
    }

    public INPUTElementGui getInput(int index)
    {
        if (index < 0 || index >= inputs.size())
            return null;
        return (INPUTElementGui) inputs.get(index);
    }

    public void addInput(INPUTElementGui input)
    {
        if (input == null)
            return;
        input.setForm(this);
        input.getInputElement().setForm(getFORMElement());
        inputs.add(input);
        this.formElement.addInput(input.getInputElement());
    }

    public void flat()
    {
        flatRadios();
        flatCheckboxes();
    }

    private void flatRadios()
    {
        HashMap<String, RadioINPUTElementGui> hm = new HashMap<String, RadioINPUTElementGui>();
        for (Iterator<INPUTElementGui> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElementGui input = (INPUTElementGui) i.next();
            if (input.getInputType().equals(INPUTElement.RADIO))
            {
                RadioINPUTElementGui radioInput = (RadioINPUTElementGui) input;
                String name = radioInput.getName();
                if (hm.containsKey(name))
                {
                    RadioINPUTElementGui radio = (RadioINPUTElementGui) hm.get(input.getName());
                    for (int j = 0; j < radioInput.getOptionsCount(); j++)
                        radio.addOption(radioInput.getOption(j));
                    i.remove();
                }
                else
                    hm.put(name, radioInput);
            }
        }
    }

    private void flatCheckboxes()
    {
        HashMap<String, CheckboxINPUTElementGui> hm = new HashMap<String, CheckboxINPUTElementGui>();
        for (Iterator<INPUTElementGui> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElementGui input = (INPUTElementGui) i.next();
            if (input.getInputType().equals(INPUTElement.CHECKBOX))
            {
                CheckboxINPUTElementGui checkboxInput = (CheckboxINPUTElementGui) input;
                String name = checkboxInput.getName();
                if (hm.containsKey(name))
                {
                    CheckboxINPUTElementGui checkbox = (CheckboxINPUTElementGui) hm.get(input.getName());
                    for (int j = 0; j < checkboxInput.getOptionsCount(); j++)
                        checkbox.addOption(checkboxInput.getOption(j));
                    i.remove();
                }
                else
                    hm.put(name, checkboxInput);
            }
        }
    }

    public InputStream submit() throws IOException
    {
    	return this.formElement.submit();
    }

    public InputStream submit(StringBuffer info) throws IOException
    {
        return this.formElement.submit(info);
    }

    public int getLastRes()
    {
        return this.formElement.getLastRes();
    }

    public String getLastQueryString()
    {
        return this.formElement.getLastQueryString();
    }

    public void reset()
    {
        for (Iterator<INPUTElementGui> i = inputs.iterator(); i.hasNext();)
            ((INPUTElementGui) i.next()).reset();
        this.formElement.reset();
    }

    public void clearPressed()
    {
        for (Iterator<INPUTElementGui> i = inputs.iterator(); i.hasNext();)
        {
            INPUTElementGui input = (INPUTElementGui) i.next();
            if (input.getInputType().equals(INPUTElement.IMAGE))
            {
                ((ImageINPUTElementGui) input).clearPressed();
                ((ImageINPUTElement)input.getInputElement()).clearPressed();
            }
            else if (input.getInputType().equals(INPUTElement.SUBMIT))
            {
                ((SubmitINPUTElementGui) input).clearPressed();
                ((SubmitINPUTElement)input.getInputElement()).clearPressed();
            }
        }
    }

    public Component getComponent()
    {
        if (component != null)
            return component;
        component = new JPanel(new GridBagLayout());
        int ypos = 1;
        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            component.add(new JLabel(ApplicationUtilities.getResourceString("html.form.name") +
                ": " + getName(), SwingConstants.LEFT), gbc);
            gbc.gridy = ypos++;
            component.add(new JLabel(ApplicationUtilities.getResourceString("html.form.action") +
                ": " + getAction(), SwingConstants.LEFT), gbc);
            gbc.gridy = ypos++;
            gbc.insets = new Insets(0, 0, 10, 0);
            component.add(new JLabel(ApplicationUtilities.getResourceString("html.form.method") +
                ": " + getMethod(), SwingConstants.LEFT), gbc);
        }

        {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.insets = new Insets(5, 10, 0, 0);
            for (Iterator<INPUTElementGui> i = inputs.iterator(); i.hasNext(); ypos++)
            {
                INPUTElementGui inputElement = (INPUTElementGui) i.next();
                String type = inputElement.getInputType();
                if (type.equals(INPUTElement.TEXT))
                {
                    TextINPUTElementGui textInput = (TextINPUTElementGui) inputElement;
                    JLabel label = new JLabel(textInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(textInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.PASSWORD))
                {
                    PasswordINPUTElementGui passwordInput = (PasswordINPUTElementGui) inputElement;
                    JLabel label = new JLabel(passwordInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(passwordInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.FILE))
                {
                    FileINPUTElementGui fileInput = (FileINPUTElementGui) inputElement;
                    JLabel label = new JLabel(fileInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(fileInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.HIDDEN))
                {
                    HiddenINPUTElementGui hiddenInput = (HiddenINPUTElementGui) inputElement;
                    JLabel label = new JLabel(hiddenInput.getName(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(hiddenInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.CHECKBOX))
                {
                    CheckboxINPUTElementGui checkboxInput = (CheckboxINPUTElementGui) inputElement;
                    JLabel label = new JLabel(checkboxInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(checkboxInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.RADIO))
                {
                    RadioINPUTElementGui radioInput = (RadioINPUTElementGui) inputElement;
                    JLabel label = new JLabel(radioInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(radioInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.SELECT))
                {
                    SELECTElementGui selectInput = (SELECTElementGui) inputElement;
                    JLabel label = new JLabel(selectInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    component.add(selectInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.BUTTON))
                {
                    ButtonINPUTElementGui buttonInput = (ButtonINPUTElementGui) inputElement;
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(buttonInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.RESET))
                {
                    ResetINPUTElementGui resetInput = (ResetINPUTElementGui) inputElement;
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(resetInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.SUBMIT))
                {
                    SubmitINPUTElementGui submitInput = (SubmitINPUTElementGui) inputElement;
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(submitInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.IMAGE))
                {
                    ImageINPUTElementGui imageInput = (ImageINPUTElementGui) inputElement;
                    JLabel label = new JLabel(imageInput.getAlt(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(imageInput.getComponent(), gbc);
                }
                else if (type.equals(INPUTElement.TEXTAREA))
                {
                    TEXTAREAElementGui textareaInput = (TEXTAREAElementGui) inputElement;
                    JLabel label = new JLabel(textareaInput.getLabel(), SwingConstants.RIGHT);
                    gbc.gridx = 0;
                    gbc.gridy = ypos;
                    component.add(label, gbc);
                    gbc.gridx = 1;
                    gbc.gridy = ypos;
                    component.add(textareaInput.getComponent(), gbc);
                }
            }
        }

        {// Separator
            GridBagConstraints gbcl = new GridBagConstraints();
            gbcl.gridy = ypos;
            gbcl.gridwidth = 2;
            gbcl.gridheight = GridBagConstraints.REMAINDER;
            gbcl.fill = GridBagConstraints.VERTICAL;
            gbcl.weightx = 1.0;
            gbcl.weighty = 1.0;
            component.add(new JPanel(), gbcl);
        }

        return component;
    }

}