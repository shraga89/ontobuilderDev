package ac.technion.iem.ontobuilder.gui.ontobuilder.elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

import org.lobobrowser.html.*;
import org.lobobrowser.html.gui.*;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import ac.technion.iem.ontobuilder.gui.ontobuilder.main.OntoBuilder;

import java.awt.*;

import javax.swing.JPanel;

/**
 * <p>Title: CobraHTMLPanel</p>
 * Extends a {@link JPanel}
 */
public class CobraHTMLPanel extends JPanel
{
    private static final long serialVersionUID = 1L;

    private HtmlPanel htmlPanel;

    /**
     * Constructs a CobraHTMLPanel
     *
     * @param ontoBuilder the application
     */
    public CobraHTMLPanel(final OntoBuilder ontoBuilder)
    {
        this.htmlPanel = new HtmlPanel();
    }

    /**
     * Set the URL
     *
     * @param url the {@link URL}
     * @throws IOException
     */
    public void setPage(URL url) throws IOException
    {
        URLConnection connection = url.openConnection();
        InputStream in = connection.getInputStream();

        // A Reader should be created with the correct charset,
        // which may be obtained from the Content-Type header
        // of an HTTP response.
        Reader reader = new InputStreamReader(in);

        // InputSourceImpl constructor with URI recommended
        // so the renderer can resolve page component URLs.
        InputSource is = new InputSourceImpl(reader, url.toString());

        HtmlRendererContext rendererContext = new LocalHtmlRendererContext(htmlPanel, null);

        // Set a preferred width for the HtmlPanel,
        // which will allow getPreferredSize() to
        // be calculated according to block content.
        // We do this here to illustrate the
        // feature, but is generally not
        // recommended for performance reasons.
        htmlPanel.setPreferredSize(getSize());

        // This example does not perform incremental
        // rendering.
        DocumentBuilderImpl builder = new DocumentBuilderImpl(
            rendererContext.getUserAgentContext(), rendererContext);
        try
        {
            if (getComponentCount() > 0)
                remove(0);
            Document document = builder.parse(is);
            in.close();
            // Set the document in the HtmlPanel. This
            // is what lets the document render.
            htmlPanel.setDocument(document, rendererContext);

            add(htmlPanel, BorderLayout.CENTER);

            // We pack the JFrame to demonstrate the
            // validity of HtmlPanel's preferred size.
            // Normally you would want to set a specific
            // JFrame size instead.

            // This should be done in the GUI dispatch
            // thread since the document is scheduled to
            // be rendered in that thread.
            EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    setVisible(true);
                }
            });

        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IOException();
        }

    }

    /**
     * Set the HTML text.
     * <br>Not implemented
     *
     * @param text the text to set
     */
    public void setHTMLText(String text)
    {
        try
        {
            // htmlPage.setContentType("text/html");
            // htmlPage.setText(text);
        }
        catch (Exception e)
        {
        }
    }

    /**
     * <p>Title: LocalHtmlRendererContext</p>
     * Extends a {@link SimpleHtmlRendererContext}
     */
    private static class LocalHtmlRendererContext extends SimpleHtmlRendererContext
    {
        // Override methods here to implement browser functionality
        public LocalHtmlRendererContext(HtmlPanel contextComponent, UserAgentContext userAgentContext)
        {
            super(contextComponent, userAgentContext);
        }
    }

}
