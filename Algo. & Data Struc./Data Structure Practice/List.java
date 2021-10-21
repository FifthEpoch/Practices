package lists;

public interface List<E> {
	
	public boolean add (E element);
	
	public void add (int index, E element);
	
	public void clear ();
	
	public boolean contains (E element);
	
	public boolean equals (List<E> other);
	
	public E get (int index);
	
	public int indexOf (E element);
	
	public boolean isEmpty ();
	
	public Iterator<E> iterator ();
	
	public E remove (int index);
	
	public boolean remove (E element);
	
	public E set (int index, E element);
	
	public int size ();

}
