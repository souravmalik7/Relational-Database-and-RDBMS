package database.backend.query_processing.model;

public class QueryResponse {
	private int responseStatus;
	private String response;

	public QueryResponse(int responseStatus, String response) {
		this.responseStatus = responseStatus;
		this.response = response;
	}

	public int getResponseStatus() {
		return responseStatus;
	}

	public String getResponse() {
		return response;
	}
}
