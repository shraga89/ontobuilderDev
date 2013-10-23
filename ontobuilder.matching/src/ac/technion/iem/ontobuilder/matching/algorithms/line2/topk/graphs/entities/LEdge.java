package ac.technion.iem.ontobuilder.matching.algorithms.line2.topk.graphs.entities;


/**
 * <p>
 * Title: LEdge
 * 
 * A light directed Edge, represented by (Source (short),Target (short),Weight(Float))
 * 
 * @author Omer Ben-Porat
 */


public class LEdge {
	private short sID;
	
	private short tID;
	private float w;
	public LEdge(short x,short y,float s){
		sID = x;
		tID = y;
		w = s;
	}
	public LEdge(Edge e){
		sID = (short) e.getSourceVertexID();
		tID = (short) e.getTargetVertexID();
		w = (float) e.getEdgeWeight();
	}
	public short getsID() {
		return sID;
	}
	public void setsID(short sID) {
		this.sID = sID;
	}
	public short gettID() {
		return tID;
	}
	public void settID(short tID) {
		this.tID = tID;
	}
	public float getW() {
		return w;
	}
	public void setW(float w) {
		this.w = w;
	}
	public void turnOverLEdge(){
		short t = sID;
		sID = tID;
		tID = t;
	}
	public String toString(){
		return "("+sID+","+tID+","+w+")";
	}
}
