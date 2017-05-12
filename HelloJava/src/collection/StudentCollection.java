package collection;

import static java.lang.System.out;

import java.util.ArrayList;
import java.util.Collection;

class StudentCollection {
	/////////////////// main /////////////////////////

	public static void main(String[] args) {

		
		Student a = new Student(7, "vamsi", "garikipati", "yahoo", true, 2008);
		Student b = new Student();
		
		b = a;
		
		
		
		//Thread 2 is waiting outside. Requesting entry based a's availability
		//Thread 3
		//Thread 4
		synchronized (a) {
			//TODO
			
			//Thread 1 is running
		}
		
		//Thread 1 is done
		if(a.equals(b))
		{
			System.out.println("Equals");
		} else
		{
			System.out.println("Not Equals");
		}
		
		
		Collection<Student> students = new ArrayList<>();

		students.add(new Student(7, "vamsi", "garikipati", "yahoo", true, 2008));
		students.add(new Student(8, "mahesh", "yadav", "gmail", true, 2015));
		students.add(new Student(9, "krishna", "kumar", "gmail", true, 2005));
		students.add(new Student(10, "anand", "kumar", "gmail", true, 2008));
		students.add(new Student(11, "venkata", "krishna", "gmail", true, 2008));
		students.add(new Student(12, "hari", "kumar", "gmail", false, 2015));

		for (Student student : students)
			out.println(student);

		// countCommonLname(a);
		// System.out.println("\n");
		out.println("\ncount of active students : ");
		countActiveStudents(students);
		out.println("\nname of active students as of year 2015 : ");
		nameActiveStudents2015(students);
		out.println("\nstudents with yahoo email id's : ");
		nameYahooEmail(students);
		out.println("\nhighest enrolled year : ");
		highestEnrollmentYear(students);

	}

	////////////////// methods //////////////////////

	public static void highestEnrollmentYear(Collection<Student> students) {

		int i = 0;
		int[] arr = new int[students.size()];
		for (Student aa : students) {
			arr[i] = aa.getEnrolledDate();
			i++;
		}
		int count = 1;
		int tempCount;
		int common = arr[0];
		int temp = 0;
		for (int j = 0; j < arr.length; j++) {
			temp = arr[j];
			tempCount = 0;
			for (int k = 1; k < arr.length; k++) {
				if (temp == arr[k])
					tempCount++;
			}
			if (tempCount > count) {
				common = temp;
				count = tempCount;
			}
		}
		System.out.println(common);
	}

	public static void countCommonLname(Collection<Student> a) {
		int i = 0;
		String[] str = new String[a.size()];
		for (Student aa : a) {
			str[i] = aa.getLname();
			i++;
		}
		int count = 0;
		int tempCount;
		String temp = null;
		for (int j = 0; j < str.length; j++) {
			temp = str[j];
			tempCount = 0;
			for (int k = 1; k < str.length; k++) {
				if (temp == str[k])
					tempCount++;
			}
			if (tempCount > count)
				count = tempCount;
		}
		System.out.println(count);
	}

	public static void nameYahooEmail(Collection<Student> a) {
		for (Student aa : a) {
			if (aa.getEmail() == "yahoo")
				System.out.println(aa.getFname() + " " + aa.getLname());
		}
	}

	public static void nameActiveStudents2015(Collection<Student> students) {
		students.stream().filter(p -> p.isStudentActive() == true && p.getEnrolledDate() == 2015)
				.forEach(p -> System.out.println(p.getFname() + " " + p.getLname()));

		for (Student student : students) {
			if (student.isStudentActive() == true && student.getEnrolledDate() == 2015)
				System.out.println(student.getFname() + " " + student.getLname());
		}
	}

	public static void countActiveStudents(Collection<Student> students) {

		// Using Streams to get the Counts
		System.out.println(students.stream().filter(p -> p.isStudentActive()).count());

		int count = 0;
		for (Student student : students) {
			if (student.isStudentActive() == true)
				count++;
		}
		System.out.println(count);
	}
}