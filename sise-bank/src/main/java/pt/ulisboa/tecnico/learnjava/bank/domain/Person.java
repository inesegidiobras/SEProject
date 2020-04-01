package pt.ulisboa.tecnico.learnjava.bank.domain;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class Person {

	public int getAge() {
		return this.age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public String getAddress() {
		return this.address;
	}

	private final String firstName;
	private final String lastName;
	private final String address;
	private int age;

	public Person(String firstName, String lastName, String address, int age) throws ClientException {
		checkAge(age);

		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.age = age;
	}

	private void checkAge(int age) throws ClientException {
		if (age < 0) {
			throw new ClientException();
		}
	}
}
