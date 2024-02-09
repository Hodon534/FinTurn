package com.webapp.FinTurn;

import com.webapp.FinTurn.constant.FileConstant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class FinTurnApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinTurnApplication.class, args);
		new File(FileConstant.USER_FOLDER).mkdirs(); // create folder in User Directory
		//sourceConnection.requestData("https://financialmodelingprep.com/api/v3/profile/AAPL?apikey=9d4755d5ea3df8a80fd55985527c1a41");
    }

}
