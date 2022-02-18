package unibo.progetto.acmesky;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationResultWithVariables;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.spin.plugin.variable.SpinValues;
import org.camunda.spin.plugin.variable.value.JsonValue;


@Path(value = "utente/")
public class UserController extends Controller {
	
	@GET
	@Path(value = "")
	@Produces(MediaType.TEXT_HTML)
	public Response home(@Context HttpServletRequest req) {  	
		return Response.ok(getPage("home")).build();
	}
	
	@GET
	@Path(value = "registrazione")
	@Produces(MediaType.TEXT_HTML)
	public Response registrazioneGet(@Context HttpServletRequest req) {
		try {
			return Response.ok(getPage("registrazione")).build();
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}
	}
	
	@POST
	@Path(value = "registrazione")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response registrazionePost(	@FormParam("name")		 String name,
										@FormParam("surname")	 String surname,
										@FormParam("email")		 String email,
										@FormParam("password")	 String password,
										@FormParam("birthday")	 String birthday,
										@FormParam("nationality")String nationality,
										@FormParam("cardNumber") String cardNumber,
										@FormParam("phoneNumber")String phoneNumber) {  
		
		String msgName = "MsgRegistration";

		try {
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService=processEngine.getRuntimeService();
			MessageCorrelationResultWithVariables result = runtimeService.createMessageCorrelation(msgName)
																	.processInstanceBusinessKey(generateBusinessKey())
																	.setVariable("name", name)
																	.setVariable("surname", surname)
																	.setVariable("email", email)
																	.setVariable("password", password)
																	.setVariable("birthday", birthday)
																	.setVariable("nationality", nationality)
																	.setVariable("cardNumber", cardNumber)
																	.setVariable("phoneNumber", phoneNumber)
																	.correlateWithResultAndVariables(false);
			
			VariableMap variables = result.getVariables();
			
			boolean check = variables.getValue("checkID", Boolean.class);
			if(check) {
				return Response.ok(getPage("registrazioneConferma")).build();
			}else {
				return Response.ok(getPage("registrazione")).build();
			}
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}

	}
	
	@GET
	@Path(value = "accesso")
	@Produces(MediaType.TEXT_HTML)
	public Response accessoGet(@Context HttpServletRequest req) {  
		try {
			return Response.ok(getPage("accesso")).build();
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}
	}
	
	@POST
	@Path(value = "accesso")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response accessoPost(@FormParam("email")		String email,
								@FormParam("password")	String password) {  
		
		String msgName = "MsgCredentialsLogin";
		
		try {
		
			String businessKey = generateBusinessKey();
			
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService = processEngine.getRuntimeService();
			MessageCorrelationResultWithVariables result = runtimeService.createMessageCorrelation(msgName)
																		.processInstanceBusinessKey(businessKey)
																		.setVariable("email", email)
																		.setVariable("password", password)
																		.correlateWithResultAndVariables(false);
			
			VariableMap variables = result.getVariables();
			boolean checkLogin = variables.getValue("checkLogin", Boolean.class);
			if(checkLogin) {
				return Response.ok(getPage("accessoConferma").replaceFirst("BUSINESS_KEY_VALUE", businessKey)).build();
			}else {
				return Response.ok(getPage("accesso").replaceFirst("invisible", "")).build();
			}
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}
	}
	
