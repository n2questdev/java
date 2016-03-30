package fruits;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Apple extends Fruit implements ISOStandards, FDAStandards, AGRIStandards {

	private static final long serialVersionUID = 1L;

	@Override
	public void test() {
		System.out.println("Overriding default implementation with my own");
	}

	public static void main(String[] args) {
//		a.youcantseeme();
		System.out.println("Just hello" + FRUIT_CATEGORY);
		
		Apple a = new Apple();
		a.setColor("red");
		System.out.println(a.getColor());

		
		Fruit parent = new Apple();
		System.out.println(parent.getColor());
		
		parent.setColor("blue");
		System.out.println(parent.getColor());
//		System.out.println(((Fruit) parent).getColor());
		
		parent = new Orange();
		System.out.println(parent.getColor());
		System.out.println(((Fruit) parent).getColor());
		
//		Not legal. Two siblings can't equal each other
//		Apple myapple = new Orange();
		
//		Not legal. Child can't have parent object
//		Apple apple = new Fruit();
		

		try {
			a.createReport(a);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			//Execute this regardless of exception thrown or not
			System.out.println("Report creation is attempted");
		}
		
		try {
			a.demoRuntimeException();
			System.exit(0);
		} catch(NullPointerException npe) {
			//TODO - do something to mitigate the risks exposed by NPE
		} catch(RuntimeException re) {
			//TODO - do something with RuntimeException
		} catch (Exception e) {
			//TODO - handle the unknown exceptions if occurred
		} catch (Throwable t) {
			//Throwable acts as Catch-All
			//TODO - if still something bad occurs, throwable catches all of them
		} finally {
			//Finally do after try or catch
			System.out.println("Finally");
		}
		System.out.println("After sys exit");
	}

	@Override
	public void rules() {
		System.out.println("Implementing all the rules!!!");

	}
	
	public void createReport(Fruit parent) throws IOException {
		
		File fruitsReport = new File("C:/temp/FruitsReport.txt");
		FileWriter writer = new FileWriter(fruitsReport);
		
		if(parent.getColor().equals("RED")) throw new IOException("File can't take RED color");
		
		writer.write(parent.getColor());
		
		
	}
	
	public void demoRuntimeException() throws NullPointerException, RuntimeException {
		if(4 != 5) throw new NullPointerException();
	}
}
