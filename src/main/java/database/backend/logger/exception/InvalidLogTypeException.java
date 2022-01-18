package database.backend.logger.exception;

public class InvalidLogTypeException extends Exception {
	private String errorMessage;

	public InvalidLogTypeException( String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
