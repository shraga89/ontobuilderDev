package ac.technion.iem.ontobuilder.gui.tools.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

/**
 * <p>
 * Title: ExceptionsHandeler
 * </p>
 * <p>
 * Description: handles exceptions during runtime<br>
 * exceptions may be fatal or none fatal
 * </p>
 * 
 * @author <font face="Brush Script MT"><b>Haggai Roitman</b></font>
 * @version 1.0
 */
public class ExceptionsHandler
{

    /**
     * displays error message to GAS user
     * 
     * @param component
     * @param message
     * @param title
     */
    public void displayErrorMessage(boolean fatal, Component component, String message, String title)
    {
        JOptionPane.showMessageDialog(component, message, title, JOptionPane.ERROR_MESSAGE);
        if (fatal)
            System.exit(1);
    }

}