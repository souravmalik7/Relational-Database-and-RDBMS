package database.backend.query_processing.exception;

public class QueryParseException extends Exception{

    private final String message;

    public QueryParseException(final String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return "QueryParseException{" + "error='" + message + '\'' +'}';
    }


}
