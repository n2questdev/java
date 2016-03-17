package fruits;

import java.util.Arrays;

public class Strings 

{
	
	
	public static void main(String[] args) {
	
		int[] source = new int[100];
		source[0] = 42949674;
		
		Integer integer = 100; //Wrapper class with Autoboxing
		
		short s = 10;
		
		s = (short) source[0];
				
		
//		integer = new Integer(200);
		
		//char, byte, short, int, long, float, double
		
		String[] sss = new String[] {"Hello", "String"};
		
		String[] yyy = new String[10];
		
		int[] target = new int[200];
		
		String greeting = "Hello";
		
		String longNameLikeThisAndThat; //Camel case or Camel hump notation
		
		greeting =  greeting + "World";
		
		greeting = greeting + "Abh";
		
		greeting = greeting + "!";
		
		System.out.println(greeting);
		
		StringBuffer greetingSB = new StringBuffer();
		
		greetingSB.append("Hello");
		
	//TOD
		
		//
		//
		//
		greetingSB.append("World");
		//
		//
		//./arg
		
		greetingSB.append("Abhi");
		
		greetingSB.append("sflsdsldfj");
		
		
		StringBuilder sbuilder = new StringBuilder();
	
		
		Strings ssss = new Strings();
		
		//Calling varargs method and passing different values
		ssss.test("test", "test2");
		ssss.test("test");
		ssss.test(); //passing no arguments for varargs
	}
	
	/**
	 * Method which takes strings of variable length. i.e., any number of strings ranging from 0 to n
	 * @param strings
	 */
	public void test(String...strings)
	{
		MyTestInterface myInterface = new MyTestInterface()	{
				
				@Override
				public void test() {
					System.out.println("Implementation");
					
				}
			}; 
		
	}
	
	interface MyTestInterface {
		void test();
	}


}

// Below is not an inner class
//class Test {
//	void test() {}
//}
