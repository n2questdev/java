package vamsi;

import java.util.Scanner;

public class Loops {

	public static void main(String[] args) {

		String[] months = { "january", "february", "march", "april", "may", "june", "july", "august", "september",
				"october", "november", "december" };

		String[] last = { "st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
				"th", "th", "th", "th", "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th", "st" };

		char t;
		Scanner input = new Scanner(System.in);
		do {
			System.out.println("enter year");
			int year = input.nextInt();
			System.out.println("enter month");
			int MonthNumber = input.nextInt();
			if (MonthNumber > 0 && MonthNumber < 13) {
				System.out.println("enter day");
				int DayNumber = input.nextInt();
				if (DayNumber > 0 && DayNumber < 32) {

					if (MonthNumber == 2 && DayNumber > 28)
						System.out.println("february has only 28 days.\nTry again? (y or any key to exit)");
					else {
						if ((MonthNumber != 2 && DayNumber != 31) || (MonthNumber != 4 && DayNumber != 31)
								|| (MonthNumber != 6 && DayNumber != 31) || (MonthNumber != 9 && DayNumber != 31)
								|| (MonthNumber != 11 && DayNumber != 31)) {
							String MonthName = months[MonthNumber - 1];
							String suffix = last[DayNumber - 1];

							System.out.println(MonthName + " " + DayNumber + suffix + " " + year
									+ ".\n\nTry again? (y or any key to exit)");
						} else
							System.out.println(
									"the month you entered has only 30 days.\nTry again? (y or any key to exit)");
					}
				} else
					System.out.println("day can only range from 1 to 31.\nTry again? (y or any key to exit)");
			} else
				System.out.println("month can only range from 1 to 12.\nTry again? (y or any key to exit)");
			t = input.next().charAt(0);
		} while (t == 'y');
		
		input.close();
	}
}