package fruits;

public class ElectricTableFan implements ElectricOutletSocket {

	@Override
	public void threePinSocket() {
		System.out.println("implementing three pin socket electric schematics");

	}

	@Override
	public void twoPinSocket() {
		System.out.println("implementing two pin socket electric schematics");

	}

	@Override
	public void indianSocket() {
		System.out.println("implementing indian pin socket electric schematics");
		
	}
	
}
