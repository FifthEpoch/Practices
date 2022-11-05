public class Executive extends Employee{
	
	private double bonus;

	public Executive(String name, String address, String phone, double bonus) {
		super(name, address, phone);
		setBonus(bonus);
	}
	
	/**
	 *  returns if the data stored in the current object is equal to 
	 *  the data stored in the object specified.
	 */
	public boolean equals(Executive exe) {
		if (super.equals(exe) && Double.compare(exe.getBonus(),bonus) == 0) {
			return true;
		}
		return false;
	}

	/**
	 * set the bonus amount for executive
	 */
	public void setBonus(double bonus) {
		this.bonus = bonus;
	}
	
	public double getBonus() {
		return bonus;
	}
	
	public double salary() {
		return super.salary() * 4;
	}
	
	public String toString() {
		return super.toString() + 
				"Bonus: " + bonus + "\n";
	}

}
