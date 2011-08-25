package ac.technion.iem.ontobuilder.gui.elements;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: Splash</p>
 * Extends {@link JWindow}
 */
public class Splash extends JWindow
{
    private static final long serialVersionUID = 1L;

    public Splash()
    {
        super();
        ImageIcon icon = ApplicationUtilities.getImage("splash.gif");
        JLabel splashLabel = new JLabel(icon);
        getContentPane().add(splashLabel);
        pack();
        setLocationRelativeTo(null);
    }
}
