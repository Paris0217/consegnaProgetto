package unibo.progetto.acmesky;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Proposal implements Serializable {
	private static final long serialVersionUID = 2L;
	private int id;			//è quello che usa la CA per il volo
	private String depaCountry; 
	private String depaCity; 
	private String destCountry;
	private String destCity;
	private Date date;
	private Float price;
	private Integer num_seat;
	private String company;
	
	public Proposal() {
		
	}
	
	public Proposal(int id, String depaCountry, String depaCity, String destCountry, String destCity, String date,
			Float price, int num_seat, String company) {
		this.id = id;
		this.depaCountry = depaCountry;
		this.depaCity = depaCity;
		this.destCountry = destCountry;
		this.destCity = destCity;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try { this.date = format.parse(date); }catch(Exception e) { this.date = null; }
		this.price = price;
		this.num_seat = num_seat;
		this.company = company;
	}
	
	public Proposal(int id, String depaCountry, String depaCity, String destCountry, String destCity, Date date,
			Float price, int num_seat, String company) {
		this.id = id;
		this.depaCountry = depaCountry;
		this.depaCity = depaCity;
		this.destCountry = destCountry;
		this.destCity = destCity;
		this.date = date;
		this.price = price;
		this.num_seat = num_seat;
		this.company = company;

	}

	public int getId() {
		return id;
	}

	public String getDepaCountry() {
		return depaCountry;
	}

	public String getDepaCity() {
		return depaCity;
	}

	public String getDestCountry() {
		return destCountry;
	}

	public String getDestCity() {
		return destCity;
	}

	public Date getDate() {
		return date;
	}
	
	public String getDateString() {
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm")).format(date);
	}

	public Float getPrice() {
		return price;
	}

	public int getNum_seat() {
		return num_seat;
	}

	public String getCompany() {
		return company;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setDepaCountry(String depaCountry) {
		this.depaCountry = depaCountry;
	}

	public void setDepaCity(String depaCity) {
		this.depaCity = depaCity;
	}

	public void setDestCountry(String destCountry) {
		this.destCountry = destCountry;
	}

	public void setDestCity(String destCity) {
		this.destCity = destCity;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setDate(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try { this.date = format.parse(date); }catch(Exception e) { this.date = null; }
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public void setNum_seat(int num_seat) {
		this.num_seat = num_seat;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "Proposal [id=" + id + ", depaCountry=" + depaCountry + ", depaCity=" + depaCity + ", destCountry="
				+ destCountry + ", destCity=" + destCity + ", date=" + date + ", price=" + price + ", num_seat="
				+ num_seat + ", company=" + company + "]";
	}
}
