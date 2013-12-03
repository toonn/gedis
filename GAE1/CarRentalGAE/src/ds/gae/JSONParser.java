package ds.gae;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

import ds.gae.entities.Reservation;
import ds.gae.view.ViewTools;

public class JSONParser {

	public static String toJSON(List<Reservation> reservations) throws JSONException {
		JSONObject result = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		for(Reservation r : reservations) {
			Map<String, String> map = new HashMap<>();
			map.put("rentalCompany", r.getRentalCompany());
			map.put("carType", r.getCarType());
			map.put("carId", String.valueOf(r.getCarId()));
			map.put("startDate", ViewTools.DATE_FORMAT.format(r.getStartDate()));
			map.put("endDate", ViewTools.DATE_FORMAT.format(r.getEndDate()));
			map.put("rentalPrice", String.valueOf(r.getRentalPrice()));
			jsonArray.put(new JSONObject(map));
		}
		result.put("reservations", jsonArray);
		return result.toString();
	}
}
