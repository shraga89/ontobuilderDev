package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.net.URL;
import java.io.IOException;
import java.io.InputStreamReader;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;

import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;
import ac.technion.iem.ontobuilder.gui.utils.files.html.HTMLSyntaxHighlighterEditor;

/**
 * <p>Title: SourcePanel</p>
 * Extends a {@link JPanel}
 */
public class SourcePanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private HTMLSyntaxHighlighterEditor htmlSource;

    /**
     * Constructs a SourcePanel
     * 
     * @param ontoBuilder the {@link OntoBuilder} application
     */
    public SourcePanel(OntoBuilder ontoBuilder)
    {
        setLayout(new BorderLayout());

        htmlSource = new HTMLSyntaxHighlighterEditor();
        htmlSource.setEditable(false);
        add(new JScrollPane(htmlSource), BorderLayout.CENTER);
    }

    /**
     * Get the source text
     * 
     * @return the html source text
     */
    public String getSourceText()
    {
        return htmlSource.getText();
    }

    /**
     * Set the source text
     * 
     * @param url the html source
     * @throws IOException when cannot read the source text from the URL
     */
    public void setSourceText(URL url) throws IOException
    {
        htmlSource.read(new InputStreamReader(url.openStream()), null);
    }

    /**
     * Set the source text
     * 
     * @param source the source text
     * @throws IOException
     */
    public void setSourceText(String source)
    {
        htmlSource.setText(source);
    }
}