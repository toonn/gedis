package rentalagency;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rental.Quote;
import rental.RentalCompanyRemote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

public class ReservationSession implements ReservationSessionRemote {
    final protected String client;
    final protected RentalAgency rentalAgency = RentalAgency.getInstance();
    final protected Map<String, Set<Quote>> quotes = new HashMap<String, Set<Quote>>();

    public ReservationSession(String client) {
        this.client = client;
    }

    @Override
    public Set<String> getAvailableCarTypes(Date start, Date end)
            throws RemoteException {
        Set<String> availableCarTypes = new HashSet<String>();
        for (RentalCompanyRemote company : rentalAgency.getAllCompanies()) {
            availableCarTypes.addAll(company.getAvailableCarTypeStrings(start,
                    end));
        }

        System.out.println(client + ": Checking for available cars.");
        return availableCarTypes;
    }

    @Override
    public void createQuote(String companyName, Date start, Date end,
            String carType) throws ReservationException, RemoteException {
        ReservationConstraints constraints = new ReservationConstraints(start,
                end, carType);
        Quote quote = rentalAgency.getCompany(companyName).createQuote(
                constraints, client);

        if (quotes.get(companyName) != null)
            quotes.get(companyName).add(quote);

        System.out.println(client + ": Create quote for " + carType + " at "
                + companyName + ".");
    }

    @Override
    public Set<Reservation> confirmQuotes() throws ReservationException,
            RemoteException {
        Map<String, Set<Reservation>> companyReservations = new HashMap<String, Set<Reservation>>();

        try {
            for (String companyName : quotes.keySet()) {
                companyReservations.put(companyName, rentalAgency.getCompany(
                        companyName).confirmQuotes(quotes.get(companyName)));
            }
        } catch (ReservationException e) {
            for (String companyName : companyReservations.keySet()) {
                rentalAgency.getCompany(companyName).rollbackReservations(
                        companyReservations.get(companyName));
            }
            throw e;
        }

        Set<Reservation> reservations = new HashSet<Reservation>();
        for (Set<Reservation> companyReservationz : companyReservations
                .values()) {
            reservations.addAll(companyReservationz);
        }

        System.out.println(client + ": Confirming all quotes.");
        return reservations;
    }

    @Override
    public Set<Quote> getCurrentQuotes() throws RemoteException {
        Set<Quote> allQuotes = new HashSet<Quote>();
        for (Set<Quote> companyQuotes : quotes.values())
            allQuotes.addAll(companyQuotes);

        System.out.println(client + ": Getting all quotes.");
        return Collections.unmodifiableSet(allQuotes);
    }

}
