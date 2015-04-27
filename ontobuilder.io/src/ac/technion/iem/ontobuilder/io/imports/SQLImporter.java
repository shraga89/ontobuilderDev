package ac.technion.iem.ontobuilder.io.imports;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import ac.technion.iem.ontobuilder.core.ontology.Attribute;
import ac.technion.iem.ontobuilder.core.ontology.Domain;
import ac.technion.iem.ontobuilder.core.ontology.Ontology;
import ac.technion.iem.ontobuilder.core.ontology.Relationship;
import ac.technion.iem.ontobuilder.core.ontology.Term;
import ac.technion.iem.ontobuilder.io.utils.sqlParser.checkConstraintData;
import ac.technion.iem.ontobuilder.io.utils.sqlParser.parseSQL;
import ac.technion.iem.ontobuilder.io.utils.sqlParser.slqFieldData;
import ac.technion.iem.ontobuilder.io.utils.sqlParser.sqlTableData;
import ac.technion.iem.ontobuilder.io.utils.sqlParser.useSqlParser;




public class SQLImporter implements Importer {
	public static final HashMap<String,String> TypeDomMAP;
	static
    {
		TypeDomMAP = new HashMap<String, String>();
		//--MySql data types
		TypeDomMAP.put("char", "Text");
		/*?????????
		TypeDomMAP.put("enum", ??);
		TypeDomMAP.put("set", ??);//like enum 
		?????????*/
		TypeDomMAP.put("varchar", "Text");
		TypeDomMAP.put("tinytext", "Text");
		TypeDomMAP.put("text", "Text");
		TypeDomMAP.put("blob", "Text");
		TypeDomMAP.put("mediumtext", "Text");
		TypeDomMAP.put("mediumblob", "Text");
		TypeDomMAP.put("longtext", "Text");
		TypeDomMAP.put("longblob", "Text");

		TypeDomMAP.put("tinyint", "Integer");
		TypeDomMAP.put("smallint", "Integer");
		TypeDomMAP.put("mediumint", "Integer");
		TypeDomMAP.put("int", "Integer");
		TypeDomMAP.put("bigint", "Integer"); //big integer
		TypeDomMAP.put("int", "Integer");
		
		TypeDomMAP.put("smallmoney","Integer");
		TypeDomMAP.put("money","Integer");
		TypeDomMAP.put("bit","Integer"); //allows 0,1 or NULL
		TypeDomMAP.put("binary","Integer");
		TypeDomMAP.put("varbinary","Integer");

		
		TypeDomMAP.put("unsignedtinyint", "Positive Integer");
		TypeDomMAP.put("unsignedsmallint", "Positive Integer");
		TypeDomMAP.put("unsignedmediumint", "Positive Integer");
		TypeDomMAP.put("unsignedint", "Positive Integer");
		TypeDomMAP.put("unsignedbigint", "Positive Integer");
		TypeDomMAP.put("unsignedint", "Positive Integer");
		TypeDomMAP.put("float", "Float");
		TypeDomMAP.put("double", "Float");
		TypeDomMAP.put("decimal", "Float"); //a double stored as a string

		TypeDomMAP.put("date", "Date");
		TypeDomMAP.put("time", "Time");
		TypeDomMAP.put("datetime", "Date");
		TypeDomMAP.put("timestamp","Time");
		TypeDomMAP.put("year", "Date");


		//--SQLSERVER DATA TYPES (additionally to mySql)
		TypeDomMAP.put("text", "Text");
		TypeDomMAP.put("nchar", "Text");
		TypeDomMAP.put("nvarchar", "Text");
		TypeDomMAP.put("ntext", "Text");
		TypeDomMAP.put("double", "Float");
		TypeDomMAP.put("numeric","Float");
		TypeDomMAP.put("real","Float");
		
		

		TypeDomMAP.put("datetime2", "Date");
		TypeDomMAP.put("smalldatetime",  "Date");
		TypeDomMAP.put("datetimeoffset",  "Date");//as datetime2 with addition of a time zone offset



		//--ACCSESS data types
		TypeDomMAP.put("memo", "Text");
		TypeDomMAP.put("byte", "Integer");		
		TypeDomMAP.put("long", "Integer");
		TypeDomMAP.put("short", "Integer");
		TypeDomMAP.put("single", "Float");
		TypeDomMAP.put("autonumber", "Integer");
		TypeDomMAP.put("short", "Integer");
		TypeDomMAP.put("yes/no","Boolean");
		TypeDomMAP.put("hiperlink","URL");
		TypeDomMAP.put("currency","Float");
		
    }

