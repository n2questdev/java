package fruits;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Fruit {
	
	private String color = "yellow";
	
	public static final String FRUIT_CATEGORY;
	
	//instance initializer
	{
		System.out.println("instance initializer is invoked before Object is returned to caller");
	}
	
	//static (or class level) initializer
	static {
		FRUIT_CATEGORY = "consatnts";
		System.out.println("class initializer is invoked right after the class is loaded into memory");
		
	}

	//accessor methods will decide how the attributes are initialized. So control stays inside the object. External objects shouldn't access the attributes directly
	
	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	protected void ripe() {
		System.out.println("Child class will implement this");
	}
	
	private void youCantSeeMe()
	{
		System.out.println("My private stuff. Privacy please!");
	}

	public static void main(String[] args) {
		
		//new Fruit().MYCONSTANTVALUE = "not a constant";
	}
	
	public void createReport(Fruit parent) throws IOException {
		
		File fruitsReport = new File("C:/temp/FruitsReport.txt");
		FileWriter writer = new FileWriter(fruitsReport);
		
		if(parent.getColor().equals("RED")) throw new IOException("File can't take RED color");
		
		writer.write(parent.getColor());
		
		
	}
	
	public void demoRuntimeException() throws Exception {
		if(4 != 5) throw new NullPointerException();
	}
}
