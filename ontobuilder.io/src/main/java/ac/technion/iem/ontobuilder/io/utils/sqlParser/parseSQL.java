package ac.technion.iem.ontobuilder.io.utils.sqlParser;

import java.util.ArrayList;
import java.util.List;


public class parseSQL {
	/**
	 * next variables save the data after the changes which were made on original sql txt
	 * in order to use jsqlParser (because jsql doesn't supprot foreighnKey and Check constraints.
	 * updatedSqlAll - sql txt after deleting Foreign key,check.
	 * arFkData - saves foreign-key's data
	 * arChkData - saves check-constraint's data
	 */
	public String updatedSqlAll;
	public List<foreignKeyData> arFkData; 
	public List<checkConstraintData> arChkData;
	public ArrayList<composedConstrData> arrComposedConstr;
	
	
	public parseSQL(){
		arFkData = new ArrayList<foreignKeyData>();//saves data about foreign Key
		arChkData = new ArrayList<checkConstraintData>();//saves data for check constrain
		arrComposedConstr  = new ArrayList<composedConstrData>();
	}
	
	public static String concat(String s1, String s2, int commaIndic) {
	    StringBuffer sb = new StringBuffer();
	    sb.append(s1);
	    //if not the end of the query ( ");"), then add ", "
	    if (commaIndic == 1){
	    	sb.append(",");
	    }
	    sb.append(s2);
	   return sb.toString();
	}
	
