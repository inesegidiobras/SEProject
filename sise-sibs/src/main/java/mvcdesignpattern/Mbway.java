package mvcdesignpattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class Mbway {

	ArrayList<String> MbwayFriends = new ArrayList<String>();
	HashMap<String, ClientMbway> MbwayInfo = new HashMap<String, ClientMbway>();
	Services services;
	private String phoneNumber;
	private String iban;
	private Integer code;

	public Integer associateMbway(String phoneNumber, String iban) throws ClientException {
		try {
			this.services.checkAccount(iban);
		} catch (AccountException e) {
		}
		if (checkPhoneNumber(phoneNumber)) {
			this.code = generateCodeMbway();
			ClientMbway client = new ClientMbway(iban, this.code);
			this.MbwayInfo.put(phoneNumber, client);
			return this.code;
		} else {
			throw new ClientException();
		}
	}

	private Integer generateCodeMbway() {
		Random randNum = new Random();
		int min = 100000;
		int max = 999999;
		return (randNum.nextInt((max - min) + 1) + min);
	}

	private Boolean checkPhoneNumber(String phoneNumber) throws ClientException {
		if (phoneNumber.length() == 9 || phoneNumber.matches("[0-9]+")) {
			return true;
		}
		return false;
	}

	public String confirmMbway(Integer code, String phoneNumber) throws AccountException, ClientException {
		Integer code1 = this.MbwayInfo.get(phoneNumber).getCode();
		if (code.equals(code1)) {
			return "MBway association confirmed successfully!";
		} else {
			throw new AccountException("Wrong confirmation code. Try association again.");
		}
	}

	public String transferMbway(String sourcePhoneNumber, String targetPhoneNumber, int amount)
			throws AccountException {

		if (this.services.checkAccount(this.MbwayInfo.get(sourcePhoneNumber).getIban())
				&& this.services.checkAccount(this.MbwayInfo.get(targetPhoneNumber).getIban())) {
			String sourceIban = this.MbwayInfo.get(sourcePhoneNumber).getIban();
			String targetIban = this.MbwayInfo.get(targetPhoneNumber).getIban();
			try {
				this.services.deposit(targetIban, amount);
			} catch (AccountException e) {
				return "Not enough money in the source account.";
			}
			;
			this.services.withdraw(sourceIban, amount);
		} else {
			return "Wrong phone number.";
		}
		return "Transfer performed successfully!";
	}

	public ArrayList<String> user(String phoneNumber) throws AccountException {

		this.MbwayFriends.add(phoneNumber);
		return this.MbwayFriends;
	}

	public ArrayList<String> friend(String phoneNumber) throws AccountException {

		try {
			this.services.checkAccount(this.MbwayInfo.get(phoneNumber).getIban());
		} catch (AccountException e) {
		}
		this.MbwayFriends.add(phoneNumber);
		return this.MbwayFriends;
	}

	public String splitBillMbway(int numberOfFriends, int amount) throws AccountException {
		if (this.MbwayFriends.size() - 1 > numberOfFriends) {
			return "Oh no! Too many friends.";
		}
		if (this.MbwayFriends.size() - 1 - numberOfFriends == numberOfFriends - 1) {
			return "Oh no! One friend is missing.";
		} else {
			int split = amount / numberOfFriends;
			String targetIban = this.MbwayFriends.get(0);
			for (int i = 1; i <= numberOfFriends;) {
				String sourceIban = this.MbwayFriends.get(i);
				try {
					this.services.withdraw(sourceIban, split);
				} catch (AccountException e) {
					return "Oh no! Friend does not have money to pay!";
				}
				this.services.deposit(targetIban, split);
				return "Friend payment done!";
			}
		}
		return "Bill payed successfully!";
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public String getIban() {
		return this.iban;
	}

	public Integer getCode() {
		return this.code;
	}
}
