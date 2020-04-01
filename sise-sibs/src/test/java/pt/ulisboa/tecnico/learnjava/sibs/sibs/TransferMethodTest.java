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
import pt.ulisboa.tecnico.learnjava.bank.domain.Person;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.BankException;
import pt.ulisboa.tecnico.learnjava.bank.exceptions.ClientException;
import pt.ulisboa.tecnico.learnjava.bank.services.Services;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Sibs;
import pt.ulisboa.tecnico.learnjava.sibs.domain.TransferOperation;
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
	private Person sourcePerson;
	private Person targetPerson;
	private Client sourceClient;
	private Client targetClient;

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		this.sourcePerson = new Person(FIRST_NAME, LAST_NAME, ADDRESS, 33);
		this.targetPerson = new Person(FIRST_NAME, LAST_NAME, ADDRESS, 22);
		this.sourceClient = new Client(this.sourcePerson, this.sourceBank, NIF, PHONE_NUMBER);
		this.targetClient = new Client(this.targetPerson, this.targetBank, NIF, PHONE_NUMBER);
	}

	@Test
	public void sourceAccountDoesntExistTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccountExists("sourceIban")).thenReturn(false);
		when(services.checkAccountExists("targetIban")).thenReturn(true);

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
	public void targetAccountDoesntExistTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccountExists("sourceIban")).thenReturn(true);
		when(services.checkAccountExists("targetIban")).thenReturn(false);

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
	public void accountsDontExistTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccountExists("sourceIban")).thenReturn(false);
		when(services.checkAccountExists("targetIban")).thenReturn(false);

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

	// This test suffered a transformation after the implementation of Part2
	// After Part2 the transfer is done depending on its state
	@Test
	public void accountsExistInTheSameBankTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccountExists("sourceIban")).thenReturn(true);
		when(services.checkAccountExists("targetIban")).thenReturn(true);
		when(services.checkSameBank("sourceIban", "targetIban")).thenReturn(true);

		TransferOperation transferOperation = new TransferOperation("sourceIban", "targetIban", 100);
		transferOperation.process(services);
		transferOperation.process(services);

		this.sibs.transfer("sourceIban", "targetIban", 100);

		verify(services, times(1)).deposit("targetIban", 100);
		verify(services, times(1)).withdraw("sourceIban", 100);
	}

	// This test suffered a transformation after the implementation of Part2
	@Test
	public void accountsExistInDifferentBanksTest()
			throws BankException, AccountException, SibsException, OperationException, ClientException {
		Services services = mock(Services.class);
		this.sibs = new Sibs(100, services);

		when(services.checkAccountExists("sourceIban")).thenReturn(true);
		when(services.checkAccountExists("targetIban")).thenReturn(true);
		when(services.checkSameBank("sourceIban", "targetIban")).thenReturn(false);

		TransferOperation transferOperation = new TransferOperation("sourceIban", "targetIban", 100);
		transferOperation.process(services);
		transferOperation.process(services);

		verify(services, times(1)).deposit("targetIban", 100);
		verify(services, times(1)).withdraw("sourceIban", 100);
		verify(services, times(1)).withdraw("sourceIban", 6);
	}

	@Test
	public void depositFails()
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
