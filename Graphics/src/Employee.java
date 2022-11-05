public class Employee {

	private String address;
	private String name;
	private String phone;
	
	/**
	 * stores the employee record using the specified information.
	 */
	public Employee(String name, String address, String phone) {
		this.name 		 = name;
		this.address 	 = address;
		this.phone 		 = phone;
	}
	
	/**
	 *  returns if the data stored in the current object is equal to 
	 *  the data stored in the object specified.
	 */
	public boolean equals(Employee emp) {
		if (emp.name != this.name || emp.address != this.address || emp.phone != this.phone) {
			return false;
		}
		return true;
	}

	/**
	 * returns the bimonthly pay of a regular employee.
	 */
	public double pay() {
		return salary() / 24.0;
	}
	
	/**
	 * returns the formatted contents of the base salary.
	 */
	public double salary() {
		return 100000.0;
	}
	
	/**
	 * returns the formatted contents of the employee record.
	 */
	public String toString() {
		return "Name: " + name + "\n" +
			   "Address: " + address + "\n" + 
			   "Phone: " + phone + "\n" +
			   "Salary: " + salary() + "\n" +
			   "Bimonthly pay: " + pay() + "\n";
			   
	}
	
}
