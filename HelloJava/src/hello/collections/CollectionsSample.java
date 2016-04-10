package hello.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

public class CollectionsSample {
	
	static ArrayList al = null;
	int i;
	
	public static void main(final String[] args) {
		Collection<String> c = new ArrayList<String>();
		
		test(c);
		
		al.add("String"); 
		
		
	}

	public static void test(Collection<String> c) {
		// TODO Auto-generated method stub
		
		Collection<String> c2 = new ArrayList<String>();
		
		ArrayList ar = new ArrayList();
		
		ar.add(null);
		
		Vector<String> v = new Vector<String>();
		
		v.add(null);
		
		c2.add("String");
		c2.addAll(c);
		
		Iterator<String> iter = c2.iterator();
		while (iter.hasNext()) {
			System.out.println((String) iter.next());
		}
		
		for (String string : c2) {
			System.out.println(string);
		}
		
		c2.forEach(a -> System.out.println(a));
		
	}

}
