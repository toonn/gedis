package rental;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import rentalagency.SessionCreationServiceRemote;

public class RentalCompany implements RentalCompanyRemote {

    private static Logger logger = Logger.getLogger(RentalCompany.class
            .getName());

    private String name;
    private List<Car> cars;
    private Map<String, CarType> carTypes = new HashMap<String, CarType>();

    /***************
     * CONSTRUCTOR *
     ***************/

    public RentalCompany(String name, List<Car> cars, String rentalAgencyHost) {
        logger.log(Level.INFO, "<{0}> Car Rental Company {0} starting up...",
                name);
        setName(name);
        this.cars = cars;
        for (Car car : cars)
            carTypes.put(car.getType().getName(), car.getType());

        try {
            String SCSName = "SessionCreationService";
            Registry registry = LocateRegistry.getRegistry(rentalAgencyHost);
            SessionCreationServiceRemote sessionCreationService = (SessionCreationServiceRemote) registry
                    .lookup(SCSName);

            RentalCompanyRemote stub = (RentalCompanyRemote) UnicastRemoteObject
                    .exportObject(this, 0);
            sessionCreationService.getNewManagerSession(name).registerCompany(
                    stub);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /********
     * NAME *
     ********/

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    /*************
     * CAR TYPES *
     *************/

    public Collection<CarType> getAllCarTypes() {
        return carTypes.values();
    }

    public Set<String> getAllCarTypeStrings() {
        Set<String> allCarTypeStrings = new HashSet<String>();
        for (CarType ct : getAllCarTypes())
            allCarTypeStrings.add(ct.getName());
        return allCarTypeStrings;
    }

    public CarType getCarType(String carTypeName) {
        if (carTypes.containsKey(carTypeName))
            return carTypes.get(carTypeName);
        throw new IllegalArgumentException("<" + carTypeName
                + "> No car type of name " + carTypeName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rental.RentalCompanyRemote#isAvailable(java.lang.String,
     * java.util.Date, java.util.Date)
     */
    @Override
    public boolean isAvailable(String carTypeName, Date start, Date end) {
        logger.log(Level.INFO, "<{0}> Checking availability for car type {1}",
                new Object[] { name, carTypeName });
        if (carTypes.containsKey(carTypeName))
            return getAvailableCarTypes(start, end).contains(
                    carTypes.get(carTypeName));
        throw new IllegalArgumentException("<" + carTypeName
                + "> No car type of name " + carTypeName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see rental.RentalCompanyRemote#getAvailableCarTypes(java.util.Date,
     * java.util.Date)
     */
    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<CarType>();
        for (Car car : cars) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    @Override
    public Set<String> getAvailableCarTypeStrings(Date start, Date end) {
        Set<String> availableCarTypes = new HashSet<String>();
        for (Car car : cars) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType().getName());
            }
        }
        return availableCarTypes;
    }

    /*********
     * CARS *
     *********/

    private Car getCar(int uid) {
        for (Car car : cars) {
            if (car.getId() == uid)
                return car;
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid "
                + uid);
    }

    private List<Car> getAvailableCars(String carType, Date start, Date end) {
        List<Car> availableCars = new LinkedList<Car>();
        for (Car car : cars) {
            if (car.getType().getName().equals(carType)
                    && car.isAvailable(start, end)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * rental.RentalCompanyRemote#createQuote(rental.ReservationConstraints,
     * java.lang.String)
     */

    @Override
    public Quote createQuote(ReservationConstraints constraints, String client)
            throws ReservationException {
        logger.log(
                Level.INFO,
                "<{0}> Creating tentative reservation for {1} with constraints {2}",
                new Object[] { name, client, constraints.toString() });

        CarType type = getCarType(constraints.getCarType());

        if (!isAvailable(constraints.getCarType(), constraints.getStartDate(),
                constraints.getEndDate()))
            throw new ReservationException("<" + name
                    + "> No cars available to satisfy the given constraints.");

        double price = calculateRentalPrice(type.getRentalPricePerDay(),
                constraints.getStartDate(), constraints.getEndDate());

        return new Quote(client, constraints.getStartDate(), constraints
                .getEndDate(), getName(), constraints.getCarType(), price);
    }

    // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start,
            Date end) {
        return rentalPricePerDay
                * Math.ceil((end.getTime() - start.getTime())
                        / (1000 * 60 * 60 * 24D));
    }

    /*
     * (non-Javadoc)
     * 
     * @see rental.RentalCompanyRemote#confirmQuote(rental.Quote)
     */
    public Reservation confirmQuote(Quote quote) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[] { name,
                quote.toString() });
        List<Car> availableCars = getAvailableCars(quote.getCarType(), quote
                .getStartDate(), quote.getEndDate());
        if (availableCars.isEmpty())
            throw new ReservationException(
                    "Reservation failed, all cars of type "
                            + quote.getCarType() + " are unavailable from "
                            + quote.getStartDate() + " to "
                            + quote.getEndDate());
        Car car = availableCars
                .get((int) (Math.random() * availableCars.size()));

        Reservation res = new Reservation(quote, car.getId());
        car.addReservation(res);
        return res;
    }

    public void cancelReservation(Reservation res) {
        logger.log(Level.INFO, "<{0}> Cancelling reservation {1}",
                new Object[] { name, res.toString() });
        getCar(res.getCarId()).removeReservation(res);
    }

    @Override
    public Set<Reservation> confirmQuotes(Set<Quote> quotes)
            throws ReservationException, RemoteException {
        for (Quote quote : quotes)
            logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[] {
                    name, quote.toString() });

        Set<Reservation> reservations = new HashSet<Reservation>();

        for (Quote quote : quotes) {
            List<Car> availableCars = getAvailableCars(quote.getCarType(),
                    quote.getStartDate(), quote.getEndDate());
            if (availableCars.isEmpty()) {
                rollbackReservations(reservations);
                throw new ReservationException(
                        "Reservation failed, all cars of type "
                                + quote.getCarType() + " are unavailable from "
                                + quote.getStartDate() + " to "
                                + quote.getEndDate());
            }

            Car car = availableCars.get((int) (Math.random() * availableCars
                    .size()));

            Reservation res = new Reservation(quote, car.getId());
            reservations.add(res);
            car.addReservation(res);
        }
        return reservations;
    }

    @Override
    public void rollbackReservations(Set<Reservation> reservations) {
        for (Reservation res : reservations)
            cancelReservation(res);
    }

    @Override
    public int getNbReservations(String carType) {
        int nbReservations = 0;
        for (Car car : cars) {
            if (car.getType().getName().equals(carType)) {
                nbReservations += car.nbReservations();
            }
        }

        return nbReservations;
    }

    @Override
    public int getNbReservationsBy(String renter) {
        int nbReservations = 0;
        for (Car car : cars) {
            nbReservations += car.nbReservationsBy(renter);
        }
        return nbReservations;
    }

    @Override
    public int getNbReservations() {
        int nbReservations = 0;
        for (String carType : carTypes.keySet())
            nbReservations += getNbReservations(carType);
        return nbReservations;
    }

    @Override
    public CarType getMostPopularCarType() {
        CarType mostPopularCarType = null;
        int nbReservations = Integer.MIN_VALUE;
        for (String carType : carTypes.keySet()) {
            if (getNbReservations(carType) >= nbReservations) {
                mostPopularCarType = carTypes.get(carType);
                nbReservations = getNbReservations(carType);
            }
        }
        return mostPopularCarType;
    }

}