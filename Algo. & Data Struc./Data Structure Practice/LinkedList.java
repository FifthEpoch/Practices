package lists;

public class LinkedList<E> {
	
	// data fields
	private Node<E> first; // location of first node
	private Node<E> last; // location of last node
	private int		size;

	// constructors
	public LinkedList() {
		first = null;
		last = null;
		size = 0;
	}
	
	// methods
	public boolean add(E item) {
		int oldSize = size;
		Node<E> oldLast = last;
		Node<E> newNode = new Node<E>(oldLast , item);
		last			= newNode;
		
		if (oldLast == null) {
			first = newNode;
		} else {
			oldLast.next = newNode;
		}
		size++;
		return size == oldSize + 1;
	} 
	
	public void clear() {
		Node<E> current = first;
		while (last != null) {
			
		}
	}
	
	public boolean contains(E item) {
		return (indexOf(item) >= 0)? true : false;
	}
	
	public int indexOf(E item) {
		if (first != null) {
			Node<E> current = first;
			int index = 0;
			while (current != null) {
				if (current.data == item) {
					return index;
				} else {
					index ++;
					current = current.next;
				}
			}
		}
		return -1;
	}
	
	public boolean isEmpty() {
		return (size == 0)? true : false;
	}
	
	public int size() {
		return size;
	}
	
	public String toString() {
		if (size == 0) {
			return "[]";
		} else {
			StringBuilder result = new StringBuilder("[" + first.data);
			for(Node<E> x = first.next; x != null; x = x.next) {
				result.append(", ").append(x.data);
			}
			return result.toString();
		}
	}
	
	// nested class
	private static class Node<E> implements Iterator<E> {
		Node<E> prev; // location of the previous node
		Node<E> next; // location of the next node
		E 		data;
		
		public Node(Node<E> prev, E data) {
			this(prev, null, data); //if it's a tail node
		}
		
		public Node(Node<E> prev, Node<E> next, E data) {
			this.prev = prev;
			this.next = next;
			this.data = data;
		}

		@Override
		public boolean hasNext() {
			return (next != null)? true : false;
		}

		@Override
		public E next() {
			return (E) next;
		}

		@Override
		public void remove() {
			this.prev.next = this.next;
			this.next.prev = this.prev;
			this.prev = null;
			this.next = null;
			this.data = null;
		}
	}
	
}
