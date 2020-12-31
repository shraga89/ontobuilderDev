package ac.technion.iem.ontobuilder.io.utils.sqlParser;

import java.util.ArrayList;

public class slqFieldData {
	public String fieldName = "";
	public String fieldType = "";
	public String fieldDefValue = "";
	public ArrayList<String> lFieldSpecConstr = new ArrayList<String>(); 
	
	public slqFieldData(){
		this.fieldName = "";
		this.fieldType = "";
		this.fieldDefValue = "";
		this.lFieldSpecConstr = new ArrayList<String>(); 

	}
}
