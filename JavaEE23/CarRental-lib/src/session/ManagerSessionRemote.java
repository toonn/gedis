package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.Car;
import rental.CarType;
import rental.ReservationException;

@Remote
public interface ManagerSessionRemote {
    
    public void addCar(String companyName, Car car) throws ReservationException;
    
    public void addCarType(String companyName, CarType type) throws ReservationException;
    
    public void addCompany(String companyName, List<Car> cars);
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
      
    public int getNumberOfReservationsBy(String renter);
}