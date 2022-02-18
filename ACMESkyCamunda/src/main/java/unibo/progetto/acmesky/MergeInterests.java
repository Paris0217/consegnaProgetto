package unibo.progetto.acmesky;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;

public class MergeInterests implements JavaDelegate{
	
	private class Tuple<T0, T1> {
		private T0 start;
		private T1 end;
		
		public Tuple(T0 start, T1 end) {
			this.start = start;
			this.end = end;
		}
		
		public T0 getStart() {
			return start;
		}
		
		public T1 getEnd() {
			return end;
		}
	}
	
	private int checkCities(String city0, String city1, String country0, String country1) {
		/*
		 * ys0 = ys1 && ys0 != null -> si possono inglobare a vicenda, ma non sono nulle 3
		 * ys0 = ys1 && ys0  = null -> si possono inglobare a vicenda					 3
		 * ys0 != ys1 && ys0 != null && ys1 != null -> non si inglobano					 0
		 * ys0 != ys1 && ys0 = null && ys1 != null -> la seconda entra nella prima		 1
		 * ys0 != ys1 && ys0 != null && ys1 = null -> la prima entra nella seconda		 2
		 * ys0 != ys1 && ys0 = null && ys1 = null -> si possono inglobare a vicenda		 3
		 */
		
		/*
		 * ys0 = ys1 && ys0 != null -> si possono inglobare a vicenda, ma non sono nulle 3 -> non cambia
		 * ys0 = ys1 && ys0  = null -> si possono inglobare a vicenda					 3 -> cambia
		 * 		xs0 = xs1		-> 3
		 * 		xs0 != xs1		-> 0
		 * ys0 != ys1 && ys0 != null && ys1 != null -> non si inglobano					 0 -> non cambia 
		 * ys0 != ys1 && ys0 = null && ys1 != null -> la seconda entra nella prima		 1 -> cambia
		 * 		xs0 = xs1		-> 1
		 * 		xs0 != xs1		-> 0
		 * ys0 != ys1 && ys0 != null && ys1 = null -> la prima entra nella seconda		 2 -> cambia
		 * 		xs0 = xs1		-> 1
		 * 		xs0 != xs1		-> 0
		 * ys0 != ys1 && ys0 = null && ys1 = null -> si possono inglobare a vicenda		 3 -> cambia
		 * 		xs0 = xs1		-> 3
		 * 		xs0 != xs1		-> 0
		 * 
		 */
		

		if( city0.equals(city1) && (!city0.equals("")) ) {
			return 3;
		} else if( (city0.equals(city1) && city0.equals("")) && country0.equals(country1) ) {
			return 3;
		} else if( (city0.equals(city1) && city0.equals("")) && (!country0.equals(country1)) ) {
			return 0;
		} else if( (!city0.equals(city1)) && (!city0.equals("")) && (!city1.equals("")) ) {
			return 0;
		} else if( (!city0.equals(city1)) && (city0.equals("")) && (!city1.equals("")) && country0.equals(country1) ) {
			return 1;
		} else if( (!city0.equals(city1)) && (city0.equals("")) && (!city1.equals("")) && (!country0.equals(country1)) ) {
			return 0;
		} else if( (!city0.equals(city1)) && (!city0.equals("")) && (city1.equals("")) && country0.equals(country1) ) {
			return 2;
		} else if( (!city0.equals(city1)) && (!city0.equals("")) && (city1.equals("")) && (!country0.equals(country1)) ) {
			return 0;
		} else if( (!city0.equals(city1)) && (city0.equals("")) && (city1.equals("")) && country0.equals(country1) ) {
			return 3;
		} else if( (!city0.equals(city1)) && (city0.equals("")) && (city1.equals("")) && (!country0.equals(country1)) ) {
			return 0;
		}
		
		System.err.println("checkCities: sono arrivato dove non dovevo");
		return 0;
		
	}
	
	private Tuple<Date, Date> checkTimestamps(Date timeStart0, Date timeEnd0, Date timeStart1, Date timeEnd1) {
		int flagStart = timeStart0.compareTo(timeStart1);
		int flagEnd = timeEnd0.compareTo(timeEnd1);
		int flagEndStart = timeEnd0.compareTo(timeStart1);
		int flagStartEnd = timeStart1.compareTo(timeEnd0);
		
		if(flagStart < 0 && flagEnd < 0 && flagEndStart < 0) {
//			in questo caso si lasciano separate
			return null;
		} else if(flagStart <= 0 && flagEnd >= 0) {
			return new Tuple<Date, Date>(timeStart0, timeEnd0);
		} else if(flagStart == 0 && flagEnd < 0) {
			return new Tuple<Date, Date>(timeStart0, timeEnd1);
		} else if(flagStart > 0 && flagEnd <= 0) {
			return new Tuple<Date, Date>(timeStart1, timeEnd1);
		} else if(flagStart > 0 && flagEnd > 0 && flagStartEnd > 0) {
//			in questo caso si lasciano separate
			return null;
		} else if(flagStart < 0 && flagEnd < 0 && flagEndStart >= 0) {
			return new Tuple<Date, Date>(timeStart0, timeEnd1);
		} else if(flagStart > 0 && flagEnd > 0 && flagStartEnd <= 0) {
			return new Tuple<Date, Date>(timeStart1, timeEnd0);
		}
		
		System.err.println("checkTimestamps: sono arrivato dove non dovevo");
		return null;
	}
	
