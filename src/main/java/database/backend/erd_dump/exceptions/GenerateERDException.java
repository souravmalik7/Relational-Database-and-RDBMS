package database.backend.erd_dump.exceptions;

public class GenerateERDException extends Exception {

	private final String message;
	public GenerateERDException(final String message) {
		// TODO Auto-generated constructor stub
        super(message);
        this.message = message;
	}

}
