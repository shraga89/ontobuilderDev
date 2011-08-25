package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.impl;

import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Edge;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.NegativeCycleInGraphException;
import ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities.Path;

/**
 * <p>Title: Floyd_Warshall_Algorithm</p>
 * <p>Description: Finds all paired min weighted paths in the graph
 *    Assumes no negative cycles in the graph</p>
 * @author Haggai Roitman
 * @version 1.1
 */
public class Floyd_Warshall_Algorithm 
{
  /**represents null*/
  public static final int NIL = -1;
  /**signs that two vertexes in the graph are not connected*/
  public static final double INF = Double.POSITIVE_INFINITY;
  /**weights matrix*/
  private double[][] d;
  /**graph vertexes count*/
  private int vCount;
  /**parent matrix for paths reconstruction*/
  private int[][] pred;
  /**paths reconstruction matrix*/
  private Path[][] pathsMatrix;

  /**
   * Constructs a  Floyd_Warshall_Algorithm
   * @param graph the adj. matrix of the graph on which the algorithm works on
   * @param vertexes num of vertexes in the graph
   */
  public Floyd_Warshall_Algorithm(double [][] graph,int vertexes)
  {
        d = graph;
        vCount = vertexes;
        pred = new int[vCount][vCount];
        pathsMatrix = new Path[vCount][vCount];
  }

  /**
   * Release resources
   */
  public void nullify()
  {
        d = null;
        pred = null;
        pathsMatrix = null;
  }
  
  /**
   * Get the cost matrix of the algorithm
   *
   * @return the cost matrix
   */
  public double[][] getCostMatrix()
  {
  	return d;
  }

  /**
   * Runs the floyd warshall algorithm
   * <br>O(V^3)
   */
  public void runAlgorithm()
  {
    for (int i=0;i<vCount;i++)//pred initialization
      for (int j=0;j<vCount;j++){
    if (i==j || d[i][j] == INF)
      pred[i][j] = NIL;
    else if (i!=j && d[i][j] <INF)
      pred[i][j] = i;
      }
      for (int k=0;k<vCount;k++){//the algorithm runs in O(V^3)
        for (int i=0;i<vCount;i++)
          for (int j=0;j<vCount;j++){
            pred[i][j]/*(k)*/ = (d[i][j]/*(k-1)*/ <= (d[i][k]/*(k-1)*/  + d[k][j]/*(k-1)*/ )) ? pred[i][j]/*(k-1)*/  : pred[k][j]/*(k-1)*/ ;
            d[i][j]/*(k)*/  = (d[i][j]/*(k-1)*/  <= (d[i][k]/*(k-1)*/ +d[k][j]/*(k-1)*/ )) ? d[i][j]/*(k-1)*/  : (d[i][k]/*(k-1)*/ +d[k][j]/*(k-1)*/ );
          }

      }
  }



  /**
   * Reconstructs the minimum path in the graph from source to target
   * <br>O(V)
   * @param source vertex
   * @param target vertex
   * @return the minimum path between source to target
   * @throws NegativeCycleInGraphException the given graph to floyd warshall algorithm included
   *         negative cycle
   */
  public Path reconstructOnePath(int source,int target)throws NegativeCycleInGraphException //O(V)
  {
    int t = target;
    int s = source;
    Path p = new Path(d[s][t],vCount);
    while(pred[s][t] != NIL){
      p.addFollowingEdgeToPath(new Edge(pred[s][t],t,d[pred[s][t]][t],true));
      t = pred[s][t];
    }
    pathsMatrix[source][target] = p;
    return p;
  }

  /**
   * Reconstruct all minimum paths in the graph
   * <br>O(V^3)
   * @throws NegativeCycleInGraphException the given graph to floyd warshall algorithm included
   *         negative cycle
   */
  public void reconstructAllPaths()throws NegativeCycleInGraphException //O(V^3)
  {
    for (int source=0;source<vCount;source++)
      for (int target=0;target<vCount;target++){
    if (source == target) continue;
    int t = target;
    int s = source;
    int lenth = 0;
    Path p = new Path(d[s][t],vCount);
    while(pred[s][t] != NIL){
      lenth++;
      p.addFollowingEdgeToPath(new Edge(pred[s][t],t,d[pred[s][t]][t],true));
      t = pred[s][t];
    }
    pathsMatrix[source][target] = p;
      }
  }

  /**
   * Creates a printable string of the matrix
   * O(V^3)
   * @return matrix
   */
  public String  printFinalMatrixs()
  {
    String result = "";
    result += System.getProperty("line.separator")+System.getProperty("line.separator")+"Min distances matrix:"+System.getProperty("line.separator")+"\n--------------------------\n"+System.getProperty("line.separator");
    for (int i=0;i<vCount;i++){
      for (int j=0;j<vCount;j++)
        result += ((d[i][j] == INF ? "I" : ""+d[i][j])+",");
      result +=  System.getProperty("line.separator")+"\n";
    }
    result += System.getProperty("line.separator")+System.getProperty("line.separator")+"\n\nPred matrix:"+System.getProperty("line.separator")+"\n--------------------------\n"+System.getProperty("line.separator");
    for (int i=0;i<vCount;i++){
      for (int j=0;j<vCount;j++)
        result += ((pred[i][j] == NIL ? "N" : ""+pred[i][j])+",");
      result += System.getProperty("line.separator")+"\n";
    }
    return result;
  }

  /**
   * Creates a printable minimum path from source to target
   * O(V)
   * @param source from which vertex
   * @param target to which vertex
   */
  public String printResult(int source,int target)
  {
    String result = "";
    result += (System.getProperty("line.separator")+"\nshortest path from vertex "+source+" to vertex "+target+": Min path cost:"+d[source-1][target-1]+System.getProperty("line.separator"));
    if (source == target) return "";
    Path p = pathsMatrix[source-1][target-1];
    result += p.printPath();
    return result;
  }
}