package unibo.progetto.acmesky;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.MessageCorrelationResultWithVariables;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.spin.json.SpinJsonNode;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;

@Path(value = "offerta/")
public class OfferController extends Controller {
	
	@GET
	@Path(value = "{company}/{flyID}")
	@Produces(MediaType.TEXT_HTML)
	public Response modificaInteressi(@PathParam("company") String company,
									  @PathParam("flyID") 	int flyID) {
		String msgName = "MsgInfoOffer";
		
		try {
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService = processEngine.getRuntimeService();
			MessageCorrelationResultWithVariables result = runtimeService.createMessageCorrelation(msgName)
																		 .processInstanceBusinessKey(generateBusinessKey())
																		 .setVariable("flyID", flyID)
																		 .setVariable("company", company)
																		 .correlateWithResultAndVariables(false);
			
			VariableMap variables = result.getVariables();
			boolean availabilityResp = variables.getValue("availability", Boolean.class);
			if( availabilityResp ) {
				int availableSeats = variables.getValue("availableSeats", Integer.class);
				return Response.ok(getPage("postiDisponibili").replaceFirst("AVAILABLESEATS", Integer.toString(availableSeats))).build();
			} else {
				return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
			}
		}catch(Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}
	}
	
	@GET
	@Path(value = "{tokenURI}")
	@Produces(MediaType.TEXT_HTML)
	public Response acquistaOfferta(@PathParam("tokenURI") String tokenURI) {
		String msgName = "MsgBuyOffer";
		
		try {
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService = processEngine.getRuntimeService();
			
			String businessKey = generateBusinessKey();
			
			MessageCorrelationResultWithVariables result = runtimeService.createMessageCorrelation(msgName)
																		 .processInstanceBusinessKey(businessKey)
																		 .setVariable("tokenURI", tokenURI)
																		 .setVariable("businessKey", businessKey)
																		 .correlateWithResultAndVariables(false);
			
			VariableMap variables = result.getVariables();
			ObjectValue userProfileObj = variables.getValueTyped("userProfile");
			
			if(variables.getValue("checkURI", Boolean.class)==null) {
				return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Link non valido")).build();
			} else if(!(variables.getValue("checkURI", Boolean.class))) {
				return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Posti terminati")).build();
			}
			
			int quantity = variables.getValue("quantity", Integer.class);
			double priceTotal = variables.getValue("priceTotal", Double.class);
			User user = (User) userProfileObj.getValue();
			
			String pageCode = getPage("inserimentoDatiTickets");
			pageCode = pageCode.replaceFirst("BUSINESSKEYTOSET", businessKey);
			pageCode = pageCode.replaceFirst("NOMETOSET", user.getName());
			pageCode = pageCode.replaceFirst("COGNOMETOSET", user.getSurname());
			pageCode = pageCode.replaceFirst("COMPLEANNOTOSET", user.getBirthday().toString());
			pageCode = pageCode.replaceFirst("NATIONALITYTOSET", user.getNationality());
			
			pageCode = pageCode.replaceFirst("INDEXTOSET", Integer.toString(quantity));
			
			for(int i=1; i<quantity; i++) {
				String fragmentPage=  "   <h3>Dati persona biglietto "+(i+1)+":</h3>\n"
									+ "	  <div class=\"mb-3\">\n"
									+ "	    <label for=\"name-"+i+"\" class=\"form-label\">Nome</label>\n"
									+ "	    <input type=\"text\" class=\"form-control\" id=\"name-"+i+"\" name=\"name-"+i+"\" value=\"\" required>\n"
									+ "	  </div>\n"
									+ "	  <div class=\"mb-3\">\n"
									+ "	    <label for=\"surname-"+i+"\" class=\"form-label\">Cognome</label>\n"
									+ "	    <input type=\"text\" class=\"form-control\" id=\"surname-"+i+"\" name=\"surname-"+i+"\" value=\"\" required>\n"
									+ "	  </div>\n"
									+ "	  <div class=\"mb-3\">\n"
									+ "	  	<label for=\"birthday-"+i+"\">Data di nascita</label>\n"
									+ "      	<input type=\"date\" class=\"form-control\" id=\"birthday-"+i+"\" name=\"birthday-"+i+"\" value=\"\" required>\n"
									+ "	  </div>\n"
									+ "	  <div class=\"mb-3\">\n"
									+ "	  	<label for=\"nationality-"+i+"\">Nazionalità</label>\n"
									+ "      	<input type=\"text\" class=\"form-control\" id=\"nationality-"+i+"\" name=\"nationality-"+i+"\" value=\"\" required>\n"
									+ "	  </div>\n"
									+ "      <!-- -------- Inserire qui --------- -->";
				pageCode=pageCode.replaceFirst("<!-- -------- Inserire qui --------- -->", fragmentPage);
			}
			pageCode=pageCode.replaceFirst("<!-- -------- Inserire qui --------- -->", "");
			
			if(priceTotal>1000) {
				String formPage = "        <h3>Dati per il trasporto:</h3>\n"
								+ "        <div class=\"mb-3\">\n"
								+ "          <div class=\"form-check\">\n"
								+ "            <input class=\"form-check-input\" type=\"checkbox\" id=\"checkRental\" checked>\n"
								+ "            <label class=\"form-check-label\" for=\"checkRental\">\n"
								+ "              Spuntare se si vuole il servizio\n"
								+ "            </label>\n"
								+ "          </div>\n"
								+ "        </div>\n"
								+ "      	<div id=\"RentalDiv\">\n"
								+ "	      		<div class=\"mb-3\">\n"
								+ "	        		<label for=\"address\" class=\"form-label\">Indirizzo</label>\n"
								+ "	        		<input type=\"text\" class=\"form-control\" id=\"address\" name=\"address\" value=\"\" required>\n"
								+ "	      		</div>\n"
								+ "	      		<div class=\"mb-3\">\n"
								+ "	        		<label for=\"phoneNumber\" class=\"form-label\">Numero di telefono:</label>\n"
								+ "	        		<input type=\"text\" class=\"form-control\" id=\"phoneNumber\" name=\"phoneNumber\" value=\"\" required>\n"
								+ "	      		</div>\n"
								+ "      	</div>\n"
								+ "			SCRIPTTOSET";
				
				String scriptPage = "        <script type=\"text/javascript\">\n"
								  + "          $(\"input#checkRental\").click(function () {\n"
								  + "            $(\"input#address\").attr(\"required\", this.checked);\n"
								  + "            $(\"input#phoneNumber\").attr(\"required\", this.checked);\n"
								  + "            $(\"div#RentalDiv\").toggle(this.checked);\n"
								  + "          });\n"
								  + "        </script>\n";
				
				pageCode = pageCode.replaceFirst("FORMCARTOSET", formPage);
	//			La replaceFirst() da errore con il $ mentre la replace() non lo da.
				pageCode = pageCode.replace("SCRIPTTOSET", scriptPage);	
			} else {
				pageCode = pageCode.replaceFirst("FORMCARTOSET", "");
			}
			
			return Response.ok(pageCode).build();
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}
	}
	
	
//	Questa serve per continuare l'acquisto. riceve i dati del form.
	@POST
	@Path(value = "dataForTikets")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response dataForTikets(	@FormParam("businessKey") 	String businessKey,
									@FormParam("jsonData")		String jsonDataString,
									@FormParam("address")		String address,
									@FormParam("phoneNumber")	String phoneNumber) {
		
		String msgName = "MsgConfAndTicketData";
		
		try {
			JsonValue jsonValue = SpinValues.jsonValue(jsonDataString).create();
			
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService=processEngine.getRuntimeService();
			MessageCorrelationBuilder msg = runtimeService.createMessageCorrelation(msgName)
														  .processInstanceBusinessKey(businessKey)
														  .setVariable("dataForTikets", jsonValue);
			
			if( ( address != null && !address.equals("") ) && ( phoneNumber != null && !phoneNumber.equals("")) ) {
				msg.setVariable("address", address)
				   .setVariable("phoneNumber", phoneNumber)
				   .setVariable("requiredBookCN", true);
			} else {
				msg.setVariable("address", "")
				   .setVariable("phoneNumber", "")
				   .setVariable("requiredBookCN", false);
			}
			
			MessageCorrelationResultWithVariables result =	msg.correlateWithResultAndVariables(false);
			
			VariableMap variables = result.getVariables();
			
			int payment_id = variables.getValue("payment_id", Integer.class);
			double priceTotal = variables.getValue("priceTotal", Double.class);
			String cardNumber = variables.getValue("cardNumber", String.class);
			
			String pageCode = getPage("bancaRedirect");
			pageCode = pageCode.replaceFirst("PAYMENTIDTOSET", Integer.toString(payment_id));
			pageCode = pageCode.replaceFirst("COSTTOSET", Double.toString(priceTotal));
			pageCode = pageCode.replaceFirst("CARDNUMBERTOSET", cardNumber);
			pageCode = pageCode.replaceFirst("BUSINESSKEYTOSET", businessKey);
			
			(new BankRepository()).addSession(payment_id, variables.getValue("businessKeySubInstance", String.class));
			
			return Response.ok(pageCode).build();
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}
	}
	
