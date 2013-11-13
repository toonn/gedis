package session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;
import rental.ReservationException;

@Stateless
public class ManagerSession implements ManagerSessionRemote {

    @PersistenceContext
    EntityManager em;

    @Override
    public void addCar(String companyName, Car car) throws ReservationException {
        getRentalCompany(companyName).addCar(car);
        em.persist(car);
    }

    @Override
    public void addCarType(String companyName, CarType type) throws ReservationException {
        getRentalCompany(companyName).addCarType(type);
        em.persist(type);
    }

    @Override
    public CarType getCarType(CarType type) {
        for (CarType ct : getCarTypes(type.getCompanyName())) {
            if (ct.getName().equals(type.getName())) {
                type = ct;
                break;
            }
        }

        return type;
    }

    @Override
    public void addCompany(String companyName, List<Car> cars) {
        CarRentalCompany company = new CarRentalCompany(companyName, cars);
        em.persist(company);
    }

    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            return new HashSet<CarType>(getRentalCompany(company).getAllTypes());
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            for (Car c : getRentalCompany(company).getCars(type)) {
                out.add(c.getId());
            }
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            return getRentalCompany(company).getCar(id).getReservations().size();
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            for (Car c : getRentalCompany(company).getCars(type)) {
                out.addAll(c.getReservations());
            }
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }

    @Override
    public int getNumberOfReservationsBy(String renter) {
        Set<Reservation> out = new HashSet<Reservation>();
        for (CarRentalCompany crc : getRentalCompanies()) {
            out.addAll(crc.getReservationsBy(renter));
        }
        return out.size();
    }

    private List<CarRentalCompany> getRentalCompanies() {
        TypedQuery<CarRentalCompany> query =
                em.createNamedQuery("getAllRentalCompanies", CarRentalCompany.class);
        return query.getResultList();
    }

    private CarRentalCompany getRentalCompany(String companyName) throws rental.ReservationException {
        TypedQuery<CarRentalCompany> query =
                em.createNamedQuery("getCompany", CarRentalCompany.class);
        query.setParameter("companyName", companyName);

        try {
            return query.getSingleResult();
        } catch (Exception e) {
            throw new ReservationException("Company doesn't exist!: " + companyName);
        }
    }

    public String getMostPopularCompany() throws rental.ReservationException{
        TypedQuery<String> query =
                em.createNamedQuery("getMostPopularCompany", String.class);
        
        try {
            return query.getResultList().get(0);
        } catch (Exception e) {
            throw new ReservationException("There are no companies.");
        }
    }
    
    public CarType getMostPopularCarTypeIn(String companyName) throws rental.ReservationException {
        TypedQuery<String> query =
                em.createNamedQuery("getMostPopularCarTypeIn", String.class);
        query.setParameter("companyName", companyName);
        
//        try {
            String mostPopularCarTypeName = query.getResultList().get(0);
            CarType mostPopularCarType = null;
            
            for (CarType ct : getCarTypes(companyName))
                if (ct.getName().equals(mostPopularCarTypeName))
                    mostPopularCarType = ct;
            
            return mostPopularCarType;
//        } catch (Exception e) {
//            throw new ReservationException("Company doesn't exist!: " + companyName);
//        }
    }
}