	private static Ontology sqlOntology;

	
	@Override
	public Ontology importFile(File schemaFile, File instanceFile)
			throws ImportException, UnsupportedOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	//create classes and terms from parsedTables
	public  Ontology importFile(File txtFile) throws ImportException {
	    // parsedTables - this is an ArrayList which saves for each table it's parsed data
	    ArrayList<sqlTableData> parsedTables = new ArrayList<sqlTableData>();
        /* parsedSQL: 
         * 1. updatedSql (after removing special constraints)
	     * 2. arFkData - saves foreign key constrains for given table
	     * 3. arChkData - saves check constraint data for given table 
	     */
		// create an empty ontology with the name of the file
        sqlOntology = new Ontology(txtFile.getName().substring(0, txtFile.getName().length()-4).toLowerCase());
        sqlOntology.setFile(txtFile);
        sqlOntology.setLight(true);
   
        // read txt file with sql commands
        try(BufferedReader br = new BufferedReader(new FileReader(txtFile))) {
	    	StringBuilder sb = new StringBuilder();
	 	    String line = br.readLine();
 	        while (line != null) {
 	            sb.append(line);
 	            sb.append(System.lineSeparator());
 	            line = br.readLine();
 	        }
 	        sb.toString();
 	        // split the  file by fields' definitions
 	        String readedTxt = sb.toString().replace("/","&").replace("*","&");
 	        readedTxt = readedTxt.replaceAll("(&&)[^&]*(&&)", "");
 	        readedTxt = readedTxt.replace("\r","").replace("\n","").replace("\t"," ").replace("  "," ");
 	        String[] arTablesDefinitions = readedTxt.toLowerCase().split("create");
 	        
 	        /**TABLE --------------------------------------------------------------------------------------------**/
 	        //get data from each table
 	        for (int j = 0; j < arTablesDefinitions.length; j++){
 	        	// parsed Table will save the table's data
 	        	sqlTableData  parsedTable = new sqlTableData();
 	 	        parseSQL updatedObject = new parseSQL() ;
 	        	if (arTablesDefinitions[j]!=null && !arTablesDefinitions[j].isEmpty()){
 	        		arTablesDefinitions[j] = "create"+arTablesDefinitions[j].replace("\r","").replace("\n","").replace("\t"," ").replace("  "," ");
 	        		// parseSql object with "parseSQL" and prepare code for jsqlParser using.
 	        		updatedObject.parseSpecialConstraint(arTablesDefinitions[j]);
 	        		//save foreign key data to table's data
					if (updatedObject.arFkData != null){
						int jj=0;
						while (updatedObject.arFkData != null && jj<updatedObject.arFkData.size()){
							parsedTable.lFKConstr.add(updatedObject.arFkData.get(jj));
							jj++;
						}
					}
					//save check constraint data to table's data

					if (updatedObject.arChkData != null){
						int jj=0;
						while (updatedObject.arChkData != null && jj<updatedObject.arChkData.size()){
							parsedTable.lCheckConstr.add(updatedObject.arChkData.get(jj));
							jj++;
						}
					}
					//save composedConstraint data to the table's data
					if (updatedObject.arrComposedConstr != null){
						int jj=0;
						while (updatedObject.arrComposedConstr != null && jj<updatedObject.arrComposedConstr.size()){
							parsedTable.uniqueConstr.add(updatedObject.arrComposedConstr.get(jj));
							jj++;
						}
					}

 	        		//parse the create table command by jsqlParser(parsing of UPDATESQL after deleting things that jsqParser can't deal with) 
 	        		CCJSqlParserManager pm = new CCJSqlParserManager();   		
 	        		net.sf.jsqlparser.statement.Statement statement = pm.parse(new StringReader(updatedObject.updatedSqlAll));
 	        		if (statement instanceof CreateTable) {
 			   			CreateTable createTableStatement = (CreateTable) statement;
 			   			useSqlParser tablesNamesFinder = new useSqlParser();
 			   			//save table to table's data
 			   			parsedTable.tableName = tablesNamesFinder.getTablesList(createTableStatement);
 			   			System.out.println("parsed table: " +parsedTable.tableName);
 			   			List columnsDefinitionList = tablesNamesFinder.getColumnsList(createTableStatement);

 			   			//get table fields' data
 			   			/**TABLE's FIELDS ------------------------------------------------------------------*/
 			   			for (Iterator iter = columnsDefinitionList.iterator(); iter.hasNext();) {
 			   				slqFieldData fieldData = new slqFieldData();
			   				ColumnDefinition colDef = (ColumnDefinition) iter.next();
 			   				//get filed's name: colDef.getColumnName()
 			   				fieldData.fieldName = colDef.getColumnName().toLowerCase();
 			   				//get filed's dataType: colDef.getColDataType().getDataType()
 			   				fieldData.fieldType = colDef.getColDataType().getDataType().toLowerCase();
 			   				//get filed's specialString: colDef.getColumnSpecStrings()
 			   				//if primary key definition is in the line with filed definition, so it'll be saved at SpecStrings
 			   				if (colDef.getColumnSpecStrings()!=null && !colDef.getColumnSpecStrings().isEmpty()){
 			   					for (int i=0; i<colDef.getColumnSpecStrings().size(); i++){
 			   						if (!colDef.getColumnSpecStrings().get(i).equals("default")){
 			   							if (!colDef.getColumnSpecStrings().get(i).equals("primary")){
 			   								if (colDef.getColumnSpecStrings().get(i).equals("not") ){
	 			   								fieldData.lFieldSpecConstr.add((String) colDef.getColumnSpecStrings().get(i)+" "+colDef.getColumnSpecStrings().get(i+1));
	 			   								i++;
 			   								}
 			   								else{//unique
	 			   								fieldData.lFieldSpecConstr.add((String) colDef.getColumnSpecStrings().get(i));
	 			   								i++;
 			   								}
 			   							}
 			   							else{ // primary key
 			   								parsedTable.primaryKey.add(fieldData.fieldName);
 			   								i++;
 			   							}
 			   						}
 			   						else{ // if default
 			   							fieldData.fieldDefValue = ((String) colDef.getColumnSpecStrings().get(i+1)).replace("'", "").replace(" ",""); 
 			   							i++;
 			   						}
 			   						
 			   					}
 			   						
 			   				}
 			   				//save tables's fields' list
 			   				parsedTable.listFields.add(fieldData);
 			   			}
 			 	        /**TABLE's FIELDS end------------------------------------------------------------------*/
 			   			//get indexes
 			   			//if primary key definition is in other line from filed definition, so it'll be saved at Indexes
 			   			List columnsIndexes = tablesNamesFinder.getIndexes(createTableStatement);
 		   				if (columnsIndexes!=null && !columnsIndexes.isEmpty()){
 		   					String colIndex = "";
 		   					for (int ii=0; ii < columnsIndexes.size(); ii++){
 		   						colIndex = columnsIndexes.get(ii).toString();
 			   					if (colIndex.contains("primary key")){
 			   						colIndex = colIndex.replace("primary key","");
 			   						String[] tempPKFields = colIndex.split(",");
 			   						for (int kk=0; kk < tempPKFields.length ; kk++){
 			   							parsedTable.primaryKey.add(tempPKFields[kk].replace("(","").replace(")","").replace(" ",""));
 			   						}
 			   					}
 		   					}
 		   				}
 		   			}
 	        		parsedTables.add(parsedTable);
 	        	}	//if arFieldDefinitions is not empty
 	        }//arTablesDefinitions
 	        /**TABLE end--------------------------------------------------------------------------------------------*/
 	    }//try**/   

	    catch (IOException e) {
			e.printStackTrace();
		} catch (JSQLParserException e) {
			e.printStackTrace();
		}

        //create classes and terms from parsedTables
        createOntology(parsedTables);
		return sqlOntology;
	}
	
