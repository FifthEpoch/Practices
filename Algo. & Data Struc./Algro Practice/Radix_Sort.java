import java.util.*;

public class Radix_Sort {

	public static void radixSort(int[] array) {
		
		final int RADIX = 10;
		List<Integer>[] bucket = new ArrayList[RADIX];
		
		for (int i = 0; i < bucket.length; i++) {
			bucket[i] = new ArrayList<Integer>();
		}
		
		boolean maxLen = false;
		int temp 	   = -1;
		int placement  = 1;
		while (!maxLen) {
			maxLen = true;
			
			for (Integer i : array) {
				
				temp = i / placement;
				bucket[temp % RADIX].add(i);
				
				if (maxLen && temp > 0) {
					maxLen = false;
				}
			}
			int x = 0;
			for (int a = 0; x < RADIX; a++) {
				for (Integer i : bucket[a]) {
					array[x++] = i;
				}
				bucket[a].clear();
			}
			placement *= RADIX;
		}
	}
	
	public static void main(String[] args) {
		
int[] testArray = {3,8,23,7,0,46,65,21,16,5,89};
		
		System.out.println("Before: " + Arrays.toString(testArray));
		
		radixSort(testArray);
		
		System.out.println("After : " + Arrays.toString(testArray));
		
	}
	
}
