package database.backend.erd_dump.controller;

import static database.backend.logger.constant.LogConstant.EVENT_LOG;
import static database.backend.logger.constant.LogConstant.QUERY_LOG;
import static database.backend.query_processing.constant.QueryProcessingConstants.DATABASE_PATH;
import static database.backend.query_processing.constant.QueryProcessingConstants.MEATADATA_PATH;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.stream.Stream;

import database.backend.erd_dump.constant.ERDConstant;
import database.backend.erd_dump.exceptions.GenerateERDException;
import database.backend.query_processing.constant.QueryProcessingConstants;
import database.backend.query_processing.controller.QueryProcessingController;
import database.backend.query_processing.exception.QueryProcessException;
import database.backend.query_processing.model.QueryResponse;
import database.frontend.Printer;

public class GenerateERDController {

	private Printer printer;
	private Scanner scanner;

	public GenerateERDController(Printer printer, Scanner scanner) {
		// TODO Auto-generated constructor stub
		this.printer = printer;
		this.scanner = scanner;
	}
	
	private boolean checkIfDatabaseExists(String databaseName) {
		String databasePath = DATABASE_PATH + databaseName;
		File database = new File(databasePath);
		String metadataPath = MEATADATA_PATH + databaseName;
		File metadata = new File(metadataPath);
		if (database.isDirectory() || metadata.isDirectory() ){
			return true;
		}
			
		return false;	
	}
	

	
	public QueryResponse GenerateERD(String DbName) throws GenerateERDException {
		String databasePath = DATABASE_PATH + DbName;
		String metadataPath= MEATADATA_PATH + DbName  ;
		if (checkIfDatabaseExists(DbName)){
			
			File folder = new File(metadataPath);
			File[] listOfFiles = folder.listFiles();
			
			try {
			FileWriter fileWriter = new FileWriter(ERDConstant.ERD_PATH+"GeneratedERDOutput.txt", true);

			 for (File file : listOfFiles) 
			 {
			        if (file.isFile()) {
			        	
			        	BufferedReader input;
			   
			        	fileWriter.append("------------------------------------------------------------------------------------------------------------------------------\n");
			        	fileWriter.append(String.format("%50s \r\n",file.getName()));
			        	fileWriter.append("------------------------------------------------------------------------------------------------------------------------------\n");
			        	fileWriter.append(String.format("%20s %20s %20s %20s %20s %20s \n","Column Name", "Column Type", "Keys", "Foreign Table Name", "Foreign Column name", "Relationship"));
			        	
						input = new BufferedReader(new FileReader(file));
						
						String line;
						while((line = input.readLine()) != null)
						{
							 final String[] columns = line.split("[\\|()]");
							 for(int i=0; i<columns.length;i++) {
								 fileWriter.append(String.format("%20s ", columns[i]));		
							 }
							 if(columns.length>3 && columns[2].equals(ERDConstant.FOREIGN_KEY)) {
								 fileWriter.append(String.format("%20s ", "1:N"));
							 }
							 fileWriter.append("\n\n");
						}
						input.close();
						
						
			        }
			    }
			 fileWriter.flush();
			 fileWriter.close();
			 System.out.println("ERD Generated");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} else {
			throw new GenerateERDException("Database does not exist!");
			//System.out.println("Database does not exist!");
		}
		return null;
	}

}
