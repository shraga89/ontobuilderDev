package ac.technion.iem.ontobuilder.io.utils.sqlParser;

import java.util.ArrayList;
import java.util.List;


public class sqlTableData {
	public String tableName;
	public ArrayList <slqFieldData> listFields = new ArrayList<slqFieldData>();
	public ArrayList<String> primaryKey = new ArrayList<String>();
	public List<foreignKeyData> lFKConstr = new ArrayList<foreignKeyData>();;
	public List<checkConstraintData> lCheckConstr = new ArrayList<checkConstraintData>();
	public List<composedConstrData> uniqueConstr = new ArrayList<composedConstrData>();

	public sqlTableData(){
		this.tableName = null;
		this.listFields = new ArrayList<slqFieldData>();
		this.primaryKey = new ArrayList<String>();
		this.lFKConstr = new ArrayList<foreignKeyData>();;
		this.lCheckConstr = new ArrayList<checkConstraintData>();
		this.uniqueConstr = new ArrayList<composedConstrData>();
	}
}
