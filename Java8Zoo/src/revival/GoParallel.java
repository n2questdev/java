package revival;

import java.util.Arrays;
import java.util.List;

public class GoParallel {
public static void main(String[] args) {
	List<Integer> arr = Arrays.asList(0, 1,2,3,4,5,6,7,8,9,10);
	
//	arr.stream().filter(a -> a%5==0).parallel().map(a -> a*5).reduce(0, (c,e) -> c+e);
	
	for (int i = 0; i < 1000000; i++) {
			arr.stream().filter(a -> a%5==0).parallel().map(a -> a*5).reduce(0, (c,e) -> c+e);	
	}
	
}
}
