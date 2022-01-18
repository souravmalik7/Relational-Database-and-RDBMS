package database.backend.generate.analytics;

import static database.backend.logger.constant.LogConstant.EVENT_LOG;
import static database.backend.logger.constant.LogConstant.QUERY_LOG;
import static database.backend.query_processing.constant.QueryProcessingConstants.DATABASE_PATH;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import database.backend.authentication.constant.ConstantValues;
import database.backend.authentication.exception.UserAuthenticationException;
import database.backend.query_processing.constant.QueryProcessingConstants;
import database.backend.query_processing.controller.QueryProcessingController;
import database.backend.query_processing.exception.QueryProcessException;
import database.backend.query_processing.model.QueryResponse;
import database.frontend.Printer;
import database.frontend.UserSession;

public class generateAnalytics {
	
	private Printer printer;
	private Scanner scanner;
	private UserSession session;

	public generateAnalytics(Printer printer, Scanner scanner, UserSession session) {
		
		this.printer = printer;
		this.scanner = scanner;
		this.session = session;
	}
	
	public void generatingAnalytics() throws QueryProcessException
	{
		
		while(true)
		{
			printer.print("1. Analytics of queries executed on database");
			printer.print("2. Analytics of queries executed on table");
			printer.print("3. exit");
			String choice = scanner.nextLine();
			
				switch(choice)
				{
				case "1":
					try {
						printer.print("Enter the database for generating analytics");
						String database = scanner.nextLine();
						String exist = databaseValdiation(database);
						if (exist.equalsIgnoreCase("Database Exist")) {
							fetchAnalytics(database, choice);
						}
					}
						catch(Exception E)
						{	
							System.out.println(E.getMessage());
						}
					break;
				case "2":
					try {
						printer.print("Enter the database for generating analytics");
						String database = scanner.nextLine();
						String exist = databaseValdiation(database);
						if (exist.equalsIgnoreCase("Database Exist")) {
							fetchAnalytics(database, choice);
						}
						}
						catch(Exception E)
						{	
							System.out.println(E.getMessage());
						}
					break;
				case "3":
					return;
				}
			}

	}
	
	private void fetchAnalytics(String database,String choice) throws UserAuthenticationException {
		String filepath = "";
		boolean databaseName;
		if(choice.equalsIgnoreCase("1"))
			filepath = ConstantValues.USERDBQUERIES_FILE;
		else
			filepath = ConstantValues.USERINDQUERIES_FILE;
		
		try (BufferedReader usersFileReader = new BufferedReader(new FileReader(filepath))) {
		      List<String> userDetails = usersFileReader.lines().collect(Collectors.toList());;
		      for(int i=0;i<userDetails.size();i++) {
		    	  String line = userDetails.get(i);
		         String[] DetailsArr = line.split(" ");
		         if(choice.equalsIgnoreCase("1"))
		        	  databaseName = DetailsArr[7].equals(database);
		         else
		        	  databaseName = DetailsArr[10].equals(database);
		        if (databaseName) {
		        	printer.print(line);
		        }
		      }
		    } catch (IOException e) {
		      throw new UserAuthenticationException("Database error!!!!!!!!");
		    }	
	}
	
	private String databaseValdiation(String databasename) throws QueryProcessException {
		String message;
		String databasePath = DATABASE_PATH + databasename;
		File database = new File(databasePath);
		if (database.isDirectory()){
			return "Database Exist";

		}else{
			message = "Database does not exist!";
			throw new QueryProcessException(message);
		}
	}
	
	

}
