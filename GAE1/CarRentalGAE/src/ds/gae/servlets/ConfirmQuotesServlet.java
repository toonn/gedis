package ds.gae.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.channel.ChannelService;
import com.google.appengine.api.channel.ChannelServiceFactory;

import ds.gae.CarRentalModel;
import ds.gae.entities.Quote;
import ds.gae.view.JSPSite;

@SuppressWarnings("serial")
public class ConfirmQuotesServlet extends HttpServlet {

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession();
		HashMap<String, ArrayList<Quote>> allQuotes = (HashMap<String, ArrayList<Quote>>) session
				.getAttribute("quotes");

		ArrayList<Quote> qs = new ArrayList<Quote>();

		for (String crcName : allQuotes.keySet()) {
			qs.addAll(allQuotes.get(crcName));
		}
		CarRentalModel.get().confirmQuotes(qs);

		session.setAttribute("quotes", new HashMap<String, ArrayList<Quote>>());

		ChannelService channelService = ChannelServiceFactory
				.getChannelService();
		// channelKey moet normaal user-afhankelijk zijn
		String channelKey = "xyz";
		String token = channelService.createChannel(channelKey);
		session.setAttribute("token", token);

		// BufferedReader in = new BufferedReader(new FileReader(
		// JSPSite.CONFIRM_QUOTES_RESPONSE.url().substring(1)));
		//
		// String confirmQuotesResponse = "";
		// String inputLine;
		// while ((inputLine = in.readLine()) != null)
		// confirmQuotesResponse += inputLine;
		// in.close();
		//
		// confirmQuotesResponse.replaceAll("\\{\\{ token \\}\\}", token);
		//
		//resp.setContentType("text/html");
		//resp.getWriter().write("<script>alert('hello world');</script>");
		// resp.getWriter().write("<body><script>channel = new goog.appengine.Channel('"+ token +"');socket = channel.open();socket.onopen = onOpened;socket.onmessage = onMessage;socket.onerror = onError;socket.onclose = onClose;var quotes = '';function onOpened() {alert('channel opened');document.getElementById('response').innerHTML='Your reservations are currently being processed, results will be shown in a moment.';}function onMessage(msg) {quotes += msg;document.getElementById('response').innerHTML=quotes;}function onMessage(err) {alert(err);}function onClose() {}</script><p id='response'>response<p>");

		// If you wish confirmQuotesReply.jsp to be shown to the client as
		// a response of calling this servlet, please replace the following line
		resp.sendRedirect(JSPSite.CONFIRM_QUOTES_RESPONSE.url());
		// resp.sendRedirect(JSPSite.CREATE_QUOTES.url());
	}
}
