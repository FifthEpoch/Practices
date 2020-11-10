
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Excel_Import {

	public static ArrayList<Integer> readIntColumn(String filePath, int columnIndex) throws IOException
    {
        InputStream ExcelFileToRead = new FileInputStream(filePath);
        XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);

        XSSFWorkbook test = new XSSFWorkbook(); 

        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row; 
        XSSFCell cell;
        ArrayList<Integer> data = new ArrayList<Integer>();
        
        Iterator<?> rows = sheet.rowIterator();

        while (rows.hasNext()){

            row=(XSSFRow) rows.next();
            Iterator<?> cells = row.cellIterator();
            while (cells.hasNext()){
            
                cell=(XSSFCell) cells.next();
                
                if (cell.getColumnIndex() == columnIndex) {
                	
                	data.add((int)cell.getNumericCellValue());  
                }	
            }
        }
        	System.out.println(data);
            return data;
    }
	
	public static ArrayList<String> readStrColumn(String filePath, int columnIndex) throws IOException
    {
        InputStream ExcelFileToRead = new FileInputStream(filePath);
        XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);

        XSSFWorkbook test = new XSSFWorkbook(); 

        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row; 
        XSSFCell cell;
        ArrayList<String> data = new ArrayList<String>();
        
        Iterator<?> rows = sheet.rowIterator();

        while (rows.hasNext()){

            row=(XSSFRow) rows.next();
            Iterator<?> cells = row.cellIterator();
            while (cells.hasNext()){
            
                cell=(XSSFCell) cells.next();
                
                if (cell.getColumnIndex() == columnIndex && row.getRowNum() != 0) {
                	
                	data.add(cell.getStringCellValue());  
                }	
            }
        }
        	System.out.println(data);
            return data;
    }
	
	public static ArrayList<Double> readDoubleColumn(String filePath, int columnIndex) throws IOException
    {
        InputStream ExcelFileToRead = new FileInputStream(filePath);
        XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);

        XSSFWorkbook test = new XSSFWorkbook(); 

        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row; 
        XSSFCell cell;
        ArrayList<Double> data = new ArrayList<Double>();
        
        Iterator<?> rows = sheet.rowIterator();

        while (rows.hasNext()){

            row=(XSSFRow) rows.next();
            Iterator<?> cells = row.cellIterator();
            while (cells.hasNext()){
            
                cell=(XSSFCell) cells.next();
                
                if (cell.getColumnIndex() == columnIndex && row.getRowNum() != 0) {
                	
                	data.add((double)cell.getNumericCellValue());  
                }	
            }
        }
        	System.out.println(data);
            return data;
    }
	
	public static Set<Integer> getUniqueEntries(ArrayList<Integer> a){
		
		Set<Integer> set = new HashSet<Integer>(a);
		set.addAll(a);
		
		System.out.println( "\nUnique entry size: " + set.size());
		System.out.println(set);
		
		return set;
	}
	
	public static void compare2Sets(Set<Integer> a, Set<Integer> b) {
		
		int count = 0;
		for (int n: a) {
			for (int m: b) {
				
				if (m == n) {
					System.out.println("found match: " + m);;
					count++;
				}
			}
		}
		System.out.println("Total matches = " + count);
	}
	
	
	public static HashSet<Integer> countLevelPerSector(String filePath, String precinct, String sector, int shift, int force) throws FileNotFoundException {
		
		InputStream ExcelFileToRead = new FileInputStream(filePath);
	    XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(ExcelFileToRead);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	     XSSFSheet sheet = wb.getSheetAt(0);
	     XSSFRow row; 
	     XSSFCell cell;
	     Set<Integer> id = new HashSet<Integer>();
	     
	     int counter = 0;
	     int totalCount = 0;
	        
	     Iterator<?> rows = sheet.rowIterator();
	     while (rows.hasNext()){
	    	 
	    	 int qualifier = 0;
	    	 boolean isPrecinct = true;
		     boolean isSector = true;
		     boolean isShift = true;
	    	 
	    	 row=(XSSFRow) rows.next();
	         Iterator<?> cells = row.cellIterator();
	         
	         while (cells.hasNext()){
	            
	        	 cell=(XSSFCell) cells.next();
	        	 
	        	 if (cell.getRowIndex() != 0) {
	        		 
	        		 // check precinct
		             if (cell.getColumnIndex() == 5) {
		            	 String str = cell.getStringCellValue();

		            	 if (str.equalsIgnoreCase(precinct)) {
		            		 qualifier += 1;
		            	 } else {
		            		 isPrecinct = false;
		            	 }
		             }
		             
		             // check sector
		             if (cell.getColumnIndex() == 6) {
		            	 String str = cell.getStringCellValue();

		            	 if (str.contains(sector)) {
		            		 qualifier += 1;
		            	 } else {
		            		 isSector = false;
		            	 }
		             }
		             
		             if (cell.getColumnIndex() == 2) {
		            	 
		            	 String str = cell.getStringCellValue();
		            	 
		            	 switch (force) {
		            	 
		            	 case 1: if (str.contains("Level 1 - Use of Force")) {
		            		 		qualifier++;
		            	 				} 
		            	 		break;
		            	 case 2: if (str.contains("Level 2 - Use of Force")) {
	 		 						qualifier++;
	 	 								}
		            	 		break;
		            	 case 3: if (str.contains("Level 3 - Use of Force")) {
		 		 					qualifier++;
		 	 							}
		            	 		break;
		            	 case 4: if (str.contains("Level 3 - OIS")) {
		 		 					qualifier++;
		 	 							}
		            	 }
		             }	 
		             
		             
		             // check if it's within shift
		             if (cell.getColumnIndex() == 4) {
		            	 
		            	 double time = cell.getNumericCellValue();
		            	 
		            	 switch (shift) {
		            	 
		            	 case 1: if (time >= 0.14652777777777778 && time <= 0.4791666666666667) {
		            		 		qualifier++;
		            	 				} else {
		            	 					isShift = false;
		            	 				}
		            	 	break;
		            	 case 2: if (time >= 0.4798611111111111 && time <= 0.8125) {
	 		 						qualifier++;
	 	 								} else {
	 	 									isShift = false;
	 	 								}
		            	 	break;
		            	 case 3: if (time >= 0.8131944444444444 || time <= 0.14583333333333334) {
		 		 					qualifier++;
		 	 							} else {
		 	 								isShift = false;
		 	 							}
		            	 } 
		             }
		             
		             if (cell.getColumnIndex() == 8) {
		            	 
		            	 if (qualifier == 3) {
		                	 counter += 1;
		                	 id.add((int)cell.getNumericCellValue());
		                 }
		            	 if (isPrecinct == true && isSector == true && isShift == true) {
		    	        	 totalCount += 1;
		    	         }
		             }
	        	 } 
	        } 

	     }
	     System.out.println("Precinct: " + precinct);
	     System.out.println("Sector: " + sector);
	     System.out.println("Level of force: all" );
	     System.out.println("Shift: " + shift);
	     System.out.println();
	     System.out.println("Unique officers: " + id.size());
	     System.out.println("Count: " + counter);
	     System.out.println("- - - - - - - - - - - - - -");
	     return (HashSet<Integer>) id;
	}
	
	public static void uniqueOfficerPerPrecinct(String filePath, String precinct) throws FileNotFoundException {
		InputStream ExcelFileToRead = new FileInputStream(filePath);
	    XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(ExcelFileToRead);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	     XSSFSheet sheet = wb.getSheetAt(0);
	     XSSFRow row; 
	     XSSFCell cell;
	     Set<Integer> id = new HashSet<Integer>();
	     
	     int counter = 0;
	     int totalCount = 0;
	        
	     Iterator<?> rows = sheet.rowIterator();
	     while (rows.hasNext()){
	    	 
	    	 row=(XSSFRow) rows.next();
	         Iterator<?> cells = row.cellIterator();
	         
	         boolean isPrecinct = false;
	         
	         while (cells.hasNext()){
	            
	        	 cell=(XSSFCell) cells.next();
	        	 
	        	 if (cell.getRowIndex() != 0) {
	        		 
	        		 // check precinct
		             if (cell.getColumnIndex() == 5) {
		            	 String str = cell.getStringCellValue();

		            	 if (str.equalsIgnoreCase(precinct)) {
		            		 isPrecinct = true;
		            	 } 
		             }
		             if (cell.getColumnIndex() == 8 && isPrecinct == true) {
		            	 id.add((int)cell.getNumericCellValue());
		             }
		         }
		
	        }
	      }
	     System.out.println("Precinct " + precinct + " unique officers: " + id.size());
	 }  
	
	public static void getOfficerInfo(String filePath, int officerID) throws FileNotFoundException {
		
		Map<String, Integer> mapP = new TreeMap<String, Integer>(); 
		mapP.put("East", 0);
		mapP.put("North", 0);
		mapP.put("South", 0);
		mapP.put("Southwest", 0);
		mapP.put("West", 0);
		mapP.put("X", 0);
		
		Map<String, Integer> mapS = new TreeMap<String, Integer>();
		mapS.put("BOY", 0);
		mapS.put("CHARLIE", 0);
		mapS.put("DAVID", 0);
		mapS.put("EDWARD", 0);
		mapS.put("FRANK", 0);
		mapS.put("GEORGE", 0);
		mapS.put("JOHN", 0);
		mapS.put("KING", 0);
		mapS.put("LINCOLN", 0);
		mapS.put("MARY", 0);
		mapS.put("NORA", 0);
		mapS.put("OCEAN", 0);
		mapS.put("QUEEN", 0);
		mapS.put("ROBERT", 0);
		mapS.put("SAM", 0);
		mapS.put("UNION", 0);
		mapS.put("UNKNOWN", 0);
		mapS.put("WILLIAM", 0);
		
		int level1 = 0; int level2 = 0; int level3 = 0; int OIS = 0;
		
		int firstWatch = 0; int secondWatch = 0; int thirdWatch = 0;
		
		int black = 0; int white = 0; int asian = 0; int hispanic = 0; 
		int nativeAmericans = 0; int pacIsland = 0; int unknownRace = 0;
		
		int totalCount = 0;
		
		int total2and3onBlacks = 0;
		int total2and3onMinority = 0;
		int total2and3onNonWhites = 0;
		
		InputStream ExcelFileToRead = new FileInputStream(filePath);
	    XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(ExcelFileToRead);
		} catch (IOException e) {
			System.out.println();
			e.printStackTrace();
		}
		
		XSSFRow row;
	    XSSFSheet sheet = wb.getSheetAt(0);
		Iterator<Row> rows = sheet.rowIterator();
		
	    while (rows.hasNext()){
	    	
	    	row=(XSSFRow) rows.next();
	    	
	    	if (row.getRowNum() != 0) {
	    		
	    		int id = (int)row.getCell(8).getNumericCellValue();
   			 	if (id == officerID) {
   			 		 
	    			 totalCount += 1;
	    			 
	    			 String precinct = row.getCell(5).getStringCellValue();
	    			 String sector = row.getCell(6).getStringCellValue();
	    			 
	    			 mapP.put(precinct, mapP.get(precinct) + 1);
	    			 mapS.put(sector, mapS.get(sector) + 1);
	    			 
	    			 String forceLevel = row.getCell(2).getStringCellValue();
	    			 switch(forceLevel) {
	    			 case "Level 1 - Use of Force": level1++;
	    			 	break;
	    			 case "Level 2 - Use of Force":	level2++;
	    			 	break;
	    			 case "Level 3 - Use of Force": level3++;
	    			 	break;
	    			 case "Level 3 - OIS": OIS++;
	    			 }
	    			 double time = row.getCell(4).getNumericCellValue();
	    			 if (time >= 0.14652777777777778 && time <= 0.4791666666666667) {
	    				 firstWatch += 1;
	    			 } else if (time >= 0.4798611111111111 && time <= 0.8125) {
	    				 secondWatch += 1;
	    			 } else if (time >= 0.8131944444444444 || time <= 0.14583333333333334){
	    				 thirdWatch += 1;
	    			 }
	    			 String race = row.getCell(10).getStringCellValue();
	    			 switch (race) {
	    			 case "Asian": asian++;
	    			 	break;
	    			 case "Black or African American": black++;
	    			 	break;
	    			 case "White": white++;
	    			 	break;
	    			 case "Hispanic or Latino":	hispanic++;
	    			 	break;
	    			 case "American Indian/Alaska Native": nativeAmericans++;
	    			 	break;
	    			 case "Nat Hawaiian/Oth Pac Islander": pacIsland++;
	    			 	break;
	    			 case "Not Specified": unknownRace++;	
	    			 }
	    			 
	    			 if (!race.contains("White")) {
	    				 if (!forceLevel.contains("Level 1")) {
	    					 total2and3onNonWhites++;
	    					 if (!race.contains("Not Specified")) {
	    						 total2and3onMinority++;
	    						 if (race.contains("Black")) {
	    							 total2and3onBlacks++;
	    						 }
	    					 }
	    				 }
	    			 }
	    		 }	 
	    	 }
	    }

		double percentageOfBlacks = (double)black / totalCount; 
		double percentageOfMinority = (double)(black + hispanic + nativeAmericans + pacIsland)/ totalCount; 
		double percentageOfNonWhites = (double)(black + hispanic + nativeAmericans + pacIsland + unknownRace)/ totalCount;
	    
	    System.out.println("Officer ID: " + officerID);
	    System.out.println("Total use-of-force incidents: " + totalCount);
	    System.out.println();
	    System.out.println("Level 1: " + level1);
	    System.out.println("Level 2: " + level2);
	    System.out.println("Level 3: " + level3);
	    System.out.println("OIS: " + OIS);
	    System.out.println();
	    System.out.println("Use-of-force reports during");
	    System.out.println("First watch: " + firstWatch);
	    System.out.println("Second watch: " + secondWatch);
	    System.out.println("Third watch: " + thirdWatch);
	    System.out.println();
	    System.out.println("Use-of-force reports in");
	    System.out.println("East precinct: " + mapP.get("East"));
	    System.out.println("North precinct: " + mapP.get("North"));
	    System.out.println("South precinct: " + mapP.get("South"));
	    System.out.println("Southwest precinct: " + mapP.get("Southwest"));
	    System.out.println("West precinctt: " + mapP.get("West"));
	    System.out.println("Reports with no location data: " + mapP.get("X"));
	    System.out.println();
	    System.out.println("use-of-force involving aa: " + black);
	    System.out.println("use-of-force involving minority: " + (black + hispanic + nativeAmericans + pacIsland));
	    System.out.println("% of aa: " + percentageOfBlacks);
	    System.out.println("% of minorities (excluding aa): " + percentageOfMinority);
	    System.out.println("% of all minorities: " + percentageOfNonWhites);
	    System.out.println();
	    System.out.println("level 2 & 3 on aa: " + total2and3onBlacks);
	    System.out.println("level 2 & 3 on minority (excluding aa): " + total2and3onMinority);
	    System.out.println("level 2 & 3 on all minorities: " + total2and3onNonWhites);
	    
	    }
	
	public static void main(String[] args) throws IOException {
		
		String filePath = "/Users/tingting/Desktop/SPD_Data_Analysis/Excels_For_Java/Use_Of_Force_ALL.xlsx";
		
		String precinct = "West";
		String sector = "QUEEN";

		uniqueOfficerPerPrecinct(filePath, precinct);
		System.out.println();
		
		countLevelPerSector(filePath, precinct, sector, 3, 2);
		System.out.println();

		
	}
	
	
	/* Solution 1:
	 * 
	 * compare2Sets(getUniqueEntries(readIntColumn(file1, index1)),
	 *			getUniqueEntries(readIntColumn(file2, index2)));
	 * 
	 * Solution 2:
	 * 
	 * Set <Integer> set = getUniqueEntries(readIntColumn(file1, index1));
	 * 
	 * Solution 3:
	 * 
	 * String file1 = "/Users/tingting/Desktop/PBData/Excels_For_Java/PFU_ID.xlsx";
	 *	int index1 = 0;
	 *	String file2 = "/Users/tingting/Desktop/PBData/Excels_For_Java/South_UNION_ID_ALL.xlsx";
	 *	int index2 = 0;
	 *	
	 *	try {
	 *		compare2Sets(getUniqueEntries(readIntColumn(file1, index1)),
	 *				 			getUniqueEntries(readIntColumn(file2, index2)));
	 *	} catch (IOException e) {
	 * 		// TODO Auto-generated catch block
	 *		e.printStackTrace();
	 *	}
	 *
	 */

}


