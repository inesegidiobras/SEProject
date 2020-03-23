package mvcdesignpattern;

import javax.swing.JOptionPane;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class MbwayController {

	private Mbway model;
	private MbwayView view;

	public MbwayController(Mbway model, MbwayView view) {
		this.model = model;
		this.view = view;
	}

	public void setMbwayAssociation(String phoneNumber, String iban) throws ClientException {
		iban = this.view.inputIban();
		phoneNumber = this.view.inputPhoneNumber();
		Integer code = this.model.associateMbway(phoneNumber, iban);
		JOptionPane.showMessageDialog(null, "The confirmation code is " + code + " (don’t share it with anyone)");
		JOptionPane.showMessageDialog(null, "An Error occurred.");
	}

	public void setMbwayConfirmation(Integer code, String phoneNumber) throws ClientException, AccountException {
		this.model.confirmMbway(code, phoneNumber);
		JOptionPane.showMessageDialog(null, "MBway association confirmed successfully!");
		JOptionPane.showMessageDialog(null, "Wrong confirmation code. Try association again.");
	}

	public void setMbwayTransfer(String sourcePhoneNumber, String targetPhoneNumber, int amount)
			throws ClientException, AccountException {
		this.model.transferMbway(sourcePhoneNumber, targetPhoneNumber, amount);
		JOptionPane.showMessageDialog(null, "Not enough money in the source account.");
		JOptionPane.showMessageDialog(null, "Wrong phone number.");
		JOptionPane.showMessageDialog(null, "Transfer performed successfully!");
	}

	public void setMbwayBill(int numberOfFriends, int amount) throws ClientException, AccountException {
		this.model.splitBillMbway(numberOfFriends, amount);
		JOptionPane.showMessageDialog(null, "Oh no! Too many friends.");
		JOptionPane.showMessageDialog(null, "Oh no! One friend is missing.");
		JOptionPane.showMessageDialog(null, "Oh no! Friend does not have money to pay!");
		JOptionPane.showMessageDialog(null, "Friend payment done!");
		JOptionPane.showMessageDialog(null, "Bill payed successfully!");
	}
}
