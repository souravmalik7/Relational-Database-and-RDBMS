package database.backend.sql_dump;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class DumpCreation {

    String databaseName;

    public void createDataDump(String query) {

        String editedQuery = query.replaceAll("[^a-zA-Z0-9]", " ");
        String[] splitQuery = editedQuery.split("\\s+");

        File sqlDump = new File("src/main/java/database/filesystem/DataDumps");
        if (!sqlDump.exists()) {
            sqlDump.mkdirs();
        }

        if (query.contains("create database") || query.contains("CREATE DATABASE")) {
            databaseName = splitQuery[2];

            try {
                new FileOutputStream("src/main/java/database/filesystem/DataDumps/" + databaseName + ".txt", false).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (query.contains("use database") || query.contains("USE DATABASE")) {
            databaseName = splitQuery[2];

            try {
                new FileOutputStream("src/main/java/database/filesystem/DataDumps/" + databaseName + ".txt", true).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (isDuplicateQuery(query) == false) {
            addQueryToFile(query);
        }
    }

    public boolean isDuplicateQuery(String query) {
        boolean isDuplicate = false;
        StringBuilder allQueries = null;
        FileReader fileReader = null;
        String line;

        try {
            fileReader = new FileReader("src/main/java/database/filesystem/DataDumps/" + databaseName + ".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                if (line.equalsIgnoreCase(query)) {
                    isDuplicate = true;
                    break;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isDuplicate;
    }

    public void addQueryToFile(String query) {
        try {
            FileWriter fileWriter = new FileWriter("src/main/java/database/filesystem/DataDumps/" + databaseName + ".txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.append(query);
            bufferedWriter.append("\n");
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processUpdateQueries(String tableName, String setCondition, String whereCondition,
                                     String setColumn, String whereColumn) {
        try {
            FileReader fileReader = new FileReader("src/main/java/database/filesystem/DataDumps/" + databaseName + ".txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuilder inputBuffer = new StringBuilder();
            String line;
            String queryLine = null;
            String editedQueryLine = null;
            String allQueries = null;
            String newQueries = null;

            while ((line = bufferedReader.readLine()) != null) {
                inputBuffer.append(line);
                inputBuffer.append('\n');

                String modifiedLine = line.replaceAll("[^a-zA-Z0-9]", " ");
                String[] editedLine = modifiedLine.split("\\s+");
                List<String> stringList = Arrays.asList(modifiedLine.split("\\s+"));

                if (editedLine[0].equalsIgnoreCase("INSERT") && editedLine[1].equalsIgnoreCase("INTO")
                        && editedLine[2].equalsIgnoreCase(tableName)) {
                    if (stringList.contains(whereColumn) && stringList.contains(whereCondition)) {
                        queryLine = line;
                        break;
                    }
                }
            }

            fileReader.close();
            if (queryLine != null) {
                editedQueryLine = queryLine.replaceAll(whereCondition, setCondition);
                allQueries = inputBuffer.toString();
                newQueries = allQueries.replaceAll(Pattern.quote(queryLine), editedQueryLine);

                FileOutputStream fileOut = new FileOutputStream("src/main/java/database/filesystem/DataDumps/" + databaseName + ".txt", false);
                fileOut.write(newQueries.getBytes());
                fileOut.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportDump(String dbName) {
        File source = new File("src/main/java/database/filesystem/DataDumps/" + dbName + ".txt");
        File destination = new File("src/main/java/database/filesystem/SQLDumps/" + dbName + ".txt");
        File sqlFile = new File("src/main/java/database/filesystem/SQLDumps/" + dbName + ".sql");
        if(source.exists()){
            try {
                Files.copy(source.toPath(),destination.toPath());
                if (!sqlFile.exists()) {
                    destination.renameTo(sqlFile);
                    System.out.println("SQL file generated successfully!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            System.out.println("Invalid database name");
        }
    }
}
