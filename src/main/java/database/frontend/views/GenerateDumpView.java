package database.frontend.views;

import database.backend.authentication.exception.UserAuthenticationException;
import database.backend.query_processing.exception.QueryProcessException;
import database.backend.sql_dump.DumpCreation;
import database.frontend.Printer;
import database.frontend.UserSession;

import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class GenerateDumpView {
	private Printer printer;
	private Scanner scanner;
	private UserSession session;
	private DumpCreation dumpCreation = new DumpCreation();

	public GenerateDumpView(Printer printer, Scanner scanner, UserSession session) {
		this.printer = printer;
		this.scanner = scanner;
		this.session = session;
	}

	public void generateDumps() throws UserAuthenticationException, IOException, QueryProcessException {
		MainMenuView mainMenuView = new MainMenuView(printer,scanner,session);
		String dbName;
		printer.print("Please enter the database name : ");
		dbName = scanner.nextLine();
		dumpCreation.exportDump(dbName);
		mainMenuView.displayMainMenu();
	}
}
