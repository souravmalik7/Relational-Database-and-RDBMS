package database.frontend.views;

import database.backend.erd_dump.controller.GenerateERDController;
import database.backend.erd_dump.exceptions.GenerateERDException;
import database.backend.query_parsing.exception.QueryParsingException;
import database.backend.query_processing.controller.QueryProcessingController;
import database.backend.query_processing.exception.QueryProcessException;
import database.backend.query_processing.model.QueryResponse;
import database.frontend.Printer;

import static database.backend.query_processing.constant.QueryProcessingConstants.SUCCESS_STATUS;

import java.util.Scanner;

public class GenerateErdView {
	private Printer printer;
	private Scanner scanner;

	public GenerateErdView(Printer printer, Scanner scanner) {
		this.printer = printer;
		this.scanner = scanner;
	}
	
	public void generateERD(String query) {
		QueryResponse response = null;
		GenerateERDController generateErdController=new GenerateERDController(printer, scanner);
		
			printer.print("Enter the Database Name: ");
			String userInput = scanner.nextLine();
			try {
				response = generateErdController.GenerateERD(userInput);
				
			}catch (GenerateERDException e){
				printer.print(e.toString());
			}

			
		

	}

}
