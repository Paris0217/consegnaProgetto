package unibo.progetto.acmesky;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
	private static final long serialVersionUID = 5L;
	private long id;
	private String name;
	private String surname;
	private String email;
	private String password;
	private Date birthday;
	private String nationality;
	private String token;
	private String cardNumber;
	private String phoneNumber;
	
	public User(long id, String name, String surname, String email, String password, Date birthday,
			String nationality, String token, String cardNumber, String phoneNumber) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.nationality = nationality;
		this.token = token;
		this.cardNumber = cardNumber;
		this.phoneNumber = phoneNumber;
	}
	
	public User(long id, String name, String surname, String email, String password, Date birthday,
			String nationality, String cardNumber, String phoneNumber) {
		this.id = id;
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
		this.nationality = nationality;
		this.cardNumber = cardNumber;
		this.phoneNumber = phoneNumber;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getNationality() {
		return nationality;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setNationality(String nationality) {
		this.nationality = nationality;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCardNumber() {
		if(cardNumber==null) { return ""; }
		else { return cardNumber; }
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
}
