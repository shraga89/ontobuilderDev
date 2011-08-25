package ac.technion.iem.ontobuilder.gui.tools.topk;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;

/**
 * <p>Title: SMSplash</p>
 * Extends {@link JWindow}
 */
public class SMSplash extends JWindow
{
    private static final long serialVersionUID = -8551839665384689235L;

    public SMSplash()
    {
        super();
        JLabel splashLabel = new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
            "images/smsplash.gif")));
        getContentPane().add(splashLabel);
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getSize().width / 2, screenSize.height / 2 -
            getSize().height / 2);
    }
}