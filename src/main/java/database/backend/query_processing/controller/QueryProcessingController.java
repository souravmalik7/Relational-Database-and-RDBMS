package database.backend.query_processing.controller;

import database.backend.query_processing.exception.QueryProcessException;
import database.backend.authentication.Session;
import database.backend.authentication.constant.ConstantValues;
import database.backend.authentication.exception.UserAuthenticationException;
import database.backend.authentication.model.User;
import database.backend.logger.controller.LogController;
import database.backend.query_parsing.controller.QueryParsingController;
import database.backend.query_parsing.exception.QueryParsingException;
import database.backend.query_processing.constant.QueryProcessingConstants;
import database.backend.query_processing.model.QueryResponse;
import database.backend.sql_dump.DumpCreation;
import database.frontend.Printer;
import database.frontend.UserSession;
import database.frontend.views.MainMenuView;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static database.backend.logger.constant.LogConstant.*;
import static database.backend.query_parsing.constant.QueryParsingConstant.CREATE_TABLE_REGEX;
import static database.backend.query_parsing.constant.QueryParsingConstant.INSERT_QUERY_REGEX;
import static database.backend.query_processing.constant.QueryProcessingConstants.*;

public class QueryProcessingController {

    DumpCreation dumpCreation = new DumpCreation();
    private final LogController logController;
    private final QueryParsingController queryParserController;
    private final UserSession session;
    Printer printer;
    Scanner scanner;
    private static String currentDatabase = null;
    private static int dbcount = 0;
    private static int querycount = 0;


    public static String getCurrentDatabase() {
        return currentDatabase;
    }

    public static void setCurrentDatabase(String currentDatabase) {
        QueryProcessingController.currentDatabase = currentDatabase;
    }


    public QueryProcessingController(Printer printer, Scanner scanner, UserSession session2) {
        this.queryParserController = new QueryParsingController();
        this.logController = new LogController();
        this.printer = printer;
        this.scanner = scanner;
        this.session = session2;
    }

    public QueryResponse processQuery(String query) throws QueryProcessException, QueryParsingException, UserAuthenticationException, IOException {

        logController.log("Query processing started", GENERAL_LOG);

        try {
            queryParserController.parse(query);

        } catch (QueryParsingException e) {
            logController.log("Invalid query: " + e.getErrorMessage(), GENERAL_LOG);
            throw e;
        }

        if (query.contains("CREATE DATABASE")) {
            return creteDatabase(query);
        } else if (query.contains("USE DATABASE")) {
            return useDatabase(query);

        } else if (currentDatabase == null) {
            throw new QueryProcessException("Select database first");
        } else if (query.contains("DROP TABLE")) {
            return dropTable(query);
        } else if (query.contains("SELECT")) {
            return selectTable(query);
        } else if (query.contains("CREATE TABLE")) {
            return createTable(query);
        } else if (query.contains("UPDATE")) {
            return updateData(query);
        } else if (query.contains("INSERT INTO")) {
            return insertIntoTable(query);
        } else if (query.contains("DELETE FROM")) {
            return deleteRow(query);
        } else if (query.contains("BEGIN TRANSACTION")) {
            return executeTransaction(query);
        } else {
            return null;
        }
    }

