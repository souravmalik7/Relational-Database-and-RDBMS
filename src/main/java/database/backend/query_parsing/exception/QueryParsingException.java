package database.backend.query_parsing.exception;

public class QueryParsingException extends Exception {
	private String errorMessage;

	public QueryParsingException(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public String toString() {
		return "QueryParsingException{" +
					   "errorMessage='" + errorMessage + '\'' +
					   '}';
	}
}
