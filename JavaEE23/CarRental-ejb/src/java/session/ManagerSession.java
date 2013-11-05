package session;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationException;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            return new HashSet<CarType>(RentalStore.getRental(company).getAllTypes());
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCars(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.add(c.getId());
            }
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public Set<Reservation> getReservations(String company, String type, int id) {
        try {
            return RentalStore.getRental(company).getCar(id).getReservations();
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Reservation> getReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.addAll(c.getReservations());
            }
        } catch (ReservationException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public Set<Reservation> getReservationsBy(String renter) {
        Set<Reservation> out = new HashSet<Reservation>();
        for(CarRentalCompany crc : RentalStore.getRentals().values()) {
            out.addAll(crc.getReservationsBy(renter));
        }
        return out;
    }
}