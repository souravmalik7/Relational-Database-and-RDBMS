package database;

import database.backend.authentication.Session;
import database.backend.authentication.controller.LoginController;
import database.backend.authentication.controller.RegisterationController;
import database.backend.authentication.exception.UserAuthenticationException;
import database.backend.authentication.model.User;
import database.backend.query_processing.exception.QueryProcessException;
import database.frontend.Printer;
import database.frontend.UserSession;
import database.frontend.views.MainMenuView;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class EntryPoint {

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, UserAuthenticationException, IOException, QueryProcessException {

        //printer and scanner objects
        Printer printer=new Printer();
        Scanner scanner = new Scanner(System.in);
        LoginController loginCont = new LoginController();
        RegisterationController registerCont = new RegisterationController();

        printer.printTitle("Welcome to database: ");

        while (true) {

            printer.print("1. User login.");
            printer.print("2. User Sign up.");
            printer.print("3. Exit.");
            printer.print("Select an option:");

            String userInput = scanner.nextLine();
            UserSession session= null;

            switch (userInput) {
                case "1":
                	User user = loginCont.userLogin(scanner);

                	if(user!= null)
                	{
                      session= new UserSession(user);
                      Session.setUsername(user.getUsername());
                      MainMenuView mainMenuView = new MainMenuView(printer, scanner, session);
                      mainMenuView.displayMainMenu();
                    }
                    break;
                case "2":
                	registerCont.userRegisteration(scanner);
                    break;
                case "3":
                    if(session!=null)
                        session.destroySession();
                    System.exit(0);
                default:
                    break;
            }
        }
    }
}
