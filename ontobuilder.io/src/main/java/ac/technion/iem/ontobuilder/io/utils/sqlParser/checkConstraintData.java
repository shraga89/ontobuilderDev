package ac.technion.iem.ontobuilder.io.utils.sqlParser;
import java.util.ArrayList;
import java.util.List;

public class checkConstraintData {
	public String tName;
	public String checkName;//new
	public ArrayList<String> colNames;
	
	
	
	private  void checkConstraintData(){
		this.tName = "";
		this.checkName =""; //new
		this.colNames = new ArrayList<String>();
	}
	
/*	private void setTName(String tableName){
		this.tName = tableName;
	}
	private void setrefTName(String tableName){
		this.refTName = tableName;
	}	*/
	private void addColName(String columnName){
		this.colNames.add(columnName);
	}
	private String getTName(){
		return this.tName;
	}
}