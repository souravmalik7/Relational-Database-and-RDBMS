package database.backend.authentication.controller;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import database.backend.authentication.constant.ConstantValues;
import database.backend.authentication.exception.UserAuthenticationException;
import database.backend.authentication.validations.UserValidation;
import database.security.questions.SecurityQuestions;

public class RegisterationController {
	
	UserValidation valid;
	
	public RegisterationController()
	{
		valid = new UserValidation();
	}
	
	public void userRegisteration(Scanner sc) throws UserAuthenticationException, InvalidKeySpecException {
		try {
			System.out.println("Enter username in alphnumeric characters only");
			String username = sc.nextLine();
			valid.userValidation(username,"username");
			System.out.println("Enter email");
			String useremail = sc.nextLine();
			valid.userValidation(useremail,"email");
			System.out.println("Enter password *1 Capital letter,1 Small letter,1 special character,1 digit, min 6 length - max 10 length");
			String userpass = sc.nextLine();
			valid.userValidation(userpass,"password");
			
			SecurityQuestions sq = new SecurityQuestions();
			List<String> securityQuestions = new ArrayList<String>();
			List<String> securityAnswers = new ArrayList<String>();
			securityQuestions = sq.getSecurityQuestions();
			
			for(int i=0;i<securityQuestions.size();i++)
			{
				System.out.println(securityQuestions.get(i));
				String answer = sc.nextLine();
				securityAnswers.add(answer);		
			}
			
			valid.userSecurityValidation(securityAnswers);
			
			valid.ifUserisvalid(username,useremail);
			
			String userRegistered = userDetailsIntoFile(username,useremail,userpass,securityAnswers);
			if(userRegistered.equalsIgnoreCase("Registered successfull"))
				System.out.println(username + " registered successfully");
			else
				System.out.println(username + " registeration unsuccessfull");	
		}
		catch(Exception E)
		{
			System.out.println(E.getMessage());
		}
		
	}

	
	public String userDetailsIntoFile(String username, String useremail, String userpass, List<String> securityAnswers)
		      throws UserAuthenticationException, InvalidKeySpecException {
		    try (final FileWriter fileWriter = new FileWriter(ConstantValues.USERDETAILS_FILE, true)) {
		      long linecount = Files.lines(Paths.get(ConstantValues.USERDETAILS_FILE)).count();
		      long newuserId = linecount + 1;
		      
		      String hashedpassword = valid.hashPassword(userpass);
		      String newUserDetails = newuserId + " " + username + " " + useremail + " " + hashedpassword;
		      
		      for(int i=0;i<securityAnswers.size();i++)
				{
		    	  newUserDetails = newUserDetails + " " + securityAnswers.get(i);
				}
		      fileWriter.append('\n');
		      fileWriter.append(newUserDetails);
		      return "Registered successfull";
		    } catch (final IOException | NoSuchAlgorithmException e) {
		      throw new UserAuthenticationException("Exception thrown, try after sometime");
		    }
		  }


}
