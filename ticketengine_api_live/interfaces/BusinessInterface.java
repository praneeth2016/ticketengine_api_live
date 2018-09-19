package interfaces;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

public interface BusinessInterface {

	public String getCitiesList(@QueryParam("api_key") String key,@Context HttpServletRequest request);
	public String getServicesList(@QueryParam("api_key") String key,@QueryParam("from") int from,@QueryParam("to") int to,@QueryParam("date") String date,@Context HttpServletRequest request);
}
