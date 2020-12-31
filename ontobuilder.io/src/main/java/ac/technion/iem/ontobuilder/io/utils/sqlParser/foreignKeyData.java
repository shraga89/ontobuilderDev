package ac.technion.iem.ontobuilder.io.utils.sqlParser;

import java.util.ArrayList;

public class foreignKeyData {
	public String tName;
	public ArrayList<String> colNames;
	public String refTName;
	public ArrayList<String> refColNames;
	/*public int onDeleteCascade;*/
	public String refAction;
}
