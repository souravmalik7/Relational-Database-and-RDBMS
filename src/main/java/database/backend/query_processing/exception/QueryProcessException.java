package database.backend.query_processing.exception;

public class QueryProcessException extends Exception{

    private final String message;

    public QueryProcessException(final String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return "QueryProcessException{" + "error='" + message + '\'' +'}';
    }


}
