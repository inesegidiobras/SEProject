package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

public class RetryStateMethodTest {

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
	private Person sourcePerson;
	private Person targetPerson;
	private String sourceIban;
	private String targetIban;

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
	}

	@Test
	public void invalidDepositRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.checkAccountExists(this.sourceIban)).thenReturn(true);
		when(serviceMock.checkAccountExists(this.targetIban)).thenReturn(true);
		when(serviceMock.checkSameBank(this.sourceIban, this.targetIban)).thenReturn(true);
		doThrow(new AccountException()).when(serviceMock).deposit(this.targetIban, 100);

//		Sibs sibs = new Sibs(100, serviceMock);
//		sibs.transfer(this.sourceIban, this.targetIban, 100);
//		sibs.processOperations();
//
//		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
//		verify(serviceMock).withdraw(this.sourceIban, 100);
//		verify(serviceMock, times(3)).deposit(this.targetIban, 100);
//		verify(serviceMock, never()).withdraw(this.sourceIban, 6);
//		verify(serviceMock).deposit(this.sourceIban, 100);
//		verify(serviceMock, never()).withdraw(this.targetIban, 100);
//		verify(serviceMock, never()).deposit(this.sourceIban, 6);
//		assertTrue(transfer.getState() instanceof Error);
	}

	@Test
	public void invalidWithdrawRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services services = mock(Services.class);

		when(services.checkAccountExists("sourceIban")).thenReturn(false);
		when(services.checkAccountExists("targetIban")).thenReturn(true);

		when(services.checkSameBank(this.sourceIban, this.targetIban)).thenReturn(true);
		doThrow(new AccountException()).when(services).withdraw(this.sourceIban, 100);

		Sibs sibs = new Sibs(100, services);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations(0);

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		verify(services, times(3)).withdraw(this.sourceIban, 100);
		verify(services, never()).deposit(this.targetIban, 100);
		verify(services, never()).withdraw(this.sourceIban, 6);
		verify(services, never()).deposit(this.sourceIban, 100);
		verify(services, never()).withdraw(this.targetIban, 100);
		verify(services, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof Error);
	}

	@Test
	public void invalidWithdrawComissionRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).when(serviceMock).withdraw(this.sourceIban, 6);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		verify(serviceMock).withdraw(this.sourceIban, 100);
		verify(serviceMock).deposit(this.targetIban, 100);
		verify(serviceMock, times(3)).withdraw(this.sourceIban, 6);
		verify(serviceMock).deposit(this.sourceIban, 100);
		verify(serviceMock).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof ERROR);
	}

	@Test
	public void invalidWithdrawComissionOneRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).doNothing().when(serviceMock).withdraw(this.sourceIban, 6);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		// After the first attempt to withdraw the value and fail, the trasfer is in the
		// RETRY state, so it is invoked the second time
		verify(serviceMock).withdraw(this.sourceIban, 100);
		verify(serviceMock).deposit(this.targetIban, 100);
		verify(serviceMock, times(2)).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof COMPLETED);
	}

	@Test
	public void invalidWithdrawOneRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).doNothing().when(serviceMock).withdraw(this.sourceIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		// After the first attempt to withdraw the value and fail, the trasfer is in the
		// RETRY state, so it is invoked the second time
		verify(serviceMock, times(2)).withdraw(this.sourceIban, 100);
		verify(serviceMock).deposit(this.targetIban, 100);
		verify(serviceMock).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof COMPLETED);
	}

	@Test
	public void invalidDepositOneRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).doNothing().when(serviceMock).deposit(this.targetIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		// After the first attempt to deposit the value and fail, the trasfer is in the
		// RETRY state, so it is invoked the second time
		verify(serviceMock).withdraw(this.sourceIban, 100);
		verify(serviceMock, times(2)).deposit(this.targetIban, 100);
		verify(serviceMock).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer.getState() instanceof COMPLETED);
	}

	public void multipleTransfersCompletedAfterOneRetry()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).doNothing().when(serviceMock).deposit(this.targetIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.transfer(this.sourceIban, this.targetIban, 50);
		sibs.processOperations();

		TransferOperation transfer1 = (TransferOperation) sibs.getOperation(0);
		TransferOperation transfer2 = (TransferOperation) sibs.getOperation(1);
		verify(serviceMock).withdraw(this.sourceIban, 100);
		verify(serviceMock, times(2)).deposit(this.targetIban, 100);
		verify(serviceMock).withdraw(this.sourceIban, 50);
		verify(serviceMock).deposit(this.targetIban, 50);
		verify(serviceMock).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer1.getState() instanceof COMPLETED);
		assertTrue(transfer2.getState() instanceof COMPLETED);
	}

	@Test
	public void multipleTransfersCompletedAfterOneRetryAndFail()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services serviceMock = mock(Services.class);

		when(serviceMock.verifyAccountExistanceInBank(this.sourceIban)).thenReturn(true);
		when(serviceMock.verifyAccountExistanceInBank(this.targetIban)).thenReturn(true);
		when(serviceMock.verifySameBank(this.sourceIban, this.targetIban)).thenReturn(false);
		doThrow(new AccountException()).when(serviceMock).withdraw(this.sourceIban, 100);

		Sibs sibs = new Sibs(100, serviceMock);
		sibs.transfer(this.sourceIban, this.targetIban, 100);
		sibs.transfer(this.sourceIban, this.targetIban, 50);
		sibs.processOperations();

		TransferOperation transfer1 = (TransferOperation) sibs.getOperation(0);
		TransferOperation transfer2 = (TransferOperation) sibs.getOperation(1);
		verify(serviceMock, times(3)).withdraw(this.sourceIban, 100);
		verify(serviceMock, never()).deposit(this.targetIban, 100);
		verify(serviceMock).withdraw(this.sourceIban, 50);
		verify(serviceMock).deposit(this.targetIban, 50);
		verify(serviceMock, never()).withdraw(this.sourceIban, 6);
		verify(serviceMock, never()).deposit(this.sourceIban, 100);
		verify(serviceMock, never()).withdraw(this.targetIban, 100);
		verify(serviceMock, never()).deposit(this.sourceIban, 6);
		assertTrue(transfer1.getState() instanceof ERROR);
		assertTrue(transfer2.getState() instanceof COMPLETED);
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

}
