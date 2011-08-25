package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.io.IOException;
import java.net.URL;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.text.html.HTMLDocument;

import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;

/**
 * <p>Title: HTMLPanel</p>
 * Extends a {@link JPanel}
 */
public class HTMLPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private JEditorPane htmlPage;

    /**
     * Constructs a HTMLPanel
     *
     * @param ontoBuilder the ontoBuilder application
     */
    public HTMLPanel(final OntoBuilder ontoBuilder)
    {
        setLayout(new BorderLayout());
        htmlPage = new JEditorPane();
        htmlPage.setEditable(false);
        htmlPage.addHyperlinkListener(new HyperlinkListener()
        {
            public void hyperlinkUpdate(HyperlinkEvent e)
            {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    JEditorPane pane = (JEditorPane) e.getSource();
                    if (e instanceof HTMLFrameHyperlinkEvent)
                    {
                        HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                        HTMLDocument doc = (HTMLDocument) pane.getDocument();
                        doc.processHTMLFrameHyperlinkEvent(evt);
                    }
                    else
                    {
                        try
                        {
                            ontoBuilder.getBrowser().submitURL(e.getURL());
                        }
                        catch (Throwable t)
                        {
                        }
                    }
                }
            }
        });
        JScrollPane scrollPane = new JScrollPane(htmlPage);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Set the URL
     *
     * @param url the {@link URL}
     * @throws IOException when cannot set the page according to the URL
     */
    public void setPage(URL url) throws IOException
    {
        htmlPage.setPage(url);
    }

    /**
     * Set the HTML text
     *
     * @param text the text
     */
    public void setHTMLText(String text)
    {
        try
        {
            htmlPage.setContentType("text/html");
            htmlPage.setText(text);
        }
        catch (Exception e)
        {
        }
    }
}