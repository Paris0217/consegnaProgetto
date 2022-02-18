package unibo.progetto.acmesky;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

@Component
public class BankRepository {

	private static final Map<Integer, String> paymentsIDToSessionId = new HashMap<>();
	
	@PostConstruct
	public void initData() {
		// In caso di debugging settare qui dei valori
	}
	
	public String findSession(int parymentId) {
		return paymentsIDToSessionId.get(parymentId);
	}

	public void addSession(int parymentId, String sessionId) {
		paymentsIDToSessionId.put(parymentId, sessionId);
	}
	
	public void removeSession(int parymentId) {
		try {
			paymentsIDToSessionId.remove(parymentId);
		}catch(Exception e) {
			
		}
	}

//	Serve per fare debugging
	public void printAllValues() {
		System.out.println("lista di (pagamentoID, sessioneID): ");
		paymentsIDToSessionId.forEach((key, val) -> System.out.println(key + ": " + val));
	}

}
