package ds.gae;

import static com.google.appengine.api.taskqueue.TaskOptions.Builder.withPayload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;

import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.entities.ReservationConstraints;

public class CarRentalModel {

	public Map<String, CarRentalCompany> CRCS = new HashMap<String, CarRentalCompany>();

	private static CarRentalModel instance;

	private EntityManager em;

	public static CarRentalModel get() {
		if (instance == null)
			instance = new CarRentalModel();
		return instance;
	}

	private EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

	/**
	 * Get the car types available in the given car rental company.
	 * 
	 * @param crcName
	 *            the car rental company
	 * @return The list of car types (i.e. name of car type), available in the
	 *         given car rental company.
	 */
	public Set<String> getCarTypesNames(String crcName) {
		em = getEntityManager();
		TypedQuery<String> query = em.createNamedQuery(
				"getCarTypeNamesByCompany", String.class);
		query.setParameter("companyName", crcName);
		Set<String> names = new HashSet<String>(query.getResultList());
		em.close();
		return names;
	}

	/**
	 * Get all registered car rental companies
	 * 
	 * @return the list of car rental companies
	 */
	public Collection<String> getAllRentalCompanyNames() {
		em = getEntityManager();
		TypedQuery<String> query = em.createNamedQuery("getCompanyNames",
				String.class);
		Collection<String> names = query.getResultList();
		em.close();
		return names;
	}

	/**
	 * Create a quote according to the given reservation constraints (tentative
	 * reservation).
	 * 
	 * @param company
	 *            name of the car renter company
	 * @param renterName
	 *            name of the car renter
	 * @param constraints
	 *            reservation constraints for the quote
	 * @return The newly created quote.
	 * 
	 * @throws ReservationException
	 *             No car available that fits the given constraints.
	 */
	public Quote createQuote(String company, String renterName,
			ReservationConstraints constraints) throws ReservationException {
		// FIXED: use persistence instead

		em = getEntityManager();
		TypedQuery<CarRentalCompany> query = em.createNamedQuery("getCompany",
				CarRentalCompany.class);
		query.setParameter("companyName", company);
		try {
			CarRentalCompany crc = query.getSingleResult();
			Quote q = crc.createQuote(constraints, renterName);
			return q;
		} catch (NoResultException e) {
			throw new ReservationException("CarRentalCompany not found.");
		} finally {
			em.close();
		}

		/*
		 * CarRentalCompany crc = CRCS.get(company); Quote out = null;
		 * 
		 * if (crc != null) { out = crc.createQuote(constraints, renterName); }
		 * else { throw new ReservationException("CarRentalCompany not found.");
		 * }
		 * 
		 * return out;
		 */
	}

	/**
	 * Confirm the given quote.
	 * 
	 * @param q
	 *            Quote to confirm
	 * 
	 * @throws ReservationException
	 *             Confirmation of given quote failed.
	 */
	public void confirmQuote(Quote q) throws ReservationException {
		// FIXED: use persistence instead

		em = getEntityManager();
		TypedQuery<CarRentalCompany> query = em.createNamedQuery("getCompany",
				CarRentalCompany.class);
		query.setParameter("companyName", q.getRentalCompany());
		try {
			CarRentalCompany crc = query.getSingleResult();
			crc.confirmQuote(q);
		} catch (NoResultException e) {
			throw new ReservationException("CarRentalCompany not found.");
		} finally {
			em.close();
		}
	}

	/**
	 * Confirm the given list of quotes
	 * 
	 * @param quotes
	 *            the quotes to confirm
	 */
	public void confirmQuotes(List<Quote> quotes) {
		Queue queue = QueueFactory.getDefaultQueue();
		queue.add(withPayload(new ConfirmQuotesTask(quotes)));
	}

	/**
	 * Get all reservations made by the given car renter.
	 * 
	 * @param renter
	 *            name of the car renter
	 * @return the list of reservations of the given car renter
	 */
	public List<Reservation> getReservations(String renter) {
		// FIXED: use persistence instead

		em = getEntityManager();
		TypedQuery<Reservation> query = em.createNamedQuery("getReservations",
				Reservation.class);
		query.setParameter("renter", renter);
		List<Reservation> reservations = query.getResultList();
		em.close();
		return reservations;
	}

	/**
	 * Get the car types available in the given car rental company.
	 * 
	 * @param crcName
	 *            the given car rental company
	 * @return The list of car types in the given car rental company.
	 */
	public Collection<CarType> getCarTypesOfCarRentalCompany(String crcName) {
		// FIXED: use persistence instead

		em = getEntityManager();
		TypedQuery<CarType> query = em.createNamedQuery("getCarTypeByCompany",
				CarType.class);
		query.setParameter("companyName", crcName);
		Collection<CarType> carTypes = query.getResultList();
		em.close();
		return carTypes;
	}

	/**
	 * Get the list of cars of the given car type in the given car rental
	 * company.
	 * 
	 * @param crcName
	 *            name of the car rental company
	 * @param carType
	 *            the given car type
	 * @return A list of car IDs of cars with the given car type.
	 */
	public Collection<Integer> getCarIdsByCarType(String crcName,
			CarType carType) {
		Collection<Integer> out = new ArrayList<Integer>();
		for (Car c : getCarsByCarType(crcName, carType)) {
			out.add(c.getId());
		}
		return out;
	}

	/**
	 * Get the amount of cars of the given car type in the given car rental
	 * company.
	 * 
	 * @param crcName
	 *            name of the car rental company
	 * @param carType
	 *            the given car type
	 * @return A number, representing the amount of cars of the given car type.
	 */
	public int getAmountOfCarsByCarType(String crcName, CarType carType) {
		return this.getCarsByCarType(crcName, carType).size();
	}

	/*
	 * Get the list of cars of the given car type in the given car rental
	 * company.
	 * 
	 * @param crcName name of the car rental company
	 * 
	 * @param carType the given car type
	 * 
	 * @return List of cars of the given car type (over all car rental
	 * companies)
	 */
	private List<Car> getCarsByCarType(String crcName, CarType carType) {
		// FIXED: use persistence instead
		em = getEntityManager();
		TypedQuery<Car> query = em.createNamedQuery("getCarsByType", Car.class);
		query.setParameter("type", carType);
		List<Car> cars = query.getResultList();
		em.close();
		return cars;
	}

	/**
	 * Check whether the given car renter has reservations.
	 * 
	 * @param renter
	 *            the car renter
	 * @return True if the number of reservations of the given car renter is
	 *         higher than 0. False otherwise.
	 */
	public boolean hasReservations(String renter) {
		return this.getReservations(renter).size() > 0;
	}
}