import java.util.Arrays;

public class Heap_Sort {
	
	public static void heapSort(int[] array) {
		
		int len = array.length;
		
		for (int i = (len / 2) - 1; i >= 0; i++) {
			makeHeap(array, len, i);
		}
		
		for (int i = len - 1; i > 0; i--) {
			swap(array, 0, i);
			makeHeap(array, i, 0);
		}
	}
	
	public static void makeHeap(int[] array, int len, int index) {
		
		int root  = index;
		int left  = (index * 2) + 1;
		int right = (index * 2) + 2;
		
		// check if left is in bound && if left child is bigger than root
		if (left < len && array[left] > array[root]) {
			root = left;
		}
		
		// check if right is in bound && if right child is bigger than root
		if (right < len && array[right] > array[root]) {
			root = right;
		}
		
		// recursively organize rest of heap
		if (root != index) {
			swap(array, index, root);
			makeHeap(array, len, root);
		}
	}
	
	public static void swap(int[] array, int a, int b) {
		int temp = array[a];
		array[a] = array[b];
		array[b] = temp;
	}

	public static void main (String[] args) {
		
		int[] testArray = {57, 93, 2, 18, 77, 29, 13, 8, -2};
		
		System.out.println("before: " + Arrays.toString(testArray));
		
		heapSort(testArray);
		
		System.out.println("after: " + Arrays.toString(testArray));
	}
	
}
