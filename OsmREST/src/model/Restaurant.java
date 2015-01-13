package model;

import java.io.Serializable;

public class Restaurant implements Serializable {
	private static final long serialVersionUID = 1L;
	int id;
	private String name;
	private double latitude;
	private double longitude;
	private String address;
	private String phone;
	private String specialty;
	private String rate;
	private double prix;

	public Restaurant() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Restaurant(String name, double latitude, double longitude,
			String address, String phone, String specialty, String rate) {
		super();
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.phone = phone;
		this.specialty = specialty;
		this.rate = rate;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getSpecialty() {
		return specialty;
	}

	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getPrix() {
		return prix;
	}

	public void setPrix(double prix) {
		this.prix = prix;
	}

}