	@POST
	@Path(value = "buyTickets")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response registrazionePost(	@FormParam("businessKey")	String businessKey,
										@FormParam("paymentID")	 	String paymentID,
										@FormParam("card")		 	String card,
										@FormParam("secretNumber")	String secretNumber,
										@FormParam("cost")	 		String cost,
										@FormParam("op")	 		String operation) {
		
		try {
			if(operation.equals("Conferma")) {
				
				String msgName = "MsgRedirectBankData";
				
				
				ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
				RuntimeService runtimeService=processEngine.getRuntimeService();
				MessageCorrelationResultWithVariables result = runtimeService.createMessageCorrelation(msgName)
																			.processInstanceBusinessKey(businessKey)
																			.setVariable("paymentID", paymentID)
																			.setVariable("card", card)
																			.setVariable("secretNumber", secretNumber)
																			.setVariable("cost", cost)
																			.correlateWithResultAndVariables(false);
				
				VariableMap variables = result.getVariables();
				boolean error = true;
				try {
					error = variables.getValue("error", Boolean.class);
				}catch(Exception e) {
					error = true;
				}
				
				if(!error) {
					
					String pageCode = getPage("visualizzaTickets");
					
		//			Qui ci sono gli ID dei biglietti
					@SuppressWarnings("unchecked")
					List<Integer> ticketsId = variables.getValue("ticketsId", List.class);
					
		//			Qui ci sono i dati dei passeggeri
					JsonValue dataForTikets = variables.getValueTyped("dataForTikets");
					SpinJsonNode dataForTiketsJson = dataForTikets.getValue();
					
		/*			Contenuto del JSON
		 *			{ 
		 * 				data: [
		 * 						{
		 * 							n: int
		 * 							name: string
		 * 							surname: string
		 * 							birthday: string?
		 * 							nationality: string
		 * 						}	
		 *  				 ]
		 * 			}
		 */			
					Iterator<SpinJsonNode> dataForTiketsJsonListIterator = dataForTiketsJson.prop("data").elements().iterator();
					while(dataForTiketsJsonListIterator.hasNext()) {
						SpinJsonNode ticketJson = dataForTiketsJsonListIterator.next();
						int n = ticketJson.prop("n").numberValue().intValue();
						String name = ticketJson.prop("name").stringValue();
						String surname = ticketJson.prop("surname").stringValue();
						String birthday = ticketJson.prop("birthday").stringValue();
						String nationality = ticketJson.prop("nationality").stringValue();
						
						String pageFragment = "		<div class=\"card\" style=\"width: 18rem;\">\n"
											+ "			<div class=\"card-body\">\n"
											+ "				<h5 class=\"card-title\">Ticket</h5>\n"
											+ "				<input type=\"n\" readonly class=\"card-subtitle mb-2 text-muted\" id=\"n\" name=\"n\" value=\""+ticketsId.get(n)+"\">\n"
											+ "				<h6 class=\"card-subtitle\">NOME</h6>\n"
											+ "				<input type=\"name\" readonly class=\"card-subtitle mb-2 text-muted\" id=\"name\" name=\"name\" value=\""+name+"\">\n"
											+ "				<h6 class=\"card-subtitle\">COGNOME</h6>\n"
											+ "				<input type=\"surname\" readonly class=\"card-subtitle mb-2 text-muted\" id=\"surname\" name=\"surname\" value=\""+surname+"\">\n"
											+ "				<h6 class=\"card-subtitle\">DATA DI NASCITA</h6>\n"
											+ "				<input type=\"birthday\" readonly class=\"card-subtitle mb-2 text-muted\" id=\"birthday\" name=\"birthday\" value=\""+birthday+"\">\n"
											+ "				<h6 class=\"card-subtitle\">NAZIONALITA'</h6>\n"
											+ "				<input type=\"nazionality\" readonly class=\"card-subtitle mb-2 text-muted\" id=\"nazionality\" name=\"nazionality\" value=\""+nationality+"\">\n"
											+ "			</div>\n"
											+ "		</div>\n"
											+ "		<!-- TICKETSTOSET -->\n";
						
						pageCode=pageCode.replaceFirst("<!-- TICKETSTOSET -->", pageFragment);
					}
					
//					Controlla che ci siano anche delle prenotazioni per il noleggio
					try {
						boolean reserved = variables.getValue("reserved", Boolean.class);
						String address = variables.getValue("address", String.class);
						String name = variables.getValue("name", String.class);
						Long timestamp = Long.parseLong(variables.getValue("timestamp", String.class));
						
						Date date = new Date(timestamp);
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
						String dateFormatted = format.format(date);
						
						if(reserved) {
							String pageFragment = "			<h1 class=\"h3 mb-3 fw-normal\" align=\"center\">Noleggio</h1>\n"
												+ "			<div class=\"container-fluid\">\n"
												+ "				<div class=\"card\" style=\"width: 18rem;\">\n"
												+ "					<div class=\"card-body\">\n"
												+ "						<h6 class=\"card-subtitle\">NOMINATIVO</h6>\n"
												+ "						<input type=\"name\" readonly class=\"card-subtitle mb-2 text-muted\" id=\"name\" name=\"name\" value=\""+name+"\">\n"
												+ "						<h6 class=\"card-subtitle\">LUOGO D'INCONTRO</h6>\n"
												+ "						<input type=\"luogo\" readonly class=\"card-subtitle mb-2 text-muted\" id=\"luogo\" name=\"luogo\" value=\""+address+"\">\n"
												+ "						<h6 class=\"card-subtitle\">ORARIO</h6>\n"
												+ "						<input type=\"orario\" readonly class=\"card-subtitle mb-2 text-muted\" id=\"orario\" name=\"orario\" value=\""+dateFormatted+"\"> \n"
												+ "						<p class=\"fs-9 fst-italic\">*presentarsi due ore prima prima</p>\n"
												+ "					</div>\n"
												+ "				</div>\n"
												+ "			</div>\n";
							pageCode=pageCode.replaceFirst("<!-- RENTALTOSET -->", pageFragment);
						}
					} catch (Exception e) {
						
					}
					
					return Response.ok(pageCode).build();
				} else {
					return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "C'è stato un errore nel pagamento")).build();
				}
			} else if(operation.equals("Annulla")) {
				
				String msgName = "MsgDeleteBuyBrowser";
				
				ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
				RuntimeService runtimeService=processEngine.getRuntimeService();
				runtimeService.createMessageCorrelation(msgName)
					.processInstanceBusinessKey(businessKey)
					.correlate();
				
				return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Acquisto annullato")).build();
			} else {
				return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
			}
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "C'è stato un errore nel pagamento")).build();
		}
	}
	
	
//	Serve per fare debugging. Deve fare partire quello che farà partire il timer
	@GET
	@Path(value = "controlloOfferte")
	@Produces(MediaType.TEXT_HTML)
	public Response controlloOfferte() {
		String msgName = "MsgcontrolloOffertePeriodico";
		
		try {
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService = processEngine.getRuntimeService();
			runtimeService.createMessageCorrelation(msgName)
						  .processInstanceBusinessKey(generateBusinessKey())
						  .correlateWithResultAndVariables(false);
			
			return Response.ok("ok").build();
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}

	}
}
