package ac.technion.iem.ontobuilder.gui.elements;


/**
 * <p>Title: ProgressTask</p>
 * Extends {@link Runnable}
 */
public interface ProgressTask extends Runnable
{
    public void startTask();

    public void stopTask();

    public void pauseTask();

    public void resumeTask();

    public void setProgressBar(ProgressBar progressBar);
}