	private Interest tryToMerge(Interest interestLeft, Interest interestRight) {
		
		int flagCityStart = checkCities(interestLeft.getDepaCity(), interestRight.getDepaCity(), interestLeft.getDepaCountry(), interestRight.getDepaCountry());
		int flagCityEnd	  = checkCities(interestLeft.getDestCity(), interestRight.getDestCity(), interestLeft.getDestCountry(), interestRight.getDestCountry());

		int flag = Math.min(flagCityStart, flagCityEnd);
		
		if( flag == 0 ) {
			return null;
		} else if( flag == 1 ) {
//			interestRight -> interestLeft
			Tuple<Date, Date> newPeriod = checkTimestamps(interestLeft.getPeriodStart(), interestLeft.getPeriodEnd(), interestRight.getPeriodStart(), interestRight.getPeriodEnd());
			if(newPeriod != null) {
				return new Interest(interestLeft.getId(), 
									interestLeft.getUserId(), 
									interestLeft.getDepaCountry(),
									interestLeft.getDepaCity(),
									interestLeft.getDestCountry(),
									interestLeft.getDestCity(),
									newPeriod.getStart(),
									newPeriod.getEnd(),
									Math.max(interestLeft.getPrice(), interestRight.getPrice()),
									Math.max(interestLeft.getNumSeat(), interestRight.getNumSeat()));
			}else {
				return null;
			}
		} else if( flag == 2 ) {
//			interestLeft -> interestRight
			Tuple<Date, Date> newPeriod = checkTimestamps(interestLeft.getPeriodStart(), interestLeft.getPeriodEnd(), interestRight.getPeriodStart(), interestRight.getPeriodEnd());
			if(newPeriod != null) {
				return new Interest(interestRight.getId(), 
									interestRight.getUserId(), 
									interestRight.getDepaCountry(),
									interestRight.getDepaCity(),
									interestRight.getDestCountry(),
									interestRight.getDestCity(),
									newPeriod.getStart(),
									newPeriod.getEnd(),
									Math.max(interestLeft.getPrice(), interestRight.getPrice()),
									Math.max(interestLeft.getNumSeat(), interestRight.getNumSeat()));
			}else {
				return null;
			}
		} else if( flag == 3 ) {
//			interestRight <-> interestLeft
			Tuple<Date, Date> newPeriod = checkTimestamps(interestLeft.getPeriodStart(), interestLeft.getPeriodEnd(), interestRight.getPeriodStart(), interestRight.getPeriodEnd());
			if(newPeriod != null) {
				return new Interest(interestLeft.getId(), 
									interestLeft.getUserId(), 
									interestLeft.getDepaCountry(),
									interestLeft.getDepaCity(),
									interestLeft.getDestCountry(),
									interestLeft.getDestCity(),
									newPeriod.getStart(),
									newPeriod.getEnd(),
									Math.max(interestLeft.getPrice(), interestRight.getPrice()),
									Math.max(interestLeft.getNumSeat(), interestRight.getNumSeat()));
			}else {
				return null;
			}
		}else {
			System.err.println("tryToMerge: sono arrivato dove non dovevo");
			return null;
		}
		
	}
	
	private void merge(List<Interest> input, List<Interest> output){
		if(input.size() > 0) {
			Interest interestToMerge = input.remove(input.size() -1);
			int i;
			
			for(i=0; i<input.size(); i++) {
				Interest merged = tryToMerge(input.get(i), interestToMerge);
				if ( merged != null ) {
					input.set(i, merged);
					break;
				}
			}
			
			if(i==input.size()) {
				output.add(interestToMerge);
			}
			
			merge(input, output);
		}
	}
	
	
	@Override
	public void execute(DelegateExecution execution) throws Exception {
		
		ObjectValue interestsListObj = execution.getVariableTyped("interests");
		@SuppressWarnings("unchecked")
		List<Interest> interests = (List<Interest>) interestsListObj.getValue();
		
		List<Interest> interestsCopy = new ArrayList<Interest>();
		interests.forEach(interest -> interestsCopy.add(interest.copy()));
		
		List<Interest> interestsMerged = new ArrayList<Interest>();
		
		merge(interestsCopy, interestsMerged);
		
//		-------------------------------------------------------
//		TODO: da rimuovere appena finito il debugging
//		System.out.println("Interessi prima: ");
//		interests.forEach(el -> System.out.println(el));
//
//		System.out.println("\nInteressi dopo: ");
//		interestsMerged.forEach(el -> System.out.println(el));
//		-------------------------------------------------------
		
		ObjectValue interestsDataValue = Variables.objectValue(interests)
				.serializationDataFormat(Variables.SerializationDataFormats.JAVA)
				.create();
		
		execution.setVariable("interests", interestsDataValue);

		ObjectValue interestsMergedDataValue = Variables.objectValue(interestsMerged)
				.serializationDataFormat(Variables.SerializationDataFormats.JAVA)
				.create();
		
		execution.setVariable("interestsMerged", interestsMergedDataValue);


	}
}
