package database.frontend.views;

import database.frontend.Printer;

import java.util.Scanner;

public class MetaDataView {
	private Printer printer;
	private Scanner scanner;

	public MetaDataView(Printer printer, Scanner scanner) {
		this.printer = printer;
		this.scanner = scanner;
	}
}
