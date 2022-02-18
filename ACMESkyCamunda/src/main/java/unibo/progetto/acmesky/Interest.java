package unibo.progetto.acmesky;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Interest implements Serializable {
	private static final long serialVersionUID = 3L;
	private Long id = null;
	private Long userId;
	private String depaCountry; 
	private String depaCity = null; 
	private String destCountry;
	private String destCity = null;
	private Date periodStart;
	private Date periodEnd = null;
	private Float price;
	private Integer numSeat;
	
	public Interest(Long id, Long userId, String depaCountry, String depaCity, String destCountry, String destCity,
			String periodStart, String periodEnd, Float price, Integer numSeat) {
		this.id = id;
		this.userId = userId;
		this.depaCountry = depaCountry;
		this.depaCity = depaCity;
		this.destCountry = destCountry;
		this.destCity = destCity;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ITALY);
		try { this.periodStart = format.parse(periodStart); }catch(Exception e) { this.periodStart = null; }
		try { this.periodEnd   = format.parse(periodEnd);   }catch(Exception e) { this.periodEnd = null;   }
		this.price = price;
		this.numSeat = numSeat;
	}
	
	public Interest(Long id, Long userId, String depaCountry, String depaCity, String destCountry, String destCity,
			Date periodStart, Date periodEnd, Float price, Integer numSeat) {
		this.id = id;
		this.userId = userId;
		this.depaCountry = depaCountry;
		this.depaCity = depaCity;
		this.destCountry = destCountry;
		this.destCity = destCity;
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
		this.price = price;
		this.numSeat = numSeat;
	}
	
	public Interest copy() {
		return new Interest(id, userId, depaCountry, depaCity, destCountry, destCity,
				periodStart, periodEnd, price, numSeat);
	}
		
	public Long getId() {
		return id;
	}

	public Long getUserId() {
		return userId;
	}

	public String interestLeft() {
		return depaCountry;
	}

	public String getDepaCity() {
		if(depaCity == null) { return ""; }
		return depaCity;
	}
	
	public String getDepaCountry() {
		return depaCountry;
	}


	public String getDestCountry() {
		return destCountry;
	}

	public String getDestCity() {
		if(destCity == null) { return ""; }
		return destCity;
	}

	public Date getPeriodStart() {
		return periodStart;
	}
	
	public String getPeriodStartString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(periodStart);
	}
	
	public Date getPeriodEnd() {
		return periodEnd;
	}
	
	public String getPeriodEndString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(periodEnd);
	}
	
	public Float getPrice() {
		return price;
	}
	
	public Integer getNumSeat() {
		return numSeat;
	}
	
	@Override
	public String toString() {
		return "Interest [id=" + id + ", userId=" + userId + ", depaCountry=" + depaCountry + ", depaCity=" + depaCity
				+ ", destCountry=" + destCountry + ", destCity=" + destCity + ", periodStart=" + periodStart
				+ ", periodEnd=" + periodEnd + ", price=" + price + ", numSeat=" + numSeat + "]";
	}
	
}
