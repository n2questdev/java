package hello;

public class HelloJava {

	// Version of main method with "variable arguments"
	// public static void main(String... args) {
	public static void main(String[] args) {
		try {
			System.out.println("Sleeping for 10 seconds! ");
			Thread.sleep(10000);
			//I'm doing so much stuff here. executing 100s of lines of code
	
		} catch (InterruptedException e) {
			// TODO: handle here
		}

		System.out.println("Hello World! " + args.length + args[0]);
	}

	// public static int main(String[] arg) {
	// System.out.println("Fake main method");
	// return 0;
	// }
}