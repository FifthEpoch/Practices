public class CompanyClient {

	public static void main(String[] args) {
		
		Employee 	jim = new Employee("Jim Blake" , "Pike Street" , "4022");
		Manager 	kim = new Manager("Kim Mann"   , "High Street" , "3315", 12);
		Executive 	sam = new Executive("Sam Lowe" , "Jones Street", "2128", 5083.0);
		Executive	tam = new Executive("Sam Lowe" , "Jones Street", "2128", 5083.0);
		Manager 	tim = new Manager("Tim Hill"   , "Pine Street" , "3121", 5);
		Executive 	tom = new Executive("Tom Mitty", "Smith Street", "2124", 2083.0);
		
		Employee[] companyEmployee = {jim, kim, sam, tam, tim, tom};
		
		for (int i = 0; i < companyEmployee.length; i++) {
			System.out.println(companyEmployee[i].toString());
			
			if (companyEmployee[i].equals(tim)) {
				((Manager) companyEmployee[i]).setExperience(10);
				System.out.println("- Updated field: experience -\n" + companyEmployee[i].toString());
			}
			if (companyEmployee[i].equals(sam)) {
				((Executive) companyEmployee[i]).setBonus(8083.00);
				System.out.println("- Updated field: bonus -\n" + companyEmployee[i].toString());
			}
			
		}
		
		
	}

}
