package rentalagency;

import java.rmi.RemoteException;
import java.util.Set;

import rental.RentalCompanyRemote;

public interface ManagerSessionRemote extends SessionRemote {
    public void registerCompany(RentalCompanyRemote company)
            throws RemoteException;

    public void unregisterCompany(String companyName) throws RemoteException;

    public Set<String> getCompanies() throws RemoteException;

    public Set<String> getCarsAt(String companyName) throws RemoteException;

    public int getNbReservations(String companyName, String carType)
            throws RemoteException;

    public int getNbReservationsBy(String client) throws RemoteException;

    public String getMostPopularCompany() throws RemoteException;

    public String getMostPopularCarTypeAt(String companyName)
            throws RemoteException;
}
