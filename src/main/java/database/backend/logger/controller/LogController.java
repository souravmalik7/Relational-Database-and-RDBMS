package database.backend.logger.controller;

import database.backend.authentication.Session;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static database.backend.logger.constant.LogConstant.*;

public class LogController {

	private void insertLog(String message,String filepath, Instant time){

		try{
			FileWriter fileWriter=new FileWriter(filepath,true);

			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
					.withZone(ZoneId.systemDefault());

			String log = String.format("%s : %s : %s \n",dateTimeFormatter.format(time), Session.getUsername(),message);
			fileWriter.append(log);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void errorLog(String message){
		try{
			FileWriter fileWriter=new FileWriter(ERROR_LOGFILE,true);

			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
					.withZone(ZoneId.systemDefault());

			String log = String.format("%s : %s : %s \n",dateTimeFormatter.format(Instant.now()), Session.getUsername(),message);
			fileWriter.append(log);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void log(String message,int logType)  {
		String filepath;
		switch (logType){
			case GENERAL_LOG:
				filepath = GENERAL_LOGFILE;
				break;
			case EVENT_LOG:
				 filepath = EVENT_LOG_FILE;
				 break;
			case QUERY_LOG:
				 filepath = QUERY_LOGFILE;
				 break;
			default:
				errorLog("Invalid logs type used");
				return;
		}

		File file = new File(filepath);
		if(file.exists() && !file.isDirectory()) {
			this.insertLog(message,filepath,Instant.now());
		}
		else {
			try {
				file.createNewFile();
				this.insertLog(message,filepath,Instant.now());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
}
