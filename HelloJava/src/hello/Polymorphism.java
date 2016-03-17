package hello;

public class Polymorphism {
	
	static int var = 0;
	//Compile time polymorphism
	int var1 = 0;
	String s = "sdfsdfdf";
	
	public void test(String s) {
		
		s = "Hello";
	}
	public void test(int i) {
		int var = 5;
		System.out.println(var);
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
		
		String s = new String("mystring");
		
		myobj.test(s);
		
		System.out.println(s);
		
//		myobj.test();
		
		myobj.test(4, 5);
		
		myobj.test(4, 5l);
	}
	
	
}
