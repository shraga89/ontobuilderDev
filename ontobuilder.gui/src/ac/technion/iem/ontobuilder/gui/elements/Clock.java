package ac.technion.iem.ontobuilder.gui.elements;

import java.util.Date;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * <p>Title: Clock</p>
 * <p>Description: The clock of the GUI</p>
 * Extends {@link JLabel}
 */
public class Clock extends JLabel
{
    private static final long serialVersionUID = 1L;

    public Clock()
    {
        super("            ", SwingConstants.CENTER);
        final SimpleDateFormat tf = new SimpleDateFormat(" hh:mm:ss a");
        final DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        Timer timer = new Timer(1000, new ActionListener()
        {
            public void actionPerformed(ActionEvent evt)
            {
                Date date = new Date();
                setText(tf.format(date));
                setToolTipText(df.format(date));
            }
        });
        timer.start();
    }
}
