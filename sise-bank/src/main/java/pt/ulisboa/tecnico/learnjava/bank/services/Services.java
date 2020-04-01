package pt.ulisboa.tecnico.learnjava.bank.services;

import pt.ulisboa.tecnico.learnjava.bank.domain.Account;
import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;

public class Services {
	public void deposit(String iban, int amount) throws AccountException {
		Account account = getAccountByIban(iban);

		account.deposit(amount);
	}

	public void withdraw(String iban, int amount) throws AccountException {
		Account account = getAccountByIban(iban);

		account.withdraw(amount);
	}

	public Account getAccountByIban(String iban) throws AccountException {

		String code = iban.substring(0, 3);
		String accountId = iban.substring(3);
		Bank bank = Bank.getBankByCode(code);
		if (bank != null) {
			Account account = bank.getAccountByAccountId(accountId);
			if (account != null) {
				return account;
			} else {
				throw new AccountException();
			}
		}
		throw new AccountException();
	}

	public Bank getBankByIban(String iban) {
		String code = iban.substring(0, 3);
		Bank bank = Bank.getBankByCode(code);

		return bank;
	}

	public Boolean checkAccountExists(String iban) throws AccountException {
		return this.getAccountByIban(iban) != null;
	}

	public Boolean checkSameBank(String sourceIban, String targetIban) throws AccountException {
		String codesource = sourceIban.substring(0, 3);
		String codetarget = targetIban.substring(0, 3);
		Bank banksource = Bank.getBankByCode(codesource);
		Bank banktarget = Bank.getBankByCode(codetarget);
		return banksource.equals(banktarget);
	}
}
