
public class Linear_Search {
	
	public static int linearSearch(int[] array, int num){
		for (int i = array.length - 1; i >= 0; i--) {
			if (array[i] == num) {
				System.out.println(num + " found at index " + i);
				return i;
			}
		}
		System.out.println(num + " not found. ");
		return -1;
	}
	
	public static void main (String[] args) {
		
		int[] testArray = {1,3,5,7,9,11,13,15,17,19,21};
		
		linearSearch(testArray, 15);	// successful search
		linearSearch(testArray, 20);	// unsuccessful search
	}
	
}
