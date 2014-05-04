package ca.ubc.cs.cpsc211.utility;

/**
 * A collection of objects with no object repeated in the collection.
 * 
 * @invariant add(null) is not valid
 * @invariant remove(null) is not valid
 * @invariant size() >= 0
 * 
 * @author Wei(William) You
 * 		   student number:77610095
 * September 25, 2010
 */
public class ArraySet implements Set {
	
	/**
	 * the set that contains objects.
	 */
	private Object[] arraySet;

	/**
	 * construct a new set with initially 10 spaces.
	 */
	public ArraySet(){
		arraySet = new Object[10];
	}
	
	/**
	 * Double the size of the object array.
	 * @pre true
	 * @post arraySet.length = @pre.arraySet.length * 2
	 */
	//helper method for add()
	private void ToDoubleSize(){
		Object[] newArray = new Object[arraySet.length*2];
		for ( int i=0;i<arraySet.length;i++ ){
			newArray[i]=arraySet[i];
		}
		arraySet = newArray;
	}
	
	/**
	 * Adds a given object into the set.
	 * @param newItem The object to be added to the set.
	 * @pre true
	 * @post IF ( newItem !=null AND NOT @pre.contains(newItem) )
	 *       THEN contains(newItem)
	 *       ELSE  the set is not changed
	 * @return true if newItem was added to the set, false otherwise.
	 */
	public boolean add(Object newItem){
		boolean added = false;
		if ( newItem!=null && !contains(newItem) ){
			if ( size()==arraySet.length )
				ToDoubleSize();
			arraySet[size()] = newItem;
			added = true;
		}
		return added;
	}
	
	/**
	 * Check if a given object is in the set.
	 * @param item The object to be checked.
	 * @pre true
	 * @post true
	 * @return true if item is in the set, and false otherwise. 
	 */
	public boolean contains(Object item){
		boolean containItem = false;
		for (int i=0;i<arraySet.length;i++){
			if ( item!=null && item.equals(arraySet[i]) )
				containItem = true;
		}
		return containItem;
	}
	
	/**
	 * Removes a given object from the set.
	 * @param item The object to be removed.
	 * @pre true
	 * @post IF @pre.contains(item) THEN NOT contains(item)
	 *       ELSE  the set is not changed
	 * @return true if item was removed from the set, and false otherwise.
	 */	
	public boolean remove(Object item){
		boolean removed = false;
		if ( contains(item) ){
			for ( int i=0;i<size();i++ ){
				if ( arraySet[i].equals(item) ){
					arraySet[i]=arraySet[size()-1];
					arraySet[size()-1]=null;
				}
			}
			removed = true;
		}
		return removed;
	}
	
	/**
	 * Creates and returns an array with all of the objects in the set. 
	 * @pre true
	 * @post Set is not changed.
	 * @return An unordered array containing all objects in the set.
	 */
	public Object[] toArray(){
		Object[] setArray = new Object[size()];
		for (int i=0;i<setArray.length;i++){
			setArray[i]=arraySet[i];
		}
		return setArray;
	}
	
	/**
	 * Removes all elements from the set.
	 * @pre true
	 * @post size() = 0 
	 */
	public void clear(){
		for (int i=0;i<arraySet.length;i++){
			arraySet[i]=null;
		}
	}
	
	/**
	 * Returns the number of items in the set.
	 * @pre true
	 * @post true
	 * @return The number of objects contained in the set.
	 */
	public int size(){
		int setSize = 0;
		for ( int i=0;i<arraySet.length;i++ ){
			if ( arraySet[i]!=null )
				setSize++;
		}
		return setSize;
	}

	/**
	 * Returns an iterator for a set of objects.
	 * @pre true
	 * @post Set is not changed
	 * @return An iterator for the set of objects that points to the beginning of the set.
	 */
	public MyIterator iterator(){
		MyIterator arraySetIterator = new SetIterator();
		return arraySetIterator;
	}
	
	
	private class SetIterator implements MyIterator {
		
		/**
		 * item that iterator is pointing at.
		 */
		private int pointedItem;
		
		/**
		 * construct a new iterator that points to the first object in array.
		 */
		public SetIterator(){
			pointedItem = 0;
		}

		/**
		 * Checks if there are more objects in the set to iterate over.
		 * @pre true
		 * @post true
		 * @return true if there are more objects in the set to iterate over, false otherwise
		 */
		public boolean hasNext(){
			boolean containNext = false;
			if ( pointedItem<size() )
				containNext = true;
			return containNext;
		}
		
		/**
		 * Returns the next object of the set the iterator walks over.
		 * @pre @pre.hasNext()
		 * @post Iterator points to the next object in the set
		 * @return The object in the set the iterator was pointing to when the method was called.
		 */
		public Object next(){
			pointedItem++;
			return arraySet[pointedItem];
		}
		
	}

}