	/**
	 *  
	 * @param tableData  - the all table's data
	 * @param fname - field's name which is tested
	 * @param constrType: pk, fk,uniqueComp
	 * @return: if field's name is a part of composed constraint or not
	 */
	private static boolean isComposed(sqlTableData tableData, String fname,String constrType){
		boolean result = false;
		switch (constrType){
			case "pk":
									// check if primaryKey is composedTerm
									if (tableData.primaryKey.size()>1){
										for (int i=0; i<tableData.primaryKey.size(); i++){
											if (tableData.primaryKey.get(i).contains(fname)){
												result = true;
											}
										}
									}
								break;
			case "fk":
									// check if foreighnKey is composedTerm
									if (tableData.lFKConstr.size()>0){
										//check every FK constraint if it includes the field's name
										for (int i=0; i<tableData.lFKConstr.size(); i++){
											if (tableData.lFKConstr.get(i).colNames.size()>1){
												if (tableData.lFKConstr.get(i).colNames.contains(fname)){
													result = true;
												}
											}
										}
									}
									break;
			case "uniqueComp":
									// check if unique is composedTerm
									if (tableData.uniqueConstr.size()>0){
										//check every Unique constraint if it includes the field's name
										for (int i=0; i<tableData.uniqueConstr.size(); i++){
											if (tableData.uniqueConstr.get(i).filedsName.size()>1){
												if (tableData.uniqueConstr.get(i).filedsName.contains(fname)){
													result = true;
												}
											}
										}
									}
								break;
			default: break;
		}
		return result;
		
		
	}

