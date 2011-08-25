package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ac.technion.iem.ontobuilder.gui.elements.TextArea;
import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;

/**
 * <p>Title: MessagePanel</p>
 * Extends a {@link JPanel}
 */
public class MessagePanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private TextArea message;

    /**
     * Constructs a MessagePanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public MessagePanel(OntoBuilder ontoBuilder)
    {
        setLayout(new BorderLayout());
        message = new TextArea();
        message.setEditable(false);
        add(new JScrollPane(message), BorderLayout.CENTER);
    }

    /**
     * Set the message
     * 
     * @param myMessage the message to set
     */
    public void setMessage(String myMessage)
    {
        message.setText(myMessage);
    }
}