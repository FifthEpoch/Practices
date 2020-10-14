import java.util.Arrays;

public class Counting_Sort {
	
	public static void countSort(int[] array, int x) {
		
		// counting data in array
		int[] counter = new int[x + 1];
		for (int i : array) {
			counter[i]++;
		}
		
		// sorting 
		int index = 0;
		for (int i = 0; i < counter.length; i++) {
			
			while (counter[i] > 0) {
				
				array[index++] = i;
				counter[i]--;
			}
		}
	}
	
	public static int findMax(int[] array) {
		
		int max = array[0];
		
		for (int i = 1; i < array.length; i++) {
			max = (array[i] > max) ? array[i] : max;
		}
		
		return max;
	}
	
	public static void main (String[] args) {
		
		int[] testArray = {3,8,23,7,0,46,65,21,16,5,89};
		
		System.out.println("Before: " + Arrays.toString(testArray));
		
		countSort(testArray, findMax(testArray));
		
		System.out.println("After : " + Arrays.toString(testArray));
		
	}

}
