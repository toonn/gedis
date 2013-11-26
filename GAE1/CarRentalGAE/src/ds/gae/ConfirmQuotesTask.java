package ds.gae;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.appengine.api.taskqueue.DeferredTask;

import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;

public class ConfirmQuotesTask implements DeferredTask {
	private final List<Quote> quotes;
	private EntityManager em;

	public ConfirmQuotesTask(List<Quote> quotes) {
		this.quotes = quotes;
	}

	private EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

	@Override
	public void run() {
		List<Reservation> reservations = new ArrayList<>();
		try {
			for (Quote q : quotes) {
				em = getEntityManager();
				try {
					TypedQuery<CarRentalCompany> query = em.createNamedQuery(
							"getCompany", CarRentalCompany.class);
					query.setParameter("companyName", q.getRentalCompany());
					CarRentalCompany crc = query.getSingleResult();
					reservations.add(crc.confirmQuote(q));
				} finally {
					em.close();
				}
			}
			reservationsSucceeded(reservations);
		} catch (ReservationException e) {
			for (Reservation res : reservations) {
				em = getEntityManager();
				try {
					TypedQuery<CarRentalCompany> query2 = em.createNamedQuery(
							"getCompany", CarRentalCompany.class);
					query2.setParameter("companyName", res.getRentalCompany());
					CarRentalCompany crc = query2.getSingleResult();
					crc.cancelReservation(res);
				} finally {
					em.close();
				}
			}
			reservationsFailed();
		}
	}

	public void reservationsSucceeded(List<Reservation> reservations) {
		// TODO implement back-channel
	}

	public void reservationsFailed() {
		// TODO implement back-channel
	}

}
