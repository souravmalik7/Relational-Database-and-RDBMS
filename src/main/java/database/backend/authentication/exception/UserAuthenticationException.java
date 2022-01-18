package database.backend.authentication.exception;

public final class UserAuthenticationException extends Exception {

  private final String userErrorMessage;

  public UserAuthenticationException(final String userErrorMessage) {
    super(userErrorMessage);
    this.userErrorMessage = userErrorMessage;
  }

  public String getErrorMessage() {
    return userErrorMessage;
  }

  @Override
  public String toString() {
    return "UserAuthenticationException{" +
        "errorMessage='" + userErrorMessage + '\'' +
        '}';
  }
}
