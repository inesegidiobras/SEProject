package pt.ulisboa.tecnico.learnjava.bank.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.CheckingAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.domain.Person;
import pt.ulisboa.tecnico.learnjava.bank.domain.SalaryAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.SavingsAccount;
import pt.ulisboa.tecnico.learnjava.bank.domain.YoungAccount;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;

public class WithdrawMethodTest {
	private static final String OWNER_NAME = "João";

	private CheckingAccount checking;
	private SavingsAccount savings;
	private SalaryAccount salary;
	private YoungAccount young;

	@Before
	public void setUp() throws AccountException, BankException, ClientException {
		Bank bank = new Bank("CGD");

		Person person = new Person("José", "Manuel", "Street", 33);
		Person youngperson = new Person("José", "Manuel", "Street", 17);
		Client client = new Client(person, bank, "123456789", "987654321");
		Client youngclient = new Client(youngperson, bank, "123456780", "987654321");

		this.checking = new CheckingAccount(client, 100);
		this.savings = new SavingsAccount(client, 100, 10);
		this.salary = new SalaryAccount(client, 100, 1000);
		this.young = new YoungAccount(youngclient, 100);
	}

	@Test
	public void successForCheckingAccount() throws AccountException {
		this.checking.withdraw(50);

		assertEquals(50, this.checking.getBalance());
	}

	@Test
	public void negativeAmountForCheckingAccount() {
		try {
			this.checking.withdraw(-10);
			fail();
		} catch (AccountException e) {
			assertEquals(100, this.checking.getBalance());
		}
	}

	@Test
	public void notEnoughBalanceForCheckingAccount() {
		try {
			this.checking.withdraw(200);
			fail();
		} catch (AccountException e) {
			assertEquals(100, this.checking.getBalance());
		}
	}

	@Test
	public void successForSavingsAccount() throws AccountException {
		this.savings.withdraw(100);

		assertEquals(0, this.savings.getBalance());
	}

	@Test
	public void negativeAmountForSavingsAccount() {
		try {
			this.savings.withdraw(-10);
			fail();
		} catch (AccountException e) {
			assertEquals(100, this.checking.getBalance());
		}
	}

	@Test
	public void amountNotEqualToBalanceInSavingsAccount() {
		try {
			this.savings.withdraw(50);
			fail();
		} catch (AccountException e) {
			assertEquals(100, this.savings.getBalance());
		}
	}

	@Test
	public void successNegativeBalanceForSalaryAccount() throws AccountException {
		this.salary.withdraw(900);

		assertEquals(-800, this.salary.getBalance());
	}

	@Test
	public void negativeAmountForSalaryAccount() {
		try {
			this.salary.withdraw(-10);
			fail();
		} catch (AccountException e) {
			assertEquals(100, this.checking.getBalance());
		}
	}

	@Test
	public void failNegativeBalanceForSalaryAccount() throws AccountException {
		try {
			this.salary.withdraw(2000);
			fail();
		} catch (AccountException e) {
			assertEquals(100, this.salary.getBalance());
		}
	}

	@Test
	public void noWithdrawForYoung() throws AccountException {
		try {
			this.young.withdraw(100);
			fail();
		} catch (AccountException e) {
			assertEquals(100, this.young.getBalance());
		}
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