	private static ArrayList<String> createStringList(String tableName,ArrayList<String> colNamesList){
		/**
		 * function helps save table name and constraint's fields as an Array List of Strings
		 */
		ArrayList<String> tableAndFileds = new ArrayList<String>();
		
		tableAndFileds.add(tableName);
		for (int i = 0; i< colNamesList.size();i++){
			tableAndFileds.add("_"+colNamesList.get(i));
		}
		return tableAndFileds;
		
	}
	
	
	private static ArrayList<Long> createLongList(HashMap<String,Long> mapedData){
		/**
		 * function helps save table name and constraint's fields as an Array List of Strings
		 */
		ArrayList<Long> returnedData = new ArrayList<Long>();
		
		for (int i = 0; i< mapedData.size();i++){
			returnedData.add(mapedData.get(mapedData.get(i)));

		}
		return returnedData;
		
	}
	
	private static String createNameForComposedTerm(ArrayList<String> tableAndColNamesList, String constrType){
		/**
		 * function returns the name of composed term.
		 * The name contains table type of Constraint + tableName + fields' list
		 */
		String compConstrName;
		
		if (constrType!=null && !constrType.isEmpty()){
			compConstrName = constrType+"_";
		}
		else{
			compConstrName = "";	
		}
		
		for (int i = 0; i< tableAndColNamesList.size();i++){
			compConstrName = compConstrName + tableAndColNamesList.get(i);
		}
		return compConstrName;
		
	}

	/**
	 * Function returns all keys for given value
	 * @param map
	 * @param value
	 * @return
	 */
	public static <T, E> Set<T> getKeysByValue(HashMap<T, E> map, E value) {
	    Set<T> keys = new HashSet<T>();
	    for (Entry<T, E> entry : map.entrySet()) {
	        if (value.equals(entry.getValue())) {
	            keys.add(entry.getKey());
	        }
	    }
	    return keys;
	}

