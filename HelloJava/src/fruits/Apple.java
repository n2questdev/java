package fruits;

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
		System.out.println(((Fruit) parent).getColor());
		
		parent = new Orange();
		System.out.println(parent.getColor());
		System.out.println(((Fruit) parent).getColor());
		
//		Not legal. Two siblings can't equal each other
//		Apple myapple = new Orange();
		
//		Not legal. Child can't have parent object
//		Apple apple = new Fruit();
	}

	@Override
	public void rules() {
		System.out.println("Implementing all the rules!!!");

	}
}
