package database.backend.authentication.model;

public class User {
	
	private int userID;
	private String username;
	private String password;
	private String userEmail;
	private String userSecurityAnsQ1;
	private String userSecurityAnsQ2;
	private String userSecurityAnsQ3;
	
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserSecurityAnsQ1() {
		return userSecurityAnsQ1;
	}

	public void setUserSecurityAnsQ1(String userSecurityAnsQ1) {
		this.userSecurityAnsQ1 = userSecurityAnsQ1;
	}

	public String getUserSecurityAnsQ2() {
		return userSecurityAnsQ2;
	}

	public void setUserSecurityAnsQ2(String userSecurityAnsQ2) {
		this.userSecurityAnsQ2 = userSecurityAnsQ2;
	}

	public String getUserSecurityAnsQ3() {
		return userSecurityAnsQ3;
	}

	public void setUserSecurityAnsQ3(String userSecurityAnsQ3) {
		this.userSecurityAnsQ3 = userSecurityAnsQ3;
	}

	  
	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	

	public User(int l, String username, String userEmail, String userPassword, String userSecurityAnsQ1, String userSecurityAnsQ2, String userSecurityAnsQ3) {
		this.userID = l;
		this.username = username;
		this.password = password;
	    this.userEmail = userEmail;
	    this.userSecurityAnsQ1 = userSecurityAnsQ1;
	    this.userSecurityAnsQ2 = userSecurityAnsQ2;
	    this.userSecurityAnsQ3 = userSecurityAnsQ3;
	}

	@Override
	public String toString() {
		return "User [userID=" + userID + ", username=" + username + ", password=" + password + ", userEmail="
				+ userEmail + ", userSecurityAnsQ1=" + userSecurityAnsQ1 + ", userSecurityAnsQ2=" + userSecurityAnsQ2
				+ ", userSecurityAnsQ3=" + userSecurityAnsQ3 + "]";
	}
}
