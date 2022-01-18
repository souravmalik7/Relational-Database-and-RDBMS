package database.frontend;

import database.backend.authentication.model.User;

public class UserSession {
	private User user;
	public static String sessionActive = "N";

	public UserSession(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}

	public void destroySession() {
		this.user =null;
	}
}
