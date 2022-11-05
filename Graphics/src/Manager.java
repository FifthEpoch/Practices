public class Manager extends Employee {

	private int experience;

	public Manager(String name, String address, String phone, int experience) {
		super(name, address, phone);
		setExperience(experience);
	}
	
	/**
	 *  returns if the data stored in the current object is equal to 
	 *  the data stored in the object specified.
	 */
	public boolean equals(Manager man) {
		if (super.equals(man) && Integer.compare(experience,man.getExperience()) == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * set the amount of experience of a Manager
	 */
	public void setExperience(int experience) {
		this.experience = experience;
	}
	
	public int getExperience() {
		return experience;
	}
	
	public double salary() {
		return super.salary() + 12000 * experience;
	}
	
	public String toString() {
		return super.toString() + 
				"Experience: " + experience + "\n";
	}
	
}
