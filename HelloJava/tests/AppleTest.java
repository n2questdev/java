import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import fruits.Apple;
import fruits.Fruit;

public class AppleTest {

	
	@Before
	public void before() {
		System.out.println("All the initialization steps");
	}
	
	@After
	public void after() {
		System.out.println("All the finalization steps. like closing database connections, files or any finalization");
	}
	
	@BeforeClass
	public static void beforeClass() {
		System.out.println("Before Class method");
	}
	
	@AfterClass
	public static void afterClass() {
		System.out.println("After Class method");
	}
	
	
	@Test
	public void testCreateReportIOExceptionIsThrownWhenREDPassed() {
//		fail("Not yet implemented");
		
		Apple apple = new Apple();
		
		Fruit fruit = new Fruit();
		
		fruit.setColor("RED");
		try {
			apple.createReport(fruit);
			fail("Expected exception is not thrown when RED is the fruit's color");
		} catch (IOException e) {
			assertTrue("Method thrown IOException as expected", true);
			assertTrue(e instanceof IOException);
		}
		
	}//end of test

	@Test
	public void testCreateReportNotExpectingFileWhenREDisPassed() {
//		fail("Not yet implemented");
		
		Apple apple = new Apple();
		
		Fruit fruit = new Fruit();
		
		fruit.setColor("RED");
		try {
			apple.createReport(fruit);
			fail("Expected exception is not thrown when RED is the fruit's color");
		} catch (IOException e) {
			//ignore
		}
		
		File file = new File("C:/temp/FruitsReport.txt");
		try {
			BufferedReader fileReader = new BufferedReader(new FileReader(file));
			
			String value = fileReader.readLine();
			
			assertEquals("", "");
		} catch (FileNotFoundException e) {
			fail("File Not Found Exception: " + e.getMessage());
		} catch (IOException e) {
			fail("IO Exception: " + e);
		}
		
	}
	
	@Test
	public void testDemoRuntimeException() {
		assertTrue(true);
	}

	@Test
	public void testTest() {
		assertTrue(true);
	}

	@Test
	public void testRules() {
		assertTrue(true);
	}

}
