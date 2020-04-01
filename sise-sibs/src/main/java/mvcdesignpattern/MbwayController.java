package mvcdesignpattern;

import javax.swing.JOptionPane;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.PhoneNumberException;

public class MbwayController {

	private Mbway model;
	private MbwayView view;

	public MbwayController(Mbway model, MbwayView view) {
		this.model = model;
		this.view = view;
	}

	public void setMbwayAssociation(String phoneNumber, String iban) throws ClientException {
		try {
			this.model.checkMbway(phoneNumber, iban);
			this.model.clientDoesNotExist(phoneNumber, iban);
			this.model.associateMbway(phoneNumber, iban);
			Integer code = this.model.associateMbway(phoneNumber, iban);
			JOptionPane.showMessageDialog(null, "The confirmation code is " + code + " (don’t share it with anyone)");
		} catch (AccountException e) {
			this.view.associateMbwayMessageError();
		}
	}

	public void setMbwayConfirmation(Integer code, String phoneNumber)
			throws ClientException, AccountException, PhoneNumberException {

		try {
			this.model.confirmMbwayCodeError(code, phoneNumber);
			this.view.confirmMbwayMessageSuccess();
		} catch (PhoneNumberException e) {
			this.view.confirmMbwayMessageErrorPhone();
		} catch (AccountException e) {
			this.view.confirmMbwayMessageErrorCode();
		}
	}

	public void setMbwayTransfer(String sourcePhoneNumber, String targetPhoneNumber, int amount)
			throws AccountException, PhoneNumberException {
		try {
			this.model.transferMbway(sourcePhoneNumber, targetPhoneNumber, amount);
			this.view.transferMbwayMessageSuccess();
		} catch (PhoneNumberException e) {
			this.view.transferMbwayMessageErrorPhone();
		} catch (AccountException e) {
			this.view.transferMbwayMessageErrorMoney();
		}
	}

	public void setNumberOfFriends(int numberOfFriends, int amount) throws ClientException, AccountException {

		try {
			this.model.checkNumberFriends(numberOfFriends);
		} catch (AccountException e) {
			this.view.numberOfFriendsMbwayMessage();
		}
	}

	public void setFriendPaymentBill(String phoneNumber, int amountSplit) throws ClientException, AccountException {

		try {
			this.model.friendPaymentBill(phoneNumber, amountSplit);
			this.view.friendMbwayMessageSuccess();
		} catch (PhoneNumberException e) {
			JOptionPane.showMessageDialog(null, "Friend " + phoneNumber + " is not registered.");
		} catch (AccountException e) {
			this.view.friendMbwayMessageErrorMoney();
		}
	}

	public void setUserPaymentBill(String phoneNumber, int amountSplit, int amount)
			throws ClientException, AccountException, PhoneNumberException {

		try {
			this.model.userPaymentBill(phoneNumber, amountSplit, amount);
			this.view.userMbwayMessageSuccess();
		} catch (PhoneNumberException e) {
			JOptionPane.showMessageDialog(null, phoneNumber + " is not registered.");
		} catch (AccountException e) {
			this.view.userMbwayMessageErrorMoney();
		}
	}

	public void setSplitBillMbway(int numberOfFriends, int amount) throws ClientException, AccountException {

		try {
			this.model.splitBillMbway(numberOfFriends, amount);
			this.view.splitBillMbwayMessageSuccess();
		} catch (AccountException e) {
			this.view.splitBillMbwayMessageErrorMoney();
		}
	}
}
