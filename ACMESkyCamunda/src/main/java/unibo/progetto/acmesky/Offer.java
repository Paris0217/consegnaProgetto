package unibo.progetto.acmesky;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Offer implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private int flyID;
	private String depaCity; 
	private String destCity;
	private Date date;
	private Float price;
	private int num_seat;
	private boolean availability;
	private String company;
	
	public Offer() {

	}
	
	public Offer(long id, int flyID, String depaCity, String destCity, String date, Float price, int num_seat, boolean availability, String company) {
		this.id = id;
		this.flyID = flyID;
		this.depaCity = depaCity;
		this.destCity = destCity;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try { this.date = format.parse(date); }catch(Exception e) { this.date = null; }
		this.price = price;
		this.num_seat = num_seat;
		this.availability = availability;
		this.company = company;
	}
	
	public Offer(long id, int flyID, String depaCity, String destCity, Date date, Float price, int num_seat, boolean availability, String company) {
		this.id = id;
		this.flyID = flyID;
		this.depaCity = depaCity;
		this.destCity = destCity;
		this.date = date;
		this.price = price;
		this.num_seat = num_seat;
		this.availability = availability;
		this.company = company;
	}

	public Offer(int flyID, String depaCity, String destCity, String date, Float price, int num_seat, boolean availability, String company) {
		this.flyID = flyID;
		this.depaCity = depaCity;
		this.destCity = destCity;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try { this.date = format.parse(date); }catch(Exception e) { this.date = null; }
		this.price = price;
		this.num_seat = num_seat;
		this.availability = availability;
		this.company = company;
	}
	
	public Offer(int flyID, String depaCity, String destCity, Date date, Float price, int num_seat, boolean availability, String company) {
		this.flyID = flyID;
		this.depaCity = depaCity;
		this.destCity = destCity;
		this.date = date;
		this.price = price;
		this.num_seat = num_seat;
		this.availability = availability;
		this.company = company;
	}

	public long getId() {
		return id;
	}
	
	public int getFlyID() {
		return flyID;
	}

	public String getDepaCity() {
		return depaCity;
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

	public boolean isAvailability() {
		return availability;
	}
	
	public String getCompany() {
		return company;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setFlyID(int flyID) {
		this.flyID = flyID;
	}

	public void setDepaCity(String depaCity) {
		this.depaCity = depaCity;
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

	public void setAvailability(boolean availability) {
		this.availability = availability;
	}
	
	public void setCompany(String company) {
		this.company = company;
	}
	
	@Override
	public String toString() {
		return "Offer [id=" + id + ", flyID=" + flyID + ", depaCity=" + depaCity + ", destCity=" + destCity + ", date="
				+ date + ", price=" + price + ", num_seat=" + num_seat + ", availability=" + availability + ", company="
				+ company + "]";
	}
}