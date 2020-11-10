import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class UseOfForce {
	
	int level1 = 0; int level2 = 0; int level3 = 0; int OIS = 0;
	
	int firstWatch = 0; int secondWatch = 0; int thirdWatch = 0;
	
	int black = 0; int white = 0; int asian = 0; int hispanic = 0; 
	int nativeAmericans = 0; int pacIsland = 0; int unknownRace = 0;
	
	int totalCount = 0;
	
	int total2and3onBlacks = 0;
	int total2and3onMinority = 0;
	int total2and3onNonWhites = 0;
	
	int daysSinceLast = 0;
	
	

	public static void main(String[] args) throws IOException {
		
		String filePath = "/Users/tingting/Desktop/SPD_Data_Analysis/Excels_For_Java/Use_Of_Force_ALL.xlsx";
		
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
		
		InputStream ExcelFileToRead = new FileInputStream(filePath);
	    XSSFWorkbook  wb = new XSSFWorkbook(ExcelFileToRead);
	    
	    
	}
	
}