	public static String containsChars(String str) {
		/**
		 * function used in check constraint parser
		 */
		ArrayList<String> specialChars = new ArrayList<String>();
		specialChars.add("<>");
		specialChars.add("=<");
		specialChars.add(">=");
		specialChars.add(">");
		specialChars.add("=");
		specialChars.add("<");
	    for (int i = 0; i < specialChars.size(); i++) {
	    	if (str.contains(specialChars.get(i))){
	    		return specialChars.get(i);
	    	}
	    }
	    return null;
	}
	
	
	public void parseSpecialConstraint(String sqlAll){

		ArrayList <Object> returnedData = new ArrayList <Object>();
		//sqlAll = "CREATE TABLE Persons(P_Id int NOT NULL , LastName varchar(255) NOT NULL, CHECK( P_Id is not null);";
		sqlAll = sqlAll.toLowerCase();
		//sqlAll = sqlAll.replace("  ", " "); 
		//sqlAll = sqlAll.replace("\t", " ");
		
		// check that string is not empty
				if (sqlAll != null && !sqlAll.isEmpty())
				{
				
					// get start position of FOREIGHN KEY
					String[] arSqlAll; 		//saves splited sqlAll
					String[] arFieldDef;
					//String sFieldDef;		 //saves filed definition from sqlAll
						
					
					// split SQL by "," so each cell will contain the field definition
					arSqlAll = sqlAll.split(",");
					for (int i = 0; i < arSqlAll.length; i++)
					{
						//check that string is not empty
						if (arSqlAll[i] != null && !arSqlAll[i].isEmpty())
						{
							//if arSqlAll[i] includes foreignKey:
							if (arSqlAll[i].contains("foreign key"))
							{
								if (arSqlAll[i].contains(")references")){
									arSqlAll[i] = arSqlAll[i].replace(")references",") references");
								}
								String lastChar = "";
								if ((arSqlAll[i].contains("))"))||(arSqlAll[i].contains(") )"))){
									if ((arSqlAll[i].contains(");"))||(arSqlAll[i].contains(") ;"))){
										lastChar = ");";
										arSqlAll[i] = arSqlAll[i].replace(";","");
										int indx = arSqlAll[i].lastIndexOf(")");
										arSqlAll[i] = arSqlAll[i].substring(0,indx);
									}
									else{
										lastChar = ")";
									}
								}
								//initialize fkData
								foreignKeyData fkData= new foreignKeyData();
								fkData.colNames = new ArrayList<String>();
								fkData.refColNames = new ArrayList<String>();
								fkData.tName = "currentTable";
								fkData.refAction = null;
								/*--------- CASE3 ---------*/
								// CASE3: if arSqlAll[i] includes " col name Foreign Key REFERENCES ..()":
								if (arSqlAll[i].contains("foreign key references"))
								{
									//split field's definition by words
									arFieldDef = arSqlAll[i].split(" ");
									int j=0;
									while ((arFieldDef[j] == null) || (arFieldDef[j].isEmpty())){
										arFieldDef[j]="";
										j++;
									}
									while (!arFieldDef[j].contains("foreign")){
										j++;
									}
									if (arSqlAll[i].contains("create table")){
										int k = 0;
										while (!arFieldDef[k].contains("(")){
											k++;
										}
										String temp = arFieldDef[k].replace("(", "&");
										// if not the only one char "("
										if (temp.length()>1){
											String[] temp2 = temp.split("&");
											//if "create table tName( col"
											if (temp.lastIndexOf("&")+1 == temp.length()){
												fkData.colNames.add(arFieldDef[k+1]);
											}
											//if "create table tName(col"
											else if (temp2.length > 1){
												fkData.colNames.add(temp2[temp2.length-1]);
											}
											//if "create table tName (col"
											else {
												fkData.colNames.add(temp2[0]);
											}
										}
										else{
											fkData.colNames.add(arFieldDef[k+1]);
										}
									}
									else{
											fkData.colNames.add(arFieldDef[j-1]);
									}
																		
									arFieldDef[j] = ""; //foreign
									arFieldDef[j+1] = ""; // key
									arFieldDef[j+2] = ""; // references
									j = j+3;
									// find refTablename and refColName
									//if " refTNAme(..."
									if (arFieldDef[j].contains("(")){
									//if " refTNAme(refCol)"
										if (arFieldDef[j].contains(")")){
											arFieldDef[j] = arFieldDef[j].replace("(", "&");
											arFieldDef[j] = arFieldDef[j].replace(")","&");
											String[] arRefTableField = arFieldDef[j].split("&");
											fkData.refTName = arRefTableField[0];
											//arRefTableField[1] = arRefTableField[1].replace(";","");
											fkData.refColNames.add(arRefTableField[1]);
											arFieldDef[j] = arFieldDef[j].replace(arFieldDef[j],"");
											j++;
										}
										//if " refTNAme(_refCol.."
										else{
											fkData.refTName = arFieldDef[j].replace("(","");
											arFieldDef[j] = "";
											j++;
											//if " refTNAme(_refCol)"
											if (arFieldDef[j].contains(")")){
												arFieldDef[j] = arFieldDef[j].replace(")","");
												fkData.refColNames.add(arFieldDef[j].replace(";",""));
												arFieldDef[j] = "";
											}
											else{
												//if " refTNAme(_refCol_)"
												fkData.refColNames.add(arFieldDef[j]);
												arFieldDef[j] = "";
											}
											j++;
										}
									}
									//if " refTNAme_(..."
									else{
										fkData.refTName = arFieldDef[j];
										arFieldDef[j] = "";
										j++;
										//find referenced columns
										if ((arFieldDef[j].contains("("))&&(arFieldDef[j].contains(")"))){
											arFieldDef[j] = arFieldDef[j].replace("(", "");
											arFieldDef[j]=arFieldDef[j].replace(")","");
											arFieldDef[j]=arFieldDef[j].replace(";","");
											fkData.refColNames.add(arFieldDef[j]);
											arFieldDef[j]="";
											j++;
										}
										else{
											if (arFieldDef[j].contains("(")){
												arFieldDef[j]=arFieldDef[j].replace("(","");
												j++;
											}
											if (arFieldDef[j].contains(" ")){
												arFieldDef[j]=arFieldDef[j].replace(" ","");
												j++;
											}
											if (!(arFieldDef[j]== "")||(arFieldDef[j]== " ")){
												if (arFieldDef[j].contains(")")){
													fkData.refColNames.add(arFieldDef[j].replace(")", ""));	
												}		
												else{
													fkData.refColNames.add(arFieldDef[j]);
													if (j+1 < arFieldDef.length){
														arFieldDef[j+1] = arFieldDef[j+1].replace(")","");
													}
												}
												arFieldDef[j] = "";
											}
										}
									}
									if (arSqlAll[i].contains("on delete") || arSqlAll[i].contains("on update")){
										if (arSqlAll[i].contains("on delete cascade")){
											fkData.refAction = "onDeleteCascade";
										}
										if (arSqlAll[i].contains("on delete restrict")){
											fkData.refAction = "onDeleteRestrict";
										}
										if (arSqlAll[i].contains("on delete set defualt")){
											fkData.refAction = "onDeleteSetDefault";
										}
										if (arSqlAll[i].contains("on delete set null")){
											fkData.refAction = "onDeleteSetNull";
										}
										if (arSqlAll[i].contains("on delete no action")){
											fkData.refAction = "onDeleteNoAction";
										}
										if (arSqlAll[i].contains("on update cascade")){
											fkData.refAction = "onUpdateCascade";
										}
										if (arSqlAll[i].contains("on update restrict")){
											fkData.refAction = "onUpdateRestrict";
										}
										if (arSqlAll[i].contains("on update set defualt")){
											fkData.refAction = "onUpdateSetDefault";
										}
										if (arSqlAll[i].contains("on update set null")){
											fkData.refAction = "onUpdateSetNull";
										}
										if (arSqlAll[i].contains("on update no action")){
											fkData.refAction = "onUpdateNoAction";
										}
										while (j < arFieldDef.length){
											arFieldDef[j] = "";
											j++;
										
										}
									}
								}//end CASE3
								/*--------- CASE1 ---------*/
								else if (arSqlAll[i].contains(")")){ 				
									//split field's definition by words
									arFieldDef = arSqlAll[i].split(" ");
									if (arSqlAll[i].contains("on delete") || arSqlAll[i].contains("on update")){
										if (arSqlAll[i].contains("on delete cascade")){
											fkData.refAction = "onDeleteCascade";
										}
										if (arSqlAll[i].contains("on delete restrict")){
											fkData.refAction = "onDeleteRestrict";
										}
										if (arSqlAll[i].contains("on delete set defualt")){
											fkData.refAction = "onDeleteSetDefault";
										}
										if (arSqlAll[i].contains("on delete set null")){
											fkData.refAction = "onDeleteSetNull";
										}
										if (arSqlAll[i].contains("on delete no action")){
											fkData.refAction = "onDeleteNoAction";
										}
										if (arSqlAll[i].contains("on update cascade")){
											fkData.refAction = "onUpdateCascade";
										}
										if (arSqlAll[i].contains("on update restrict")){
											fkData.refAction = "onUpdateRestrict";
										}
										if (arSqlAll[i].contains("on update set defualt")){
											fkData.refAction = "onUpdateSetDefault";
										}
										if (arSqlAll[i].contains("on update set null")){
											fkData.refAction = "onUpdateSetNull";
										}
										if (arSqlAll[i].contains("on update no action")){
											fkData.refAction = "onUpdateNoAction";
										}
									}
									arSqlAll[i] = "";
									for (int j = 0; j < arFieldDef.length; j++)
									{
										if (arFieldDef[j] != null && !arFieldDef[j].isEmpty()){
											if (arFieldDef[j] == " "){ 
												arFieldDef[j]="";
											}
											//if foreign key constraint has name,then delete it
											if (arFieldDef[j].contains("constraint")){
												//delete constraint and it's name
												arFieldDef[j]="";
												arFieldDef[j+1]="";
												j = j+1;
											}
											while ((j<arFieldDef.length) && (arFieldDef[j]==null || arFieldDef[j].isEmpty())){
												j++;
											}
											//if word is "foreign"
											if (arFieldDef[j].contains("foreign")){
												//delete string "foreign key"
												arFieldDef[j] = "";
												arFieldDef[j+1] = arFieldDef[j+1].replace("key", "");
												//go to the field name
												j = j+1;
												while (arFieldDef[j]==null || arFieldDef[j].isEmpty()){
													j++;
												}
												if ((arFieldDef[j].contains("("))&&(arFieldDef[j].contains(")"))){
													arFieldDef[j] = arFieldDef[j].replace("(", "");
													arFieldDef[j]=arFieldDef[j].replace(")","");
													fkData.colNames.add(arFieldDef[j]);
													arFieldDef[j]=arFieldDef[j].replace(arFieldDef[j],"");
													j++;
												}
												else{
													if (arFieldDef[j].contains("(")){
														arFieldDef[j]=arFieldDef[j].replace("(","");
														if ( arFieldDef[j] == null || arFieldDef[j].isEmpty()){
															j++;	
														}
													}
													if (arFieldDef[j].contains(" ")){
														arFieldDef[j]=arFieldDef[j].replace(" ","");
														//j++;
													}
													
													if (!((arFieldDef[j]== "")||(arFieldDef[j]== " "))){
														if (arFieldDef[j].contains(")")){
															arFieldDef[j] = arFieldDef[j].replace(")","");
														}
														fkData.colNames.add(arFieldDef[j]);
														arFieldDef[j] = arFieldDef[j].replace(arFieldDef[j],"");
														arFieldDef[j+1]=arFieldDef[j+1].replace(")","");
														j++;
													}
													if (arFieldDef[j].contains(")")){
														arFieldDef[j]=arFieldDef[j].replace(")","");
														j++;
													}
												}
											}
										}
										if (arFieldDef[j].contains("references"))
										{
											//delete word "references"
											arFieldDef[j]=arFieldDef[j].replace("references","");
										//	arFieldDef[j+1]=arFieldDef[j+1].replace(" ","");
											// save referenced tableName and Fileds, then delete it from sql
											j = j+1;
											//if " refTNAme(..."
											if (arFieldDef[j].contains("(")){
												//if " refTNAme(refCol)"
												if (arFieldDef[j].contains(")")){
													arFieldDef[j] = arFieldDef[j].replace("(", "&");
													arFieldDef[j] = arFieldDef[j].replace(")","&");
													String[] arRefTableField = arFieldDef[j].split("&");
													fkData.refTName = arRefTableField[0];
													//arRefTableField[1] = arRefTableField[1].replace(";","");
													fkData.refColNames.add(arRefTableField[1]);
													arFieldDef[j] = arFieldDef[j].replace(arFieldDef[j],"");
													j++;
												}
												//if " refTNAme(_refCol.."
												else{
													fkData.refTName = arFieldDef[j].replace("(","");
													arFieldDef[j] = "";
													j++;
													//if " refTNAme(_refCol)"
													if (arFieldDef[j].contains(")")){
														arFieldDef[j] = arFieldDef[j].replace(")","");
														fkData.refColNames.add(arFieldDef[j].replace(";",""));
														arFieldDef[j] = "";
													}
													else{
														//if " refTNAme(_refCol_)"
														fkData.refColNames.add(arFieldDef[j]);
														arFieldDef[j] = "";
													}
													j++;
												}
											}
											//if " refTNAme_(..."
											else{
												fkData.refTName = arFieldDef[j];
												arFieldDef[j] = arFieldDef[j].replace(arFieldDef[j], "");
												j++;
												//find referenced columns
												if ((arFieldDef[j].contains("("))&&(arFieldDef[j].contains(")"))){
													arFieldDef[j] = arFieldDef[j].replace("(", "");
													arFieldDef[j]=arFieldDef[j].replace(")","");
													arFieldDef[j]=arFieldDef[j].replace(";","");
													fkData.refColNames.add(arFieldDef[j]);
													arFieldDef[j]=arFieldDef[j].replace(arFieldDef[j],"");
													j++;
												}
												else{
													if (arFieldDef[j].contains("(")){
														arFieldDef[j] = arFieldDef[j].replace("(","");
														if (arFieldDef[j] == null || arFieldDef[j].isEmpty()){
															j++;
														}
													}
													if (arFieldDef[j].contains(" ")){
														arFieldDef[j]=arFieldDef[j].replace(" ","");
														j++;
													}
													if (!(arFieldDef[j]== "")||(arFieldDef[j]== " ")){
														if (arFieldDef[j].contains(")")){
															fkData.refColNames.add(arFieldDef[j].replace(")", ""));	
														}		
														else{
															fkData.refColNames.add(arFieldDef[j]);
															arFieldDef[j+1]=arFieldDef[j+1].replace(")","");
														}
														arFieldDef[j]=arFieldDef[j].replace(arFieldDef[j],"");
													}
												}
											}
											if (fkData.refAction != null){
												while (j < arFieldDef.length){
													arFieldDef[j] = "";
													j++;
												}
											}
										} //"references"								
									} //for each arFiledDef
								} // if CASE1
								else{ // start case2
									while (!(arSqlAll[i].contains(")")) && i<=arSqlAll.length){
										//split field's definition by words
										arFieldDef = arSqlAll[i].split(" ");
										if (arFieldDef.length>1){
											for (int j = 0; j < arFieldDef.length; j++){
												if (arFieldDef[j] != null && !arFieldDef[j].isEmpty()){
													if (arFieldDef[j] == " "){ 
														arFieldDef[j]="";
													}
													//if foreign key constraint has name,then delete it
													if (arFieldDef[j].contains("constraint")){
														//delete constraint and it's name
														arFieldDef[j]="";
														arFieldDef[j+1]="";
														j = j+1;
													}
													//if word is "foreign"
													if (arFieldDef[j].contains("foreign")){
														//delete string "foreign key"
														arFieldDef[j]="";
														arFieldDef[j+1]=arFieldDef[j+1].replace("key", "");
														//go to the field name
														while (arFieldDef[j] == null || arFieldDef[j].isEmpty()){
															j = j+1;	
														}
														if (arFieldDef[j].contains("(")){
															arFieldDef[j] = arFieldDef[j].replace("(", "");
															if (arFieldDef[j]!= null && !arFieldDef[j].isEmpty()){
																fkData.colNames.add(arFieldDef[j]);
																arFieldDef[j]=arFieldDef[j].replace(arFieldDef[j],"");	
															}
															else{
																fkData.colNames.add(arFieldDef[j+1]);
																arFieldDef[j+1]=arFieldDef[j+1].replace(arFieldDef[j+1],"");
															}
														}
													}
												}
											}
											//i++;
										}
										
										else{
											fkData.colNames.add(arFieldDef[0]);
										}
										arSqlAll[i] = ""; 
										i++;
									} // while not")"
									//split field's definition by words
									int k =0;
									if (arSqlAll[i].contains(")references")){
										arSqlAll[i] = arSqlAll[i].replace(")references",") references");
									}
									arFieldDef = arSqlAll[i].split(" ");
									arSqlAll[i] = "";
									while (arFieldDef[k]==null || arFieldDef[k].isEmpty()){
										k++;
									}
									// if " colName)"
									if (arFieldDef[k].contains(")")){
										arFieldDef[k] = arFieldDef[k].replace(")","");
										fkData.colNames.add(arFieldDef[k]);
										arFieldDef[k] = arFieldDef[k].replace(arFieldDef[k],"");
										k++;
									}
									else{ // if " colName, )"
										fkData.colNames.add(arFieldDef[k]);
										arFieldDef[k] = "";
										arFieldDef[k+1] = "";
										k=k+2;
									}
									// delete REFERNCES
									arFieldDef[k] = "";
									// references "refTableName (.."
									if (!arFieldDef[k+1].contains("(")){
										fkData.refTName = arFieldDef[k+1];
										arFieldDef[k+1] = "";
										arFieldDef[k+2] = arFieldDef[k+2].replace("(", "");
										// references "refTableName (refCol.."
										if (arFieldDef[k+2]!= null && !arFieldDef[k+2].isEmpty()){
											fkData.refColNames.add(arFieldDef[k+2]);
											arFieldDef[k+2] = "";
											//k = k+3;
										}
										else{ // "( refCol"
											arFieldDef[k+2] = "";
											fkData.refColNames.add(arFieldDef[k+3].replace("(",""));
											arFieldDef[k+3] = "";
											//k = k+4;
										}
									}
									// references "refTableName(.."
									else{
										arFieldDef[k+1] = arFieldDef[k+1].replace("(","&");
										String[] arRefTnameCol = arFieldDef[k+1].split("&");
										arFieldDef[k+1] = "";
										fkData.refTName = arRefTnameCol[0];
										if (arRefTnameCol.length>1){
											fkData.refColNames.add(arRefTnameCol[1]);
										}
										else {
											fkData.refColNames.add(arFieldDef[k+2]);
											arFieldDef[k+2] = "";
										}
										//k=k+2;
									}
									i++;
									//get all referenced columns
									while (!(arSqlAll[i].contains(")")) && i<=arSqlAll.length ){
										fkData.refColNames.add(arSqlAll[i]);
										arSqlAll[i] = ""; 
										i++;
									} 
									if (arSqlAll[i].contains(";")){
										lastChar = ");";
									}
									//referential Action: ON DELETE, ON UPDATE
									if (arSqlAll[i].contains("on delete") || arSqlAll[i].contains("on update")){
										if (arSqlAll[i].contains("on delete cascade")){
											fkData.refAction = "onDeleteCascade";
										}
										if (arSqlAll[i].contains("on delete restrict")){
											fkData.refAction = "onDeleteRestrict";
										}
										if (arSqlAll[i].contains("on delete set defualt")){
											fkData.refAction = "onDeleteSetDefault";
										}
										if (arSqlAll[i].contains("on delete set null")){
											fkData.refAction = "onDeleteSetNull";
										}
										if (arSqlAll[i].contains("on delete no action")){
											fkData.refAction = "onDeleteNoAction";
										}
										if (arSqlAll[i].contains("on update cascade")){
											fkData.refAction = "onUpdateCascade";
										}
										if (arSqlAll[i].contains("on update restrict")){
											fkData.refAction = "onUpdateRestrict";
										}
										if (arSqlAll[i].contains("on update set defualt")){
											fkData.refAction = "onUpdateSetDefault";
										}
										if (arSqlAll[i].contains("on update set null")){
											fkData.refAction = "onUpdateSetNull";
										}
										if (arSqlAll[i].contains("on update no action")){
											fkData.refAction = "onUpdateNoAction";
										}
										arSqlAll[i] = arSqlAll[i].replace(")", "");
										arSqlAll[i] = arSqlAll[i].replace(";", "");
										String [] temp = arSqlAll[i].split(" ");
										k = 0;
										while (temp[k] == null ||temp[k] == " " || temp[k].isEmpty()){
											k++;
										}
										fkData.refColNames.add(temp[k]);
										arSqlAll[i] = ""; 
									}
									else{									
										arSqlAll[i] = arSqlAll[i].replace(")", "");
										arSqlAll[i] = arSqlAll[i].replace(";", "");
										if (arSqlAll[i] != null && !arSqlAll[i].isEmpty()){
											fkData.refColNames.add(arSqlAll[i]);
											arSqlAll[i] = ""; 
										}
										else{
											fkData.refColNames.add(arSqlAll[i+1]);
										}
									}
								}// else is CASE2	
								arFkData.add(fkData);
								arSqlAll[i] = "";
								for (int j= 0; j < arFieldDef.length; j++){
									if (arFieldDef[j] != null && !arFieldDef[j].isEmpty()){
										arSqlAll[i] = concat(arSqlAll[i]," "+arFieldDef[j],0);
									}
								}
								if( lastChar!=null){
									arSqlAll[i] = concat(arSqlAll[i],lastChar,0);
								}
							}//filed definition with Foreign Key or references
					
							// if it is CHECK CONSTRAINT
							
							else if (arSqlAll[i].contains("check (") || arSqlAll[i].contains("check(")){
								checkConstraintData chkConstrData= new checkConstraintData();
								chkConstrData.colNames = new ArrayList<String>();
								chkConstrData.tName = "currentTable";
								chkConstrData.checkName = arSqlAll[i].substring(arSqlAll[i].indexOf("check"),arSqlAll[i].length()); 
								
								
								arFieldDef = arSqlAll[i].split(" ");
								arSqlAll[i] = "";
								int checkDone = 0;
								int j=0;
								int countOpen = 0;
								int countClose = 0;
								while (arFieldDef[j] == null || arFieldDef[j].isEmpty() || arFieldDef[j] == " " || arFieldDef[j] == ""){
									arFieldDef[j] = "";
									j++;
								}
								//if check constraint has name,then delete it
								if (arFieldDef[j].contains("constraint")){
									//delete constraint and it's name
									arFieldDef[j] = "";
									arFieldDef[j+1] = "";
									arFieldDef[j+2] = "";
									j = j+2;
								}
								while (j < arFieldDef.length &&!arFieldDef[j].contains("check")){
									j++;
								}
 								/*if (arFieldDef[j]== null || arFieldDef[j].isEmpty()){
									j++;
								}*/
 								// constrains "check(", so delete it
								if (arFieldDef[j].contains("check(")){
									countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();									arFieldDef[j] = arFieldDef[j].replace("check(","");
									// if arfieldDef[j] contained "check(" without name
								}//end of "check("
								// "check (.."
								else{
									arFieldDef[j]= "" ; //delete "check"
								
								}// end of "check ("
								
								//get the fields' names and  all constraints
								while (j<arFieldDef.length){
									if (arFieldDef[j]==null ||arFieldDef[j].isEmpty()){
										j++;
									}
									else{
										if(!(arFieldDef[j].replace("(", "").replace(")", "").equals("or")) & !(arFieldDef[j].replace("(", "").replace(")", "").equals("and")) & !(arFieldDef[j].replace("(", "").replace(")", "").equals("in")) & !(arFieldDef[j].replace("(", "").replace(")", "").equals("between")) & !(arFieldDef[j].replace("(", "").replace(")", "").equals("not")) & !(arFieldDef[j].replace("(", "").replace(")", "").equals("is")) & !(arFieldDef[j].replace("(", "").replace(")", "").equals("like"))){
											//if arFieldData doesn't contains the constraint,but only the field's name
											if (containsChars(arFieldDef[j]) == null){
												countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();
												if (!chkConstrData.colNames.contains(arFieldDef[j].replace("(", ""))){
													chkConstrData.colNames.add(arFieldDef[j].replace("(", ""));	
												}
												arFieldDef[j] = "";
											}
											//field and one of the special conditions are together
											else{
												countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();
												countClose = countClose + arFieldDef[j].length() - arFieldDef[j].replace(")", "").length();
												arFieldDef[j] = arFieldDef[j].replace("(", "").replace(")","").replace(" ","");
												String[] tempArrStr = arFieldDef[j].split(containsChars(arFieldDef[j]));
												//if the special constraint in separate place from field name or condition's right side
												if (tempArrStr.length<2){
													//if condition is together with field's name
													System.out.println(tempArrStr.length);
													if (tempArrStr.length!=0){
														if(arFieldDef[j].indexOf(tempArrStr[0]) < arFieldDef[j].indexOf(containsChars(arFieldDef[j]))){
															if (!chkConstrData.colNames.contains(tempArrStr[0])){
																chkConstrData.colNames.add(tempArrStr[0]);	
															}
															arFieldDef[j] = "";
															//delete condition's right side
															arFieldDef[j+1] = "";
														}
														// there is only condition and right side
														else{
															arFieldDef[j] = "";
														}
													}		 
													else{
														arFieldDef[j] = "";
														arFieldDef[j+1] = "";
													}
												}
												else{
													if (!chkConstrData.colNames.contains(tempArrStr[0])){
														chkConstrData.colNames.add(tempArrStr[0]);	
													}
													arFieldDef[j] = "";
												}
											}
										}
										// conatains AND, OR, IN or BETWEEN
										else{
											if((arFieldDef[j].replace("(", "").replace(")", "").equals("or"))){
												countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();
												countClose = countClose + arFieldDef[j].length() - arFieldDef[j].replace(")", "").length();
												arFieldDef[j] = arFieldDef[j].replace("(", "").replace(")", "").replace("or", "");
											}
											else if((arFieldDef[j].replace("(", "").replace(")", "").equals("and"))){
												countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();
												countClose = countClose + arFieldDef[j].length() - arFieldDef[j].replace(")", "").length();
												arFieldDef[j] = arFieldDef[j].replace("(", "").replace(")", "").replace("and", "");
											}
											else if (arFieldDef[j].replace("(", "").replace(")", "").equals("not")){
												countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();
												countClose = countClose + arFieldDef[j].length() - arFieldDef[j].replace(")", "").length();
												arFieldDef[j] = "";
											}
											else if(arFieldDef[j].replace("(", "").replace(")", "").equals("between")){
												countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();
												countClose = countClose + arFieldDef[j].length() - arFieldDef[j].replace(")", "").length();
												arFieldDef[j] = "";
												countOpen = countOpen + arFieldDef[j+1].length() - arFieldDef[j+1].replace("(", "").length();
												countClose = countClose + arFieldDef[j+1].length() - arFieldDef[j+1].replace(")", "").length();
												arFieldDef[j+1] = "";
												countOpen = countOpen + arFieldDef[j+2].length() - arFieldDef[j+2].replace("(", "").length();
												countClose = countClose + arFieldDef[j+2].length() - arFieldDef[j+2].replace(")", "").length();
												arFieldDef[j+2] = "";
												countOpen = countOpen + arFieldDef[j+3].length() - arFieldDef[j+3].replace("(", "").length();
												countClose = countClose + arFieldDef[j+3].length() - arFieldDef[j+3].replace(")", "").length();
												arFieldDef[j+3] = "";
											}
											else if((arFieldDef[j].replace("(", "").replace(")", "").equals("in"))){
												countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();
												countClose = countClose + arFieldDef[j].length() - arFieldDef[j].replace(")", "").length();
												arFieldDef[j]="";
												countOpen = countOpen + arFieldDef[j+1].length() - arFieldDef[j+1].replace("(", "").length();
												countClose = countClose + arFieldDef[j+1].length() - arFieldDef[j+1].replace(")", "").length();
												arFieldDef[j+1] = "";
												i++;
												while (!arSqlAll[i].contains(")")){
													chkConstrData.checkName = chkConstrData.checkName + arSqlAll[i];
													arSqlAll[i] = "";
													i++;
												}
												chkConstrData.checkName = chkConstrData.checkName + arSqlAll[i];
												arSqlAll[i] = "";
												i++;
												if (i<arSqlAll.length){
													j=0;
													arFieldDef = arSqlAll[i].split(" ");	
												}
											}
											else if((arFieldDef[j].replace("(", "").replace(")", "").equals("is"))){
												countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();
												countClose = countClose + arFieldDef[j].length() - arFieldDef[j].replace(")", "").length();
												arFieldDef[j] = "";
												if (!arFieldDef[j+1].replace("(", "").replace(")", "").equals("not")){
													countOpen = countOpen + arFieldDef[j+1].length() - arFieldDef[j+1].replace("(", "").length();
													countClose = countClose + arFieldDef[j+1].length() - arFieldDef[j+1].replace(")", "").length();
													arFieldDef[j+1] = "";
												}
											}
											else if((arFieldDef[j].replace("(", "").replace(")", "").equals("like"))){
												countOpen = countOpen + arFieldDef[j].length() - arFieldDef[j].replace("(", "").length();
												countClose = countClose + arFieldDef[j].length() - arFieldDef[j].replace(")", "").length();
												arFieldDef[j] = "";
												i++;
												while (!arSqlAll[i].contains(")")){
													chkConstrData.checkName = chkConstrData.checkName + arSqlAll[i];
													arSqlAll[i] = "";
													i++;
												}
												chkConstrData.checkName = chkConstrData.checkName + arSqlAll[i];
												arSqlAll[i] = "";
												i++;
												if (i<arSqlAll.length){
													j=0;
													arFieldDef = arSqlAll[i].split(" ");	
												}
											}
											
										}
									} //arrFieldDef not empty
								}//while not checkDone
								if (chkConstrData.checkName.contains(");")){
									chkConstrData.checkName =chkConstrData.checkName.substring(0, chkConstrData.checkName.length()-2); 
								}
								arChkData.add(chkConstrData);
								
								if (i < arSqlAll.length){
									arSqlAll[i] = "";
								}
								for (int m= 0; m < arFieldDef.length; m++){
									if (arFieldDef[m] != null && !arFieldDef[m].isEmpty()){
										arSqlAll[i] = concat(arSqlAll[i]," "+arFieldDef[m],0);
									}
								}
							} // end of CHECK CONSTRAINT
								/** OLD
								checkConstraintData chkConstrData= new checkConstraintData();
								chkConstrData.colNames = new ArrayList<String>();
								chkConstrData.rightPredicate = new ArrayList<String>();
								chkConstrData.tName = "currentTable";
								
								arFieldDef = arSqlAll[i].split(" ");
								arSqlAll[i] = "";
								int flagRightPredicate = 0;
								int j=0;
								while( (j < arFieldDef.length) && (flagRightPredicate == 0)){
									while (arFieldDef[j] == null || arFieldDef[j].isEmpty() || arFieldDef[j] == " " || arFieldDef[j] == ""){
										arFieldDef[j] = "";
										j++;
									}
				
									//if check constraint has name,then delete it
									if (arFieldDef[j].contains("constraint")){
										//delete constraint and it's name
										arFieldDef[j] = "";
										arFieldDef[j+1] = "";
										arFieldDef[j+2] = "";
										j = j+2;
									}
									while (j < arFieldDef.length &&!arFieldDef[j].contains("check")){
										j++;
									}
 									if (arFieldDef[j]== null || arFieldDef[j].isEmpty()){
										j++;
									}
 									// constrains "check(", so delete it
									if (arFieldDef[j].contains("check(")){
										arFieldDef[j] = arFieldDef[j].replace("check(","");
										// if arfieldDef[j] contained "check(" without name
										if (arFieldDef[j]==null || arFieldDef[j].isEmpty()){
											j++;
										}
									}//end of "check("
									// "check (.."
									else{
										arFieldDef[j]= "" ; //delete "check"
										j++;
										arFieldDef[j] = arFieldDef[j].replace("(","") ;
										if( arFieldDef[j] == null|| arFieldDef[j].isEmpty()){
											j++;
										}
									}// end of "check ("
									//get the filed name and  arFieldData doesn't contains the constraint
									int flag_ColName = 0;
									if (containsChars(arFieldDef[j]) == null){
										chkConstrData.colNames.add(arFieldDef[j].replace("(", ""));
										arFieldDef[j] = "";
										j++;
										flag_ColName = 1;
									}
									//	the field name and constraint is together
									// here should check if there is between, in or special char
									
										if (containsChars(arFieldDef[j])==null){
											if (flag_ColName == 0){
												chkConstrData.colNames.add( arFieldDef[j]);
												flag_ColName = 1;
												j++;	
											}
											// does condition include BETWEEN?
											if(arFieldDef[j].contains("between")){
												chkConstrData.condition = "between";
												arFieldDef[j] = "";
												j++;
												while (j < arFieldDef.length){
													if (arFieldDef[j].contains("and")){
														arFieldDef[j] = "";
													}
													else{
														chkConstrData.rightPredicate.add(arFieldDef[j].replace(")", "").replace(";",""));
														arFieldDef[j] = "";
														flagRightPredicate = 1;
													}
													j++;
												}
												
											} // end if "between"
											// does condition include IN?
											else if(arFieldDef[j].contains("in")){
												chkConstrData.condition = "in";
												arFieldDef[j] = arFieldDef[j].replace("in(", "");
												if (arFieldDef[j] == null || arFieldDef[j].isEmpty()){
													j++;
												}
												if (arFieldDef[j].contains(")")){
													chkConstrData.rightPredicate.add(arFieldDef[j].replace("(", "").replace(")", "").replace(";",""));
													arFieldDef[j] = "";
												}
												// in (a,b,c...)
												else{
													chkConstrData.rightPredicate.add(arFieldDef[j].replace("(", ""));
													arFieldDef[j] = "";
													int flag_FoundEnd = 0;
													arSqlAll[i] = "";
													while ((i < arSqlAll.length) || (flag_FoundEnd == 0)){
														if(arSqlAll[i].contains(")")){
															flag_FoundEnd = 1;
															if (arSqlAll[i] != null && !arSqlAll[i].isEmpty()){
																chkConstrData.rightPredicate.add(arSqlAll[i].replace(")", "").replace(";",""));
																arFieldDef[j] = "";
															}
															arSqlAll[i] = "";
															flagRightPredicate = 1;
														}
														else{
															if (arSqlAll[i] != null && !arSqlAll[i].isEmpty()){
																chkConstrData.rightPredicate.add(arSqlAll[i]);
																arSqlAll[i] = "";	
															}
														}
														i++;
													}
														
												}
												
											} // end if "in"
											// does condition include IS NOT NULL?
											else if(arFieldDef[j].contains("is")){
												chkConstrData.condition = "IS NOT NULL";
												arFieldDef[j] = "";
												arFieldDef[j+1] = "";
												arFieldDef[j+2] = "";
												j = j+2;
												flagRightPredicate = 1;
											}
											// does condition include LIKE?
											else if(arFieldDef[j].contains("like")){
												chkConstrData.condition = "like";
												arFieldDef[j] = "";
												j++;
												while (j < arFieldDef[j].length()){
													chkConstrData.rightPredicate.add(arFieldDef[j].replace("'", "").replace(")", "").replace(";",""));
													j++;
												}
												flagRightPredicate = 1;
											} // end if "like"
											
										}
										else{
												
											//get the condition
											chkConstrData.condition = containsChars(arFieldDef[j]);
											//	split by special one of special chars
											String[] arCheckFieldNameCondition = arFieldDef[j].split(chkConstrData.condition);
											arFieldDef[j] = "";
											if (arCheckFieldNameCondition != null && arCheckFieldNameCondition.length > 0){
												//first word is the colName
												int k = 0;
												while ((k<arCheckFieldNameCondition.length) && (arCheckFieldNameCondition[k] == null || arCheckFieldNameCondition[k].isEmpty())){
													k++;
												}
												if (flag_ColName == 0){
													chkConstrData.colNames.add(arCheckFieldNameCondition[k]);
													flag_ColName = 1;
													k++;
												}
												if(arCheckFieldNameCondition.length-1 == k){
													arCheckFieldNameCondition [k] = arCheckFieldNameCondition[k].replace(" ", "");
													if (arCheckFieldNameCondition[k]!=null && !arCheckFieldNameCondition[k].isEmpty()){
														chkConstrData.rightPredicate.add(arCheckFieldNameCondition[k].replace(")", "").replace(";",""));
														flagRightPredicate = 1;
													}
													else{
														chkConstrData.rightPredicate.add(arCheckFieldNameCondition[k].replace(")", "").replace(";",""));
														flagRightPredicate = 1;
														arFieldDef[j] = "";
													}
												}
												else{
													chkConstrData.rightPredicate.add(arCheckFieldNameCondition[k].replace(")", "").replace(";",""));
													flagRightPredicate = 1;
													arFieldDef[j] = "";
													j++;
												}
											}
											else{
												j++;
												chkConstrData.rightPredicate.add(arFieldDef[j].replace(")", "").replace(";",""));
												flagRightPredicate = 1;
												arFieldDef[j] = "";
												j++;
											}
										}
								}//end of for arFielddef
								arChkData.add(chkConstrData);
								if (i < arSqlAll.length){
									arSqlAll[i] = "";
								}
								for (int m= 0; m < arFieldDef.length; m++){
									if (arFieldDef[m] != null && !arFieldDef[m].isEmpty()){
										arSqlAll[i] = concat(arSqlAll[i]," "+arFieldDef[m],0);
									}
								}
							} // end of CHECK CONSTRAINT
							**/
	//------------------------------------------------------------------------------------------------------------------------------------------						
							
							/* 
							 * COMPOSED CONSTRAINT
							 */
							else if (arSqlAll[i].contains("unique")){
								boolean flag_EndComposedConstr = false;
								arFieldDef = arSqlAll[i].split(" ");
								if (arFieldDef[0].contains("constraint")|| arFieldDef[0].contains("unique")){
								// named constraint
								/* constraint cNAme Unique(f1,
								 */
									composedConstrData composedCData = new composedConstrData();
									int ii = 0;
									if (arFieldDef[ii].contains("constraint")){
										arFieldDef[ii].replace("constraint", "");
										arFieldDef[ii+1] ="";
										ii=2;
									}
									// if constrName(f_name,..
								
									if (arFieldDef[ii].contains("(")){
										String[] tempString = arFieldDef[ii].replace("(","&").split("&");
										if (tempString.length == 2){
											//String[] tempString = arFieldDef[ii].split("(");
											arFieldDef[ii]="";
											composedCData.constrName = tempString[0];
											flag_EndComposedConstr = tempString[1].contains(")");
											composedCData.filedsName.add(tempString[1].replace(")",""));
										}
									}
									if (composedCData.constrName.isEmpty() || composedCData.constrName==null){
										composedCData.constrName = arFieldDef[ii].replace("(", "");
										arFieldDef[ii]="";
									}
									ii = ii+1;
									arFieldDef[ii] = arFieldDef[ii].replace("(","");
									while (arFieldDef[ii].isEmpty() || arFieldDef[ii] == null){
										arFieldDef[ii] = arFieldDef[ii].replace("(","");
										ii++;
									}
									flag_EndComposedConstr = arFieldDef[ii].contains(")");
									composedCData.filedsName.add(arFieldDef[ii].replace(")",""));
									arSqlAll[i]="";
									//find other fields in composed constraint
									while (!flag_EndComposedConstr){
										i++;
										flag_EndComposedConstr = arSqlAll[i].contains(")");
										composedCData.filedsName.add(arSqlAll[i].replace(")","").replace(" ", "").replace(";",""));
										arSqlAll[i]="";
									}
									arrComposedConstr.add(composedCData);
								}
							}

						} //if arSqlAll[i]] not empty
					}// for arSqlAll[i]]
					updatedSqlAll = arSqlAll[0];
					for (int i = 1; i < arSqlAll.length; i++){
						if (arSqlAll[i] != null && !arSqlAll[i].isEmpty()){
							updatedSqlAll = concat(updatedSqlAll,arSqlAll[i],1);
						}
					}
					
					if (!updatedSqlAll.replace(" ", "").contains(");")){
						String[] temp = updatedSqlAll.split(" ");
						int countOpen = 0;
						int countClose = 0;
						for (int i=0; i< temp.length; i++){
							if (temp[i].contains("(")) { countOpen++;}
							if (temp[i].contains(")")){
								String word = temp[i]; 
								for (int j=0; j< word.length(); j++){
									if (word.charAt(j) == ')'){
										countClose++;
									}
								}
							}
						}
						if (countOpen > countClose){
							updatedSqlAll = updatedSqlAll + ");";
						}
						else{
							updatedSqlAll = updatedSqlAll + ";";
						}
					}
					
					if (updatedSqlAll.contains(",)") || updatedSqlAll.contains(", )")){
						String p1;
						p1 = updatedSqlAll.substring(0,updatedSqlAll.lastIndexOf(","));
						updatedSqlAll = p1 +  updatedSqlAll.substring(updatedSqlAll.lastIndexOf(",")+1,updatedSqlAll.lastIndexOf(";")+1);
					}
				} // if sqlAll not empty 
			}
	
}//CLASS