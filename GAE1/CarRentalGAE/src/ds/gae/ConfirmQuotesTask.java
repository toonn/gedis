package ds.gae;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.appengine.api.channel.ChannelMessage;
import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;
import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.labs.repackaged.org.json.JSONException;

import ds.gae.entities.CarRentalCompany;
import ds.gae.entities.Quote;
import ds.gae.entities.Reservation;
import ds.gae.view.ViewTools;

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
		ChannelService channelService = ChannelServiceFactory
				.getChannelService();
		String channelKey = "xyz";

		String reservationsString = "";
		for (Reservation r : reservations) {
			reservationsString += "<tr><td>" + r.getRentalCompany() + "</td>"
					+ "<td>" + r.getCarType() + "/" + r.getCarId() + "</td>"
					+ "<td>" + ViewTools.DATE_FORMAT.format(r.getStartDate())
					+ " - " + ViewTools.DATE_FORMAT.format(r.getEndDate())
					+ "</td>" + "<td class='numbers'>" + r.getRentalPrice()
					+ " \u20ac</td></tr>";
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String message = reservationsString;
		channelService.sendMessage(new ChannelMessage(channelKey, message));
	}

	public void reservationsFailed() {
		ChannelService channelService = ChannelServiceFactory
				.getChannelService();
		String channelKey = "xyz";
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		channelService.sendMessage(new ChannelMessage(channelKey, "fail"));
	}

}
