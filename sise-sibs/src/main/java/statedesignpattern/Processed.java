package statedesignpattern;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public class Processed implements TransferOperationState {

	@Override
	public void process(Context state) throws AccountException, OperationException {
		// TODO Auto-generated method stub

	}

	@Override
	public void cancel(Context state) throws OperationException, AccountException {
		// TODO Auto-generated method stub

	}

}
