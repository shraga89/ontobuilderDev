package ac.technion.iem.ontobuilder.io.utils.sqlParser;
/**
 * file implements the TableVisitor (in order to use jsqlParser
 */

import java.util.ArrayList;
import java.util.List;


import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.CreateTable;




public class useSqlParser{
		private String table;
		private List<String> columnsDefinition;
		private List<String> columnsIndexes;

		public useSqlParser(){
			
		}
		
		public String getTablesList(CreateTable ctStatement) {
			table = new String();
			table = ctStatement.getTable().getName();
		return table;
		}
				
		@SuppressWarnings("unchecked")
		public List<String> getColumnsList(CreateTable ctStatement){
			columnsDefinition = new ArrayList<String>();
			columnsDefinition = ctStatement.getColumnDefinitions();
			return columnsDefinition;
		}
		
		public List<String> getIndexes(CreateTable ctStatement){
			columnsIndexes = new ArrayList<String>();
			columnsIndexes = ctStatement.getIndexes();
			return columnsIndexes;
		}
	
		public void visit(Table tableName) {
			String tableWholeName = tableName.getWholeTableName();
			table = (tableWholeName);
		}
		


}