package session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;
import rental.ReservationException;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    @PersistenceContext
    EntityManager em;
    
    public void addCar(String companyName, Car car) throws ReservationException{
        getRentalCompany(companyName).addCar(car);
    }
    
    public void addCarType(String companyName, CarType type) throws ReservationException{
        getRentalCompany(companyName).addCarType(type);
    }
    
    public void addCompany(String companyName) {
        em.persist(new CarRentalCompany(companyName, null));
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
            for(Car c: getRentalCompany(company).getCars(type)){
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
            for(Car c: getRentalCompany(company).getCars(type)){
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
        for(CarRentalCompany crc : getRentalCompanies()) {
            out.addAll(crc.getReservationsBy(renter));
        }
        return out.size();
    }
    
       private List<CarRentalCompany> getRentalCompanies(){
        
        Query query = em.createNamedQuery("getAllRentalCompanies");
        List<CarRentalCompany> companies = query.getResultList();
        
        return companies;
    }
    
    private CarRentalCompany getRentalCompany(String companyName) throws rental.ReservationException{
        Query query = em.createNamedQuery("getCompany");
        query.setParameter("companyName", companyName);
        
        List<CarRentalCompany> company = query.getResultList();
        if (company.isEmpty()) {
            throw new ReservationException("Company doesn't exist!: " + companyName);
        }
        
        return company.get(0);
    }
}