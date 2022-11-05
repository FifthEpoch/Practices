import java.util.Arrays;
import java.util.Stack;

public class Quicksort {
	
	public static void quicksort(int[] array) {
		
		Stack<Integer> stack = new Stack<Integer>();
		stack.push(0);
		stack.push(array.length);
		
		while (! stack.isEmpty()) {
			int end = (int) stack.pop();
			int start = (int) stack.pop();
			if (end - start < 2) {
				continue;
			}
			int index = start + ((end - start) / 2);
			index = partition(array, index, start, end);
			
			// higher half indexes
			stack.push(index + 1);
			stack.push(end);
			
			// lower half indexes
			stack.push(start);
			stack.push(index);
		}
	}
	
	public static int partition(int[] array, int index, int start, int end) {
		int low = start;
		int high = end - 2;
		int pivot = array[index];
		swap(array, index, end - 1);
		
		while(low < high) {
			if (array[low] < pivot) {
				low++;
			} else if (array[high] >= pivot) {
				high--;
			} else {
				swap(array, low, high);
			}
		}
		int newIndex = high;
		if (array[high] < pivot) {
			newIndex++;
		}
		swap(array, end - 1, newIndex);
		
		return newIndex;
	}
	
	public static void swap(int[] array, int a, int b) {
		int temp = array[a];
		array[a] = array[b];
		array[b] = temp;
	}
	
	public static void main (String[] args) {
		
		int[] testArray = {3,8,23,7,0,46,65,21,16,5,89};
		
		System.out.println("Before: " + Arrays.toString(testArray));
		
		quicksort(testArray);
		
		System.out.println("After : " + Arrays.toString(testArray));
		
		
	}
}
