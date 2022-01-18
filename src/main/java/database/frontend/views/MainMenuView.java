package database.frontend.views;

import database.backend.authentication.exception.UserAuthenticationException;
import database.backend.generate.analytics.generateAnalytics;
import database.backend.query_processing.exception.QueryProcessException;
import database.frontend.Printer;
import database.frontend.UserSession;

import java.io.IOException;
import java.util.Scanner;

public class MainMenuView {

	private Printer printer;
	private Scanner scanner;
	private UserSession session;

	public MainMenuView() {
	}

	public MainMenuView(Printer printer, Scanner scanner, UserSession session) {
		this.printer = printer;
		this.scanner = scanner;
		this.session = session;

		printer.print("Welcome "+session.getUser().getUsername());

	}

	public void displayMainMenu() throws UserAuthenticationException, IOException, QueryProcessException {
		printer.printTitle("Main Menu");
		while (true) {
			printer.print("1. Execute Database Query.");
			printer.print("2. Generate Database Dump.");
			printer.print("3. Generate Entity Relation Diagram.");
			printer.print("4. View Analytics.");
			printer.print("5. Logout.");

			printer.print("Choose an option:");
			String input = scanner.nextLine();

			switch (input) {
				case "1":
					QueryExecutionView queryExecutionView=new QueryExecutionView(printer,scanner,session);
					queryExecutionView.executeQuery("");
					break;
				case "2":
					GenerateDumpView generateDumpView = new GenerateDumpView(printer,scanner,session);
					generateDumpView.generateDumps();
					break;
				case "3":
					GenerateErdView generateErdView= new GenerateErdView(printer,scanner);
					generateErdView.generateERD("");
					break;
				case "4":
					generateAnalytics genAnalytics = new generateAnalytics(printer,scanner,session);
					genAnalytics.generatingAnalytics();
					break;
				case "5":
					session.destroySession();
					System.exit(0);
					return;
				default:
					break;
			}
		}
	}
}
