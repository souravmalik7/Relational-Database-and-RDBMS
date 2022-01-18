package database.backend.authentication;

public class Session {
	private static String username;

	public static void setUsername(String username) {
		Session.username = username;
	}

	public static String getUsername() {
		return username;
	}
}
