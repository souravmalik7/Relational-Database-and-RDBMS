package database.frontend.views;

import database.backend.query_processing.exception.QueryProcessException;
import database.backend.authentication.Session;
import database.backend.authentication.exception.UserAuthenticationException;
import database.backend.query_parsing.exception.QueryParsingException;
import database.backend.query_processing.controller.QueryProcessingController;
import database.backend.query_processing.model.QueryResponse;
import database.frontend.Printer;
import database.frontend.UserSession;

import java.io.IOException;
import java.util.Scanner;

import static database.backend.query_processing.constant.QueryProcessingConstants.SUCCESS_STATUS;

public class QueryExecutionView {
	private Printer printer;
	private Scanner scanner;
	private UserSession session;

	public QueryExecutionView(Printer printer, Scanner scanner, UserSession session2) {
		this.printer = printer;
		this.scanner = scanner;
		this.session = session2;
	}

	public void executeQuery(String query) throws UserAuthenticationException, IOException {
		QueryResponse response = null;
		QueryProcessingController queryProcessingController=new QueryProcessingController(printer, scanner, session);
		while(true){
			printer.print("Write query: or exit");
			String userInput = scanner.nextLine();
			try {
				response = queryProcessingController.processQuery(userInput);

				if(response.getResponseStatus()==SUCCESS_STATUS){
					printer.print("Query Executed Successfully");
				}
			}catch (QueryParsingException | QueryProcessException e){
				printer.print(e.toString());
			}

			if (userInput.equals("exit")){
				break;
			}
		}

	}

}
