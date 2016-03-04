package revival;

import java.util.Arrays;
import java.util.List;

public class LambdaFruits {

	public static void main(String[] args) {
		Produce produce = new Produce();
		produce.eat(() -> {return "sour";});

		List<Integer> mylist = Arrays.asList(1,2,3,4,5,6);
		
		mylist.stream().map(e -> e*2).reduce(0, (c,e) -> c + e);
	}

}


interface Fruit {
	String getTaste();
//	String getColor();
}

class Produce {
	public void eat(Fruit f) {
		System.out.println(f.getTaste());
	}
}
