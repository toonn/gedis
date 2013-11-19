package ds.gae.listener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import ds.gae.EMF;
import ds.gae.entities.Car;
import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.CarType;

public class CarRentalServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		// This will be invoked as part of a warming request,
		// or the first user request if no warming request was invoked.

		// check if dummy data is available, and add if necessary
		if (!isDummyDataAvailable()) {
			addDummyData();
		}
	}

	private boolean isDummyDataAvailable() {
		// If the Hertz car rental company is in the datastore, we assume the
		// dummy data is available

		// FIXED: use persistence instead
		// return CarRentalModel.get().CRCS.containsKey("Hertz");

		EntityManager em = EMF.get().createEntityManager();
		boolean DDAvailable = em.find(CarRentalCompany.class, "Hertz") != null;
		em.close();
		return DDAvailable;
	}

	private void addDummyData() {
		loadRental("Hertz", "hertz.csv");
		loadRental("Dockx", "dockx.csv");
	}

	private void loadRental(String name, String datafile) {
		Logger.getLogger(CarRentalServletContextListener.class.getName()).log(
				Level.INFO, "loading {0} from file {1}",
				new Object[] { name, datafile });
		try {

			Set<Car> cars = loadData(name, datafile);
			CarRentalCompany company = new CarRentalCompany(name, cars);

			// FIXED: use persistence instead
			// CarRentalModel.get().CRCS.put(name, company);

			EntityManager em = EMF.get().createEntityManager();
			em.persist(company);
			em.close();

		} catch (NumberFormatException ex) {
			Logger.getLogger(CarRentalServletContextListener.class.getName())
					.log(Level.SEVERE, "bad file", ex);
		} catch (IOException ex) {
			Logger.getLogger(CarRentalServletContextListener.class.getName())
					.log(Level.SEVERE, null, ex);
		}
	}

	public static Set<Car> loadData(String name, String datafile)
			throws NumberFormatException, IOException {
		// FIXED: adapt the implementation of this method to your entity
		// structure
		EntityManager em = EMF.get().createEntityManager();

		Set<Car> cars = new HashSet<Car>();
		int carId = 1;

		// open file from jar
		BufferedReader in = new BufferedReader(new InputStreamReader(
				CarRentalServletContextListener.class.getClassLoader()
						.getResourceAsStream(datafile)));
		// while next line exists
		while (in.ready()) {
			// read line
			String line = in.readLine();
			// if comment: skip
			if (line.startsWith("#")) {
				continue;
			}
			// tokenize on ,
			StringTokenizer csvReader = new StringTokenizer(line, ",");
			// create new car type from first 5 fields
			CarType type = new CarType(csvReader.nextToken(),
					Integer.parseInt(csvReader.nextToken()),
					Float.parseFloat(csvReader.nextToken()),
					Double.parseDouble(csvReader.nextToken()),
					Boolean.parseBoolean(csvReader.nextToken()), name);
			TypedQuery<CarType> query = em.createNamedQuery("getCarType",
					CarType.class);
			query.setParameter("carTypeName", type.getName());
			query.setParameter("companyName", name);
			try {
				type = query.getSingleResult();
			} catch (NoResultException e) {

			}
			// create N new cars with given type, where N is the 5th field
			for (int i = Integer.parseInt(csvReader.nextToken()); i > 0; i--) {
				cars.add(new Car(carId++, type));
			}
		}

		em.close();

		return cars;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// App Engine does not currently invoke this method.
	}
}