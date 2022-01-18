package database.frontend.views;

import database.frontend.Printer;

import java.util.Scanner;

public class ImportDumpView {
	private Printer printer;
	private Scanner scanner;

	public ImportDumpView(Printer printer, Scanner scanner) {
		this.printer = printer;
		this.scanner = scanner;
	}
}
