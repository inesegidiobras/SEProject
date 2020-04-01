package mvcdesignpattern;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank.AccountType;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.PhoneNumberException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;

public class MbwayMVCPattern {

	public static void main(String[] args)
			throws ClientException, AccountException, PhoneNumberException, BankException {

		HashMap<String, ClientMbway> MbwayInfo = new HashMap<String, ClientMbway>();
		ArrayList<String> MbwayFriendsPhoneNumber = new ArrayList<String>();
		ArrayList<String> MbwayConfirmedPhoneNumbers = new ArrayList<String>();

		Services services = new Services();
		Sibs sibs = new Sibs(100, services);
		Bank cgd = new Bank("CGD");

		Mbway model = new Mbway(services, MbwayInfo, MbwayConfirmedPhoneNumbers);

		MbwayView view = new MbwayView();

		MbwayController controller = new MbwayController(model, view);

		Client clientOne = new Client(cgd, "Ana", "Íris", "333333333", "918318903", "Street", 29);
		Client clientTwo = new Client(cgd, "Inês", "Brás", "444444444", "966209505", "Street", 24);
		Client clientThree = new Client(cgd, "Rui", "Alves", "555555555", "917654321", "Street", 24);
		Client clientFour = new Client(cgd, "Teresa", "Alves", "666666666", "917654321", "Street", 24);

		cgd.createAccount(AccountType.CHECKING, clientOne, 500, 0);
		cgd.createAccount(AccountType.CHECKING, clientTwo, 1000, 0);
		cgd.createAccount(AccountType.CHECKING, clientThree, 100, 0);
		cgd.createAccount(AccountType.CHECKING, clientFour, 100, 0);

		System.out.println(cgd.createAccount(AccountType.CHECKING, clientOne, 500, 0));
		System.out.println(cgd.createAccount(AccountType.CHECKING, clientTwo, 1000, 0));
		System.out.println(cgd.createAccount(AccountType.CHECKING, clientThree, 100, 0));
		System.out.println(cgd.createAccount(AccountType.CHECKING, clientFour, 100, 0));

		while (true) {

			String userMethod = JOptionPane.showInputDialog(
					"Choose Mbway Operation:" + "\n" + "[1] Associate Mbway" + "\n" + "[2] Confirm Mbway" + "\n"
							+ "[3] Mbway Transfer" + "\n" + "[4] Mbway SplitBill" + "\n" + "Exit");
			if (userMethod.equals("Exit")) {
				break;
			}
			if (userMethod.equals("1")) {
				String userPhoneNumber = view.inputPhoneNumber();
				String userIban = view.inputIban();
				controller.setMbwayAssociation(userPhoneNumber, userIban);

			}
			if (userMethod.equals("2")) {
				String userPhoneNumber = view.inputPhoneNumber();
				Integer code = view.inputCode();
				controller.setMbwayConfirmation(code, userPhoneNumber);
			}
			if (userMethod.equals("3")) {
				String userPhoneNumber = view.inputPhoneNumber();
				String targetPhoneNumber = view.inputFriendPhoneNumber();
				Integer amount = view.inputTransferAmount();
				controller.setMbwayTransfer(userPhoneNumber, targetPhoneNumber, amount);
			}
			if (userMethod.equals("4")) {
				Integer numberOfFriends = view.inputNumberOfFriends();
				Integer amount = view.inputTotalAmount();
				String userPhoneNumber = view.inputPhoneNumber();
				Integer userAmountSplit = view.inputSplittedAmount();
				MbwayFriendsPhoneNumber.add(userPhoneNumber);
				ArrayList<Integer> amountSplitList = new ArrayList<>();
				amountSplitList.add(userAmountSplit);
				System.out.println(services.getAccountByIban("CGDCK5").getBalance());
				while (!view.inputFriend().equals("End")) {
					String friendPhoneNumber = view.inputFriendPhoneNumber();
					Integer amountSplit = view.inputSplittedAmount();
					amountSplitList.add(amountSplit);
					if (!MbwayFriendsPhoneNumber.contains(friendPhoneNumber)) {
						MbwayFriendsPhoneNumber.add(friendPhoneNumber);
					}
				}
				if (numberOfFriends != (MbwayFriendsPhoneNumber.size() - 1)) {
					controller.setNumberOfFriends(numberOfFriends, amount);
				} else {
					controller.setUserPaymentBill(userPhoneNumber, userAmountSplit, amount);
					for (int i = 1; i < MbwayFriendsPhoneNumber.size(); i++) {
						controller.setFriendPaymentBill(MbwayFriendsPhoneNumber.get(i), amountSplitList.get(i));
						System.out.println(services.getAccountByIban("CGDCK6").getBalance());
						System.out.println(services.getAccountByIban("CGDCK7").getBalance());
					}
					controller.setSplitBillMbway(numberOfFriends, amount);
					System.out.println(services.getAccountByIban("CGDCK5").getBalance());
					System.out.println(services.getAccountByIban("CGDCK6").getBalance());
				}
				MbwayFriendsPhoneNumber.clear();
				amountSplitList.clear();
			}
		}
	}
}
