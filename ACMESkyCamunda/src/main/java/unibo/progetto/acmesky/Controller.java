package unibo.progetto.acmesky;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.stream.Collectors;

public class Controller {
	
	private Random rand = new Random();
	
	protected String getPage(String pageName) {
		String code  = "";
		try {
			BufferedReader rd = new BufferedReader(new FileReader("src/main/resources/pages/"+pageName+".html"));
			code = rd.lines().collect(Collectors.joining());
			rd.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		return code;
	}
	
	protected String generateBusinessKey() {
		return String.valueOf(rand.nextInt(1000));
	}
	
}
