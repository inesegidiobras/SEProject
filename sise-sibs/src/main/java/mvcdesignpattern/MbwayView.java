package mvcdesignpattern;

import javax.swing.JOptionPane;

public class MbwayView {

	private String iban;
	private String phoneNumber;
	private Integer code;
	private Integer transferAmount;
	private String targetPhoneNumber;
	private Integer numberOfFriends;
	private Integer splitAmount;
	private String message;
	private Integer number;

	public String inputIban() {
		return this.iban = JOptionPane.showInputDialog("Insert your iban: ");
	}

	public String inputPhoneNumber() {
		return this.phoneNumber = JOptionPane.showInputDialog("Insert your phone number: ");
	}

	public String inputFriendPhoneNumber() {
		return this.phoneNumber = JOptionPane.showInputDialog("Insert Friend phone number: ");
	}

	public Integer inputCode() {
		return this.code = Integer.parseInt(JOptionPane.showInputDialog("Insert your confirmation code: "));
	}

	public Integer inputTransferAmount() {
		return this.transferAmount = Integer.parseInt(JOptionPane.showInputDialog("Insert the transfer amount: "));
	}

	public String inputTransferTargetPhoneNumber() {
		return this.targetPhoneNumber = JOptionPane.showInputDialog("Insert the target Mbway phone number: ");
	}

	public Integer inputNumberOfFriends() {
		return this.numberOfFriends = Integer.parseInt(JOptionPane.showInputDialog("Insert the number of friends: "));
	}

	public Integer inputTotalAmount() {
		return this.splitAmount = Integer.parseInt(JOptionPane.showInputDialog("Insert the bill amount: "));
	}

	public Integer inputSplittedAmount() {
		return this.splitAmount = Integer.parseInt(JOptionPane.showInputDialog("Insert the splitted amount: "));
	}

	public void associateMbwayMessageError() {
		JOptionPane.showMessageDialog(null, "An Error occured. Please try again.");
	}

	public void associateMbwayMessageSuccess() {
		JOptionPane.showMessageDialog(null, "The confirmation code is " + this.code + " (don’t share it with anyone)");
	}

	public void confirmMbwayMessageErrorCode() {
		JOptionPane.showMessageDialog(null, "Wrong confirmation code. Try association again, choose option 1.");
	}

	public void confirmMbwayMessageErrorPhone() {
		JOptionPane.showMessageDialog(null, "Wrong Phone Number. Try confirmation again.");
	}

	public void confirmMbwayMessageSuccess() {
		JOptionPane.showMessageDialog(null, "MBway association confirmed successfully!");
	}

	public void transferMbwayMessageErrorPhone() {
		JOptionPane.showMessageDialog(null, "Wrong phone number.");
	}

	public void transferMbwayMessageErrorMoney() {
		JOptionPane.showMessageDialog(null, "Not enough money in the source account.");
	}

	public void transferMbwayMessageSuccess() {
		JOptionPane.showMessageDialog(null, "Transfer performed successfully!");
	}

	public void numberOfFriendsMbwayMessage() {
		JOptionPane.showMessageDialog(null, "Incorrect number of friends. Try split again.");
	}

	public void friendMbwayMessageErrorPhone() {
		JOptionPane.showMessageDialog(null, "Friend " + this.phoneNumber + " is not registered.");
	}

	public void friendMbwayMessageErrorMoney() {
		JOptionPane.showMessageDialog(null, "Oh no! Friend does not have money to pay!");
	}

	public void friendMbwayMessageSuccess() {
		JOptionPane.showMessageDialog(null, "Friend payment done!");
	}

	public void userMbwayMessageErrorMoney() {
		JOptionPane.showMessageDialog(null, "Oh no! You don't have money to pay!");
	}

	public void userMbwayMessageSuccess() {
		JOptionPane.showMessageDialog(null, "Your payment was done!");
	}

	public void splitBillMbwayMessageErrorMoney() {
		JOptionPane.showMessageDialog(null, "Something went wrong. The money was refunded. Please try split again.");
	}

	public void splitBillMbwayMessageSuccess() {
		JOptionPane.showMessageDialog(null, "Bill payed successfully!");
	}

	public String inputFriend() {
		return this.message = JOptionPane
				.showInputDialog("Enter your Friends (Press 'Ok' to start or 'End' to finish).");
	}
}
