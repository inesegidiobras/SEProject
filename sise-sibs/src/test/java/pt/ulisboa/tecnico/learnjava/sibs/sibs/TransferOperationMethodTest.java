package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import statedesignpattern.Cancelled;
import statedesignpattern.Deposited;
import statedesignpattern.Error;
import statedesignpattern.Processed;
import statedesignpattern.Registered;
import statedesignpattern.Retry;
import statedesignpattern.Withdrawn;

public class TransferOperationMethodTest {

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
	private String sourceIban;
	private String targetIban;
	private String sourceIbanError;
	private String targetIbanError;

	@Before
	public void setUp() throws BankException, AccountException, ClientException {
		this.sourceBank = new Bank("CGD");
		this.targetBank = new Bank("BPI");
		this.sourcePerson = new Person(FIRST_NAME, LAST_NAME, ADDRESS, 33);
		this.targetPerson = new Person(FIRST_NAME, LAST_NAME, ADDRESS, 22);
		this.sourceClient = new Client(this.sourcePerson, this.sourceBank, NIF, PHONE_NUMBER);
		this.targetClient = new Client(this.targetPerson, this.targetBank, NIF, PHONE_NUMBER);
		this.sourceIban = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 1000, 0);
		this.targetIban = this.targetBank.createAccount(Bank.AccountType.CHECKING, this.targetClient, 1000, 0);
		this.sourceIbanError = this.sourceBank.createAccount(Bank.AccountType.CHECKING, this.sourceClient, 0, 0);
	}

	@Test
	public void confirmRegisteredStateTest() throws OperationException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Registered);
		assertEquals(1000, services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void confirmWithdrawnStateTest() throws OperationException, SibsException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(services);
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Withdrawn);
		assertEquals(894, services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void confirmDepositedStateTest() throws OperationException, SibsException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(services);
		transferOperation.process(services);
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Deposited);
		assertEquals(894, services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1100, services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void confirmProcessedStateTest() throws OperationException, SibsException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(services);
		transferOperation.process(services);
		transferOperation.process(services);
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Processed);
		assertEquals(894, services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1100, services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelRegisteredStateTest() throws OperationException, SibsException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.cancel();
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Cancelled);
		assertEquals(1000, services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelWithdrawnStateTest() throws OperationException, SibsException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(services);
		transferOperation.cancel();
		// When a Withdrawn State Operation is cancelled the withdrawn amount is
		// refunded
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Cancelled);
		assertEquals(1000, services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelDepositedStateTest() throws OperationException, SibsException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(services);
		transferOperation.process(services);
		transferOperation.cancel();
		// When a Deposited State Operation is cancelled the withdrawn amount and the
		// deposited amount are refunded
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Cancelled);
		assertEquals(1000, services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelProcessedStateTest() throws OperationException, SibsException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(services);
		transferOperation.process(services);
		transferOperation.process(services);
		transferOperation.cancel();
		// It is not possible to cancel Processed State Operations, so the deposits and
		// withdraws should be done and the state should remain the same
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Processed);
		assertEquals(894, services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1100, services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void cancelCancelledStateTest() throws OperationException, SibsException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIban, this.targetIban, 100);
		transferOperation.process(services);
		transferOperation.cancel();
		transferOperation.cancel();
		// It is not possible to cancel Cancelled State Operations, the state should
		// remain the same
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Cancelled);
		assertEquals(1000, services.getAccountByIban(this.sourceIban).getBalance());
		assertEquals(1000, services.getAccountByIban(this.targetIban).getBalance());
	}

	@Test
	public void retryErrorStateTest() throws OperationException, SibsException, AccountException {
		Services services = new Services();
		this.sibs = new Sibs(100, services);

		TransferOperation transferOperation = new TransferOperation(this.sourceIbanError, this.targetIban, 100);
		transferOperation.process(services);
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Retry);
		transferOperation.process(services);
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Retry);
		transferOperation.process(services);
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Retry);
		transferOperation.process(services);
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Error);
		// It is not possible to cancel Error State Operations, the state should
		// remain the same
		transferOperation.cancel();
		assertTrue(transferOperation.getStateContext().getCurrentState() instanceof Error);
		assertEquals(0, services.getAccountByIban(this.sourceIbanError).getBalance());
		assertEquals(1000, services.getAccountByIban(this.targetIban).getBalance());
	}

	@After
	public void tearDown() {
		Bank.clearBanks();
	}

}
