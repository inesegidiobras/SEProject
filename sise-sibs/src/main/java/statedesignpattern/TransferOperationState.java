package statedesignpattern;

import pt.ulisboa.tecnico.learnjava.bank.exceptions.AccountException;
import pt.ulisboa.tecnico.learnjava.sibs.domain.Context;
import pt.ulisboa.tecnico.learnjava.sibs.exceptions.OperationException;

public interface TransferOperationState {

	void process(Context state) throws AccountException, OperationException;

	void cancel(Context state) throws OperationException, AccountException;

}
