package database.backend.query_parsing.constant;

public class QueryParsingConstant {

	public static final String CREATE = "create";
	public static final String SELECT = "select";
	public static final String CREATE_DATABASE = "create database";
	public static final String CREATE_DATABASE_REGEX = "^CREATE\\s+DATABASE\\s+[a-zA-Z*]+;";
	public static final String USE_DATABASE = "use database";
	public static final String USE_DATABASE_REGEX = "^USE\\s+DATABASE\\s+[a-zA-Z*]+;";
	public static final String CREATE_TABLE = "create table";
	public static final String CREATE_TABLE_REGEX = "^CREATE\\s+TABLE\\s+(\\S+)\\s*\\(([^;]+)\\);$";
	public static final String DROP_TABLE = "drop table";
	public static final String DROP_TABLE_REGEX = "^DROP\\s+TABLE\\s+([a-zA-Z]+);";
	public static final String SELECT_ALL = "";
	public static final String SELECT_ALL_QUERY_REGEX = "";
	public static final String SELECT_DISTINCT = "";
	public static final String SELECT_DISTINCT_QUERY_REGEX = "";
	public static final String SELECT_SPECIFIC = "select *";
	public static final String SELECT_SPECIFIC_QUERY_REGEX = "^SELECT\\s[*+]\\sFROM\\s+[a-zA-Z0-9=\\s]*;";
	public static final String INSERT = "insert into";
	public static final String INSERT_QUERY_REGEX = "^INSERT\\s+INTO\\s+(\\S+)\\s*\\(([^;]+)\\);$";
	public static final String DELETE_DATA = "delete from";
	public static final String DELETE_QUERY_REGEX = "^DELETE\\s+FROM\\s+[a-zA-Z=\\.@-_\\s]*+;";
	public static final String UPDATE_DATA = "update";
	public static final String UPDATE_QUERY_REGEX = "^UPDATE\\s+([a-zA-Z])\\s+SET\\s+[a-zA-Z=\\.@-_\\s]\\s+WHERE\\s+[a-zA-Z=\\.@-_\\s]*+;";
	public static final String START_TRANSACTION = "begin transaction";
	public static final String START_TRANSACTION_QUERY = "^BEGIN\\s+TRANSACTION\\s+[a-zA-Z*]+;";
	public static final String COMMIT = "commit";
	public static final String COMMIT_QUERY = "";
	public static final String ROLLBACK = "rollback";
	public static final String ROLLBACK_QUERY = "";
}