	@POST
	@Path(value = "operazione")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response operazione(	@FormParam("businessKey") String businessKey,
								@FormParam("op")		  String op) {
		
		String msgName   = "MsgOpSelected";
		try {
			String operation = "";
			if(op.equals("modificaInformazioniUtente"))		{	operation = "updateUser";		}
			else if(op.equals("modificaInteressiUtente"))	{	operation = "updateInterests";	}
			else if(op.equals("eliminaUtente")) 			{	operation = "deleteUser";		}
			else											{	operation = "exit";				}
			
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService = processEngine.getRuntimeService();
			MessageCorrelationResultWithVariables result = runtimeService.createMessageCorrelation(msgName)
																		.processInstanceBusinessKey(businessKey)
																		.setVariable("operation", operation)
																		.correlateWithResultAndVariables(false);
			
			VariableMap variables = result.getVariables();
			
			if(op.equals("modificaInformazioniUtente")) {	
			
				String pageCode = getPage("modificaInformazioniUtente");
				ObjectValue userProfileObj = variables.getValueTyped("userProfile");
				User user = (User) userProfileObj.getValue();
				
				pageCode=pageCode.replaceFirst("BUSINESSKEYTOSET", businessKey);
				pageCode=pageCode.replaceFirst("NOMETOSET", user.getName());
				pageCode=pageCode.replaceFirst("COGNOMETOSET", user.getSurname());
				pageCode=pageCode.replaceFirst("EMAILTOSET", user.getEmail());
				pageCode=pageCode.replaceFirst("PASSWORDTOSET", user.getPassword());
				pageCode=pageCode.replaceFirst("COMPLEANNOTOSET", user.getBirthday().toString());
				pageCode=pageCode.replaceFirst("NATIONALITYTOSET", user.getNationality());
				pageCode=pageCode.replaceFirst("CARDNUMBERTOSET", user.getCardNumber());
				pageCode=pageCode.replaceFirst("PHONENUMBERTOSET", user.getPhoneNumber());
				
				return Response.ok(pageCode).build();
			}else if(op.equals("modificaInteressiUtente")) {
				
				String pageCode = getPage("modificaInteressiUtente");
				long user_id = variables.getValue("id", Long.class);
				ObjectValue interestsListObj = variables.getValueTyped("interests");
				@SuppressWarnings("unchecked")
				List<Interest> interestsList = (List<Interest>) interestsListObj.getValue();
				int counter = 0;
				for(Interest interest: interestsList) {
					String pageFragment ="<div class=\"accordion-item mt-3\" id=\"interest-"+counter+"\">\n"
							+ "          <h2 class=\"accordion-header\" id=\"panelsStayOpen-heading-"+counter+"\">\n"
							+ "            <button class=\"accordion-button collapsed\" id=\"btnAccordionHeader-"+counter+"\" type=\"button\" data-bs-toggle=\"collapse\" data-bs-target=\"#panelsStayOpen-collapse-"+counter+"\" aria-expanded=\"false\" aria-controls=\"panelsStayOpen-collapse-"+counter+"\">\n"
							+ "              "+interest.getDepaCountry()+" -> "+ interest.getDestCountry() +"\n"
							+ "            </button>\n"
							+ "          </h2>\n"
							+ "          <div id=\"panelsStayOpen-collapse-"+counter+"\" class=\"accordion-collapse collapse\" aria-labelledby=\"panelsStayOpen-heading-"+counter+"\">\n"
							+ "            <div class=\"accordion-body\">\n"
							+ "              <input type=\"hidden\" class=\"form-control\" id=\"id-"+counter+"\" name=\"id-"+counter+"\" value=\""+ interest.getId() +"\">\n"
							+ "              <div class=\"row g-3\">\n"
							+ "                <div class=\"col-6\">\n"
							+ "                  <label for=\"depaCountry-"+counter+"\" class=\"form-label\">Stato di partenza*:</label>\n"
							+ "                  <input type=\"text\" class=\"form-control\" id=\"depaCountry-"+counter+"\" name=\"depaCountry-"+counter+"\" value=\""+ interest.getDepaCountry() +"\" onchange='updateHeader(\""+counter+"\")' required>\n"
							+ "                </div>\n"
							+ "                <div class=\"col-6\">\n"
							+ "                  <label for=\"depaCity-"+counter+"\" class=\"form-label\">Città di partenza:</label>\n"
							+ "                  <input type=\"text\" class=\"form-control\" id=\"depaCity-"+counter+"\" name=\"depaCity-"+counter+"\" value=\""+ (interest.getDepaCity()!=null ? interest.getDepaCity(): "") +"\" onchange='addElementToList(\""+counter+"\")'>\n"
							+ "                </div>\n"
							+ "                <div class=\"col-6\">\n"
							+ "                  <label for=\"destCountry-"+counter+"\" class=\"form-label\">Stato di destinazione*:</label>\n"
							+ "                  <input type=\"text\" class=\"form-control\" id=\"destCountry-"+counter+"\" name=\"destCountry-"+counter+"\" value=\""+ interest.getDestCountry() +"\" onchange='updateHeader(\""+counter+"\")' required>\n"
							+ "                </div>\n"
							+ "                <div class=\"col-6\">\n"
							+ "                  <label for=\"destCity-"+counter+"\" class=\"form-label\">Città di destinazione:</label>\n"
							+ "                  <input type=\"text\" class=\"form-control\" id=\"destCity-"+counter+"\" name=\"destCity-"+counter+"\" value=\""+ (interest.getDestCity()!=null ? interest.getDestCity(): "") +"\" onchange='addElementToList(\""+counter+"\")'>\n"
							+ "                </div>\n"
							+ "                <div class=\"col-4\">\n"
							+ "                  <label for=\"periodStart-"+counter+"\" class=\"form-label\">Periodo inzio*:</label>\n"
							+ "                  <input type=\"date\" class=\"form-control\" id=\"periodStart-"+counter+"\" name=\"periodStart-"+counter+"\" value=\""+ interest.getPeriodStart() +"\" onchange='addElementToList(\""+counter+"\")' required>\n"
							+ "                </div>\n"
							+ "                <div class=\"col-4\">\n"
							+ "                  <label for=\"periodEnd-"+counter+"\" class=\"form-label\">Periodo fine*:</label>\n"
							+ "                  <input type=\"date\" class=\"form-control\" id=\"periodEnd-"+counter+"\" name=\"periodEnd-"+counter+"\" value=\""+ (interest.getPeriodEnd()!=null ? interest.getPeriodEnd(): "") +"\" onchange='addElementToList(\""+counter+"\")'>\n"
							+ "                </div>\n"
							+ "                <div class=\"col-2\">\n"
							+ "                  <label for=\"price-"+counter+"\" class=\"form-label\">Prezzo*:</label>\n"
							+ "                  <input type=\"number\" class=\"form-control\" id=\"price-"+counter+"\" name=\"price-"+counter+"\" value=\""+ interest.getPrice() +"\" min=\"0\" onchange='addElementToList(\""+counter+"\")' required>\n"
							+ "                </div>\n"
							+ "                <div class=\"col-2\">\n"
							+ "                  <label for=\"numSeat-"+counter+"\" class=\"form-label\">Numero di  biglietti*:</label>\n"
							+ "                  <input type=\"number\" class=\"form-control\" id=\"numSeat-"+counter+"\" name=\"numSeat-"+counter+"\" value=\""+ interest.getNumSeat() +"\" min=\"1\" onchange='addElementToList(\""+counter+"\")' required>\n"
							+ "                </div>\n"
							+ "              </div>\n"
							+ "              <div class=\"text-center\">\n"
							+ "                <a class=\"btn btn-danger mt-3\" id=\"btnDelete-"+counter+"\" onClick='deleteInterest(\""+counter+"\")' >Elimina</a>\n"
							+ "              </div>\n"
							+ "            </div>\n"
							+ "          </div>\n"
							+ "        </div>\n"
							+ "        <!-- -------- Inserire qui --------- -->";
					pageCode=pageCode.replaceFirst("<!-- -------- Inserire qui --------- -->", pageFragment);
					counter+=1;
				}
				pageCode=pageCode.replaceFirst("INDEXCOUNTERTOSET", String.valueOf(counter));
				pageCode=pageCode.replaceFirst("BUSINESSKEYVALUETOSET", businessKey);
				pageCode=pageCode.replaceFirst("USERIDTOSET", String.valueOf(user_id));
				
				return Response.ok(pageCode).build();
			}else if(op.equals("eliminaUtente")) {
				String pageCode = getPage("eliminaUtente");
				pageCode=pageCode.replaceFirst("BUSINESS_KEY_VALUE", businessKey);
				return Response.ok(pageCode).build();
			}else {
				return Response.ok(getPage("home")).build();
			}
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}
	}
	
