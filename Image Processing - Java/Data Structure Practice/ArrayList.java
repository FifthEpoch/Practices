package lists;

public class ArrayList<E> {
	
	private E[] data;
	private int size;
	
	public static final int DEFAULT_CAPACITY = 10;
	
	
	//constructor
	public ArrayList() {
		this(DEFAULT_CAPACITY);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList(int capacity) {
		data = (E[]) new Object[capacity];
		size = 0;
	}

	public boolean add(E item) {
		int oldSize 	= size;
		ensureCapacity(size + 1);
		data[size++] 	= item;
		
		return size == (oldSize + 1);
	}
	
	@SuppressWarnings("unchecked")
	public void ensureCapacity(int minCapacity) {
		if (minCapacity > data.length) {
			int newLength = Math.max(2 * data.length + 1, minCapacity);
			E[] newArray = (E[])new Object[newLength];
			
			for (int i = 0; i < size; i++) {
				newArray[i] = data[i];
			}
			data = newArray;
		}
	}
	
	public E remove(int index) {
		E value = data[index];
		shiftLeft(index);
		data[size--] = null;
		return value;
	}
	
	public void shiftLeft(int index) {
		for (int i = index; i < size; i++) {
			data[i] = data[i + 1];
		}
	}
	
	public int size() {
		return size;
	}
	
	public String toString() {
		if (size == 0) {
			return "[]";
		} else {
			StringBuilder result;
			result = new StringBuilder("[" + data[0]);
			for(int i = 1; i < size; i++) {
				result.append(", ").append(data[i]);
			}
			result.append("]");
			return result.toString();
		}
	}
}
