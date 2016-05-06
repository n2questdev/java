package fruits;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class Apple extends Fruit implements ISOStandards, FDAStandards, AGRIStandards {

	private static final long serialVersionUID = 1L;
	
	public static final String FILE_PATH = "C:/temp/FruitsReport.txt";

	@Override
	public void test() {
		System.out.println("Overriding default implementation with my own");
	}

	public static void main(String[] args) {
		
		Collection c = new ArrayList();
		
		
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
	
	/**
	 * Create REport will create a file and do this and this
	 * @author Juluri
	 * @exception IOException
	 * 
	 */
	public void createReport(Fruit parent) throws IOException {
		
		if(parent.getColor().equals("RED")) throw new IOException("File can't take RED color");
		
		File fruitsReport = new File(FILE_PATH);
		FileWriter writer = new FileWriter(fruitsReport);
		writer.write(parent.getColor());

	}
	
	public void demoRuntimeException() throws NullPointerException, RuntimeException {
		if(4 != 5) throw new NullPointerException();
	}
}
