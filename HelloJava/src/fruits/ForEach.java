package fruits;

public class ForEach {

	public static void main(String[] args) {
		int[] arrayofInts = new int[] {1,2,3,4,5,6,7,8,9,10};
		int j=0;
		
		for(int i = 0; i < arrayofInts.length;i++)
		{
			if (i == 5) continue;
			System.out.println(arrayofInts[i]);
		}
		
//		for (int i : arrayofInts) {
//			System.out.println(i);
//		}

	}

}
