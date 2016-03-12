package hello;

public abstract class Polymorphism {
	
	
	//Compile time polymorphism
	
	public abstract void test();
	
	public void test(int i) {
		//TODO
	}
	
	public void test(int first, int second) {
		//TODO
	}
	

	public void test(int i, long f) {
		//TODO
	}
	
	
	// Runtime Polymorphism
	
	
	
	public static void main(String[] args) {
		Polymorphism myobj = new Polymorphism();
		
		myobj.test();
		
		myobj.test(4, 5);
		
		myobj.test(4, 5l);
	}
	
	
}
