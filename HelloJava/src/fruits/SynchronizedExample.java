package fruits;

public class SynchronizedExample {
	public void test() {
		
		//Synchronized block inside a unsynchronized method using object locks. Uses Object lock. Object level contention
		synchronized (this) {
			System.out.println("test");
		}
		
	}
	
	/**
	 * Synchronized method. Uses object lock and object level contention
	 */
	public synchronized void threadSafeMethod() {
		
	}
	
	public void methodd() {
		
		//Synchronized block inside a unsynchronized method using class locks. Uses class lock. HIGH level contention
		synchronized (SynchronizedExample.class) {
			System.out.println("Class lock! ");
			
		}
	}

}
