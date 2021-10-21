import java.util.Arrays;

public class Bubble_Sort {
	
	public static void bubbleSort(int[] array) {
		for (int i = 0; i < array.length; i++) {
			for (int j = array.length - 1; j > i; j--) {
				if (array[j] < array[j - 1]) {
					swap(array, j, j - 1);
				}
			}
		}
		System.out.println("Sorted: \n" + Arrays.toString(array));
	}
	
	public static void swap(int[] array, int smaller, int bigger) {
		int temp = array[smaller];
		array[smaller] = array[bigger];
		array[bigger] = temp;
	}
	
	public static void main (String[] args) {
		
		int[] testArray = {3,8,23,7,0,46,65,21,16,5,89};
		
		bubbleSort(testArray);
	}
}