	/**
	 * Translate parsed schema to ontology:
	 * 1. Make an OntologyClass object for each simple type
	 * 2. Make term tree from elements by:
	 * 		1. recursively adding complex type elements as subterms
	 * 		2. replacing extension markers with terms from the referenced type
	 * 3. map composed constraints (with constraint which includes more than 1 field( pk,fk,unique).
	 * @param result parsed sql;
	 */
	private static void createOntology(ArrayList<sqlTableData> parsedTables) {		
		/**Next 2 Maps will be used for setting foreign key relation
		* primaryKeyFieldsMap - maps the primaryKey (either simple or complex term) to the term's ID
		*						  Key is the TermId
		*						  Value is the list, when 1st member of the list is a table name and the rest are the fields's names
		* fk_termID_referencedPK - maps the foreignKey term (either simple foreign key or composed one) to the  resourceTerm's ID.
		*						  Key is the resourceTermId_value (value need to be added to the id, because the same term could be referenced to some tables
		*						  Value is the list, when 1st member of the list is referenced table name and the rest are the referenced fields
		*							
		**/

		HashMap<Long,String> primaryKeyFieldsMap = new HashMap<Long,String>();
		HashMap<String,String> fk_termID_referencedPK = new HashMap<String,String>();

		// make Term for each Table AND it's fields
		for (int i = 0; i<parsedTables.size(); i++){
			//map composed terms (with constraint which includes more than 1 field( pk,fk,unique).
			HashMap<Long,String> pk_ComposedTerm_ID_MAP = new HashMap<Long,String>();
			HashMap<Long,String> fk_ComposedSourceTerm_ID_MAP = new HashMap<Long,String>();
			HashMap<Long,String> unique_ComposedTerm_ID_MAP = new HashMap<Long,String>();
 
			Term termTable;
			Term termField;
			try {
				//make term from tableData
				termTable = makeTerm(parsedTables.get(i));
				System.out.println("termTable: " +termTable.getName());
				
				sqlOntology.addTerm(termTable);
				
				Long compPK_ID = null;
				//make term for composed PrimaryKey Term
				if (parsedTables.get(i).primaryKey.size()>1){
					Term compPKterm = makeComposedTerm(termTable,parsedTables.get(i).tableName+"primarykey","primaryKey");
					compPK_ID = compPKterm.getId();
					compPKterm.setDomain(new Domain("ontology.domain.primaryKeyComposed"));
					//pk_ComposedTerm_ID_MAP.put(compPKterm.getName(), compPKterm.getId());
					sqlOntology.addTerm(compPKterm);
				}

				//make term from each table's field
				for (int j=0; j<parsedTables.get(i).listFields.size(); j++){
					// check if field is part of composed PK, then add it as a son to composedPK_term
					if (isComposed(parsedTables.get(i),parsedTables.get(i).listFields.get(j).fieldName,"pk")){
						termField = makeTerm(sqlOntology.getTermByID(compPK_ID), parsedTables.get(i).listFields.get(j));
						sqlOntology.addTerm(termField);
						Attribute attr = new Attribute();
						attr.setName("primarykeycomposed");
						attr.setDomain(new Domain ("ontology.domain.primaryKeyComposed"));
						termField.addAttribute(attr);
						pk_ComposedTerm_ID_MAP.put(termField.getId(),termField.getName());
						primaryKeyFieldsMap.put(termField.getId(),termTable.getName()+"_"+termField.getName());
					}
					else{
						termField = makeTerm(termTable, parsedTables.get(i).listFields.get(j));
					}
					//if term is in the list of Check Constraint, then add this constraint as attribute
					if ( fieldHasCheckConstraint(parsedTables.get(i).lCheckConstr, parsedTables.get(i).listFields.get(j).fieldName)!=-1){
						Attribute attr = new Attribute();
						attr.setName(parsedTables.get(i).lCheckConstr.get(fieldHasCheckConstraint(parsedTables.get(i).lCheckConstr, parsedTables.get(i).listFields.get(j).fieldName)).checkName);
						attr.setDomain(new Domain ("ontology.domain.checkConstr"));
						termField.addAttribute(attr);
					}
					
					// if term is primaryKey (not composed), then add pk indication as attribute
					if(!isComposed(parsedTables.get(i),termField.getName(),"pk") && parsedTables.get(i).primaryKey.contains(termField.getName())){
						Attribute attr = new Attribute();
						attr.setName("primarykey");
						attr.setDomain(new Domain ("ontology.domain.primaryKey"));
						termField.addAttribute(attr);
						primaryKeyFieldsMap.put(termField.getId(), termTable.getName()+"_"+termField.getName());
					}			
					
					//for each table's FK constraint do
					for (int jj = 0; jj < parsedTables.get(i).lFKConstr.size(); jj++){
						// if FK jj contains termFieldName
						if (parsedTables.get(i).lFKConstr.get(jj).colNames.contains(termField.getName())){
							String attrDomain = "ontology.domain.foreignKey";
							String refFieldName = "";
							Attribute attr = new Attribute();
							//if term is part of composedTerm
							if (parsedTables.get(i).lFKConstr.get(jj).colNames.size()>1){
								//create string list from table name and composedFk's fields: tableName_filedsNames
								ArrayList<String> listComposedFKmembers = createStringList(parsedTables.get(i).tableName,parsedTables.get(i).lFKConstr.get(jj).colNames);
								//create attribute's data  for composedForeignKey: 
								//	- name = tableName+filedsNamesInConstraint
								//  - domain = "foreignKeyComposed"+refAction
								attrDomain = attrDomain+"Composed";
								if (parsedTables.get(i).lFKConstr.get(jj).refAction != null){
									attrDomain = attrDomain + parsedTables.get(i).lFKConstr.get(jj).refAction;
								}	
								attr.setName(createNameForComposedTerm(listComposedFKmembers,null));
								attr.setDomain(new Domain(attrDomain));
								fk_ComposedSourceTerm_ID_MAP.put(termField.getId(),termField.getName());
								//find place of term in the composed FK and the referenced field will be at the same place in array "refColNames"
								int jj_i = 0;
								int termFound = 0;
								while ((jj_i<=parsedTables.get(i).lFKConstr.get(jj).colNames.size())&& termFound==0){
									if (parsedTables.get(i).lFKConstr.get(jj).colNames.get(jj_i).contains(termField.getName())){
										refFieldName = parsedTables.get(i).lFKConstr.get(jj).refColNames.get(jj_i);
										termFound = 1;
									}
									else{
										jj_i++;
									}
								}
							}
							else{ //termField is part of simple FK
								attr.setName("foreignkey");
								if (parsedTables.get(i).lFKConstr.get(jj).refAction != null){
									attrDomain = attrDomain + parsedTables.get(i).lFKConstr.get(jj).refAction;
								}	
								attr.setDomain(new Domain(attrDomain));
								refFieldName = parsedTables.get(i).lFKConstr.get(jj).refColNames.get(0);
							}
							fk_termID_referencedPK.put(termField.getId()+"_"+parsedTables.get(i).lFKConstr.get(jj).refTName+"_"+refFieldName,parsedTables.get(i).lFKConstr.get(jj).refTName+"_"+refFieldName);
							
							termField.addAttribute(attr);
						} //if FK contains termFieldName
					}//for each FK jj
					
					//for each table's UNIQUE constraint do
					for (int jj = 0; jj < parsedTables.get(i).uniqueConstr.size(); jj++){
						Attribute attr = new Attribute();
						//if unique constraint contains termFieldName
						if (parsedTables.get(i).uniqueConstr.get(jj).filedsName.contains(termField.getName())){
							// check if constraint is unique
							if (parsedTables.get(i).uniqueConstr.get(jj).filedsName.size()>1){
								//create string list from table name and composedUNIQUE's fields
								ArrayList<String> listComposedUniqueMembers = createStringList(parsedTables.get(i).tableName,parsedTables.get(i).uniqueConstr.get(jj).filedsName);
									//create attribute's data  for composedUnique: 
									//	- name = tableName+filedsNamesInConstraint
									//  - domain = "uniqueComposed"
								 	attr.setName(createNameForComposedTerm(listComposedUniqueMembers,null));
									attr.setDomain(new Domain("ontology.domain.uniqueComposed"));
									unique_ComposedTerm_ID_MAP.put(termField.getId(),termField.getName());
									termField.addAttribute(attr);
								}
						} //if Unique contains termFieldName
					}

					//add another special string (not null, unique which is on the same line with field's definition) as attribute
					for (int k =0; k <parsedTables.get(i).listFields.get(j).lFieldSpecConstr.size(); k++){
						Attribute attr = new Attribute();
						attr.setName(parsedTables.get(i).listFields.get(j).lFieldSpecConstr.get(k));
						attr.setDomain(new Domain ("ontology.domain."+attr.getName()));
						termField.addAttribute(attr);
					}
					
					sqlOntology.addTerm(termField);
				}//end of for "each table's field"
	
				//SET RELATION BETWEEN TERMS IN THE COMPOSED CONSTRAINTS
				//set relation "partOfComposedPK"
				//if PK is composed
				if ( parsedTables.get(i).primaryKey.size()>1){
					//find ID for each  TERM and set relation "partOfComposedPK_tableName_fieldsNames" between each term in PK
					String composedPK_relationName = createNameForComposedTerm(createStringList(parsedTables.get(i).tableName,parsedTables.get(i).primaryKey),"partOfComposedPK");
					for (int jj_i = 0; jj_i<(parsedTables.get(i).primaryKey.size()-1);jj_i++){
						Set<Long> sourceTermID_set = getKeysByValue(pk_ComposedTerm_ID_MAP,parsedTables.get(i).primaryKey.get(jj_i));
						if (sourceTermID_set.iterator().hasNext()){
							Long sourceTermID = sourceTermID_set.iterator().next();
							for (int jj_j = jj_i+1; jj_j<parsedTables.get(i).primaryKey.size();jj_j++){
								Set<Long> termTarget_ID_set = getKeysByValue(pk_ComposedTerm_ID_MAP,parsedTables.get(i).primaryKey.get(jj_j));
								Long termTarget_ID = termTarget_ID_set.iterator().next();
								sqlOntology.getTermByID(sourceTermID).addRelationship(new Relationship(sqlOntology.getTermByID(sourceTermID),composedPK_relationName, sqlOntology.getTermByID(termTarget_ID)));
								sqlOntology.getTermByID(termTarget_ID).addRelationship(new Relationship(sqlOntology.getTermByID(termTarget_ID),composedPK_relationName, sqlOntology.getTermByID(sourceTermID)));
							}	
						}
						else{
							System.out.println("Term's name at composed primary key wasn't found");
						}
						
					}
				}
				//set relation "partOfComposedFK"
				//for each table's FK constraint do
				for (int jj = 0; jj < parsedTables.get(i).lFKConstr.size(); jj++){
					//if FK is composed
					if ( parsedTables.get(i).lFKConstr.get(jj).colNames.size()>1){
						//find ID for each source TERM and set relation "partOfComposedFK_tableName_fieldsNames" between each term in FK
						String composedFK_relationName = createNameForComposedTerm(createStringList(parsedTables.get(i).tableName,parsedTables.get(i).lFKConstr.get(jj).colNames),"partOfComposedFK");
						for (int jj_i = 0; jj_i<(parsedTables.get(i).lFKConstr.get(jj).colNames.size()-1);jj_i++){
							Set<Long> sourceTermID_set = getKeysByValue(fk_ComposedSourceTerm_ID_MAP,parsedTables.get(i).lFKConstr.get(jj).colNames.get(jj_i));
							Long sourceTermID = sourceTermID_set.iterator().next();
							for (int jj_j = jj_i+1; jj_j<parsedTables.get(i).lFKConstr.get(jj).colNames.size();jj_j++){
								Set<Long> termTarget_ID_set = getKeysByValue(fk_ComposedSourceTerm_ID_MAP,parsedTables.get(i).lFKConstr.get(jj).colNames.get(jj_j));
								Long termTarget_ID = termTarget_ID_set.iterator().next();
								sqlOntology.getTermByID(sourceTermID).addRelationship(new Relationship(sqlOntology.getTermByID(sourceTermID),composedFK_relationName, sqlOntology.getTermByID(termTarget_ID)));
								sqlOntology.getTermByID(termTarget_ID).addRelationship(new Relationship(sqlOntology.getTermByID(termTarget_ID),composedFK_relationName, sqlOntology.getTermByID(sourceTermID)));
							}
						}
					}
				}			
				//set relation "partOfComposedUNIQUE"
				//for each table's UNIQUE constraint do
				for (int jj = 0; jj < parsedTables.get(i).uniqueConstr.size(); jj++){
					//if UNIQUE is composed
					if ( parsedTables.get(i).uniqueConstr.get(jj).filedsName.size()>1){
						//find ID for each TERM and set relation "partOfComposedUNIQUE_tableName_fieldsNames" between each term in UNIQUE
						String composedUNIQUE_relationName = createNameForComposedTerm(createStringList(parsedTables.get(i).tableName, parsedTables.get(i).uniqueConstr.get(jj).filedsName),"partOfComposedUNIQUE");
						for (int jj_i = 0; jj_i<(parsedTables.get(i).uniqueConstr.get(jj).filedsName.size()-1);jj_i++){
							Set<Long> sourceTermID_set = getKeysByValue(unique_ComposedTerm_ID_MAP, parsedTables.get(i).uniqueConstr.get(jj).filedsName.get(jj_i));
							Long sourceTermID = sourceTermID_set.iterator().next();
							for (int jj_j = jj_i+1; jj_j<parsedTables.get(i).uniqueConstr.get(jj).filedsName.size();jj_j++){
								Set<Long> termTarget_ID_set = getKeysByValue(unique_ComposedTerm_ID_MAP, parsedTables.get(i).uniqueConstr.get(jj).filedsName.get(jj_j));
								Long termTarget_ID = termTarget_ID_set.iterator().next();
								sqlOntology.getTermByID(sourceTermID).addRelationship(new Relationship(sqlOntology.getTermByID(sourceTermID),composedUNIQUE_relationName, sqlOntology.getTermByID(termTarget_ID)));
								sqlOntology.getTermByID(termTarget_ID).addRelationship(new Relationship(sqlOntology.getTermByID(termTarget_ID),composedUNIQUE_relationName, sqlOntology.getTermByID(sourceTermID)));
							}
						}
					}
				}
			}//try
			catch (Exception e) {
				e.printStackTrace();
			}
		}//end of for "each table"
	
		//add relationships "referenesTo" && "referencedBy"
		for (int i = 0; i<parsedTables.size(); i++){
			//set foregnKey relationship
			if (parsedTables.get(i).lFKConstr!=null && !parsedTables.get(i).lFKConstr.isEmpty())
			{
				//for each FK in table(i) set ForeignKey relationship for terms
				for (int j = 0; j < parsedTables.get(i).lFKConstr.size(); j++){
					//set relation for each term in fk
					for (int k=0; k<parsedTables.get(i).lFKConstr.get(j).colNames.size();k++){
						//get sourceTermID (terms to which the term is referencedTo)
						Set<String> sourceTermID_set = getKeysByValue(fk_termID_referencedPK, parsedTables.get(i).lFKConstr.get(j).refTName+"_"+parsedTables.get(i).lFKConstr.get(j).refColNames.get(k));primaryKeyFieldsMap.get(parsedTables.get(i).lFKConstr.get(j).refTName+"_"+parsedTables.get(i).lFKConstr.get(j).refColNames.get(k));
						// find TermID for referenced Fields. Referenced field is primary key of other table
						Set<Long> refTermID_set = getKeysByValue(primaryKeyFieldsMap, parsedTables.get(i).lFKConstr.get(j).refTName+"_"+parsedTables.get(i).lFKConstr.get(j).refColNames.get(k));primaryKeyFieldsMap.get(parsedTables.get(i).lFKConstr.get(j).refTName+"_"+parsedTables.get(i).lFKConstr.get(j).refColNames.get(k));
						Long sourceTermID;
						String[] tempArrString;
						for (String sourceTermID_String : sourceTermID_set) {
							tempArrString = sourceTermID_String.split("_");
							sourceTermID =  Long.parseLong(tempArrString[0]);  
							for (Long refTermID : refTermID_set) {
								refTermID = refTermID_set.iterator().next();
								//set relationship between two terms
								if (refTermID!=null && sourceTermID!=null){
									sqlOntology.getTermByID(sourceTermID).addRelationship(new Relationship(sqlOntology.getTermByID(sourceTermID),createNameForComposedTerm(createStringList(parsedTables.get(i).tableName,parsedTables.get(i).lFKConstr.get(j).colNames),"")+"_referencesTo", sqlOntology.getTermByID(refTermID)));
									sqlOntology.getTermByID(refTermID).addRelationship(new Relationship( sqlOntology.getTermByID(refTermID),createNameForComposedTerm(createStringList(parsedTables.get(i).tableName,parsedTables.get(i).lFKConstr.get(j).colNames),"")+"_referencedBy",sqlOntology.getTermByID(sourceTermID)));
									fk_termID_referencedPK.remove(sourceTermID);
								}	
							}//for refTermID
						}//for sourceTermID
					}//for each term in fk
				}//for each FK in table (i)
			}
		}
//		
	}

