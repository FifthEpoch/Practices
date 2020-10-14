import java.util.Arrays;

public class Merge_Sort {
	
	public static void mergeSort(int[] array) {
		
		mergeSort(array, 0, array.length - 1);
		
	}
	
	private static void mergeSort(int[] array, int start, int end) {
		
		int mid = (start + end) / 2;
		if (start < end) {
			mergeSort(array, start, mid);
			mergeSort(array, mid + 1, end);
		}
		
		int i 	   = 0;
		int left   = start;
		int right  = mid + 1;
		int[] temp = new int[end - start + 1];
		
		while (left <= mid && right <= end) {
			temp[i++] = array[left] < array[right] ? array[left++] : array[right++];
		}
		
		// deal with last piece
		while (left <= mid) {
				temp[i++] = array[left++];
		}
		while (right <= end) {
				temp[i++] = array[right++];
		}	
		
		// reset i
		i = 0;
		while (start <= end) {
			array[start++] = temp[i++];
		}
	}

	public static void main (String[] args) {
		
		int[] testArray = {3,8,23,7,0,46,65,21,16,5,89};
		
		System.out.println("Before: " + Arrays.toString(testArray));
		
		mergeSort(testArray);
		
		System.out.println("After : " + Arrays.toString(testArray));
		
	}
	
}
