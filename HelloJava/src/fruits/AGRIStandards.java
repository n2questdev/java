package fruits;

import java.io.Serializable;

public interface AGRIStandards  extends Serializable {
	
	void rules();
	
	default void test() {
		System.out.println("What?? interface can have method?? you just said no methods in interface?????");
		System.out.println("Welcome to Java 8, where conventional wisdom is challenged");
	}

}
