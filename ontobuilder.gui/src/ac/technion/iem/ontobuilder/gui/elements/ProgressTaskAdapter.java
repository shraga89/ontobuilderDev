package ac.technion.iem.ontobuilder.gui.elements;


/**
 * <p>Title: ProgressTaskAdapter</p>
 * Extends {@link ProgressTask}
 */
public class ProgressTaskAdapter implements ProgressTask
{
    static final short RUN = 0;
    static final short STOP = 1;
    static final short SUSPEND = 2;
    static final short RESUME = 3;

    protected short condition = RUN;
    protected ProgressBar progressBar;

    public void setProgressBar(ProgressBar progressBar)
    {
        this.progressBar = progressBar;
        progressBar.setTask(this);
    }

    public void startTask()
    {
        if (progressBar != null)
            progressBar.setMinimum(0);
        Thread t = new Thread(this);
        t.start();
    }

    public synchronized void stopTask()
    {
        condition = STOP;
        if (progressBar != null)
        {
            progressBar.setMaximum(0);
            progressBar.setValue(0);
        }
    }

    public synchronized void pauseTask()
    {
        condition = SUSPEND;
    }

    public synchronized void resumeTask()
    {
        condition = RESUME;
    }

    public void run()
    {
    }
}
