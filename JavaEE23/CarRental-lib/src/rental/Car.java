package rental;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import rental.CarType;
import rental.Reservation;

@Entity
public class Car implements Serializable{

    private int id;
    private CarType type;
    private Set<Reservation> reservations;
    private String companyName;
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String name) {
        companyName = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(CarType type) {
        this.type = type;
    }

    public void setReservations(Set<Reservation> reservations) {
        this.reservations = reservations;
    }

    /***************
     * CONSTRUCTOR *
     ***************/
    protected Car() {}
    
    public Car(int uid, CarType type) {
    	this.id = uid;
        this.type = type;
        this.reservations = new HashSet<Reservation>();
    }

    /******
     * ID *
     ******/
    @Id 
    public int getId() {
    	return id;
    }
    
    /************
     * CAR TYPE *
     ************/
    
    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    public CarType getType() {
        return type;
    }

    /****************
     * RESERVATIONS *
     ****************/

    public boolean isAvailable(Date start, Date end) {
        if(!start.before(end))
            throw new IllegalArgumentException("Illegal given period");

        for(Reservation reservation : reservations) {
            if(reservation.getEndDate().before(start) || reservation.getStartDate().after(end))
                continue;
            return false;
        }
        return true;
    }
    
    public void addReservation(Reservation res) {
        reservations.add(res);
    }
    
    public void removeReservation(Reservation reservation) {
        // equals-method for Reservation is required!
        reservations.remove(reservation);
    }

    @OneToMany(orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "carId")
    public Set<Reservation> getReservations() {
        return reservations;
    }
}