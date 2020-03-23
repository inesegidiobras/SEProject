package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import pt.ulisboa.tecnico.learnjava.bank.domain.Bank;
import pt.ulisboa.tecnico.learnjava.bank.domain.Client;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.SibsException;

public class TransferMethodTest {
	private static final String ADDRESS = "Ave.";
	private static final String PHONE_NUMBER = "987654321";
	private static final String NIF = "123456789";
	private static final String LAST_NAME = "Silva";
	private static final String FIRST_NAME = "António";

	private Sibs sibs;
	private Bank sourceBank;
	private Bank targetBank;
	private Client sourceClient;
	private Client targetClient;

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		this.sourceClient = new Client(this.sourceBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 33);
		this.targetClient = new Client(this.targetBank, FIRST_NAME, LAST_NAME, NIF, PHONE_NUMBER, ADDRESS, 22);
	}

	@Test
	public void SourceAccountDoesntExistTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccount("sourceIban")).thenReturn(false);
		when(services.checkAccount("targetIban")).thenReturn(true);

		try {
			this.sibs.transfer("sourceIban", "targetIban", 100);
			fail();
		} catch (AccountException e) {
			verify(services, times(0)).deposit("targetIban", 100);
			verify(services, times(0)).withdraw("sourceIban", 100);
		}
		;

		doThrow(AccountException.class).when(services).deposit("targetIban", 100);

		assertNotNull(this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0));

	}

	@Test
	public void TargetAccountDoesntExistTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccount("sourceIban")).thenReturn(true);
		when(services.checkAccount("targetIban")).thenReturn(false);

		try {
			this.sibs.transfer("sourceIban", "targetIban", 100);
			fail();
		} catch (AccountException e) {
			verify(services, times(0)).deposit("targetIban", 100);
			verify(services, times(0)).withdraw("sourceIban", 100);
		}
		;

		doThrow(AccountException.class).when(services).deposit("targetIban", 100);

		assertNotNull(this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0));

	}

	@Test
	public void AccountsDoesntExistTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccount("sourceIban")).thenReturn(false);
		when(services.checkAccount("targetIban")).thenReturn(false);

		try {
			this.sibs.transfer("sourceIban", "targetIban", 100);
			fail();
		} catch (AccountException e) {
			verify(services, times(0)).deposit("targetIban", 100);
			verify(services, times(0)).withdraw("sourceIban", 100);
		}
		;

		doThrow(AccountException.class).when(services).deposit("targetIban", 100);

	}

	@Test
	public void AccountsExistBankExistTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccount("sourceIban")).thenReturn(true);
		when(services.checkAccount("targetIban")).thenReturn(true);
		when(services.checkSameBank("sourceIban", "targetIban")).thenReturn(true);

		this.sibs.transfer("sourceIban", "targetIban", 100);
		verify(services, times(1)).deposit("targetIban", 100);
		verify(services, times(1)).withdraw("sourceIban", 100);

	}

	@Test
	public void AccountsExistBankDoesntExistTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccount("sourceIban")).thenReturn(true);
		when(services.checkAccount("targetIban")).thenReturn(true);
		when(services.checkSameBank("sourceIban", "targetIban")).thenReturn(false);

		this.sibs.transfer("sourceIban", "targetIban", 100);
		verify(services).deposit("targetIban", 100);
		verify(services).withdraw("sourceIban", 106);

	}

	@Test
	public void DepositFails()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		try {
			this.sibs.transfer("sourceIban", "targetIban", 100);
			fail();
		} catch (AccountException e) {
			verify(services, times(0)).deposit("targetIban", 100);
			verify(services, times(0)).withdraw("sourceIban", 100);
		}
		;
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
