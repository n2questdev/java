package vamsi;

public class Fruit {

	static long fruitCount;
	
	public Fruit() {
		this.color = "red";
		this.weight = 1.1f;
		this.shape = "round";
	}
	
	public Fruit(String color, float weight, String shape){
		this.color = color;
		this.weight = weight;
		this.shape = shape;
	}
	
	public Fruit(String color) {
		this();
		this.color = color;
	}
	
	String color;
	float weight;
	String shape;
	
	public static void main(String[] args) {
		Fruit f = new Fruit();
		f.color = "something";
		f.weight = 10.0f
				;
		f.shape="something";
		
		
		Fruit f2 = new Fruit("something", 10.0f, "something");
		
		System.out.println(f.weight);
	}
	
	
}
