package rentalagency;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;

import rental.CarType;
import rental.RentalCompanyRemote;

public class ManagerSession implements ManagerSessionRemote {
    final protected String manager;
    final protected RentalAgency rentalAgency = RentalAgency.getInstance();

    public ManagerSession(String manager) {
        this.manager = manager;
    }

    @Override
    public void registerCompany(RentalCompanyRemote company)
            throws RemoteException {
        rentalAgency.registerCompany(company);
        System.out.println(manager + ": Registering " + company.getName() + ".");
    }

    @Override
    public void unregisterCompany(String companyName) throws RemoteException {
        rentalAgency.unregisterCompany(companyName);
        System.out.println(manager + ": Unregistering " + companyName + ".");
    }

    @Override
    public Set<String> getCompanies() throws RemoteException {
        Set<String> companyNames = new HashSet<String>();
        for (RentalCompanyRemote company : rentalAgency.getAllCompanies()) {
            companyNames.add(company.getName());
        }

        System.out.println(manager + ": Listing all companies.");
        return companyNames;
    }

    @Override
    public Set<String> getCarsAt(String companyName) throws RemoteException {
        System.out.println(manager + ": Listing cars at " + companyName + ".");
        return rentalAgency.getCompany(companyName).getAllCarTypeStrings();
    }

    @Override
    public int getNbReservations(String companyName, String carType)
            throws RemoteException {
        System.out.println(manager + ": Getting nb of reservations for " + carType + " at " + companyName + ".");
        return rentalAgency.getCompany(companyName).getNbReservations(carType);
    }

    @Override
    public int getNbReservationsBy(String client) throws RemoteException {
        int nbReservations = 0;
        for (RentalCompanyRemote company : rentalAgency.getAllCompanies()) {
            nbReservations += company.getNbReservationsBy(client);
        }
        System.out.println(manager + ": Getting nb of reservations by " + client + ".");
        return nbReservations;
    }

    @Override
    public String getMostPopularCompany() throws RemoteException {
        String mostPopularCompany = "";
        int nbReservations = Integer.MIN_VALUE;
        for (RentalCompanyRemote company : rentalAgency.getAllCompanies()) {
            int candidateNbReservations = company.getNbReservations();
            if (candidateNbReservations >= nbReservations) {
                mostPopularCompany = company.getName();
                nbReservations = candidateNbReservations;
            }
        }
        System.out.println(manager + ": Getting most popular company.");
        return mostPopularCompany;
    }

    @Override
    public CarType getMostPopularCarTypeAt(String companyName)
            throws RemoteException {
        System.out.println(manager + ": Getting most popular cartype at " + companyName + ".");
        return rentalAgency.getCompany(companyName).getMostPopularCarType();
    }

}
