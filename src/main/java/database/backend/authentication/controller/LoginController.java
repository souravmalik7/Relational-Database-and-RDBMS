package database.backend.authentication.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import database.frontend.Printer;

import database.backend.authentication.Session;
import database.backend.authentication.exception.UserAuthenticationException;
import database.backend.authentication.model.User;
import database.security.questions.SecurityQuestions;
import database.backend.authentication.validations.UserValidation;

public class LoginController {

	UserValidation valid;
	
	public LoginController()
	{
		valid = new UserValidation();
	}

	public User userLogin(Scanner sc) throws UserAuthenticationException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		try {
			System.out.println("Enter username or your email");
			String userNameEmail = sc.nextLine();
			
			if(userNameEmail.indexOf("@") != -1)
				valid.userValidation(userNameEmail,"email");
			else
				valid.userValidation(userNameEmail,"username");
			
			System.out.println("Enter password");
			String userPass = sc.nextLine();
			valid.userValidation(userPass,"password");
			
			SecurityQuestions sq = new SecurityQuestions();
			List<String> securityQuestions = new ArrayList<String>();
			List<String> securityAnswers = new ArrayList<String>();
			securityQuestions = sq.getSecurityQuestions();
			Random rand = new Random();
			int num = rand.nextInt(3);
			String securityQuestion = securityQuestions.get(num);
			
			System.out.println("Enter asnwer for " + securityQuestion);
			String answer = sc.nextLine();
			List<String> Securityans = new ArrayList<String>();
			Securityans.add(answer);
			valid.userSecurityValidation(Securityans);
			User UserD =  valid.userLogin(userNameEmail, userPass, answer, num);
			if(UserD != null)
			{
				System.out.println("User logged in as " + UserD.getUsername());
				return UserD;
			}
			else
				throw new UserAuthenticationException("User doesnt exist");
		}
		catch(Exception E)
		{
			System.out.println(E.getMessage());
		}
		return null;
	}

}