	@POST
	@Path(value = "modificaInformazioni")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response modificaInformazioni(@FormParam("businessKey")	String businessKey,
										 @FormParam("name")			String name,
										 @FormParam("surname")		String surname,
										 @FormParam("email")		String email,
										 @FormParam("password")		String password,
										 @FormParam("birthday")		String birthday,
										 @FormParam("nationality")	String nationality,
										 @FormParam("cardNumber")	String cardNumber,
										 @FormParam("phoneNumber")	String phoneNumber) { 
		
		String msgName = "MsgReqUpdateInformations";
		
		try {
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService=processEngine.getRuntimeService();
			MessageCorrelationResultWithVariables result = runtimeService.createMessageCorrelation(msgName)
																		 .processInstanceBusinessKey(businessKey)
																		 .setVariable("name", name)
																		 .setVariable("surname", surname)
																		 .setVariable("email", email)
																		 .setVariable("password", password)
																		 .setVariable("birthday", birthday)
																		 .setVariable("nationality", nationality)
																		 .setVariable("cardNumber", cardNumber)
																		 .setVariable("phoneNumber", phoneNumber)
																		 .correlateWithResultAndVariables(false);
			
			VariableMap variables = result.getVariables();
			boolean check = variables.getValue("checkChangeInfo", Boolean.class);
			if(!check) {
				return Response.ok(getPage("modificaInteressi")).build();
			} else {
				return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "C'è stato un errore nella modifica delle informazioni")).build();
			}
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}

	}
	
	@POST
	@Path(value = "modificaInteressi")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response modificaInteressi(	@FormParam("businessKey") 	String businessKey,
										@FormParam("jsonInterests")	String jsonInterests) {
		
		String msgName = "MsgReqUpdateInterests";
		
		try {
			JsonValue jsonValue = SpinValues.jsonValue(jsonInterests).create();
					
			ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
			RuntimeService runtimeService=processEngine.getRuntimeService();
			MessageCorrelationResultWithVariables result = runtimeService.createMessageCorrelation(msgName)
																		 .processInstanceBusinessKey(businessKey)
																		 .setVariable("interests", jsonValue)
																		 .correlateWithResultAndVariables(false);
			
			VariableMap variables = result.getVariables();
			boolean check = variables.getValue("checkResUpdateInterests", Boolean.class);
			if(check) {
				return Response.ok(getPage("modificaInteressi")).build();
			} else {
				return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "A causa di un errore interno non tutti gli interessi non stati aggiornati")).build();
			}
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}
	}
	
	@POST
	@Path(value = "elimina")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public Response eliminaAccount(	@FormParam("businessKey") 	String businessKey,
									@FormParam("op")	String op) {  
		
		String msgName = "MsgDeleteUser";
		
		try {
			if(op.equals("yes")) {
				ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
				RuntimeService runtimeService=processEngine.getRuntimeService();
				MessageCorrelationResultWithVariables result = runtimeService.createMessageCorrelation(msgName)
																			 .processInstanceBusinessKey(businessKey)
																			 .correlateWithResultAndVariables(false);
				
				VariableMap variables = result.getVariables();
		//		TODO: gestire la pagine di ritorno
				boolean checkDelete = variables.getValue("checkDeleteUser", Boolean.class);
				if(!checkDelete) {
					return Response.ok(getPage("eliminazioneConferma")).build();
				}else {
					return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
				}
			} else if(op.equals("no")) {
				return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Annullato")).build();
			} else {
				return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
			}
		} catch (Exception e) {
			return Response.ok(getPage("errore").replaceFirst("ERRORTOSET", "Errore interno al server")).build();
		}
	}
	
}