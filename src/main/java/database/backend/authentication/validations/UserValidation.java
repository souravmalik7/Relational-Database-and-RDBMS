package database.backend.authentication.validations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import database.backend.authentication.constant.ConstantValues;
import database.backend.authentication.exception.UserAuthenticationException;
import database.backend.authentication.model.User;

public class UserValidation {
	
	public void userValidation(String validationString, String valdiationToBePerformed) throws UserAuthenticationException
	{
		if(valdiationToBePerformed.equalsIgnoreCase("email"))
		{
			boolean userEmailValid = userEmailValidation(validationString);
			if(!userEmailValid)
			{
				throw new UserAuthenticationException("Invalid email entered");
			}			
		}
		else if(valdiationToBePerformed.equalsIgnoreCase("username"))
		{
			boolean usernameValid = usernameValidation(validationString);
			if(!usernameValid)
			{
				throw new UserAuthenticationException("Invalid username entered");
			}			
		}
		else if(valdiationToBePerformed.equalsIgnoreCase("password"))
		{
			boolean passwordValid = passwordValidation(validationString);
			if(!passwordValid)
			{
				throw new UserAuthenticationException("Invalid password entered");
			}			
		}
	}
	
	private boolean passwordValidation(String password) {
		boolean valid;
		if(password == null || password.isEmpty())
		{
			valid = false;
		}
		else
			valid =	Pattern.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{6,10}$",password);
		return valid;
	}

	private boolean usernameValidation(String username) 
	{
		boolean valid;
		if(username == null || username.isEmpty())
		{
			valid = false;
		}
		else
			valid =	Pattern.matches("[A-Za-z\\d]+",username);
		return valid;
	}

	public boolean userEmailValidation(String email)
	{
		boolean valid;
		if(email == null || email.isEmpty())
		{
			valid = false;
		}
		else
			valid =	Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9+.]+$",email);
		return valid;
			
	}

	public void userSecurityValidation(List<String> securityAnswers) throws UserAuthenticationException {
		
		boolean valid;
		for(int i=0;i<securityAnswers.size();i++)
		{
			String answer = securityAnswers.get(i);	
			if(answer == null || answer.isEmpty())
			{
				valid = false;
			}
			else
				valid =	Pattern.matches("[A-Za-z\\\\d]+",answer);
			
			if(!valid)
				throw new UserAuthenticationException("Invalid security answer");
		}
		
	}

	public boolean ifUserisvalid(String username, String useremail) throws UserAuthenticationException {
		try (BufferedReader usersFileReader = new BufferedReader(new FileReader(ConstantValues.USERDETAILS_FILE))) {
		      String userDetails;

		      List<String> lst = usersFileReader.lines().collect(Collectors.toList());
		      for(int j=0;j<lst.size();j++)
		      {

		    	  userDetails = lst.get(j);
		         String[] DetailsArr = userDetails.split(" ");
		         boolean userName = DetailsArr[1].equals(username);
		         boolean userEmail = DetailsArr[2].equals(useremail);
		        if (userName || userEmail) {
		        	throw new UserAuthenticationException("User already exist");
		        }
		      }
		      return false;
		    } catch (IOException e) {
		      throw new UserAuthenticationException("Database error!!!!!!!!");
		    }
	}

	public String hashPassword(String userpass) throws NoSuchAlgorithmException, InvalidKeySpecException {
		
		final MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
	    return String.format("%064x", new BigInteger(1, messageDigest.digest(userpass.getBytes(StandardCharsets.UTF_8))));
	}

	public User userLogin(String userNameEmail, String userPass, String answer, int num) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, UserAuthenticationException {
		User UD = null;
		
		try (BufferedReader ReadFile = new BufferedReader(new FileReader(ConstantValues.USERDETAILS_FILE))) {
		      String userDetails = "";
		      boolean userEmail = false,userName = false;
		      int count = 0;
		      List<String> lst = ReadFile.lines().collect(Collectors.toList());
		      //System.out.println(ReadFile.lines().count());
		      
		      System.out.println(lst.size());
		      for(int j=0;j<lst.size();j++)
		      {
		    	  userDetails = lst.get(j);
		         String[] DetailsArr = userDetails.split(" ");
		         if(userNameEmail.indexOf("@") != -1)
		         {
		        	  userEmail = DetailsArr[2].equalsIgnoreCase(userNameEmail);
		        	  userName = true;
		         }
		         else
		         {
		        	  userName = DetailsArr[1].equalsIgnoreCase(userNameEmail);
		        	  userEmail = true;
		         }
		         String hashedNewPass = hashPassword(userPass);
		         boolean hashedpass = hashedNewPass.equalsIgnoreCase(DetailsArr[3]);
		         
		         boolean securityAns = DetailsArr[num + 4].equals(answer);
		         
		         
		         if(userEmail && userName && hashedpass && securityAns)
		         {
		        	 UD = new User(Integer.parseInt(DetailsArr[0]) ,DetailsArr[1], DetailsArr[2], hashPassword(userPass), DetailsArr[4], DetailsArr[5], DetailsArr[6]);
		        	 count++;
		        	 return UD;
		         }		
		}
		if(count == 0)
			throw new UserAuthenticationException("User doesnt exist");
		}
		 catch (IOException e) {
		      throw new UserAuthenticationException("Database error!!!!!!!!");
		    }
		return UD;
	}
}





