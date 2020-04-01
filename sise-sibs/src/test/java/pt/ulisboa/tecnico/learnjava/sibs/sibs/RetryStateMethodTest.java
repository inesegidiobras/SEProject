package pt.ulisboa.tecnico.learnjava.sibs.sibs;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
import statedesignpattern.Processed;

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
	public void invalidWithdrawOneRetryState()
			throws SibsException, AccountException, OperationException, BankException, ClientException {

		Services services = mock(Services.class);
		Sibs sibs = new Sibs(100, services);

		when(services.checkAccountExists("sourceIban")).thenReturn(true);
		when(services.checkAccountExists("targetIban")).thenReturn(true);
		when(services.checkSameBank("sourceIban", "targetIban")).thenReturn(false);
		doThrow(new AccountException()).doNothing().when(services).withdraw("sourceIban", 100);

		sibs.transfer("sourceIban", "targetIban", 100);
		sibs.processOperations();

		TransferOperation transfer = (TransferOperation) sibs.getOperation(0);
		verify(services, times(2)).withdraw("sourceIban", 100);
		verify(services).deposit("targetIban", 100);
		verify(services).withdraw("sourceIban", 6);
		assertTrue(transfer.getState() instanceof Processed);
	}

}
