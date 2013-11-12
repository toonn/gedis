package session;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;
import java.util.Map;
import java.util.HashMap;
import javax.persistence.TypedQuery;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();

    @Override
    public Set<String> getAllRentalCompanies() {
        Query query = em.createNamedQuery("getAllRentalCompanyNames");
        Set<String> companySet = new HashSet<String>(query.getResultList());
        return companySet;
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        
        List<CarType> availableCarTypes = new LinkedList<CarType>();
        for(String crc : getAllRentalCompanies()) {
            for(CarType ct : getRentals().get(crc).getAvailableCarTypes(start, end)) {
                if(!availableCarTypes.contains(ct))
                    availableCarTypes.add(ct);
            }
        }
        return availableCarTypes;
    }
    
    private Map<String, CarRentalCompany> getRentals(){
        Map<String, CarRentalCompany> rentals = new HashMap<String, CarRentalCompany>();
        TypedQuery<CarRentalCompany> query = 
                em.createNamedQuery("getAllRentalCompanies", CarRentalCompany.class);
        List<CarRentalCompany> companies = query.getResultList();
        for (CarRentalCompany crc : companies)
            rentals.put(crc.getName(), crc);
        return rentals;
    }
    
    private CarRentalCompany getRentalCompany(String companyName) throws ReservationException{
        TypedQuery<CarRentalCompany> query = 
                em.createNamedQuery("getCompany", CarRentalCompany.class);
        query.setParameter("companyName", companyName);
        try {
            return query.getSingleResult();
        } catch(Exception e) {
            throw new ReservationException("Company doesn't exist!: " + companyName);
        }
    }

    @Override
    public Quote createQuote(String company, ReservationConstraints constraints) throws ReservationException {
        Quote out = getRentalCompany(company).createQuote(constraints, renter);
        quotes.add(out);
        return out;
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
        try {
            for (Quote quote : quotes) {
                done.add(getRentalCompany(quote.getRentalCompany()).confirmQuote(quote));
            }
        } catch (ReservationException e) {
            for(Reservation r:done)
                getRentalCompany(r.getRentalCompany()).cancelReservation(r);
            throw e;
        }
        return done;
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }
}