	/**
	 *  
	 * @param lCheckConstr - list of check constraints of the table
	 * @param fieldName
	 * @return function returns the index if the  table's checkConstraint if the field is in the constraint,
	 * otherwise the function will return -1
	 */
	private static int fieldHasCheckConstraint(	List<checkConstraintData> lCheckConstr, String fieldName) {
		
		int position = -1;
		int i = 0;
		 while (position==-1 && i< lCheckConstr.size()){
			 int j = 0;
			 while (position==-1 && j< lCheckConstr.get(i).colNames.size()){
				 if (lCheckConstr.get(i).colNames.get(j).equals(fieldName)){
					 position = i;
				 }
				 j++;
			 }
			 i++;
			 
		 }
		
		return position;
	}


	/**
	 * create new Simple term from table type
	 * arguments: parent (is null) and sqlTableData
	 */
	private static Term makeTerm(sqlTableData table) {

		Term term = new Term(table.tableName);
		term.setSuperClass(sqlOntology.findClass("table"));
		term.setOntology(sqlOntology);

		return term;		
	}
	
	/**
	 * create new Simple term from field
	 * arguments: parent(is Table) and sqlTableData
	 */
	private static Term makeTerm(Term parent, slqFieldData field) {
		
		Term term;
		if (!field.fieldDefValue.isEmpty() && field.fieldDefValue!= null){
			term = new Term(field.fieldName,field.fieldDefValue);
		}
		else{
			term = new Term(field.fieldName);	
		}
		
		//get filed's type and check that it's exists in the TypeDomMap
		String t = field.fieldType;
		String domName = (TypeDomMAP.containsKey(t)?TypeDomMAP.get(t):t);						
		term.setDomain(new Domain(domName));
		term.setSuperClass(sqlOntology.findClass("field"));

		//set relationships 
		if (parent != null){ 
			term.setParent(parent);
			parent.addTerm(term);
		}
		
		
		term.setOntology(sqlOntology);
		return term;
	}

	/**
	 * create new COMPOSED term
	 * arguments: parent(is Table) and sqlTableData
	 */
	private static Term makeComposedTerm(Term parent, String tName, String type) {
		Term term = new Term(tName);
		term.setSuperClass(sqlOntology.findClass(type));
		//set relationships 
		if (parent != null){ 
			term.setParent(parent);
			parent.addTerm(term);
		}
		term.setOntology(sqlOntology);
		return term;
	} 
	
	
}
