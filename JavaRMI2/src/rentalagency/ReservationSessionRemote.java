package rentalagency;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Set;

import rental.Quote;
import rental.Reservation;
import rental.ReservationException;

public interface ReservationSessionRemote extends SessionRemote {
    public Set<String> getAvailableCarTypes(Date start, Date end)
            throws RemoteException;

    public void createQuote(String companyName, Date start, Date end,
            String carType) throws ReservationException, RemoteException;

    public Set<Reservation> confirmQuotes() throws ReservationException,
            RemoteException;

    public Set<Quote> getCurrentQuotes() throws RemoteException;
}
