package rentalagency;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class SessionCreationService implements SessionCreationServiceRemote {
    /*
     * (non-Javadoc)
     * 
     * @see
     * rentalagency.SessionCreationServiceRemote#getNewReservationSession(java
     * .lang.String)
     */
    @Override
    public ReservationSession getNewReservationSession(String client) {
        ReservationSessionRemote stub = null;
        System.setSecurityManager(null);
        try {
            stub = (ReservationSessionRemote) UnicastRemoteObject.exportObject(
                    new ReservationSession(client), 0);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return (ReservationSession) stub;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * rentalagency.SessionCreationServiceRemote#getNewManagerSession(java.lang
     * .String)
     */
    @Override
    public ManagerSessionRemote getNewManagerSession(String manager) {
        System.setSecurityManager(null);
        ManagerSessionRemote stub = null;
        try {
            stub = (ManagerSessionRemote) UnicastRemoteObject.exportObject(
                    new ManagerSession(manager), 0);
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return (ManagerSessionRemote) stub;
    }

    public static void main(String[] args) {
        System.setSecurityManager(null);
        try {
            String name = "SessionCreationService";
            SessionCreationServiceRemote sessionCreationService = new SessionCreationService();
            SessionCreationServiceRemote stub = (SessionCreationServiceRemote) UnicastRemoteObject
                    .exportObject(sessionCreationService, 0);
            Registry registry = LocateRegistry.getRegistry();
            registry.rebind(name, stub);
            
            System.out.println("SessionCreationService started...");
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
