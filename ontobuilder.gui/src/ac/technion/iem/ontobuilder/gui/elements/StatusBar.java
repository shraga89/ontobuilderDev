package ac.technion.iem.ontobuilder.gui.elements;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

/**
 * <p>Title: StatusBar</p>
 * Extends {@link JPanel}
 */
public class StatusBar extends JPanel
{
    private static final long serialVersionUID = 1L;

    Status status;
    JLabel coordinates;
    Clock clock;
    ProgressBar progress;

    /**
     * Constructs a default StatusBar
     */
    public StatusBar()
    {
        Border border = BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createLoweredBevelBorder());
        setLayout(new BorderLayout());
        add(status = new Status(""), BorderLayout.CENTER);
        status.setBorder(border);
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(clock = new Clock(), BorderLayout.EAST);
        clock.setBorder(border);
        rightPanel.add(progress = new ProgressBar(), BorderLayout.WEST);
        progress.setBorder(border);
        rightPanel.add(coordinates = new JLabel("                    "), BorderLayout.CENTER);
        coordinates.setBorder(border);
        // coordinates.setFont(new Font("Monospaced",Font.PLAIN,getFont().getSize()));
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Sets the tasks for the coordinates
     *
     * @param text the text to set
     */
    public void setCoordinatesText(String text)
    {
        coordinates.setText(text);
    }

    /**
     * Set the x,y coordinates for the status bar
     *
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public void setCoordinates(int x, int y)
    {
        String xs = String.valueOf(x);
        char[] xc =
        {
            ' ', ' ', ' ', ' '
        };
        for (int i = xs.length() - 1; i >= 0; i--)
            xc[3 - xs.length() + i + 1] = xs.charAt(i);
        String ys = String.valueOf(y);
        char[] yc =
        {
            ' ', ' ', ' ', ' '
        };
        for (int i = ys.length() - 1; i >= 0; i--)
            yc[3 - ys.length() + i + 1] = ys.charAt(i);
        coordinates.setText(" x: " + new String(xc) + " , y: " + new String(yc) + " ");
    }

    /**
     * Clear the coordinates text
     */
    public void clearCoordinates()
    {
        coordinates.setText("");
    }

    /**
     * Set the status to display
     *
     * @param text the text of the status
     */
    public void setStatus(String text)
    {
        status.setText(text);
    }

    /**
     * Set the status bar's icon
     *
     * @param icon the icon to set
     */
    public void setStatusIcon(ImageIcon icon)
    {
        status.setIcon(icon);
    }

    /**
     * Clear the status bar's icon
     */
    public void clearStatusIcon()
    {
        status.setIcon(null);
    }

    /**
     * Sets a temporal status in the status bar
     *
     * @param text the text to display
     */
    public void setTemporalStatus(String text)
    {
        status.setText(text);
        Thread t = new Thread(new Runnable()
        {
            long ticket = status.getTickect();

            public void run()
            {
                try
                {
                    Thread.sleep(10000);
                }
                catch (Exception e)
                {
                }
                finally
                {
                    if (status.getTickect() == ticket)
                        clearStatus();
                }
            }
        });
        t.start();
    }

    /**
     * Clear the status
     */
    public synchronized void clearStatus()
    {
        status.setText("");
    }

    /**
     * Get the progress bar of the status bar 
     *
     * @return the progress bar
     */
    public ProgressBar getProgressBar()
    {
        return progress;
    }

    /**
     * Internal status class
     */
    class Status extends JLabel
    {
        private static final long serialVersionUID = 1L;

        private long ticket;

        public Status(String text)
        {
            super(" " + text, SwingConstants.LEFT);
        }

        public synchronized void setText(String text)
        {
            ticket = System.currentTimeMillis();
            super.setText(" " + text);
        }

        public synchronized long getTickect()
        {
            return ticket;
        }
    }
}