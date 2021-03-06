package ds.gae.entities;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import com.google.appengine.api.datastore.Key;

@NamedQueries({
		@NamedQuery(name = "getCarType", query = "SELECT carType FROM CarType carType "
				+ "WHERE carType.name = :carTypeName "
				+ "AND carType.companyName = :companyName"),
		@NamedQuery(name = "getCarTypeNamesByCompany", query = "SELECT carType.name FROM CarType carType "
				+ "WHERE carType.companyName = :companyName"),
		@NamedQuery(name = "getCarTypeByCompany", query = "SELECT carType FROM CarType carType "
				+ "WHERE carType.companyName = :companyName")})
@Entity
public class CarType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;

	private String name;
	private int nbOfSeats;
	private boolean smokingAllowed;
	private double rentalPricePerDay;
	// trunk space in liters
	private float trunkSpace;

	private String companyName;

	@OneToMany(mappedBy = "type", cascade = {CascadeType.PERSIST,
			CascadeType.REMOVE})
	private Set<Car> cars = new HashSet<>();

	/***************
	 * CONSTRUCTOR *
	 ***************/
	protected CarType() {
	}

	public CarType(String name, int nbOfSeats, float trunkSpace,
			double rentalPricePerDay, boolean smokingAllowed, String companyName) {
		this.name = name;
		this.nbOfSeats = nbOfSeats;
		this.trunkSpace = trunkSpace;
		this.rentalPricePerDay = rentalPricePerDay;
		this.smokingAllowed = smokingAllowed;
		this.companyName = companyName;
	}

	public Key getKey() {
		return key;
	}

	public Set<Car> getCars() {
		return Collections.unmodifiableSet(cars);
	}

	public void addCar(Car car) {
		cars.add(car);
	}

	public void addCars(Collection<Car> cars) {
		cars.addAll(cars);
	}

	public String getName() {
		return name;
	}

	public int getNbOfSeats() {
		return nbOfSeats;
	}

	public boolean isSmokingAllowed() {
		return smokingAllowed;
	}

	public double getRentalPricePerDay() {
		return rentalPricePerDay;
	}

	public float getTrunkSpace() {
		return trunkSpace;
	}

	public String getCompanyName() {
		return companyName;
	}

	/*************
	 * TO STRING *
	 *************/

	@Override
	public String toString() {
		return String
				.format("Car type: %s \t[seats: %d, price: %.2f, smoking: %b, trunk: %.0fl]",
						getName(), getNbOfSeats(), getRentalPricePerDay(),
						isSmokingAllowed(), getTrunkSpace());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CarType other = (CarType) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public boolean isAvailable(Date start, Date end) {
		for (Car car : getCars()) {
			if (car.isAvailable(start, end)) {
				return true;
			}
		}
		return false;
	}
}