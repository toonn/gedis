package client;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import rental.CarType;
import rental.Reservation;
import rentalagency.ManagerSessionRemote;
import rentalagency.ReservationSessionRemote;
import rentalagency.SessionCreationServiceRemote;

public class Client extends
        AbstractScriptedTripTest<ReservationSessionRemote, ManagerSessionRemote> {
    protected SessionCreationServiceRemote sessionCreationService;

    public Client(String scriptFile,
            SessionCreationServiceRemote sessionCreationService) {
        super(scriptFile);
        this.sessionCreationService = sessionCreationService;
    }

    @Override
    protected ReservationSessionRemote getNewReservationSession(String name)
            throws Exception {
        return sessionCreationService.getNewReservationSession(name);
    }

    @Override
    protected ManagerSessionRemote getNewManagerSession(String name) throws Exception {
        return sessionCreationService.getNewManagerSession(name);
    }

    @Override
    protected void checkForAvailableCarTypes(ReservationSessionRemote session,
            Date start, Date end) throws Exception {
        session.getAvailableCarTypes(start, end);
    }

    @Override
    protected void addQuoteToSession(ReservationSessionRemote session, Date start,
            Date end, String carType, String carRentalName) throws Exception {
        session.createQuote(carRentalName, start, end, carType);
    }

    @Override
    protected List<Reservation> confirmQuotes(ReservationSessionRemote session)
            throws Exception {
        List<Reservation> reservations = new ArrayList<Reservation>(session
                .confirmQuotes());
        return reservations;
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName)
            throws Exception {
        return ms.getNbReservationsBy(clientName);
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms,
            String carRentalCompanyName, String carType) throws Exception {
        return ms.getNbReservations(carRentalCompanyName, carType);
    }

    @Override
    protected String getMostPopularCarRentalCompany(ManagerSessionRemote ms)
            throws Exception {
        return ms.getMostPopularCompany();
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms,
            String carRentalCompanyName) throws Exception {
        return ms.getMostPopularCarTypeAt(carRentalCompanyName);
    }

    public static void main(String[] args) throws Exception {

        System.setSecurityManager(null);
        String host = "localhost";
        if (args.length > 0)
            host = args[0];
        Registry registry = LocateRegistry.getRegistry(host);
        SessionCreationServiceRemote sessionCreationService = (SessionCreationServiceRemote) registry
                .lookup("SessionCreationService");

        Client client = new Client("../assignment-RMI-23_src/trips", sessionCreationService);
        client.run();
    }

}
