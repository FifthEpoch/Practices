
public class Binary_Search {

	public static int binarySearch(int[] array, int num) {
		int low = 0;
		int high = array.length;
		int mid = (low + high) / 2;
		
		while (low <= high) {
			if (array[mid] == num) {
				return mid;
			} else if (array[mid] < num) {
				low = mid + 1;
			} else {
				high = mid -1;
			}
		}
		return -1;
	}
	
	public static int recursiveBinarySearch(int[] array, int num) {
		int index = recursiveHelper(array, num, 0, array.length - 1);
		return index;
	}
	
	private static int recursiveHelper(int[] array, int num, int low, int high) {
		if (low > high) {
			return -1;
		} 
		
		int mid = (low + high) / 2;
		
		if (array[mid] == num) {
			return mid;
			
		} else if (array[mid] < num) {
			return recursiveHelper(array, num, mid + 1, high);
			
		} else { //array[mid] > num
			return recursiveHelper(array, num, low, mid - 1);
		}
	}
	
	public static void printResult(int num, int index) {
		String msg = (index < 0) ? ("" + num + " not found. ") : ("" + num + " found at index " + index);
		System.out.println(msg);
	}
	
	public static void main (String[] args) {
		
		int[] testArray = {1,3,5,7,9,11,13,15,17,19,21};
		
		int num1 = 17;	// successful search
		printResult(num1, recursiveBinarySearch(testArray, num1));
		printResult(num1, binarySearch(testArray, num1));
		
		int num2 = 8;	// unsuccessful search
		printResult(num1, recursiveBinarySearch(testArray, num2));
		printResult(num1, binarySearch(testArray, num2));
	}
}
