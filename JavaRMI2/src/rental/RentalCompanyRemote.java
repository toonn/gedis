package rental;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Set;

public interface RentalCompanyRemote extends Remote {

    public abstract boolean isAvailable(String carTypeName, Date start, Date end)
            throws RemoteException;

    public abstract Set<String> getAllCarTypeStrings() throws RemoteException;

    public abstract Set<String> getAvailableCarTypeStrings(Date start, Date end)
            throws RemoteException;

    /****************
     * RESERVATIONS *
     ****************/

    public abstract Quote createQuote(ReservationConstraints constraints,
            String client) throws ReservationException, RemoteException;

    public abstract Set<Reservation> confirmQuotes(Set<Quote> quotes)
            throws ReservationException, RemoteException;

    public abstract void rollbackReservations(Set<Reservation> reservations)
            throws RemoteException;

    public abstract String getName() throws RemoteException;

    public abstract int getNbReservations(String carType)
            throws RemoteException;

    public abstract int getNbReservationsBy(String renter)
            throws RemoteException;

    public abstract int getNbReservations() throws RemoteException;

    public abstract CarType getMostPopularCarType() throws RemoteException;

}