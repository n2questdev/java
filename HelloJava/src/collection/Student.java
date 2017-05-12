package collection;

public class Student {

	////////////////// variables /////////////////////

	private int id;
	private String fname;
	private String lname;
	private String email;
	private boolean studentActive;
	private int enrolledDate;

	//////////////// setters & getters ///////////////

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isStudentActive() {
		return studentActive;
	}

	public void setStudentActive(boolean studentActive) {
		this.studentActive = studentActive;
	}

	public int getEnrolledDate() {
		return enrolledDate;
	}

	public void setEnrolledDate(int enrolledDate) {
		this.enrolledDate = enrolledDate;
	}

	////////////////// constructor ///////////////////

	public Student(int id, String fname, String lname, String email, boolean studentActive, int enrolledDate) {
		super();
		this.id = id;
		this.fname = fname;
		this.lname = lname;
		this.email = email;
		this.studentActive = studentActive;
		this.enrolledDate = enrolledDate;
	}

	public Student() {
		super();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
	
	public boolean equals(Object obj) {
		return this.getId() == ((Student) obj).getId();
	}
	
	@Override
	public int hashCode() {
		return id + fname.length() + lname.length() + email.length() + (studentActive == true ? 1 : 0) + enrolledDate;
	}
	
	@Override
	public String toString() {
		return id + " " + fname + " " + lname + " " + email + " " + studentActive;
	}
	
	
}