    private QueryResponse executeTransaction(String query) throws QueryParsingException, IOException, UserAuthenticationException, QueryProcessException {
        MainMenuView mainMenuView = new MainMenuView();
        QueryParsingController queryParsingController = new QueryParsingController();
        ArrayList<String> transactionQueries = new ArrayList<>();
        ArrayList<String> tableNames = new ArrayList<>();
        boolean isCorrect = false;
        boolean lock = false;

        System.out.println("Please enter query: ");
        String sql = scanner.nextLine();

        while (!(sql.toLowerCase().contains("commit")) && !(sql.toLowerCase().contains("rollback"))) {
            String tableName = null;
            String[] queryWords = sql.split(" ");
            transactionQueries.add(sql);

            if (sql.toLowerCase().contains("insert")) {
                tableName = queryWords[2];
            } else if (sql.toLowerCase().contains("update")) {
                tableName = queryWords[1];
            } else if (sql.toLowerCase().contains("create")) {
                tableName = queryWords[2];
            } else if (sql.toLowerCase().contains("delete")) {
                tableName = queryWords[2];
            }

            if (!tableNames.contains(tableName)) {
                tableNames.add(tableName);
            }

            sql = scanner.nextLine();
        }

        if (sql.toLowerCase().contains("rollback")) {
            printer.print("rollback demanded. Cleaning the history.");
            transactionQueries.clear();
        } else {
            for (String sqlQuery : transactionQueries) {
                isCorrect = queryParsingController.parse(sqlQuery);
                if (isCorrect == false) {
                    System.out.println("Invalid query. Please check the syntax.");
                    break;
                } else {
                    isCorrect = true;
                }
            }

            if (isCorrect == true) {
                try {
                    String line;
                    File file = new File("src/main/java/database/filesystem/Transactions/TransactionTable.txt");

                    if (file.length() > 0) {
                        FileReader fileReader = new FileReader("src/main/java/database/filesystem/Transactions/TransactionTable.txt");
                        BufferedReader bufferedReader = new BufferedReader(fileReader);

                        while ((line = bufferedReader.readLine()) != null) {
                            String storedTableName = line;
                            if (tableNames.contains(line)) {
                                lock = true;
                                break;
                            }
                        }
                        fileReader.close();
                        bufferedReader.close();
                    } else if (file.length() == 0 || lock == false) {
                        FileWriter fileWriter = new FileWriter("src/main/java/database/filesystem/Transactions/TransactionTable.txt", true);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                        for (String name : tableNames) {
                            bufferedWriter.append(name);
                            bufferedWriter.append("\n");
                            bufferedWriter.flush();
                            logController.log("Added locks to table", GENERAL_LOG);
                        }
                        bufferedWriter.close();
                        fileWriter.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (lock == false) {
                    boolean commit = false;
                    for (String sqlQuery : transactionQueries) {
                        try {
                            if (sqlQuery.toLowerCase().contains("insert")) {
                                insertIntoTable(sqlQuery);
                                commit = true;
                            } else if (sqlQuery.toLowerCase().contains("update")) {
                                updateData(sqlQuery);
                                commit = true;
                            } else if (sqlQuery.toLowerCase().contains("create")) {
                                createTable(sqlQuery);
                                commit = true;
                            } else if (sqlQuery.toLowerCase().contains("delete")) {
                                deleteRow(sqlQuery);
                                commit = true;
                            } else {
                                printer.print("invalid query");
                                commit = false;
                                break;
                            }
                        } catch (Exception e) {
                            printer.print("query processing failed, invalid query");
                            commit = false;
                            break;
                        }
                    }
                    if (commit == false) {
                        printer.print("query is not correct, exiting...");
                        mainMenuView.displayMainMenu();
                        return new QueryResponse(0, "Transaction failed");
                    } else {
                        printer.print("Commit performed successfully");
                        try {
                            String input = null;
                            String result;
                            String filePath = "src/main/java/database/filesystem/Transactions/TransactionTable.txt";
                            Scanner sc = new Scanner(new File(filePath));
                            StringBuffer sb = new StringBuffer();
                            while (sc.hasNextLine()) {
                                input = sc.nextLine();
                                sb.append(input);
                            }
                            result = sb.toString();
                            for (String table : tableNames) {
                                result = result.replaceAll(table, "");
                            }
                            PrintWriter writer = new PrintWriter("src/main/java/database/filesystem/Transactions/TransactionTable.txt");
                            writer.append(result);
                            writer.flush();
                            logController.log("Removed locks from table", GENERAL_LOG);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    System.out.println("The transaction cannot proceed because the table is being updated by another user.");
                    return new QueryResponse(0, "Transaction failed");
                }

            } else {
                return new QueryResponse(0, "Transaction failed");
            }
        }
        return new QueryResponse(QueryProcessingConstants.SUCCESS_STATUS, "Transaction executed");
    }

    private QueryResponse insertIntoTable(String query) throws QueryProcessException, UserAuthenticationException, IOException {

        Matcher matcher = Pattern.compile(INSERT_QUERY_REGEX).matcher(query);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            String tableColumns = matcher.group(2);

            String tablePath = DATABASE_PATH + "/" + currentDatabase + "/" + tableName;
            String[] sepValues = tableColumns.split(INSERT_VALUES_SEPERATOR);

            if (sepValues.length == 2) {
                String columnsNames = sepValues[0].trim().replace(")", "").replace(" ", "");
                String columnsValues = sepValues[1].trim().replace("(", "").replace("'", "").replace("\"", "");

                String[] columns = columnsNames.split(",");
                String[] values = columnsValues.split(",");
                StringBuilder raw = new StringBuilder();

                if (columns.length != values.length) {
                    throw new QueryProcessException("columns and values miss match or syntax error");
                }

                LinkedHashMap<String, String> metaData = getMetaData(getCurrentDatabase(), tableName);
                LinkedHashMap<String, String> ConstraintData = getConstraintData(getCurrentDatabase(), tableName);

                Set<String> keys = metaData.keySet();
                int col = 0;
                for (String key : keys) {

                    int dataIndex = Arrays.asList(columns).indexOf(key);
                    if (dataIndex == -1) {
                        throw new QueryProcessException("Column not found ");
                    }
                    String data = values[dataIndex].trim();

                    try {
                        switch (metaData.get(key)) {

                            case INT_DATATYPE:
                                int rawIntData = Integer.parseInt(data);
                                break;
                            case FLOAT_DATATYPE:
                                float rawFloatData = Float.parseFloat(data);
                                break;
                            case BINARY_DATATYPE:
                                boolean rawBooleanData = Boolean.parseBoolean(data);
                                break;
                        }
                    } catch (Exception e) {
                        throw new QueryProcessException("Error while parsing data,Invalid datatype ");
                    }
                    if (ConstraintData.get(key) != "") {
                        //handle primary key
                        if (ConstraintData.get(key).trim().equals("PK")) {
                            if (checkKeyExist(tablePath, data.trim(), col)) {
                                throw new QueryProcessException("Data with value: " + data + " Already Exist in table");
                            }
                        }
                        //handle Foreign key
                        else {
                            String[] fk = ConstraintData.get(key).split(" ");
                            String table = fk[1].split("\\(")[0];
                            String columnName = fk[1].split("\\(")[1].replace(")", "");
                            String path = DATABASE_PATH + "/" + currentDatabase + "/" + table;

                            int indColumn = getColumnIndex(path, columnName);

                            if (!checkKeyExist(path, data, indColumn)) {
                                throw new QueryProcessException("Data with value: " + data + " Not Exist in REFERENCE table");
                            }
                        }
                    }
                    raw.append(data + SEPERATOR);
                    col += 1;
                }
                try (FileWriter fw = new FileWriter(tablePath, true);
                     BufferedWriter bw = new BufferedWriter(fw);
                     PrintWriter out = new PrintWriter(bw)) {
                    raw.setLength(raw.length() - 1);
                    out.println(raw);
                } catch (IOException e) {
                    throw new QueryProcessException("Error while inserting data in file");
                }
            }
            dumpCreation.createDataDump(query);
            logController.log("Data Inserted Successfully in " + tableName, GENERAL_LOG);
            logController.log(currentDatabase + " : " + query, QUERY_LOG);
            dbcount = dbcount + 1;
            User user = session.getUser();
            writeToDbQueryFile(dbcount, user, currentDatabase);
            return new QueryResponse(QueryProcessingConstants.SUCCESS_STATUS, "Data inserted ");
        }
        throw new QueryProcessException("Error while processing query, Invalid Query");
    }

    private int getColumnIndex(String path, String columnName) throws QueryProcessException {

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            String[] metaValues = line.split(SEPERATOR);// this error took my 30 mins (๑′°︿°๑)
            int count = 0;
            while (count < metaValues.length) {
                if (columnName.trim().equals(metaValues[count])) {
                    return count;
                }
                count += 1;
            }
        } catch (IOException e) {
            throw new QueryProcessException("Invalid Foreign table Name");
        }
        throw new QueryProcessException("Column not found in Foreign table");
    }

    private boolean checkKeyExist(String tablePath, String data, int column) throws QueryProcessException {

        try (BufferedReader br = new BufferedReader(new FileReader(tablePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] metaValues = line.split(SEPERATOR);// this error took my 30 mins (๑′°︿°๑)

                if (metaValues[column].equals(data)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new QueryProcessException("Invalid table ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private LinkedHashMap<String, String> getConstraintData(String currentDatabase, String tableName) throws QueryProcessException {

        LinkedHashMap<String, String> metaData = new LinkedHashMap<>();
        String tableMetaPath = MEATADATA_PATH + "/" + currentDatabase + "/" + tableName;

        try (BufferedReader br = new BufferedReader(new FileReader(tableMetaPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] metaValues = line.split("\\|");// this error took my 30 mins (๑′°︿°๑)
                if (metaValues.length == 3) {
                    metaData.put(metaValues[0], metaValues[2]);
                } else if (metaValues.length == 4) {
                    metaData.put(metaValues[0], metaValues[2] + " " + metaValues[3].replace(" ", ""));
                } else {
                    metaData.put(metaValues[0], "");
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new QueryProcessException("Invalid table ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return metaData;
    }

    private LinkedHashMap<String, String> getMetaData(String currentDatabase, String tableName) throws QueryProcessException {
        LinkedHashMap<String, String> metaData = new LinkedHashMap<>();
        String tableMetaPath = MEATADATA_PATH + "/" + currentDatabase + "/" + tableName;

        try (BufferedReader br = new BufferedReader(new FileReader(tableMetaPath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] metaValues = line.split("\\|");   // this error took my 30 mins (๑′°︿°๑)
                metaData.put(metaValues[0], metaValues[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new QueryProcessException("Invalid table ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return metaData;

    }

    private QueryResponse createTable(String query) throws QueryProcessException, UserAuthenticationException {

        Matcher matcher = Pattern.compile(CREATE_TABLE_REGEX).matcher(query);

        if (matcher.find()) {
            String tableName = matcher.group(1);
            String tableColumns = matcher.group(2);

            String[] columnsInfo = tableColumns.split(",");

            String tablePath = DATABASE_PATH + "/" + currentDatabase + "/" + tableName;
            String tableMetaPath = MEATADATA_PATH + "/" + currentDatabase + "/" + tableName;
            File tableFile = new File(tablePath);
            File tablemetafile = new File(tableMetaPath);

            if (tableFile.exists() && tablemetafile.exists()) {
                throw new QueryProcessException("Table Already Exist");
            }
            try {
                tableFile.createNewFile();
                tablemetafile.createNewFile();

                StringBuilder tableHeader = new StringBuilder();
                StringBuilder metaData = new StringBuilder();

                for (String column : columnsInfo) {

                    String[] col = column.trim().split(" ");

                    if (column.contains(PRIMARY_KEY) && col.length == 4) {

                        String tempCol = col[0] + META_SEPERATOR + col[1] + META_SEPERATOR + "PK" + NEWLINE_SEPERATOR;
                        tableHeader.append(col[0] + SEPERATOR);
                        metaData.append(tempCol);

                    } else if (column.contains(FOREIGN_KEY) && column.contains(REFERENCES) && col.length == 6) {

                        String table = col[5].split("\\(")[0];
                        String columnName = col[5].split("\\(")[1].replace(")", "");
                        String path = DATABASE_PATH + "/" + currentDatabase + "/" + table;

                        try {
                            getColumnIndex(path, columnName);
                        } catch (QueryProcessException e) {
                            tableFile.delete();
                            tablemetafile.delete();
                            throw e;
                        }

                        String tempCol = col[0] + META_SEPERATOR + col[1] + META_SEPERATOR + "FK" + META_SEPERATOR + col[5] + NEWLINE_SEPERATOR;
                        tableHeader.append(col[0] + SEPERATOR);
                        metaData.append(tempCol);

                    } else if (col.length == 2) {
                        String tempCol = col[0] + META_SEPERATOR + col[1] + NEWLINE_SEPERATOR;
                        tableHeader.append(col[0] + SEPERATOR);
                        metaData.append(tempCol);
                    } else {
                        throw new QueryProcessException("Invalid columns or Syntax Error");
                    }
                }
                tableHeader.setLength(tableHeader.length() - 1);
                tableHeader.append(NEWLINE_SEPERATOR);
                FileWriter fileWriter = new FileWriter(tablePath);
                FileWriter metaWriter = new FileWriter(tableMetaPath);

                fileWriter.append(tableHeader);
                metaWriter.append(metaData);
                fileWriter.close();
                metaWriter.close();

                dumpCreation.createDataDump(query);
                dbcount = dbcount + 1;
                User user = session.getUser();
                writeToDbQueryFile(dbcount, user, currentDatabase);
                querycount = querycount + 1;
                writeToIndQueryFile(querycount, user, currentDatabase, tableName);
                logController.log("Table Created Successfully,: " + tableName, GENERAL_LOG);
                logController.log(currentDatabase + " : " + query, QUERY_LOG);
                return new QueryResponse(QueryProcessingConstants.SUCCESS_STATUS, "SUCCESS");
            } catch (IOException e) {
                throw new QueryProcessException("Error while creeating Table : " + tableName);
            }
        } else {
            throw new QueryProcessException("Error while executing create table query");
        }
    }

    private QueryResponse useDatabase(String query) throws QueryProcessException {
        String message;
        String[] queryList = query.split(" ");
        String databaseName = queryList[2].replace(";", "");
        String databasePath = DATABASE_PATH + databaseName;
        File database = new File(databasePath);
        if (database.isDirectory()) {
            QueryProcessingController.setCurrentDatabase(databaseName);
            dumpCreation.createDataDump(query);
            logController.log(currentDatabase + " : " + query, QUERY_LOG);
            return new QueryResponse(QueryProcessingConstants.SUCCESS_STATUS, "SUCCESS");

        } else {
            message = "Database does not exist!";
            logController.log(message, EVENT_LOG);
            throw new QueryProcessException(message);
        }
    }

    private QueryResponse creteDatabase(String query) throws QueryProcessException {

        String[] queryList = query.split(" ");
        String databaseName = queryList[2].replace(";", "");
        String databasePath = DATABASE_PATH + databaseName;
        String metadataPath = MEATADATA_PATH + databaseName;

        File database = new File(databasePath);
        File metadata = new File(metadataPath);

        if (database.isDirectory() || metadata.isDirectory()) {
            logController.log("Database already Exist : " + databaseName, GENERAL_LOG);
            throw new QueryProcessException("Database Already Exist: " + databaseName);
        }

        boolean isDirectoryCreated = database.mkdir();

        // Generate directory for metadata

        boolean isMetadataCreated = metadata.mkdir();

        if (isDirectoryCreated && isMetadataCreated) {
            dumpCreation.createDataDump(query);
            logController.log("Database created: " + databaseName, GENERAL_LOG);
            logController.log(currentDatabase + " : " + query, QUERY_LOG);
            return new QueryResponse(QueryProcessingConstants.SUCCESS_STATUS, "SUCCESS");
        } else {
            String message = "something went wrong while creating database: " + databaseName;
            logController.log(message, GENERAL_LOG);
            throw new QueryProcessException(message);
        }

    }

    private QueryResponse dropTable(String query) throws QueryProcessException, UserAuthenticationException, IOException {
        String message;
        boolean isDeleted = Boolean.FALSE;
        if (getCurrentDatabase() == null) {
            throw new QueryProcessException("Select Database first");
        }
        String[] queryList = query.split(" ");
        String tableName = queryList[2].replace(";", "");

        String tablePath = DATABASE_PATH + "/" + currentDatabase + "/";
        String tableMetaPath = MEATADATA_PATH + "/" + currentDatabase + "/" + tableName;


        File tableDirectory = new File(tablePath);

        File[] tables = tableDirectory.listFiles();
        if (tables == null) {
            message = "Table " + tableName + " does not exist";
            logController.log(message, EVENT_LOG);
            throw new QueryProcessException(message);
        }
        for (File table : tables) {
            if (table.getName().equalsIgnoreCase(tableName)) {
                isDeleted = table.delete();
                break;
            }
        }
        if (isDeleted) {
            message = "Table deleted successfully";
            File myMeta = new File(tableMetaPath);
            myMeta.delete();
            dumpCreation.createDataDump(query);
            logController.log(message, GENERAL_LOG);
            logController.log(currentDatabase + " : " + query, QUERY_LOG);
            dbcount = dbcount + 1;
            User user = session.getUser();
            writeToDbQueryFile(dbcount, user, currentDatabase);
            querycount = querycount + 1;
            writeToIndQueryFile(querycount, user, currentDatabase, tableName);
            return new QueryResponse(QueryProcessingConstants.SUCCESS_STATUS, "SUCCESS");
        } else {
            message = "Error Deleting Table";
            logController.log(message, EVENT_LOG);
            throw new QueryProcessException(message);
        }
    }


    private static List<String> getColumn(String line) {
        return Arrays.asList(line.split("#"));
    }

    private boolean isTable(String tableName) throws QueryProcessException {
        String message;
        boolean isAvailable = Boolean.FALSE;
        String tablePath = DATABASE_PATH + currentDatabase;
        File tableDirectory = new File(tablePath);
        File[] tables = tableDirectory.listFiles();
        if (tables == null) {
            message = "Table " + tableName + " does not exist";
            logController.log(message, EVENT_LOG);
            throw new QueryProcessException(message);
        }
        for (File table : tables) {
            if (table.getName().equalsIgnoreCase(tableName)) {
                isAvailable = Boolean.TRUE;
                break;
            }
        }
        if (!isAvailable) {
            throw new QueryProcessException("Table " + tableName + " does not exist");
        }
        return isAvailable;
    }

    private QueryResponse selectTable(String query) throws QueryProcessException, UserAuthenticationException {
        String message;
        boolean isAvailable = Boolean.FALSE;
        String line;
        String whereColumn = "";
        String whereCondition = "";
        StringBuilder stringBuilder = new StringBuilder();

        if (currentDatabase == null) {
            throw new QueryProcessException("Select Database first");
        }

        query = query.replace(";", "");
        List<String> queryStringList = Arrays.asList(query.split(" "));
        int indexOfFromKeyword = queryStringList.indexOf("FROM");
        String tableName = queryStringList.get(indexOfFromKeyword + 1);
        isAvailable = isTable(tableName);

        if (queryStringList.contains("WHERE")) {
            String[] conditionColumn = queryStringList.get(indexOfFromKeyword + 3).split("=");
            whereColumn = conditionColumn[0];
            whereCondition = conditionColumn[1];
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(DATABASE_PATH + currentDatabase + "/" + tableName));) {
            if (queryStringList.contains("WHERE")) {
                // select with where condition
                int count = 0;
                List<String> columnNames;
                int indexOfConditionColumn = 0;
                while ((line = br.readLine()) != null) {
                    List<String> stringList = Arrays.asList(line.split("#"));
                    if (count == 0) {
                        columnNames = getColumn(line);
                        if (!columnNames.contains(whereColumn)) {
                            throw new QueryProcessException("Given Column is not available in table");
                        }
                        indexOfConditionColumn = columnNames.indexOf(whereColumn);
                        stringBuilder.append(String.join(" ", stringList)).append("\n");
                    } else {
                        if (stringList.get(indexOfConditionColumn).equals(whereCondition)) {
                            stringBuilder.append(String.join(" ", stringList)).append("\n");
                        }
                    }
                    count++;
                }
            } else {
                // select without where condition
                while ((line = br.readLine()) != null) {
                    List<String> stringList = Arrays.asList(line.split("#"));
                    stringBuilder.append(String.join(" ", stringList)).append("\n");
                }
            }
            printer.print(stringBuilder.toString());
            dumpCreation.createDataDump(query);
            logController.log(currentDatabase + " : " + query, QUERY_LOG);
            dbcount = dbcount + 1;
            User user = session.getUser();
            writeToDbQueryFile(dbcount, user, currentDatabase);
            return new QueryResponse(QueryProcessingConstants.SUCCESS_STATUS, "SUCCESS");
        } catch (IOException e) {
            message = "Select Query Failed";
            printer.print(e.toString());
            logController.log(message, EVENT_LOG);
            throw new QueryProcessException(message);
        }
    }


    private QueryResponse deleteRow(String query) throws QueryProcessException {
        String message;
        boolean isAvailable = Boolean.FALSE;
        String line;
        String whereColumn = "";
        String whereCondition = "";

        if (getCurrentDatabase() == null) {
            throw new QueryProcessException("Select Database first");
        }

        query = query.replace(";", "");
        List<String> queryStringList = Arrays.asList(query.split(" "));
        int indexOfFromKeyword = queryStringList.indexOf("FROM");
        String tableName = queryStringList.get(indexOfFromKeyword + 1);

        isAvailable = isTable(tableName);

        if (queryStringList.contains("WHERE")) {
            String[] conditionColumn = queryStringList.get(indexOfFromKeyword + 3).split("=");
            whereColumn = conditionColumn[0];
            whereCondition = conditionColumn[1];
        }

        File inputFile = new File(DATABASE_PATH + getCurrentDatabase() + "/" + tableName);
        File tempFile = new File(DATABASE_PATH + getCurrentDatabase() + "/" + tableName + "_temp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        ) {
            if (queryStringList.contains("WHERE")) {
                // delete for where condition
                int count = 0;
                List<String> columnNames;
                int indexOfConditionColumn = 0;
                while ((line = reader.readLine()) != null) {
                    List<String> stringList = Arrays.asList(line.split("#"));
                    if (count == 0) {
                        columnNames = getColumn(line);
                        if (!columnNames.contains(whereColumn)) {
                            throw new QueryProcessException("Given Column is not available in table");
                        }
                        indexOfConditionColumn = columnNames.indexOf(whereColumn);
                        writer.write(line + "\n");
                    } else {
                        if (stringList.get(indexOfConditionColumn).equals(whereCondition)) continue;
                        writer.write(line + "\n");
                    }
                    count++;
                }
            }
            writer.close();
            reader.close();
            Path source = Paths.get(DATABASE_PATH + getCurrentDatabase() + "/" + tableName + "_temp");
            inputFile.delete();
            Files.move(source, source.resolveSibling(tableName));
            message = "Delete query executed";
            dumpCreation.createDataDump(query);
            logController.log(message, GENERAL_LOG);
            logController.log(currentDatabase + " : " + query, QUERY_LOG);
            dbcount = dbcount + 1;
            User user = session.getUser();
            writeToDbQueryFile(dbcount, user, currentDatabase);
            querycount = querycount + 1;
            writeToIndQueryFile(querycount, user, currentDatabase, tableName);
            return new QueryResponse(QueryProcessingConstants.SUCCESS_STATUS, "SUCCESS");
        } catch (Exception e) {
            message = "Delete Query failed.";
            e.printStackTrace();
            logController.log(message, EVENT_LOG);
            throw new QueryProcessException(message);
        }
    }

    private QueryResponse updateData(String query) throws QueryProcessException {
        String message;
        String updateQuery;
        boolean isAvailable = Boolean.FALSE;
        String line;
        String setColumn = "";
        String setCondition = "";
        String whereColumn = "";
        String whereCondition = "";

        if (getCurrentDatabase() == null) {
            throw new QueryProcessException("Select Database first");
        }

        updateQuery = query;
        query = query.replace(";", "");
        List<String> queryStringList = Arrays.asList(query.split(" "));
        String tableName = queryStringList.get(1);
        String[] conditionColumn = queryStringList.get(3).split("=");
        setColumn = conditionColumn[0];
        setCondition = conditionColumn[1];
        conditionColumn = queryStringList.get(5).split("=");
        whereColumn = conditionColumn[0];
        whereCondition = conditionColumn[1];

        isAvailable = isTable(tableName);


        File inputFile = new File(DATABASE_PATH + getCurrentDatabase() + "/" + tableName);
        File tempFile = new File(DATABASE_PATH + getCurrentDatabase() + "/" + tableName + "_temp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        ) {
            // delete for where condition
            int count = 0;
            List<String> columnNames;
            int indexOfConditionColumn = 0;
            int indexOfSetColumn = 0;
            while ((line = reader.readLine()) != null) {
                List<String> stringList = Arrays.asList(line.split("#"));
                if (count == 0) {
                    columnNames = getColumn(line);
                    if (!columnNames.contains(whereColumn) || !columnNames.contains(setColumn)) {
                        throw new QueryProcessException("Given Column is not available in table");
                    }
                    indexOfConditionColumn = columnNames.indexOf(whereColumn);
                    indexOfSetColumn = columnNames.indexOf(setColumn);
                } else {
                    if (stringList.get(indexOfConditionColumn).equals(whereCondition)) {
                        stringList.set(indexOfSetColumn, setCondition);
                        writer.write(String.join("#", stringList) + "\n");
                        continue;
                    }
                }
                writer.write(line + "\n");
                count++;
            }


            writer.close();
            reader.close();
            Path source = Paths.get(DATABASE_PATH + getCurrentDatabase() + "/" + tableName + "_temp");
            inputFile.delete();
            Files.move(source, source.resolveSibling(tableName));
            message = "Update query executed";
            dumpCreation.processUpdateQueries(tableName, setCondition, whereCondition, setColumn, whereColumn);
            logController.log(message, GENERAL_LOG);
            logController.log(currentDatabase + " : " + query, QUERY_LOG);
            dbcount = dbcount + 1;
            User user = session.getUser();
            writeToDbQueryFile(dbcount, user, currentDatabase);
            querycount = querycount + 1;
            writeToIndQueryFile(querycount, user, currentDatabase, tableName);
            return new QueryResponse(QueryProcessingConstants.SUCCESS_STATUS, "SUCCESS");
        } catch (Exception e) {
            message = "Update Query failed.";
            e.printStackTrace();
            logController.log(message, EVENT_LOG);
            throw new QueryProcessException(message);
        }
    }

    private void writeToIndQueryFile(int querycount2, User user, String currentDatabase2, String tablename) throws UserAuthenticationException {
        List<String> lst = new ArrayList<String>();
        try (BufferedReader usersFileReader = new BufferedReader(new FileReader(ConstantValues.USERINDQUERIES_FILE))) {
            String userDetails;
            lst = usersFileReader.lines().collect(Collectors.toList());
            usersFileReader.close();
        } catch (IOException e) {
            throw new UserAuthenticationException("Database error!!!!!!!!");
        }

        StringBuffer inputBuffer = new StringBuffer();
        String flag = "N";
        String line;

        try (FileOutputStream fileOut = new FileOutputStream(ConstantValues.USERINDQUERIES_FILE)) {

            for (int i = 0; i < lst.size(); i++) {
                line = lst.get(i);
                String[] UserDetails = line.split(" ");
                if (UserDetails[1].equalsIgnoreCase(user.getUsername()) && UserDetails[7].equalsIgnoreCase(tablename)) {
                    querycount2 = querycount2 + Integer.parseInt(UserDetails[3]);
                    String replacednumber = Integer.toString(querycount2);
                    line = line.replace(UserDetails[3], Integer.toString(querycount2));

                    flag = "Y";
                }
                inputBuffer.append(line);
                inputBuffer.append('\n');

            }
            if (flag.equalsIgnoreCase("N")) {
                line = "User " + user.getUsername() + " submitted " + querycount2 + " CRUD queries on " + tablename + " on database " + currentDatabase2;
                inputBuffer.append(line);
                inputBuffer.append('\n');
            }
            fileOut.write(inputBuffer.toString().getBytes());
            fileOut.close();
        } catch (IOException e) {
            throw new UserAuthenticationException("Database error!!!!!!!!");
        }
    }


    private void writeToDbQueryFile(int dbcount2, User user, String currentDatabase2) throws UserAuthenticationException, IOException {
        {
            List<String> lst = new ArrayList<String>();
            try (BufferedReader usersFileReader = new BufferedReader(new FileReader(ConstantValues.USERDBQUERIES_FILE))) {
                String userDetails;
                lst = usersFileReader.lines().collect(Collectors.toList());
                usersFileReader.close();
            } catch (IOException e) {
                throw new UserAuthenticationException("Database error!!!!!!!!");
            }

            StringBuffer inputBuffer = new StringBuffer();
            String flag = "N";
            String line;

            try (FileOutputStream fileOut = new FileOutputStream(ConstantValues.USERDBQUERIES_FILE)) {

                for (int i = 0; i < lst.size(); i++) {
                    line = lst.get(i);
                    String[] UserDetails = line.split(" ");
                    if (UserDetails[1].equalsIgnoreCase(user.getUsername())) {
                        dbcount2 = dbcount2 + Integer.parseInt(UserDetails[3]);
                        String replacednumber = Integer.toString(dbcount2);
                        line = line.replace(UserDetails[3], replacednumber);
                        flag = "Y";
                    }
                    inputBuffer.append(line);
                    inputBuffer.append('\n');

                }
                if (flag.equalsIgnoreCase("N")) {
                    line = "User " + user.getUsername() + " submitted " + dbcount2 + " queries on database " + currentDatabase2;
                    inputBuffer.append(line);
                    inputBuffer.append('\n');
                }
                fileOut.write(inputBuffer.toString().getBytes());
                fileOut.close();
            } catch (IOException e) {
                throw new UserAuthenticationException("Database error!!!!!!!!");
            }
        }

    }

}
