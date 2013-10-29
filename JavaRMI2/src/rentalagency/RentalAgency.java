package rentalagency;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rental.RentalCompanyRemote;

public class RentalAgency {
    protected static RentalAgency instance = null;
    protected final Map<String, RentalCompanyRemote> companies = new HashMap<String, RentalCompanyRemote>();

    protected RentalAgency() {
    }

    public static RentalAgency getInstance() {
        if (instance == null) {
            instance = new RentalAgency();
        }

        return instance;
    }

    public RentalCompanyRemote getCompany(String companyName) {
        return companies.get(companyName);
    }

    public Set<RentalCompanyRemote> getAllCompanies() {
        return new HashSet<RentalCompanyRemote>(companies.values());
    }

    public void registerCompany(RentalCompanyRemote company)
            throws RemoteException {
        companies.put(company.getName(), company);
    }

    public void unregisterCompany(String name) {
        companies.remove(name);
    }
}
