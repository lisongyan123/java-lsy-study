package com.lsy.java.model;

public class Car {
	public int id;
	public int manufacturerId;
	public String model;
	public int year;
	public float rating;

	public Car(int id, int manufacturerId, String model, int year) {
		this.id = id;
		this.manufacturerId = manufacturerId;
		this.model = model;
		this.year = year;
	}

	public void setRating(float rating) {
		this.rating = rating;
	}

	@Override
	public String toString() {
		return "Car (id=" + id + ", manufacturerId=" + manufacturerId + ", model=" + model + ", year=" + year
				+ ", rating=" + rating;
	}
}