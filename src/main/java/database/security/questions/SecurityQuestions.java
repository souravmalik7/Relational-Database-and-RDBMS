package database.security.questions;

import java.util.ArrayList;
import java.util.List;

public class SecurityQuestions {
	
	private List<String> securityQuestions = new ArrayList<String>();
	
	public List<String> getSecurityQuestions() {
		return securityQuestions;
	}

	public SecurityQuestions()
	{
		initializeSecurityQuestions();
	}
	
	public void initializeSecurityQuestions()
	{
		securityQuestions.add("City in which you were born");
		securityQuestions.add("First high school teacher");
		securityQuestions.add("Mothers maiden name");
	}
	

}
