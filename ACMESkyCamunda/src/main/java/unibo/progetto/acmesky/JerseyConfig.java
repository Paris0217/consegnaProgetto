package unibo.progetto.acmesky;

import javax.ws.rs.ApplicationPath;

import org.camunda.bpm.spring.boot.starter.rest.CamundaJerseyResourceConfig;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath("ACMESky")
public class JerseyConfig extends CamundaJerseyResourceConfig {
	
	@Override
	protected void registerAdditionalResources() {
		register(new UserController());
		register(new OfferController());
	}

}
