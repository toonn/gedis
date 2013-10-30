package rentalagency;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SessionCreationServiceRemote extends Remote {

    public abstract ReservationSessionRemote getNewReservationSession(String client)
            throws RemoteException;

    public abstract ManagerSessionRemote getNewManagerSession(String manager)
            throws RemoteException;

}