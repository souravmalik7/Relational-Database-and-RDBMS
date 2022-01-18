package database.backend.query_processing.exception;

public class QueryProcessingException {
	private String ErrorMessage;

	public QueryProcessingException(String errorMessage) {
		ErrorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	@Override
	public String toString() {
		return "QueryProcessingException{" +
					   "ErrorMessage='" + ErrorMessage + '\'' +
					   '}';
	}
}
