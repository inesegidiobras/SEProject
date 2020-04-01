package mvcdesignpattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.PhoneNumberException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;

public class Mbway {

	ArrayList<Integer> MbwaySplitAmounts = new ArrayList<Integer>();
	ArrayList<String> MbwaySplitPhoneNumbers = new ArrayList<String>();
	ArrayList<String> MbwayConfirmedPhoneNumbers = new ArrayList<String>();
	HashMap<String, ClientMbway> MbwayClients = new HashMap<String, ClientMbway>();
	Services services;
	ClientMbway client;
	private String phoneNumber;
	private String iban;
	private Integer code;

	public Mbway(Services services, HashMap<String, ClientMbway> MbwayInfo,
			ArrayList<String> MbwayConfirmedPhoneNumbers) {

		this.MbwayClients = MbwayInfo;
		this.services = services;
		this.MbwayConfirmedPhoneNumbers = MbwayConfirmedPhoneNumbers;
	}

	public boolean checkMbway(String phoneNumber, String iban) throws AccountException {
		return (this.services.checkAccountExists(iban) != null);
	}

	public void clientDoesNotExist(String phoneNumber, String iban) throws AccountException {
		if (this.MbwayClients.get(phoneNumber) != null) {
			throw new AccountException();
		}
	}

	public Integer associateMbway(String phoneNumber, String iban) throws ClientException {
		if (checkPhoneNumber(phoneNumber)) {
			this.code = generateCodeMbway();
			this.client = new ClientMbway(iban, this.code);
			this.MbwayClients.put(phoneNumber, this.client);
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
		return (phoneNumber.length() == 9 || phoneNumber.matches("[0-9]+"));
	}

	public Integer mbwayClientsCode(String phoneNumber) throws AccountException, ClientException {
		Integer code = this.MbwayClients.get(phoneNumber).getCode();
		return code;
	}

	public String mbwayClientsIbanByPhone(String phoneNumber) throws PhoneNumberException {
		ClientMbway client = this.MbwayClients.get(phoneNumber);
		if (client != null) {
			return client.getIban();
		} else {
			throw new PhoneNumberException();
		}
	}

	public void confirmMbwayCodeError(Integer code, String phoneNumber)
			throws ClientException, AccountException, PhoneNumberException {

		mbwayClientsIbanByPhone(phoneNumber);
		Integer code1 = mbwayClientsCode(phoneNumber);
		if (code.intValue() != code1.intValue()) {
			this.MbwayClients.remove(phoneNumber);
			throw new AccountException();
		} else {
			this.MbwayConfirmedPhoneNumbers.add(phoneNumber);
		}
	}

	public void confirmedPhoneNumbers(String phoneNumber) throws PhoneNumberException {

		if (!this.MbwayConfirmedPhoneNumbers.contains(phoneNumber)) {
			throw new PhoneNumberException();
		}
	}

	public void transferMbway(String sourcePhoneNumber, String targetPhoneNumber, int amount)
			throws AccountException, PhoneNumberException {

		if (!sourcePhoneNumber.equals(targetPhoneNumber)) {
			confirmedPhoneNumbers(sourcePhoneNumber);
			confirmedPhoneNumbers(targetPhoneNumber);
			mbwayClientsIbanByPhone(sourcePhoneNumber);
			mbwayClientsIbanByPhone(targetPhoneNumber);
			String sourceIban = this.MbwayClients.get(sourcePhoneNumber).getIban();
			String targetIban = this.MbwayClients.get(targetPhoneNumber).getIban();
			this.services.withdraw(sourceIban, amount);
			this.services.deposit(targetIban, amount);
		} else {
			throw new PhoneNumberException();
		}
	}

	public ArrayList<Integer> userPaymentBill(String phoneNumber, Integer amountSplit, Integer amount)
			throws AccountException, PhoneNumberException {

		confirmedPhoneNumbers(phoneNumber);
		mbwayClientsIbanByPhone(phoneNumber);
		String sourceIban = this.MbwayClients.get(phoneNumber).getIban();
		this.services.withdraw(sourceIban, amount);
		this.MbwaySplitAmounts.add(amountSplit);
		this.MbwaySplitPhoneNumbers.add(phoneNumber);
		return this.MbwaySplitAmounts;
	}

	public void friendPaymentBill(String phoneNumber, int amountSplit) throws AccountException, PhoneNumberException {

		confirmedPhoneNumbers(phoneNumber);
		mbwayClientsIbanByPhone(phoneNumber);
		String sourceIban = this.MbwayClients.get(phoneNumber).getIban();
		this.services.withdraw(sourceIban, amountSplit);
		this.services.deposit(this.MbwayClients.get(this.MbwaySplitPhoneNumbers.get(0)).getIban(), amountSplit);
		this.MbwaySplitPhoneNumbers.add(phoneNumber);
		this.MbwaySplitAmounts.add(amountSplit);
	}

	public void checkNumberFriends(int numberOfFriends) throws AccountException {
		for (int i = 0; i < this.MbwayClients.size(); i++) {
			if (this.MbwayConfirmedPhoneNumbers.get(i) != null) {
				Integer number = numberOfFriends - this.MbwayConfirmedPhoneNumbers.size() - 1;
				if (number != 0) {
					throw new AccountException();
				}
			}
		}
	}

	public Integer splitBillCheckAmount(int numberOfFriends, int amount) throws AccountException {

		Integer totalAmount = 0;
		for (int i = this.MbwaySplitAmounts.size() - 1; i <= numberOfFriends && i >= 0; i--) {
			Integer amountSplit = this.MbwaySplitAmounts.get(i);
			totalAmount += amountSplit;
		}
		return totalAmount;
	}

	public void clearArraysPhoneAndAmount() throws AccountException {
		this.MbwaySplitAmounts.clear();
		this.MbwaySplitPhoneNumbers.clear();

	}

	public void refundMbwayClientsSplitted(int amount) throws AccountException {

		for (int i = 1; i < this.MbwaySplitPhoneNumbers.size(); i++) {
			this.services.deposit(this.MbwayClients.get(this.MbwaySplitPhoneNumbers.get(i)).getIban(),
					this.MbwaySplitAmounts.get(i));
			this.services.deposit(this.MbwayClients.get(this.MbwaySplitPhoneNumbers.get(0)).getIban(), amount);
			this.services.withdraw(this.MbwayClients.get(this.MbwaySplitPhoneNumbers.get(0)).getIban(),
					this.MbwaySplitAmounts.get(i));
		}
	}

	public Integer splitBillMbway(int numberOfFriends, int amount) throws AccountException {

		int totalAmount = splitBillCheckAmount(numberOfFriends, amount);
		if (totalAmount == amount) {
			clearArraysPhoneAndAmount();
			return totalAmount;
		} else {
			refundMbwayClientsSplitted(amount);
			clearArraysPhoneAndAmount();
			throw new AccountException();
		}
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
