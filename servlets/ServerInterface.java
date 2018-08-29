package servlet;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;

public interface ServerInterface extends Remote
{
    public String[] getStates(String state_regex) throws RemoteException, SQLException;
    public String[] getCapitals(String capital_regex) throws RemoteException, SQLException;
}
