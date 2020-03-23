package mvcdesignpattern;

import javax.swing.JOptionPane;

public class MbwayView {

	private String iban;
	private String phoneNumber;
	private String code;
	private Integer transferAmount;
	private String targetPhoneNumber;
	private String numberOfFriends;
	private Integer splitAmount;

	public String inputIban() {
		return this.iban = JOptionPane.showInputDialog("Insert your iban: ");
	}

	public String inputPhoneNumber() {
		return this.phoneNumber = JOptionPane.showInputDialog("Insert your phone number: ");
	}

	public String inputCode() {
		return this.code = JOptionPane.showInputDialog("Insert your confirmation code: ");
	}

	public Integer inputTransferAmount() {
		return this.transferAmount = Integer.parseInt(JOptionPane.showInputDialog("Insert the transfer amount: "));
	}

	public String inputTransferTargetPhoneNumber() {
		return this.targetPhoneNumber = JOptionPane.showInputDialog("Insert the target Mbway phone number: ");
	}

	public String inputNumberOfFriends() {
		return this.numberOfFriends = JOptionPane.showInputDialog("Insert the number of friends: ");
	}

	public Integer inputAmountToSplit() {
		return this.splitAmount = Integer.parseInt(JOptionPane.showInputDialog("Insert the bill amount: "));
	}
}
