package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.utils;

import java.util.Vector;
import java.util.Iterator;
import java.io.Serializable;

public class TopKPlot implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String metaAlgorithmName = "";
    private String xName = "";
    private String yName = "";
    private Vector<Point> graph = new Vector<Point>();

    public TopKPlot(String metaAlgorithmName, String xName, String yName)
    {
        this.metaAlgorithmName = metaAlgorithmName;
        this.xName = xName;
        this.yName = yName;
    }

    public TopKPlot()
    {
    }

    public void addPlot(Point plot)
    {
        graph.add(plot);
    }

    public boolean existKPlot(int k)
    {
        return size() >= k;
    }

    public Point getPlot(int index)
    {
        return (Point) graph.get(index);
    }

    public double getXPlot(int index)
    {
        return getPlot(index).x;
    }

    public int size()
    {
        return graph.size();
    }

    public double[] getXSeries()
    {
        double[] x = new double[size()];
        for (int i = 0; i < x.length; i++)
            x[i] = getXPlot(i);
        return x;
    }

    public double[] getYSeries()
    {
        double[] y = new double[size()];
        for (int i = 0; i < y.length; i++)
            y[i] = getYPlot(i);
        return y;
    }

    public double getYPlot(int index)
    {
        return getPlot(index).y;
    }

    public String algorithmName()
    {
        return metaAlgorithmName;
    }

    public String getGraphString()
    {
        String s = "";
        s += xName + "\t" + yName + "\n";
        Iterator<Point> it = graph.iterator();
        while (it.hasNext())
        {
            Point plot = (Point) it.next();
            s += plot.x + "\t" + plot.y + "\n";
        }
        return s;
    }

    public void printGraph()
    {
        System.out.println("Plot Graph for:" + metaAlgorithmName);
        Iterator<Point> it = graph.iterator();
        while (it.hasNext())
        {
            Point plot = (Point) it.next();
            System.out.println(/* plot.x+"\t"+ */plot.y);
        }
    }
}