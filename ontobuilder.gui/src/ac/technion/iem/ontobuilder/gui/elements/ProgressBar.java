package ac.technion.iem.ontobuilder.gui.elements;

import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JProgressBar;

/**
 * <p>
 * Title: ProgressBar
 * </p>
 * Extends {@link JProgressBar}
 */
public class ProgressBar extends JProgressBar
{
    private static final long serialVersionUID = 1L;

    private ProgressTask task;
    private MouseListener mouseListener;

    /**
     * Constructs a default ProgressBar
     */
    public ProgressBar()
    {
        mouseListener = new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                if (e.getClickCount() > 1)
                    stopTask();
            }
        };
    }

    /**
     * Set a task for the progress bar to monitor
     * 
     * @param task the task to monitor
     */
    public void setTask(ProgressTask task)
    {
        this.task = task;
        addMouseListener(mouseListener);
    }

    /**
     * Stop the task in the progress bar
     */
    private void stopTask()
    {
        if (task != null)
            task.stopTask();
        removeMouseListener(mouseListener);
    }
}
