package unibo.progetto.acmesky;

import java.util.Random;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;

public class InitPayment implements JavaDelegate {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		final float priceOneTicket = (float) execution.getVariable("priceOneTicket");
		final int quantity = (int) execution.getVariable("quantity");
		
		int payment_id = Math.abs((new Random()).nextInt());
		float priceTotal = priceOneTicket * quantity;
		
		execution.setVariable("payment_id", payment_id);
		execution.setVariable("priceTotal", priceTotal);
	}
}
