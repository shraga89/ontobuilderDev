package ac.technion.iem.ontobuilder.gui.elements;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import ac.technion.iem.ontobuilder.gui.application.ApplicationUtilities;

/**
 * <p>Title: About</p>
 * <p>Description: About dialog of the application</p>
 * Extends {@link JDialog}
 */
public class About extends JDialog
{
    private static final long serialVersionUID = 1L;

    public About(JFrame parent)
    {
        super(parent, ApplicationUtilities.getResourceString("about.windowTitle"), true);
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        setSize(new Dimension(ApplicationUtilities.getIntProperty("about.width"),
            ApplicationUtilities.getIntProperty("about.height")));
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel south = new JPanel();
        south.setLayout(new FlowLayout());
        JButton okButton;
        south
            .add(okButton = new JButton(ApplicationUtilities.getResourceString("about.button.ok")));
        okButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                dispose();
            }
        });

        panel.add(BorderLayout.SOUTH, south);
        JLabel west = new JLabel(ApplicationUtilities.getImage("about.gif"));
        west.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));
        panel.add(BorderLayout.WEST, west);
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 15));
        JLabel title = new JLabel(ApplicationUtilities.getResourceString("application.title"),
            SwingConstants.LEFT);
        if (parent != null)
            title.setFont(new Font(getFont().getFontName(), Font.BOLD, getFont().getSize() + 6));
        center.add(title);
        center.add(Box.createRigidArea(new Dimension(0, 25)));
        center.add(new JLabel(ApplicationUtilities.getResourceString("about.version") + ": " +
            ApplicationUtilities.getResourceString("application.version"), SwingConstants.LEFT));
        center.add(new JLabel(ApplicationUtilities.getResourceString("about.date") + ": " +
            ApplicationUtilities.getResourceString("application.date"), SwingConstants.LEFT));
        center.add(Box.createRigidArea(new Dimension(0, 15)));
        center.add(new JLabel(ApplicationUtilities.getResourceString("about.author"),
            SwingConstants.LEFT));
        center.add(new JLabel(ApplicationUtilities.getResourceString("application.author"),
            SwingConstants.LEFT));
        center.add(new JLabel(ApplicationUtilities.getResourceString("about.subauthor"),
            SwingConstants.LEFT));
        center.add(new JLabel(ApplicationUtilities.getResourceString("application.subauthor"),
            SwingConstants.LEFT));
        center.add(new JLabel(ApplicationUtilities.getResourceString("about.copyright"),
            SwingConstants.LEFT));
        panel.add(BorderLayout.CENTER, center);
        addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent e)
            {
                dispose();
            }
        });
        setContentPane(panel);
    }
}