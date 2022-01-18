package database.frontend.views;

import database.frontend.Printer;

import java.util.Scanner;

public class DataDictionaryView {
	private Printer printer;
	private Scanner scanner;

	public DataDictionaryView(Printer printer, Scanner scanner) {
		this.printer = printer;
		this.scanner = scanner;
	}
}
