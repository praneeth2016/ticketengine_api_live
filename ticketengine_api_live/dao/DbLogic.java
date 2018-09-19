package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import javax.servlet.http.HttpServletRequest;

import dto.AgentBalance;
import dto.BalanceResponse;
import dto.BoardingPointParams;
import dto.BoardingPoints;
import dto.CancelTicketResponse;
import dto.CancellationDetails;
import dto.CancellationTerms;
import dto.CancellationTermsParams;
import dto.Cities;
import dto.CitiesPair;
import dto.City;
import dto.CityPair;
import dto.CoachLayout;
import dto.CoachLayoutAgent;
import dto.Destination;
import dto.GetCitiesPairRequest;
import dto.GetCitiesRequest;
import dto.GetSeatingArrangement;
import dto.GetSeatingArrangementAgent;
import dto.GetServicesRequest;
import dto.IsTicketCancellableResponse;
import dto.LayoutDetails;
import dto.LayoutDetailsAgent;
import dto.Origin;
import dto.ResponseCodes;
import dto.Seat;
import dto.SeatAgent;
import dto.SeatBlockingRequest;
import dto.SeatBlockingResponse;
import dto.SeatBookingRequest;
import dto.SeatBookingResponse;
import dto.SeatDetails;
import dto.SeatDetailsAgent;
import dto.SendPNR;
import dto.ServiceDetails;
import dto.TicketDetails;

public class DbLogic {
	static Logger log = Logger.getLogger(DbLogic.class.getName());

	/**
	 * 
	 * This method authenticates the client. returns true if client passes valid
	 * key from registered IP
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return true or false.
	 */
	private boolean authenticate(String key, String ip) {
		// System.out.println(key+ip);
		// System.out.println(DbLogic.class.getName());
		int i = 0;
		
		int a = 0;
		// int isip = 0;
		DbConnection database = new DbConnection();
		Connection connection = null;
		try {
			connection = database.Get_Connection();
			PreparedStatement ps1 = connection
					.prepareStatement("SELECT distinct isip FROM agents_operator where agent_type=? and status=? and api_key=? ");
			int l = 1;
			int m = 3;
			ps1.setInt(1, m);
			ps1.setInt(2, l);
			// ps.setString(3, ip);
			ps1.setString(3, key);

			ResultSet rs1 = ps1.executeQuery();
			if (rs1.next()) {
				a = rs1.getInt("isip");
			}
			//System.out.println("ip===="+ip+"     aaaa"+a);
			// if(key.equalsIgnoreCase("qLv3lxq2sE*WAK9G") ||
			// key.equalsIgnoreCase("2aGEMl6eJDQrIw20")||
			// key.equalsIgnoreCase("RIkI93d1CIxVYd!g") ||
			// key.equalsIgnoreCase("eWjtE9YUdSkF818m") ||
			// key.equalsIgnoreCase("DTmnsbVEhjjanHNh"))
			if (a == 1) {
				// key.equalsIgnoreCase("gB!klbD1@8zGwYep") ||
				// key.equalsIgnoreCase("Q5ikxpO6F7TANZU0")
				// key.equalsIgnoreCase("qLv3lxq2sE*WAK9G") ||
				// key.equalsIgnoreCase("2aGEMl6eJDQrIw20")||
				// key.equalsIgnoreCase("RIkI93d1CIxVYd!g") ||
				// key.equalsIgnoreCase("eWjtE9YUdSkF818m") ||
				// key.equalsIgnoreCase("DTmnsbVEhjjanHNh")

				PreparedStatement ps = connection
						.prepareStatement("SELECT distinct id FROM agents_operator where agent_type=? and status=? and api_key=? ");
				int k = 1;
				int j = 3;
				ps.setInt(1, j);
				ps.setInt(2, k);
				// ps.setString(3, ip);
				ps.setString(3, key);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					i = rs.getInt("id");
				}

				if (i > 0)
					return true;
				else
					return false;
			} else {
				PreparedStatement ps = connection
						.prepareStatement("SELECT distinct id FROM agents_operator where agent_type=? and status=? and ip=?  and api_key=? ");
				int k = 1;
				int j = 3;
				ps.setInt(1, j);
				ps.setInt(2, k);
				ps.setString(3, ip);
				ps.setString(4, key);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					i = rs.getInt("id");
				}

				if (i > 0)
					return true;
				else
					return false;
			}
		} catch (SQLException e) {
			// log.error("Error in dao-authenticate() from"+e.getMessage()+"#ip:"+ip+"and key"+key);
			System.out.println("Error Code" + e.getErrorCode());
			System.out.println("Error Message" + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// log.error("Error in dao-authenticate() from"+e.getMessage()+"#ip:"+ip+"and key"+key);
			System.out.println("Error Message" + e.getMessage());
			e.printStackTrace();
		}finally{
			try {
				connection.close();
				System.out.println("conn closed in finally block in authenticate()");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Exception in conn closed in finally block in authenticate()");
			}
			
		}
		return false;

	}// authenticate()

	/**
	 * This method sends cities list to the client.
	 * 
	 * @author Mahendar
	 * @version 1.2
	 * @since 2014 April
	 * @return CitiesPair Obj.
	 * @throws SQLException
	 */

	public CitiesPair getCitiesPair(Connection connection, String key,
			HttpServletRequest request) throws SQLException {
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// logging information storing into logs_api table
		String m = "getSourceDestinationPairs()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		//int res = psl.executeUpdate();

		// log.info("getCitiesPair() Called from ip:"+ip+" api:"+key);
		ResponseCodes response = new ResponseCodes();
		CitiesPair citiespair = new CitiesPair();
		GetCitiesPairRequest feedObject = null;
		ArrayList<GetCitiesPairRequest> feedData = new ArrayList<GetCitiesPairRequest>();
		// validating the credentials
		if (key == null) {
			response.setCode(401);
			response.setMessage("Invalid API KEY or Blank!");
			citiespair.setResponseCodes(response);
			return citiespair;
		}
		boolean valid = this.authenticate(key, ip);
		// valid request
		if (valid) {
			try {
				/* Getting operator id and api type */
				PreparedStatement ps3 = connection
						.prepareStatement("SELECT operator_id,api_type FROM agents_operator where agent_type=? and status=? and api_key=? ");
				ps3.setInt(1, 3);
				ps3.setInt(2, 1);
				// ps3.setString(3, ip);
				ps3.setString(3, key);
				ResultSet rs3 = ps3.executeQuery();
				int operatorId = 0;
				String apiType = null;
				while (rs3.next()) {
					operatorId = rs3.getInt("operator_id");
					apiType = rs3.getString("api_type");
				}
				/* TicketEngin Client */
				if (apiType.equalsIgnoreCase("te") || apiType == null) {
					PreparedStatement ps = connection
							.prepareStatement("SELECT distinct from_id,from_name,to_id,to_name FROM master_buses");
					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						feedObject = new GetCitiesPairRequest();
						CityPair cityObj = new CityPair();
						cityObj.setSourcename(rs.getString("from_name"));
						cityObj.setSourceid(rs.getInt("from_id"));
						cityObj.setDestname(rs.getString("to_name"));
						cityObj.setDestid(rs.getInt("to_id"));

						feedObject.setCitypair(cityObj);
						feedData.add(feedObject);
					}
					citiespair.setCitiespair(feedData);
				} else if (apiType.equalsIgnoreCase("op")) {
					PreparedStatement ps = connection
							.prepareStatement("SELECT distinct from_id,from_name,to_id,to_name FROM master_buses where travel_id=?");
					ps.setInt(1, operatorId);
					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						feedObject = new GetCitiesPairRequest();
						CityPair cityObj = new CityPair();
						cityObj.setSourcename(rs.getString("from_name"));
						cityObj.setSourceid(rs.getInt("from_id"));
						cityObj.setDestname(rs.getString("to_name"));
						cityObj.setDestid(rs.getInt("to_id"));

						feedObject.setCitypair(cityObj);
						feedData.add(feedObject);
					}
					citiespair.setCitiespair(feedData);

				}
				return citiespair;
			} catch (SQLException e) {
				// log.error("SQLException in dao-getCitiesPair() from"+e.getMessage()+"#ip:"+ip+" and key "+key);
				System.out.println("Error Code" + e.getErrorCode());
				System.out.println("Error Message" + e.getMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error");
				citiespair.setResponseCodes(response);
				return citiespair;
				// throw e;
			} catch (Exception e) {
				// log.error("SQLException in dao-getCitiesPair() from"+e.getMessage()+"#ip:"+ip+" and key "+key);
				response.setCode(500);
				response.setMessage("Internal Server Error");
				citiespair.setResponseCodes(response);
				return citiespair;
			}
		}// if
		else {
			// log.error("SQLException in dao-getCitiesPair() from #ip:"+ip+" and key "+key);
			response.setCode(401);
			response.setMessage("Authentication failed");
			citiespair.setResponseCodes(response);
			return citiespair;
		}// close else

	}

	/**
	 * This method sends cities list to the client.
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return Cities Obj.
	 */
	public Cities getCities(Connection connection, String key,
			HttpServletRequest request) throws Exception {
		/*
		 * String ipAdd=request.getRemoteAddr(); InetAddress ip1; ip1 =
		 * InetAddress.getLocalHost(); String ip = ip1.getHostAddress();
		 */
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// logging information storing into logs_api table
		String m = "getCities()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		//int res = psl.executeUpdate();

		// log.info("getCities() Called from ip:"+ip+" api:"+key);
		ResponseCodes response = new ResponseCodes();
		Cities cities = new Cities();
		GetCitiesRequest feedObject = null;
		ArrayList<GetCitiesRequest> feedData = new ArrayList<GetCitiesRequest>();
		// validating the credentials
		if (key == null) {
			response.setCode(401);
			response.setMessage("Invalid API KEY or Blank!");
			cities.setResponseCodes(response);
			return cities;
		}
		boolean valid = this.authenticate(key, ip);
		// valid request
		if (valid) {
			try {
				/* Getting operator id and api type */
				PreparedStatement ps3 = connection
						.prepareStatement("SELECT operator_id,api_type FROM agents_operator where agent_type=? and status=? and api_key=? ");
				ps3.setInt(1, 3);
				ps3.setInt(2, 1);
				// ps3.setString(3, ip);
				ps3.setString(3, key);
				ResultSet rs3 = ps3.executeQuery();
				int operatorId = 0;
				String apiType = null;
				while (rs3.next()) {
					operatorId = rs3.getInt("operator_id");
					apiType = rs3.getString("api_type");
				}
				/* TicketEngin Client */
				if (apiType.equalsIgnoreCase("te") || apiType == null) {
					PreparedStatement ps = connection
							.prepareStatement("select distinct from_id as city_id,from_name as city_name from master_buses union select distinct to_id as city_id,to_name as city_name from master_buses");
					ResultSet rs = ps.executeQuery();

					while (rs.next()) {
						feedObject = new GetCitiesRequest();
						City cityObj = new City();
						cityObj.setCityid(rs.getInt("city_id"));
						cityObj.setCityname(rs.getString("city_name"));
						feedObject.setCity(cityObj);
						feedData.add(feedObject);
					}
					cities.setCities(feedData);
				} else if (apiType.equalsIgnoreCase("op")) {
					PreparedStatement ps = connection
							.prepareStatement("select * from (select distinct from_id as city_id,from_name as city_name from master_buses where travel_id=? union select distinct to_id as city_id,to_name as city_name from master_buses where travel_id=?) as t");
					ps.setInt(1, operatorId);
					ps.setInt(2, operatorId);
					ResultSet rs = ps.executeQuery();
					while (rs.next()) {
						feedObject = new GetCitiesRequest();
						City cityObj = new City();
						cityObj.setCityid(rs.getInt("city_id"));
						cityObj.setCityname(rs.getString("city_name"));
						feedObject.setCity(cityObj);
						feedData.add(feedObject);
					}
					cities.setCities(feedData);

				}
				return cities;
			} catch (SQLException e) {
				// log.error("SQLException in dao-getCities() from"+e.getMessage()+"#ip:"+ip+" and key "+key);
				System.out.println("Error Code" + e.getErrorCode());
				System.out.println("Error Message" + e.getMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error");
				cities.setResponseCodes(response);
				return cities;
				// throw e;
			} catch (Exception e) {
				// log.error("SQLException in dao-getCities() from"+e.getMessage()+"#ip:"+ip+" and key "+key);
				response.setCode(500);
				response.setMessage("Internal Server Error");
				cities.setResponseCodes(response);
				return cities;
			}
		}// close if
		else// invalid credentials
		{
			// log.error("SQLException in dao-getCities() from #ip:"+ip+" and key "+key);
			response.setCode(401);
			response.setMessage("Authentication failed");
			cities.setResponseCodes(response);
			return cities;
		}// close else
	}// close getCities(---)

	/**
	 * This method sends the services list for a particular date and route.
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return ServiceDetails Obj.
	 */
	public ServiceDetails getServices(Connection connection, String key,
			int from, int to, String date, HttpServletRequest request)
			throws Exception {
		/*
		 * String ipAdd=request.getRemoteAddr(); InetAddress ip1; ip1 =
		 * InetAddress.getLocalHost(); String ip = ip1.getHostAddress();
		 */
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// logging information storing into logs_api table
		String m = "getServices()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		//int res = psl.executeUpdate();

		// log.info("getServices() Called from ip:"+ip+" api:"+key);
		String ph = null;
		GetServicesRequest feedObject = null;
		BoardingPoints bpObj = null;
		BoardingPointParams bp = null;
		CancellationTerms canc_terms = null;
		CancellationTermsParams cancObj = null;
		String status = null;
		ServiceDetails sd = new ServiceDetails();
		ResponseCodes response = new ResponseCodes();
		ArrayList<GetServicesRequest> feedData = new ArrayList<GetServicesRequest>();
		boolean valid = this.authenticate(key, ip);
		String seat_fare = null;
		String lberth_fare = null;
		String uberth_fare = null;
		String seat_fare1 = null;
		String lberth_fare1 = null;
		String uberth_fare1 = null;
		String seat_fare_changed = null;
		String lberth_fare_changed = null;
		String uberth_fare_changed = null;
		String fare2 = null;
		String fare4 = null;
		String fare6 = null;
		String currency = null;

		if (valid)// valid call
		{
			try {
				/* Getting operator id and api type */
				PreparedStatement ps5 = connection
						.prepareStatement("SELECT operator_id,api_type FROM agents_operator where agent_type=? and status=? and api_key=? ");
				ps5.setInt(1, 3);
				ps5.setInt(2, 1);
				// ps5.setString(3, ip);
				ps5.setString(3, key);
				ResultSet rs5 = ps5.executeQuery();
				int operatorId = 0;
				String apiType = null;
				while (rs5.next()) {
					operatorId = rs5.getInt("operator_id");
					apiType = rs5.getString("api_type");
				}
				// checking the origin and destination id are same or not
				if (from == to) {
					response.setCode(413);
					response.setMessage("The origin and destination should not be same");
					sd.setResponseCodes(response);
					return sd;
				}
				/*
				 * if(from==0 || to==0) { response.setCode(423);
				 * response.setMessage("Invalid Origin or Destination");
				 * sd.setResponseCodes(response); return sd; }
				 */
				if (date == null || from == 0 || to == 0) {
					response.setCode(400);
					response.setMessage("Invalid Parameter Name or Parameter value is NULL");
					sd.setResponseCodes(response);
					return sd;
				}
				// convering input date string type to date format
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date input_date;
				// checking the input date format
				try {
					formatter.applyPattern("yyyy-MM-dd");
					formatter.setLenient(false);
					input_date = formatter.parse(date);
				} catch (ParseException e) {
					response.setCode(422);
					response.setMessage("Invalid Date or please check the date format yyyy-mm-dd");
					sd.setResponseCodes(response);
					return sd;
				}
				// current date
				Date cdate = new Date();
				String tdate = formatter.format(cdate);
				Date today_date = formatter.parse(tdate);
				// if input date is lesser than current date
				if (input_date.compareTo(today_date) < 0) {
					response.setCode(415);
					response.setMessage("Date should be Equal or Greater than "
							+ tdate);
					sd.setResponseCodes(response);
					return sd;
				}
				PreparedStatement ps = null;
				if (apiType.equalsIgnoreCase("te") || apiType == null) {// ticket
																		// engine
																		// client
					ps = connection
							.prepareStatement("SELECT t1.service_num,t1.from_id,t1.to_id,t1.travel_id,t1.journey_date,t1.seat_fare,t1.lberth_fare,t1.uberth_fare,t1.available_seats,t1.lowerdeck_nos,t1.upperdeck_nos,t2.from_name,t2.to_name,t2.start_time,t2.journey_time,t2.arr_time,t2.model,t2.bus_type,t2.seat_nos,t2.title,t2.currency FROM buses_list t1,master_buses t2 where t1.from_id=? and t1.to_id=? and t1.journey_date=? and t2.from_id=? and t2.to_id=? and t1.service_num=t2.service_num and t1.status=1 and t2.status=1 ");
					// SELECT
					// t1.service_num,t1.from_id,t1.to_id,t1.travel_id,t1.seat_fare,t1.lberth_fare,t1.uberth_fare,t1.available_seats,t1.lowerdeck_nos,t1.upperdeck_nos,t2.from_name,t2.to_name,t2.start_time,t2.journey_time,t2.model,t2.bus_type
					// FROM buses_list t1,master_buses t2 WHERE t1.from_id=402
					// AND t1.to_id=1020 AND t1.journey_date='2013-09-16' AND
					// t2.from_id=402 AND t2.to_id=1020
					ps.setInt(1, from);
					ps.setInt(2, to);
					ps.setString(3, date);
					ps.setInt(4, from);
					ps.setInt(5, to);

				} else if (apiType.equalsIgnoreCase("op")) {// operator specific
															// client
					ps = connection
							.prepareStatement("SELECT t1.service_num,t1.from_id,t1.to_id,t1.travel_id,t1.journey_date,t1.seat_fare,t1.lberth_fare,t1.uberth_fare,t1.available_seats,t1.lowerdeck_nos,t1.upperdeck_nos,t2.from_name,t2.to_name,t2.start_time,t2.journey_time,t2.arr_time,t2.model,t2.bus_type,t2.seat_nos,t2.title,t2.currency FROM buses_list t1,master_buses t2 where t1.from_id=? and t1.to_id=? and t1.journey_date=? and t2.from_id=? and t2.to_id=? and t1.service_num=t2.service_num and t1.status=1 and t2.status=1 and t1.travel_id=? and t2.travel_id=?");
					// SELECT
					// t1.service_num,t1.from_id,t1.to_id,t1.travel_id,t1.seat_fare,t1.lberth_fare,t1.uberth_fare,t1.available_seats,t1.lowerdeck_nos,t1.upperdeck_nos,t2.from_name,t2.to_name,t2.start_time,t2.journey_time,t2.model,t2.bus_type
					// FROM buses_list t1,master_buses t2 WHERE t1.from_id=402
					// AND t1.to_id=1020 AND t1.journey_date='2013-09-16' AND
					// t2.from_id=402 AND t2.to_id=1020
					ps.setInt(1, from);
					ps.setInt(2, to);
					ps.setString(3, date);
					ps.setInt(4, from);
					ps.setInt(5, to);
					ps.setInt(6, operatorId);
					ps.setInt(7, operatorId);
				}
				// System.out.println("ps"+ps.toString());
				ResultSet rs = ps.executeQuery();
				if (!rs.isBeforeFirst()) {
					response.setCode(402);
					response.setMessage("No Services!");
					sd.setResponseCodes(response);
					return sd;
				}

				// query for get the operator name and cancellation terms
				int availableSeats = 0;
				int tot_seats = 0;
				
				PreparedStatement ps2 = connection
						.prepareStatement("SELECT operator_title,canc_terms,other_contact,convenience_charge from registered_operators where travel_id=? ");
				// query for getting the boarding points
				PreparedStatement ps3 = connection
						.prepareStatement("SELECT * from boarding_points where service_num=? and travel_id=? and city_id=? and board_or_drop_type = ?"
								+ " union SELECT * from boarding_points where service_num=? and travel_id=? and city_id=?  and board_or_drop_type =?");
				// PreparedStatement
				// ps3=connection.prepareStatement("SELECT * from boarding_points where service_num=? and travel_id=? and city_id=?");
				List<Integer> cityOrderids = null;
				String cityOrder = null;
				while (rs.next()) {
					int travlId = rs.getInt("travel_id");
					feedObject = new GetServicesRequest();
					String service_num = rs.getString("service_num");
					System.out.println(service_num);
					String jdate = rs.getString("journey_date");
					String title = rs.getString("title");
					cityOrderids = getOrderIds(connection, from, to, travlId,service_num);
					cityOrder = cityOrderids.get(0)+"-"+cityOrderids.get(1);
					PreparedStatement pstmt = connection
							.prepareStatement("select count(*) from layout_list where service_num=? and journey_date=? and ((seat_status=? OR available_stages LIKE ?) OR (seat_status=? and (blocked_stages NOT LIKE ? OR blocked_stages IS NULL) ) )and (available=? OR available=?)"
									+ "and seat_name<>?  ");
					pstmt.setString(1, service_num);
					pstmt.setString(2, jdate);
					pstmt.setInt(3, 0);
					pstmt.setString(4, "%"+cityOrder+"%");
					pstmt.setInt(5, 2);
					pstmt.setString(6, "%"+cityOrder+"%");
					pstmt.setInt(7, 0);
					pstmt.setInt(8, 3);
					pstmt.setString(9, "GY");
					
					ResultSet rss = pstmt.executeQuery();
					while (rss.next()) {
						availableSeats = rss.getInt(1);
					}

					PreparedStatement pstmtl = connection
							.prepareStatement("select count(*) from layout_list where service_num=? and journey_date=? and seat_name<>? ");
					pstmtl.setString(1, service_num);
					pstmtl.setString(2, jdate);
					pstmtl.setString(3, "GY");

					ResultSet rssl = pstmtl.executeQuery();
					while (rssl.next()) {
						tot_seats = rssl.getInt(1);
					}

					int travel_id = rs.getInt("travel_id");
					String bus_type = rs.getString("bus_type");
					currency = rs.getString("currency");
					// System.out.println("bus_type"+bus_type);
					// int s_nos=rs.getInt("seat_nos");
					// int lb_nos=rs.getInt("lowerdeck_nos");
					// int ub_nos=rs.getInt("upperdeck_nos");
					// int available_seats=s_nos+lb_nos+ub_nos;

					// getting updated fare from master_price
					PreparedStatement ps4 = connection
							.prepareStatement("select * from master_price where service_num=? and travel_id=? and journey_date=? and from_id=? and to_id=?");
					ps4.setString(1, service_num);
					ps4.setInt(2, travel_id);
					ps4.setString(3, jdate);
					ps4.setInt(4, from);
					ps4.setInt(5, to);

					ResultSet rs4 = ps4.executeQuery();

					if (rs4.next()) {
						seat_fare = rs4.getString("seat_fare");
						lberth_fare = rs4.getString("lberth_fare");
						uberth_fare = rs4.getString("uberth_fare");
						seat_fare_changed = rs4.getString("seat_fare_changed");
						lberth_fare_changed = rs4
								.getString("lberth_fare_changed");
						uberth_fare_changed = rs4
								.getString("uberth_fare_changed");
						// System.out.println("fare"+seat_fare+"@"+lberth_fare+"@"+uberth_fare+"@"+seat_fare_changed+"@"+lberth_fare_changed+"@"+uberth_fare_changed);
					} else {
						PreparedStatement ps6 = connection
								.prepareStatement("select * from master_price where service_num=? and travel_id=? and from_id=? and to_id=? and journey_date IS NULL");
						ps6.setString(1, service_num);
						ps6.setInt(2, travel_id);
						ps6.setInt(3, from);
						ps6.setInt(4, to);

						ResultSet rs6 = ps6.executeQuery();

						if (rs6.next()) {
							seat_fare = rs6.getString("seat_fare");
							lberth_fare = rs6.getString("lberth_fare");
							uberth_fare = rs6.getString("uberth_fare");
							seat_fare_changed = rs6
									.getString("seat_fare_changed");
							lberth_fare_changed = rs6
									.getString("lberth_fare_changed");
							uberth_fare_changed = rs6
									.getString("uberth_fare_changed");
							// System.out.println("fare1"+seat_fare+"@"+lberth_fare+"@"+uberth_fare+"@"+seat_fare_changed+"@"+lberth_fare_changed+"@"+uberth_fare_changed);
						}
					}

					if (seat_fare_changed != null
							&& !seat_fare_changed.isEmpty()) {
						String[] changedSeatFare1 = seat_fare_changed
								.split("@");
						int n = changedSeatFare1.length;
						String[] changedfare = new String[n];
						for (int i = 0; i < changedSeatFare1.length; i++) {
							String[] changedSeatFare2 = changedSeatFare1[i]
									.split("#");

							changedfare[i] = changedSeatFare2[1];
						}
						HashSet<String> set_fare = new HashSet<String>();
						for (int k = 0; k < changedfare.length; k++) {
							set_fare.add(changedfare[k]);
						}
						String[] set_fare1 = set_fare
								.toArray(new String[set_fare.size()]);

						String s_fare = Arrays.toString(set_fare1);
						fare2 = seat_fare + "/"
								+ s_fare.substring(1, s_fare.length() - 1);
					} else {
						fare2 = seat_fare;
					}

					if (lberth_fare_changed != null
							&& !lberth_fare_changed.isEmpty()) {
						String[] changedSeatFare1 = lberth_fare_changed
								.split("@");
						int n = changedSeatFare1.length;
						String[] changedfare = new String[n];
						for (int i = 0; i < changedSeatFare1.length; i++) {
							String[] changedSeatFare2 = changedSeatFare1[i]
									.split("#");

							changedfare[i] = changedSeatFare2[1];
						}
						HashSet<String> set_fare = new HashSet<String>();
						for (int k = 0; k < changedfare.length; k++) {
							set_fare.add(changedfare[k]);
						}
						String[] set_fare1 = set_fare
								.toArray(new String[set_fare.size()]);

						String s_fare = Arrays.toString(set_fare1);
						fare4 = lberth_fare + "/"
								+ s_fare.substring(1, s_fare.length() - 1);
					} else {
						fare4 = lberth_fare;
						// System.out.println("fina44_fare"+fare2+"/"+fare4+"/"+fare6);
					}

					if (uberth_fare_changed != null
							&& !uberth_fare_changed.isEmpty()) {
						String[] changedSeatFare1 = uberth_fare_changed
								.split("@");
						int n = changedSeatFare1.length;
						String[] changedfare = new String[n];
						for (int i = 0; i < changedSeatFare1.length; i++) {
							String[] changedSeatFare2 = changedSeatFare1[i]
									.split("#");

							changedfare[i] = changedSeatFare2[1];
						}
						HashSet<String> set_fare = new HashSet<String>();
						for (int k = 0; k < changedfare.length; k++) {
							set_fare.add(changedfare[k]);
						}
						String[] set_fare1 = set_fare
								.toArray(new String[set_fare.size()]);

						String s_fare = Arrays.toString(set_fare1);
						fare6 = uberth_fare + "/"
								+ s_fare.substring(1, s_fare.length() - 1);
					} else {
						fare6 = uberth_fare;
						// System.out.println("fina1_fare"+fare2+"/"+fare4+"/"+fare6);
					}

					// System.out.println("fare2"+fare2+"@"+fare4+"@"+fare6);

					if (bus_type.equalsIgnoreCase("seater")) {
						seat_fare1 = fare2;
						if (seat_fare1.contains("/")) {
							String[] seat_fare2 = seat_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < seat_fare2.length; k++) {
								set_fare.add(seat_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							seat_fare = s_fare
									.substring(1, s_fare.length() - 1);
						} else {
							seat_fare = fare2;
						}
					} else if (bus_type.equalsIgnoreCase("sleeper")) {

						lberth_fare1 = fare4;
						// System.out.println("lberth_fare1"+lberth_fare1);
						if (lberth_fare1.contains("/")) {

							String[] lberth_fare2 = lberth_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < lberth_fare2.length; k++) {
								set_fare.add(lberth_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							// System.out.println("ls_fare"+s_fare);
							lberth_fare = s_fare.substring(1,
									s_fare.length() - 1);
						} else {
							lberth_fare = fare4;
						}

						uberth_fare1 = fare6;
						// System.out.println("uberth_fare1"+uberth_fare1);
						if (uberth_fare1.contains("/")) {
							// System.out.println("uif");
							String[] uberth_fare2 = uberth_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < uberth_fare2.length; k++) {
								set_fare.add(uberth_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							// System.out.println("us_fare"+s_fare);
							uberth_fare = s_fare.substring(1,
									s_fare.length() - 1);
						} else {
							uberth_fare = fare6;
						}
					} else if (bus_type.equalsIgnoreCase("seatersleeper")) {
						seat_fare1 = fare2;
						if (seat_fare1.contains("/")) {
							String[] seat_fare2 = seat_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < seat_fare2.length; k++) {
								set_fare.add(seat_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							seat_fare = s_fare
									.substring(1, s_fare.length() - 1);
						} else {
							seat_fare = fare2;
						}

						lberth_fare1 = fare4;
						if (lberth_fare1.contains("/")) {
							String[] lberth_fare2 = lberth_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < lberth_fare2.length; k++) {
								set_fare.add(lberth_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							lberth_fare = s_fare.substring(1,
									s_fare.length() - 1);
						} else {
							lberth_fare = fare4;
						}

						uberth_fare1 = fare6;
						if (uberth_fare1.contains("/")) {
							String[] uberth_fare2 = uberth_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < uberth_fare2.length; k++) {
								set_fare.add(uberth_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							uberth_fare = s_fare.substring(1,
									s_fare.length() - 1);
						} else {
							uberth_fare = fare6;
						}
					}
					if (seat_fare == null)
						seat_fare = String.valueOf(0);
					if (lberth_fare == null)
						lberth_fare = String.valueOf(0);
					if (uberth_fare == null)
						uberth_fare = String.valueOf(0);
					// System.out.println("final_fare"+seat_fare+"/"+lberth_fare+"/"+uberth_fare);

					if (availableSeats == 0) {
						status = "Soldout";
					} else {
						status = "Available";
					}
					// System.out.println("availableSeatssa"+availableSeats);
					// System.out.println("status"+status);
					ps2.setInt(1, travel_id);
					ResultSet rs2 = ps2.executeQuery();
					if (rs2.next()) {

						// getting canc.terms from registered_operators
						// 00#03#100@03#12#50@12#24#10
						if (title != null && !"".equals(title)) {
							feedObject.setTravel_name(rs2
									.getString("operator_title") + "-" + title);
							// System.out.println(title+"fh");
						} else {
							feedObject.setTravel_name(rs2
									.getString("operator_title"));
							// System.out.println(title+"fhdsg");
						}

						String canc = null;
						PreparedStatement mt = connection
								.prepareStatement("select distinct canc_terms from master_terms where service_num=? and travel_id=? and terms_date=?");
						mt.setString(1, service_num);
						mt.setInt(2, travel_id);
						mt.setString(3, jdate);

						ResultSet mtrs = mt.executeQuery();

						if (mtrs.next()) {
							canc = mtrs.getString("canc_terms");
						} else {
							PreparedStatement mt1 = connection
									.prepareStatement("select distinct canc_terms from master_terms where service_num=? and travel_id=? and terms_date IS NULL");
							mt1.setString(1, service_num);
							mt1.setInt(2, travel_id);

							// System.out.println("Before : " + mt1.toString());
							ResultSet mtrs1 = mt1.executeQuery();

							if (mtrs1.next()) {
								canc = mtrs1.getString("canc_terms");
							} else {
								canc = rs2.getString("canc_terms");
							}
						}
						// getting canc.terms from registered_operators
						// 00#03#100@03#12#50@12#24#10

						ph = rs2.getString("other_contact");
						if (canc.equalsIgnoreCase("no")) {
							String[] canc_array1 = canc.split("@");
							ArrayList<CancellationTerms> listcan = new ArrayList<CancellationTerms>();
							for (int k = 0; k < canc_array1.length; k++) {
								cancObj = new CancellationTermsParams();
								canc_terms = new CancellationTerms();
								cancObj.setFrom_time("NO");
								cancObj.setTo_time("NO");
								cancObj.setCharges("There is no cancellation for this service on "
										+ jdate);
								canc_terms.setCancellation_policy(cancObj);
								listcan.add(canc_terms);
							}
							feedObject.setCanc_terms(listcan);
						} else {
							String[] canc_array1 = canc.split("@");
							ArrayList<CancellationTerms> listcan = new ArrayList<CancellationTerms>();
							for (int k = 0; k < canc_array1.length; k++) {
								cancObj = new CancellationTermsParams();
								canc_terms = new CancellationTerms();
								String[] canc_array2 = canc_array1[k]
										.split("#");
								cancObj.setFrom_time(canc_array2[0]);
								cancObj.setTo_time(canc_array2[1]);
								cancObj.setCharges(canc_array2[2]);
								canc_terms.setCancellation_policy(cancObj);
								listcan.add(canc_terms);
							}// for
							feedObject.setCanc_terms(listcan);
							// feedObject.setCanc_terms(canc_terms);
						}
					}// close if

					// code for getting the boarding points
					ps3.setString(1, service_num);
					ps3.setInt(2, travel_id);
					ps3.setInt(3, from);
					ps3.setString(4, "board");
					ps3.setString(5, service_num);
					ps3.setInt(6, travel_id);
					ps3.setInt(7, to);
					ps3.setString(8, "drop");
					// System.out.println("service_num"+service_num);
					ResultSet rs3 = ps3.executeQuery();
					ArrayList<BoardingPoints> list = new ArrayList<BoardingPoints>();
					while (rs3.next()) {
						bpObj = new BoardingPoints();
						bp = new BoardingPointParams();

						bp.setBpid(rs3.getString("bpdp_id"));
						// System.out.println(rs3.getString("bpdp_id"));
						bp.setCity_name(rs3.getString("city_name"));
						bp.setCity_id(rs3.getInt("city_id"));
						bp.setVan_pickup(rs3.getString("is_van"));
						bp.setType(rs3.getString("board_or_drop_type"));
						String bpdet = rs3.getString("board_drop");
						String[] bpdetarray = bpdet.split("#");
						bp.setPickup_point(bpdetarray[0]);
						if (ph != null)
							bp.setLandmark(bpdetarray[2].trim() + "," + ph);
						else
							bp.setLandmark(bpdetarray[2].trim());
						bp.setTime(bpdetarray[1].trim());
						bpObj.setBoarding_point(bp);
						list.add(bpObj);

					}

					// converting bus starting time from 24 hours format to 12
					// hours format
					String _24HourTime = rs.getString("start_time");
					SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
					SimpleDateFormat _12HourSDF = new SimpleDateFormat(
							"hh:mm a");
					Date _24HourDt = _24HourSDF.parse(_24HourTime);
					String startTime = _12HourSDF.format(_24HourDt);

					feedObject.setBoarding_points(list);
					feedObject.setTravel_id(travel_id);
					feedObject.setService_number(service_num);
					feedObject.setStatus(status);
					feedObject.setFrom_id(rs.getInt("from_id"));
					feedObject.setFrom_name(rs.getString("from_name"));
					feedObject.setTo_id(rs.getInt("to_id"));
					feedObject.setTo_name(rs.getString("to_name"));
					feedObject.setDep_time(startTime);
					feedObject.setJourney_time(rs.getString("journey_time"));
					feedObject.setArrival_time(rs.getString("arr_time"));
					feedObject.setBus_type(rs.getString("bus_type"));
					feedObject.setBus_model(rs.getString("model"));
					feedObject.setTotal_seats(tot_seats);
					// feedObject.setLowerdeck_total_seats(rs.getInt("t2.lowerdeck_nos"));
					// feedObject.setUpperdeck_total_seats(rs.getInt("t2.upperdeck_nos"));
					feedObject.setJourney_date(jdate);
					feedObject.setAvailable_seats(availableSeats);
					// feedObject.setLowerdeck_available_seats(rs.getInt("lowerdeck_nos"));
					// feedObject.setUpperdeck_available_seats(rs.getInt("upperdeck_nos"));
					feedObject.setSeat_fare(seat_fare);
					feedObject.setLb_fare(lberth_fare);
					feedObject.setUb_fare(uberth_fare);
					feedObject.setCurrency(currency);
					feedObject.setconvenience_charge(rs2.getString("convenience_charge"));
					feedData.add(feedObject);
				}
				sd.setService_details(feedData);
				return sd;
			} catch (SQLException ie) {
				// log.error("Exception in dao-getServices() from"+ie.getMessage()+"#ip:"+ip+" and key"+key);
				System.out.println("Error Code" + ie.getErrorCode());
				System.out.println("Error Message" + ie.getMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error");
				sd.setResponseCodes(response);
				return sd;
			} catch (Exception e) {
				// log.error("Exception in dao-getServices() from"+e.getMessage()+"#ip:"+ip+" and key"+key);
				System.out.println("Error Message" + e.getMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error!");
				sd.setResponseCodes(response);
				// e.printStackTrace();
				return sd;
			}
		}// close if
		else {
			// log.info("Authentication Failed For dao-getServices() from ip:"+ip+" and api:"+key);
			response.setCode(401);
			response.setMessage("Authentication failed");
			sd.setResponseCodes(response);
			return sd;
		}// close else
	}// close getServices(---)

	/**
	 * This method send the All Available Services .
	 * 
	 * @author Mahendar
	 * @version 1.1
	 * @since 2014 Dec
	 * @return Service Details.
	 */
	public ServiceDetails getServicesList(Connection connection, String key,
			HttpServletRequest request) throws Exception {
		/*
		 * String ipAdd=request.getRemoteAddr(); InetAddress ip1; ip1 =
		 * InetAddress.getLocalHost(); String ip = ip1.getHostAddress();
		 */

		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// logging information storing into logs_api table
		String m = "getServiceDetailsList()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		//int res = psl.executeUpdate();

		// log.info("getServices() Called from ip:"+ip+" api:"+key);
		String ph = null;
		GetServicesRequest feedObject = null;
		BoardingPoints bpObj = null;
		BoardingPointParams bp = null;
		CancellationTerms canc_terms = null;
		CancellationTermsParams cancObj = null;
		String status = null;
		String seat_fare = null;
		String lberth_fare = null;
		String uberth_fare = null;
		String seat_fare1 = null;
		String lberth_fare1 = null;
		String uberth_fare1 = null;
		String seat_fare_changed = null;
		String lberth_fare_changed = null;
		String uberth_fare_changed = null;
		String fare2 = null;
		String fare4 = null;
		String fare6 = null;
		ServiceDetails sd = new ServiceDetails();
		ResponseCodes response = new ResponseCodes();
		ArrayList<GetServicesRequest> feedData = new ArrayList<GetServicesRequest>();
		boolean valid = this.authenticate(key, ip);
		if (valid)// valid call
		{
			try {
				/* Getting operator id and api type */
				PreparedStatement ps5 = connection
						.prepareStatement("SELECT operator_id,api_type FROM agents_operator where agent_type=? and status=? and api_key=? ");
				ps5.setInt(1, 3);
				ps5.setInt(2, 1);
				// ps5.setString(3, ip);
				ps5.setString(3, key);
				ResultSet rs5 = ps5.executeQuery();
				int operatorId = 0;
				String apiType = null;
				while (rs5.next()) {
					operatorId = rs5.getInt("operator_id");
					apiType = rs5.getString("api_type");
				}
				// checking the origin and destination id are same or not
				/*
				 * if(from==to) { response.setCode(413); response.setMessage(
				 * "The origin and destination should not be same");
				 * sd.setResponseCodes(response); return sd; } if(from==0 ||
				 * to==0) { response.setCode(423);
				 * response.setMessage("Invalid Origin or Destination");
				 * sd.setResponseCodes(response); return sd; } if(date==null ||
				 * from==0 || to==0) { response.setCode(400);
				 * response.setMessage
				 * ("Invalid Parameter Name or Parameter value is NULL");
				 * sd.setResponseCodes(response); return sd; }
				 */
				// convering input date string type to date format
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date input_date;
				// checking the input date format
				/*
				 * try{ formatter.applyPattern("yyyy-MM-dd");
				 * formatter.setLenient(false); input_date =
				 * formatter.parse(date); }catch (ParseException e) {
				 * response.setCode(422); response.setMessage(
				 * "Invalid Date or please check the date format yyyy-mm-dd");
				 * sd.setResponseCodes(response); return sd; }
				 */
				// current date
				Date cdate = new Date();
				String tdate = formatter.format(cdate);

				Calendar c = Calendar.getInstance();
				c.setTime(formatter.parse(tdate));
				c.add(Calendar.DATE, 9); // number of days to add
				String end_dt = formatter.format(c.getTime());

				// Date today_date=formatter.parse(tdate);
				// if input date is lesser than current date
				/*
				 * if(input_date.compareTo(today_date)<0){
				 * response.setCode(415);
				 * response.setMessage("Date should be Equal or Greater than "
				 * +tdate); sd.setResponseCodes(response); return sd; }
				 */
				PreparedStatement ps = null;
				if (apiType.equalsIgnoreCase("te") || apiType == null) {// ticket
																		// engine
																		// client
					ps = connection
							.prepareStatement("SELECT t1.service_num,t1.from_id,t1.to_id,t1.travel_id,t1.journey_date,t1.seat_fare,t1.lberth_fare,t1.uberth_fare,t1.available_seats,t1.lowerdeck_nos,t1.upperdeck_nos,t2.from_name,t2.to_name,t2.start_time,t2.journey_time,t2.arr_time,t2.model,t2.bus_type,t2.seat_nos,t2.title FROM buses_list t1,master_buses t2 where t1.service_num=t2.service_num and  t1.from_id=t2.from_id and t1.to_id=t2.to_id and t1.status=1 and t2.status=1 and t1.status=t2.status and journey_date between ? and ?");
					// SELECT
					// t1.service_num,t1.from_id,t1.to_id,t1.travel_id,t1.seat_fare,t1.lberth_fare,t1.uberth_fare,t1.available_seats,t1.lowerdeck_nos,t1.upperdeck_nos,t2.from_name,t2.to_name,t2.start_time,t2.journey_time,t2.model,t2.bus_type
					// FROM buses_list t1,master_buses t2 WHERE t1.from_id=402
					// AND t1.to_id=1020 AND t1.journey_date='2013-09-16' AND
					// t2.from_id=402 AND t2.to_id=1020
					ps.setString(1, tdate);
					ps.setString(2, end_dt);
				} else if (apiType.equalsIgnoreCase("op")) {// operator specific
															// client
					ps = connection
							.prepareStatement("SELECT t1.service_num,t1.from_id,t1.to_id,t1.travel_id,t1.journey_date,t1.seat_fare,t1.lberth_fare,t1.uberth_fare,t1.available_seats,t1.lowerdeck_nos,t1.upperdeck_nos,t2.from_name,t2.to_name,t2.start_time,t2.journey_time,t2.arr_time,t2.model,t2.bus_type,t2.seat_nos,t2.title FROM buses_list t1,master_buses t2 where t1.service_num=t2.service_num and t1.from_id=t2.from_id and t1.to_id=t2.to_id and t1.status=1  and t2.status=1 and t1.travel_id=? and t2.travel_id=? and journey_date between ? and ?");
					// SELECT
					// t1.service_num,t1.from_id,t1.to_id,t1.travel_id,t1.seat_fare,t1.lberth_fare,t1.uberth_fare,t1.available_seats,t1.lowerdeck_nos,t1.upperdeck_nos,t2.from_name,t2.to_name,t2.start_time,t2.journey_time,t2.model,t2.bus_type
					// FROM buses_list t1,master_buses t2 WHERE t1.from_id=402
					// AND t1.to_id=1020 AND t1.journey_date='2013-09-16' AND
					// t2.from_id=402 AND t2.to_id=1020
					ps.setInt(1, operatorId);
					ps.setInt(2, operatorId);
					ps.setString(3, tdate);
					ps.setString(4, end_dt);

				}
				// System.out.println("data"+ps.toString());
				ResultSet rs = ps.executeQuery();
				if (!rs.isBeforeFirst()) {
					response.setCode(402);
					response.setMessage("No Services!");
					sd.setResponseCodes(response);
					return sd;
				}

				// query for get the operator name and cancellation terms
				int availableSeats = 0;
				int tot_seats = 0;
				PreparedStatement ps2 = connection
						.prepareStatement("SELECT operator_title,canc_terms,other_contact,convenience_charge from registered_operators where travel_id=? ");
				// query for getting the boarding points
				PreparedStatement ps3 = connection
						.prepareStatement("SELECT * from boarding_points where service_num=? and travel_id=? and city_id=? and board_or_drop_type=? "
								+ " union SELECT * from boarding_points where service_num=? and travel_id=? and city_id=? and board_or_drop_type=? ");

				while (rs.next()) {
					feedObject = new GetServicesRequest();
					String service_num = rs.getString("service_num");
					int from_id = rs.getInt("from_id");
					int to_id = rs.getInt("to_id");
					String jdate = rs.getString("journey_date");
					String title = rs.getString("title");
					PreparedStatement pstmt = connection
							.prepareStatement("select count(*) from layout_list where service_num=? and journey_date=? and seat_status=?  and (available=? OR available=?)");
					pstmt.setString(1, service_num);
					pstmt.setString(2, jdate);
					pstmt.setInt(3, 0);
					pstmt.setInt(4, 0);
					pstmt.setInt(5, 3);

					ResultSet rss = pstmt.executeQuery();
					while (rss.next()) {
						availableSeats = rss.getInt(1);
					}

					PreparedStatement pstmtl = connection
							.prepareStatement("select count(*) from layout_list where service_num=? and journey_date=? ");
					pstmtl.setString(1, service_num);
					pstmtl.setString(2, jdate);

					ResultSet rssl = pstmtl.executeQuery();
					while (rssl.next()) {
						tot_seats = rssl.getInt(1);
					}

					int travel_id = rs.getInt("travel_id");
					String bus_type = rs.getString("bus_type");
					// int s_nos=rs.getInt("seat_nos");
					// int lb_nos=rs.getInt("lowerdeck_nos");
					// int ub_nos=rs.getInt("upperdeck_nos");
					// int available_seats=s_nos+lb_nos+ub_nos;

					// getting updated fare from master_price
					PreparedStatement ps4 = connection
							.prepareStatement("select * from master_price where service_num=? and travel_id=? and journey_date=? and from_id=? and to_id=?");
					ps4.setString(1, service_num);
					ps4.setInt(2, travel_id);
					ps4.setString(3, jdate);
					ps4.setInt(4, from_id);
					ps4.setInt(5, to_id);

					ResultSet rs4 = ps4.executeQuery();

					if (rs4.next()) {
						seat_fare = rs4.getString("seat_fare");
						lberth_fare = rs4.getString("lberth_fare");
						uberth_fare = rs4.getString("uberth_fare");
						seat_fare_changed = rs4.getString("seat_fare_changed");
						lberth_fare_changed = rs4
								.getString("lberth_fare_changed");
						uberth_fare_changed = rs4
								.getString("uberth_fare_changed");
						// System.out.println("fare"+seat_fare+"@"+lberth_fare+"@"+uberth_fare+"@"+seat_fare_changed+"@"+lberth_fare_changed+"@"+uberth_fare_changed);
					} else {
						PreparedStatement ps6 = connection
								.prepareStatement("select * from master_price where service_num=? and travel_id=? and from_id=? and to_id=? and journey_date IS NULL");
						ps6.setString(1, service_num);
						ps6.setInt(2, travel_id);
						ps6.setInt(3, from_id);
						ps6.setInt(4, to_id);

						ResultSet rs6 = ps6.executeQuery();

						if (rs6.next()) {
							seat_fare = rs6.getString("seat_fare");
							lberth_fare = rs6.getString("lberth_fare");
							uberth_fare = rs6.getString("uberth_fare");
							seat_fare_changed = rs6
									.getString("seat_fare_changed");
							lberth_fare_changed = rs6
									.getString("lberth_fare_changed");
							uberth_fare_changed = rs6
									.getString("uberth_fare_changed");
							// System.out.println("fare1"+seat_fare+"@"+lberth_fare+"@"+uberth_fare+"@"+seat_fare_changed+"@"+lberth_fare_changed+"@"+uberth_fare_changed);
						}
					}

					if (seat_fare_changed != null
							&& !seat_fare_changed.isEmpty()) {
						String[] changedSeatFare1 = seat_fare_changed
								.split("@");
						int n = changedSeatFare1.length;
						String[] changedfare = new String[n];
						for (int i = 0; i < changedSeatFare1.length; i++) {
							String[] changedSeatFare2 = changedSeatFare1[i]
									.split("#");

							changedfare[i] = changedSeatFare2[1];
						}
						HashSet<String> set_fare = new HashSet<String>();
						for (int k = 0; k < changedfare.length; k++) {
							set_fare.add(changedfare[k]);
						}
						String[] set_fare1 = set_fare
								.toArray(new String[set_fare.size()]);

						String s_fare = Arrays.toString(set_fare1);
						fare2 = seat_fare + "/"
								+ s_fare.substring(1, s_fare.length() - 1);
					} else {
						fare2 = seat_fare;
					}

					if (lberth_fare_changed != null
							&& !lberth_fare_changed.isEmpty()) {
						String[] changedSeatFare1 = lberth_fare_changed
								.split("@");
						int n = changedSeatFare1.length;
						String[] changedfare = new String[n];
						for (int i = 0; i < changedSeatFare1.length; i++) {
							String[] changedSeatFare2 = changedSeatFare1[i]
									.split("#");

							changedfare[i] = changedSeatFare2[1];
						}
						HashSet<String> set_fare = new HashSet<String>();
						for (int k = 0; k < changedfare.length; k++) {
							set_fare.add(changedfare[k]);
						}
						String[] set_fare1 = set_fare
								.toArray(new String[set_fare.size()]);

						String s_fare = Arrays.toString(set_fare1);
						fare4 = lberth_fare + "/"
								+ s_fare.substring(1, s_fare.length() - 1);
					} else {
						fare4 = lberth_fare;
						// System.out.println("fina44_fare"+fare2+"/"+fare4+"/"+fare6);
					}

					if (uberth_fare_changed != null
							&& !uberth_fare_changed.isEmpty()) {
						String[] changedSeatFare1 = uberth_fare_changed
								.split("@");
						int n = changedSeatFare1.length;
						String[] changedfare = new String[n];
						for (int i = 0; i < changedSeatFare1.length; i++) {
							String[] changedSeatFare2 = changedSeatFare1[i]
									.split("#");

							changedfare[i] = changedSeatFare2[1];
						}
						HashSet<String> set_fare = new HashSet<String>();
						for (int k = 0; k < changedfare.length; k++) {
							set_fare.add(changedfare[k]);
						}
						String[] set_fare1 = set_fare
								.toArray(new String[set_fare.size()]);

						String s_fare = Arrays.toString(set_fare1);
						fare6 = uberth_fare + "/"
								+ s_fare.substring(1, s_fare.length() - 1);
					} else {
						fare6 = uberth_fare;
						// System.out.println("fina1_fare"+fare2+"/"+fare4+"/"+fare6);
					}

					// System.out.println("fare2"+fare2+"@"+fare4+"@"+fare6);

					if (bus_type.equalsIgnoreCase("seater")) {
						seat_fare1 = fare2;
						if (seat_fare1.contains("/")) {
							String[] seat_fare2 = seat_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < seat_fare2.length; k++) {
								set_fare.add(seat_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							seat_fare = s_fare
									.substring(1, s_fare.length() - 1);
						} else {
							seat_fare = fare2;
						}
					} else if (bus_type.equalsIgnoreCase("sleeper")) {

						lberth_fare1 = fare4;
						// System.out.println("lberth_fare1"+lberth_fare1);
						if (lberth_fare1.contains("/")) {
							// System.out.println("lif");
							String[] lberth_fare2 = lberth_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < lberth_fare2.length; k++) {
								set_fare.add(lberth_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							// System.out.println("ls_fare"+s_fare);
							lberth_fare = s_fare.substring(1,
									s_fare.length() - 1);
						} else {
							lberth_fare = fare4;
						}

						uberth_fare1 = fare6;
						// System.out.println("uberth_fare1"+uberth_fare1);
						if (uberth_fare1.contains("/")) {

							String[] uberth_fare2 = uberth_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < uberth_fare2.length; k++) {
								set_fare.add(uberth_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							// System.out.println("us_fare"+s_fare);
							uberth_fare = s_fare.substring(1,
									s_fare.length() - 1);
						} else {
							uberth_fare = fare6;
						}
					} else if (bus_type.equalsIgnoreCase("seatersleeper")) {
						seat_fare1 = fare2;
						if (seat_fare1.contains("/")) {
							String[] seat_fare2 = seat_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < seat_fare2.length; k++) {
								set_fare.add(seat_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							seat_fare = s_fare
									.substring(1, s_fare.length() - 1);
						} else {
							seat_fare = fare2;
						}

						lberth_fare1 = fare4;
						if (lberth_fare1.contains("/")) {
							String[] lberth_fare2 = lberth_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < lberth_fare2.length; k++) {
								set_fare.add(lberth_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							lberth_fare = s_fare.substring(1,
									s_fare.length() - 1);
						} else {
							lberth_fare = fare4;
						}

						uberth_fare1 = fare6;
						if (uberth_fare1.contains("/")) {
							String[] uberth_fare2 = uberth_fare1.split("/");
							HashSet<String> set_fare = new HashSet<String>();
							for (int k = 0; k < uberth_fare2.length; k++) {
								set_fare.add(uberth_fare2[k]);
							}
							String[] set_fare3 = set_fare
									.toArray(new String[set_fare.size()]);

							String s_fare = Arrays.toString(set_fare3);
							uberth_fare = s_fare.substring(1,
									s_fare.length() - 1);
						} else {
							uberth_fare = fare6;
						}
					}

					if (seat_fare == null)
						seat_fare = String.valueOf(0);
					if (lberth_fare == null)
						lberth_fare = String.valueOf(0);
					if (uberth_fare == null)
						uberth_fare = String.valueOf(0);
					// System.out.println("final_fare"+seat_fare+"/"+lberth_fare+"/"+uberth_fare);

					if (availableSeats == 0) {
						status = "Soldout";
					} else {
						status = "Available";
					}

					ps2.setInt(1, travel_id);
					ResultSet rs2 = ps2.executeQuery();

					if (rs2.next()) {

						if (title != null) {
							feedObject.setTravel_name(rs2
									.getString("operator_title") + "-" + title);
						} else {
							feedObject.setTravel_name(rs2
									.getString("operator_title"));
						}

						String canc = null;
						PreparedStatement mt = connection
								.prepareStatement("select distinct canc_terms from master_terms where service_num=? and travel_id=? and terms_date=?");
						mt.setString(1, service_num);
						mt.setInt(2, travel_id);
						mt.setString(3, jdate);

						ResultSet mtrs = mt.executeQuery();

						if (mtrs.next()) {
							canc = mtrs.getString("canc_terms");
						} else {
							PreparedStatement mt1 = connection
									.prepareStatement("select distinct canc_terms from master_terms where service_num=? and travel_id=? and terms_date IS NULL");
							mt1.setString(1, service_num);
							mt1.setInt(2, travel_id);

							// System.out.println("Before : " + mt1.toString());
							ResultSet mtrs1 = mt1.executeQuery();

							if (mtrs1.next()) {
								canc = mtrs1.getString("canc_terms");
							} else {
								canc = rs2.getString("canc_terms");
							}
						}
						// getting canc.terms from registered_operators
						// 00#03#100@03#12#50@12#24#10

						ph = rs2.getString("other_contact");
						if (canc.equalsIgnoreCase("no")) {
							String[] canc_array1 = canc.split("@");
							ArrayList<CancellationTerms> listcan = new ArrayList<CancellationTerms>();
							for (int k = 0; k < canc_array1.length; k++) {
								cancObj = new CancellationTermsParams();
								canc_terms = new CancellationTerms();
								cancObj.setFrom_time("NO");
								cancObj.setTo_time("NO");
								cancObj.setCharges("There is no cancellation for this service on "
										+ jdate);
								canc_terms.setCancellation_policy(cancObj);
								listcan.add(canc_terms);
							}
							feedObject.setCanc_terms(listcan);
						} else {
							String[] canc_array1 = canc.split("@");
							ArrayList<CancellationTerms> listcan = new ArrayList<CancellationTerms>();
							for (int k = 0; k < canc_array1.length; k++) {
								cancObj = new CancellationTermsParams();
								canc_terms = new CancellationTerms();
								String[] canc_array2 = canc_array1[k]
										.split("#");
								cancObj.setFrom_time(canc_array2[0]);
								cancObj.setTo_time(canc_array2[1]);
								cancObj.setCharges(canc_array2[2]);
								canc_terms.setCancellation_policy(cancObj);
								listcan.add(canc_terms);
							}// for
							feedObject.setCanc_terms(listcan);
							// feedObject.setCanc_terms(canc_terms);
						}
					}// close if

					// code for getting the boarding points
					ps3.setString(1, service_num);
					ps3.setInt(2, travel_id);
					ps3.setInt(3, from_id);
					ps3.setString(4, "board");
					ps3.setString(5, service_num);
					ps3.setInt(6, travel_id);
					ps3.setInt(7, to_id);
					ps3.setString(8, "drop");
					ResultSet rs3 = ps3.executeQuery();
					ArrayList<BoardingPoints> list = new ArrayList<BoardingPoints>();
					while (rs3.next()) {
						bpObj = new BoardingPoints();
						bp = new BoardingPointParams();
						bp.setBpid(rs3.getString("bpdp_id"));
						// System.out.println(rs3.getString("bpdp_id"));
						bp.setCity_name(rs3.getString("city_name"));
						bp.setCity_id(rs3.getInt("city_id"));
						bp.setVan_pickup(rs3.getString("is_van"));
						bp.setType(rs3.getString("board_or_drop_type"));
						String bpdet = rs3.getString("board_drop");
						String[] bpdetarray = bpdet.split("#");
						if (ph != null)
							bp.setPickup_point(bpdetarray[0] + "," + ph);
						else
							bp.setPickup_point(bpdetarray[0]);
						bp.setLandmark(bpdetarray[2]);
						bp.setTime(bpdetarray[1]);
						bpObj.setBoarding_point(bp);
						list.add(bpObj);
						// System.out.println(bpdetarray[2]+"/"+bpdetarray[1]+"/"+bpdetarray[0]);
					}
					// converting bus starting time from 24 hours format to 12
					// hours format
					String _24HourTime = rs.getString("start_time");
					SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
					SimpleDateFormat _12HourSDF = new SimpleDateFormat(
							"hh:mm a");
					Date _24HourDt = _24HourSDF.parse(_24HourTime);
					String startTime = _12HourSDF.format(_24HourDt);

					feedObject.setBoarding_points(list);
					feedObject.setTravel_id(travel_id);
					feedObject.setService_number(service_num);
					feedObject.setStatus(status);
					feedObject.setFrom_id(rs.getInt("from_id"));
					feedObject.setFrom_name(rs.getString("from_name"));
					feedObject.setTo_id(rs.getInt("to_id"));
					feedObject.setTo_name(rs.getString("to_name"));
					feedObject.setDep_time(startTime);
					feedObject.setJourney_time(rs.getString("journey_time"));
					feedObject.setArrival_time(rs.getString("arr_time"));
					feedObject.setBus_type(rs.getString("bus_type"));
					feedObject.setBus_model(rs.getString("model"));
					feedObject.setTotal_seats(tot_seats);
					// feedObject.setLowerdeck_total_seats(rs.getInt("t2.lowerdeck_nos"));
					// feedObject.setUpperdeck_total_seats(rs.getInt("t2.upperdeck_nos"));
					feedObject.setJourney_date(jdate);
					feedObject.setAvailable_seats(availableSeats);
					// feedObject.setLowerdeck_available_seats(rs.getInt("lowerdeck_nos"));
					// feedObject.setUpperdeck_available_seats(rs.getInt("upperdeck_nos"));
					feedObject.setSeat_fare(seat_fare);
					feedObject.setLb_fare(lberth_fare);
					feedObject.setUb_fare(uberth_fare);
					feedObject.setconvenience_charge(rs2.getString("convenience_charge"));
					feedData.add(feedObject);

				}
				sd.setService_details(feedData);

				return sd;
			} catch (SQLException ie) {
				// log.error("Exception in dao-getServicesList() from"+ie.getMessage()+"#ip:"+ip+" and key"+key);
				System.out.println("Error Code" + ie.getErrorCode());
				System.out.println("Error Message111" + ie.getMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error");
				sd.setResponseCodes(response);
				return sd;
			} catch (Exception e) {
				// log.error("Exception in dao-getServicesList() from"+e.getMessage()+"#ip:"+ip+" and key"+key);
				System.out.println("Error Message222" + e.getMessage());
				System.out.println("Error Line" + e.getLocalizedMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error!");
				sd.setResponseCodes(response);
				// e.printStackTrace();
				// throw new RuntimeException(e);
				return sd;
			}
		}// close if
		else {
			// log.info("Authentication Failed For dao-getServicesList() from ip:"+ip+" and api:"+key);
			response.setCode(401);
			response.setMessage("Authentication failed");
			sd.setResponseCodes(response);
			return sd;
		}// close else

	}// getServicesList

	/**
	 * This method send the seating arrangement.
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return seats and row cols.
	 */
	public GetSeatingArrangement getSeatingArgmtDb(Connection connection,
			String key, int from, int to, String date, String srvno,
			HttpServletRequest request) throws Exception, SQLException {
		/*
		 * String ipAdd=request.getRemoteAddr(); InetAddress ip1; ip1 =
		 * InetAddress.getLocalHost(); String ip = ip1.getHostAddress();
		 */
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// logging information storing into logs_api table
		String m = "getSeating()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		//int res = psl.executeUpdate();
		// log.info("getSeatingArgmtDb() Called from ip:"+ip+" api:"+key);
		boolean valid = this.authenticate(key, ip);
		GetSeatingArrangement feedObject = new GetSeatingArrangement();
		LayoutDetails ldObj = null;
		CoachLayout clObj = null;
		SeatDetails sdetObj = null;
		Seat seatObj = null;
		boolean seat_availability = false;
		boolean is_ladies_seat = false;
		Origin origin = null;
		Destination destination = null;
		ResponseCodes response = new ResponseCodes();
		List<Integer> cityOrderIdList = null;
		String stageOrder= null;
		String blockedPnrs = null;
		if (valid) {// valid call
			try {

				// checking the origin and destination id are same or not
				if (from == to) {
					response.setCode(413);
					response.setMessage("The origin and destination should not be same");
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				/*
				 * if(from==0 || to==0) { response.setCode(423);
				 * response.setMessage("Invalid Origin or Destination");
				 * sd.setResponseCodes(response); return sd; }
				 */
				if (date == null || from == 0 || to == 0 || srvno == null) {
					response.setCode(400);
					response.setMessage("Invalid Parameter Name or Parameter value is NULL");
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				// convering input date string type to date format
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date input_date;
				// checking the input date format
				try {
					formatter.applyPattern("yyyy-MM-dd");
					formatter.setLenient(false);
					input_date = formatter.parse(date);
				} catch (ParseException e) {
					response.setCode(422);
					response.setMessage("Invalid Date or please check the date format yyyy-mm-dd");
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				// current date
				Date cdate = new Date();
				String tdate = formatter.format(cdate);
				Date today_date = formatter.parse(tdate);
				// if input date is lesser than current date
				if (input_date.compareTo(today_date) < 0) {
					response.setCode(415);
					response.setMessage("Date should be Equal or  Greater than "
							+ tdate);
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				
				

				PreparedStatement ps123 = connection
						.prepareStatement("select status from buses_list where service_num=? and from_id=? and to_id=? and journey_date=?");
				ps123.setString(1, srvno);
				ps123.setInt(2, from);
				ps123.setInt(3, to);
				ps123.setString(4, date);
				ResultSet rs123 = ps123.executeQuery();
				String service_status = null;
				if (rs123.next()) {
					service_status = rs123.getString("status");

				}
				if (service_status==null || service_status.equals("2")) {
					response.setCode(415);
					response.setMessage("Service is Temporarily not available");
					feedObject.setResponseCodes(response);
					return feedObject;
				} else if (service_status.equals("0")) {
					response.setCode(415);
					response.setMessage("Service not Available");
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				PreparedStatement psd = connection
						.prepareStatement("select t1.fwd from registered_operators t1,master_buses t2 where t2.service_num=? and t1.travel_id=t2.travel_id");
				psd.setString(1, srvno);

				ResultSet rsd = psd.executeQuery();
				int fwd = 0;
				if (rsd.next()) {
					fwd = rsd.getInt("fwd");
				}
				// getting no of days in between two days
				Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				cal1.setTime(today_date);
				cal2.setTime(input_date);

				int numberOfDays = 0;
				while (cal1.before(cal2)) {
					numberOfDays++;
					cal1.add(Calendar.DATE, 1);

				}
				// System.out.println("number of days is   "+numberOfDays);
				// System.out.println("Forward booking days are "+fwd);
				if (fwd > numberOfDays) {

				} else {
					response.setCode(402);
					response.setMessage("No Services!"); // if(!rs.isBeforeFirst())
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				PreparedStatement ps = connection
						.prepareStatement("select t1.travel_id,t1.from_id,t1.from_name,t1.to_id,t1.to_name,t1.start_time,t1.journey_time,t1.model,t1.bus_type,t1.seat_nos,t2.seat_fare,t2.lberth_fare,t2.uberth_fare,t2.available_seats,t2.journey_date,t1.service_tax,t1.isac from master_buses t1,buses_list t2 where t1.service_num=? and t1.from_id=? and t1.to_id=? and t2.service_num=? and t2.from_id=? and t2.to_id=? and t2.journey_date=? and t1.status=1 and t2.status=1 and t1.service_num=t2.service_num");
				ps.setString(1, srvno);
				ps.setInt(2, from);
				ps.setInt(3, to);
				ps.setString(4, srvno);
				ps.setInt(5, from);
				ps.setInt(6, to);
				ps.setString(7, date);
				ResultSet rs = ps.executeQuery();
				int travel_id = 0;
				String journey_date = null;
				String isac = null;

				if (rs.next()) {
					travel_id = (rs.getInt("travel_id"));
					journey_date = rs.getString("journey_date");
					isac = rs.getString("isac");
				} else {
					response.setCode(402);
					response.setMessage("No Services!"); // if(!rs.isBeforeFirst())
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				rs.previous();
				cityOrderIdList = getOrderIds( connection,from,to,  travel_id,srvno);
				if(cityOrderIdList!=null)
				{
					stageOrder = cityOrderIdList.get(0)+"-"+cityOrderIdList.get(1);
				}
				 
				PreparedStatement ps2 = connection
						.prepareStatement("select seat_name,row,col,seat_type,window,is_ladies,seat_status,available_stages,booked_stages,blocked_stages,blocked_time,blocked_pnrs,available from layout_list where service_num=? and journey_date=?");
				ps2.setString(1, srvno);
				ps2.setString(2, date);
				ResultSet rs2 = ps2.executeQuery();
				PreparedStatement ps3 = connection
						.prepareStatement("select operator_title,convenience_charge,cgst,sgst,tcs from registered_operators where travel_id=? ");
				ps3.setInt(1, travel_id);
				String travel_name = null;
				float convenience_charge = 0;
				float cgst = 0;
				float sgst = 0;
				float tcs = 0;
				ResultSet rs3 = ps3.executeQuery();
				if (rs3.next()) {
					travel_name = rs3.getString("operator_title");
					convenience_charge = rs3.getFloat("convenience_charge");
					cgst = rs3.getFloat("cgst");
					sgst = rs3.getFloat("sgst");
					tcs = rs3.getFloat("tcs");
				}
				ArrayList<LayoutDetails> ld = new ArrayList<LayoutDetails>();
				ArrayList<SeatDetails> seats_list = new ArrayList<SeatDetails>();
				Map<String, String> blockedStagesMap = new HashMap<String, String>();
				String blockedStagesFrmDB = null;
				String availableStagesFromDb = null;
				String bookedStagesFromDb = null;
				boolean isCurrentStageBlocked = false;
				boolean isCurrentStageAvailable = false;
				boolean isCurrentStageBooked = false;
				while (rs.next()) {
					feedObject = new GetSeatingArrangement();

					ldObj = new LayoutDetails();
					origin = new Origin();
					destination = new Destination();
					ldObj.setTravel_id(travel_id);
					ldObj.setTravel_name(travel_name);
					ldObj.setService_num(srvno);
					ldObj.setTravel_date(journey_date);
					ldObj.setBus_model(rs.getString("model"));
					ldObj.setBus_type(rs.getString("bus_type"));
					origin.setId(rs.getInt("from_id"));
					origin.setName(rs.getString("from_name"));
					ldObj.setOrigin(origin);
					destination.setId(rs.getInt("to_id"));
					destination.setName(rs.getString("to_name"));
					ldObj.setDestination(destination);
					clObj = new CoachLayout();
					clObj.setAvailable_seats(rs.getInt("available_seats"));
					clObj.setTotal_seats(rs.getInt("seat_nos"));
					while (rs2.next()) {
						sdetObj = new SeatDetails();
						seatObj = new Seat();
						 blockedStagesFrmDB = rs2.getString("blocked_stages");
						 availableStagesFromDb = rs2.getString("available_stages");
						 bookedStagesFromDb = rs2.getString("booked_stages");
						 blockedPnrs = rs2.getString("blocked_pnrs");
						
						
						if(availableStagesFromDb!=null) isCurrentStageAvailable = availableStagesFromDb.contains(stageOrder);
						if(bookedStagesFromDb!=null) isCurrentStageBooked = bookedStagesFromDb.contains(stageOrder);
						if(blockedStagesFrmDB!=null) isCurrentStageBlocked = blockedStagesFrmDB.contains(stageOrder);
						
						// String blocked_time=rs.getString("blocked_time");
						// SimpleDateFormat formatter = new
						// SimpleDateFormat("yyyy-MM-dd");
						// difference bw blocked time and current time

						// seat status is available and seat has not reserved
						// for any agent
						
						if (rs2.getInt("seat_status") == 0
								&& (rs2.getInt("available") == 0)) {
							seat_availability = true;
						}
						// seat status is booked or seat has reserved for any
						// agent
						else if ((rs2.getInt("seat_status") == 1)
								|| (rs2.getInt("available") != 0)) {
							seat_availability = false;
						}
						else if ((rs2.getInt("seat_status") == 4) && (isCurrentStageBooked==true || isCurrentStageAvailable==false))
						{
								seat_availability = false;
							
						}
						else if ((rs2.getInt("seat_status") == 4) && (isCurrentStageBooked==false && isCurrentStageAvailable==true))
						{
							if(isCurrentStageBlocked == false)	seat_availability = true;
							else
							{
								if(updateToAvailableFromBlocked(connection,blockedStagesFrmDB,stageOrder, 
										travel_id ,rs2.getString("seat_name"),srvno,journey_date,rs2.getInt("seat_status"), blockedPnrs))
								seat_availability = true;
								else seat_availability = false;
							}
							
						}
						else if ((rs2.getInt("seat_status") == 2)
								&& (rs2.getInt("available") == 0) && isCurrentStageBlocked == false ) {
							seat_availability = true;
						}
						
						// seat has blocked and not reserved for any agent
						else if ((rs2.getInt("seat_status") == 2)
								&& (rs2.getInt("available") == 0) && isCurrentStageBlocked == true) {
							// release the blocking seat if difference b/w
							// current and blocked time is more than 10 mints
							// calling method
							
							if(updateToAvailableFromBlocked(connection,blockedStagesFrmDB,stageOrder, travel_id ,
									rs2.getString("seat_name"),srvno,journey_date,rs2.getInt("seat_status"),blockedPnrs))
							seat_availability = true;
							else seat_availability = false;
							
						} else {
							seat_availability = false;
						}

						if (rs2.getInt("is_ladies") == 0) {
							is_ladies_seat = false;
						} else if (rs2.getInt("is_ladies") == 1) {
							is_ladies_seat = true;
						}
						// getting fares
						String seat_type = rs2.getString("seat_type");
						String seater = "s";
						String lsleeper = "l";
						String lsleeper1 = "l:s";
						String lsleeper2 = "l:b";
						String usleeper = "u";
						float seat_fare = 0;
						float seat_fare1 = 0;
						String changedSeatFare = null;
						String seat_name = rs2.getString("seat_name");
						float discount_amount = 0;
						float fare = 0;
						float service_tax = 0;
						float service_tax_amount = 0;
						// checking seat fares are different or not
						
						// getting updated fare from master_price
						PreparedStatement ps4 = connection
								.prepareStatement("select * from master_price where service_num=? and travel_id=? and journey_date=? and from_id=? and to_id=?");
						ps4.setString(1, srvno);
						ps4.setInt(2, travel_id);
						ps4.setString(3, date);
						ps4.setInt(4, from);
						ps4.setInt(5, to);

						ResultSet rs4 = ps4.executeQuery();

						if (rs4.next()) {
							if (seat_type.equalsIgnoreCase(seater)
									|| seat_type.equalsIgnoreCase(lsleeper1)) {
								changedSeatFare = rs4
										.getString("seat_fare_changed");
								seat_fare = rs4.getFloat("seat_fare");
							}
							if (seat_type.equalsIgnoreCase(lsleeper)
									|| seat_type.equalsIgnoreCase(lsleeper2)) {
								changedSeatFare = rs4
										.getString("lberth_fare_changed");
								seat_fare = rs4.getFloat("lberth_fare");
							}
							if (seat_type.equalsIgnoreCase(usleeper)) {
								changedSeatFare = rs4
										.getString("uberth_fare_changed");
								seat_fare = rs4.getFloat("uberth_fare");
							}

							/*
							 * seat_fare_changed =
							 * rs4.getString("seat_fare_changed");
							 * lberth_fare_changed =
							 * rs4.getString("lberth_fare_changed");
							 * uberth_fare_changed =
							 * rs4.getString("uberth_fare_changed");
							 */
							// System.out.println("fare"+seat_fare+"/"+changedSeatFare);
						} else {
							PreparedStatement ps6 = connection
									.prepareStatement("select * from master_price where service_num=? and travel_id=? and from_id=? and to_id=? and journey_date IS NULL");
							ps6.setString(1, srvno);
							ps6.setInt(2, travel_id);
							ps6.setInt(3, from);
							ps6.setInt(4, to);

							ResultSet rs6 = ps6.executeQuery();

							if (rs6.next()) {
								if (seat_type.equalsIgnoreCase(seater)
										|| seat_type
												.equalsIgnoreCase(lsleeper1)) {
									changedSeatFare = rs6
											.getString("seat_fare_changed");
									seat_fare = rs6.getFloat("seat_fare");
								}
								if (seat_type.equalsIgnoreCase(lsleeper)
										|| seat_type
												.equalsIgnoreCase(lsleeper2)) {
									changedSeatFare = rs6
											.getString("lberth_fare_changed");
									seat_fare = rs6.getFloat("lberth_fare");
								}
								if (seat_type.equalsIgnoreCase(usleeper)) {
									changedSeatFare = rs6
											.getString("uberth_fare_changed");
									seat_fare = rs6.getFloat("uberth_fare");
								}
								// System.out.println("faredf"+seat_fare+"/"+changedSeatFare);
							}
						}
						if (changedSeatFare != null
								&& !changedSeatFare.isEmpty()) {
							String[] faredetarray = changedSeatFare.split("@");
							for (int i = 0; i < faredetarray.length; i++) {
								String[] faredetarray1 = faredetarray[i]
										.split("#");

								String fseatname = faredetarray1[0];
								String changedfare = faredetarray1[1];

								if (seat_name.equalsIgnoreCase(fseatname)) {
									seat_fare1 = Float.parseFloat(changedfare);
									break;
								} else {
									seat_fare1 = seat_fare;
								}
							}
						} else {
							seat_fare1 = seat_fare;
						}

						// getting discount type,discount from master_discount
						PreparedStatement ps7 = connection
								.prepareStatement("select * from master_discount where service_num=? and travel_id=? and discount_date=?");
						ps7.setString(1, srvno);
						ps7.setInt(2, travel_id);
						ps7.setString(3, date);
						ResultSet rs7 = ps7.executeQuery();
						
						String discount_type = null;
						float discount = 0;
						String discount_for = null;
						float convenience_charge1 = 0;
						float cgst_amount = 0;
						float sgst_amount = 0;
						float tcs_amount = 0;
						float margin = 0;
						float agent_commission = 0;
						String comm_type = null;

						if (rs7.next()) {
							discount_type = rs7.getString("discount_type");
							discount = rs7.getFloat("discount");
							discount_for = rs7.getString("discount_for");
						} else {
							PreparedStatement ps8 = connection
									.prepareStatement("select * from master_discount where service_num=? and travel_id=? and discount_date IS NULL");
							ps8.setString(1, srvno);
							ps8.setInt(2, travel_id);
							// System.out.println("ps8"+ps8.toString());
							ResultSet rs8 = ps8.executeQuery();

							if (rs8.next()) {
								discount_type = rs8.getString("discount_type");
								discount = rs8.getFloat("discount");
								discount_for = rs8.getString("discount_for");
							}

						}
						// System.out.println("discount_for "+discount_for);
						if (discount_for != null) {
							if (!discount_for.equalsIgnoreCase("web")) {
								if (discount_type != null
										&& !discount_type.isEmpty()) {
									if (discount_type
											.equalsIgnoreCase("percent")) {
										discount_amount = (seat_fare1 * discount) / 100;
									} else {
										discount_amount = discount;
									}
								}
							} else {
								discount_amount = Float.parseFloat("0.00");
							}
						} else {
							discount_amount = Float.parseFloat("0.00");
						}
						
						DecimalFormat df = new DecimalFormat("#.##");
						
						service_tax = rs.getFloat("service_tax");
						service_tax_amount = Float.parseFloat(df
								.format((seat_fare1 * service_tax) / 100));
						
						PreparedStatement ps9 = connection
								.prepareStatement("select margin,comm_type from agents_operator where api_key=? and status=? and agent_type=?");
						ps9.setString(1, key);
						ps9.setInt(2, 1);
						ps9.setInt(3, 3);
						// System.out.println("ps8"+ps8.toString());
						ResultSet rs9 = ps9.executeQuery();

						if (rs9.next()) {
							margin = rs9.getFloat("margin");
							comm_type = rs9.getString("comm_type");							
						}
						
						if(comm_type.equalsIgnoreCase("percent")) {
							agent_commission = Float.parseFloat(df
									.format((seat_fare1 * margin) / 100));
							convenience_charge1 = Float
									.parseFloat(df
											.format((agent_commission * convenience_charge) / 100));
						} else if(comm_type.equalsIgnoreCase("rupees")) {
							agent_commission = Float.parseFloat(df
									.format(margin));
							convenience_charge1 = Float
									.parseFloat(df
											.format((agent_commission * convenience_charge) / 100));
						}
						
						/*convenience_charge1 = Float
								.parseFloat(df
										.format((seat_fare1 * convenience_charge) / 100));*/
						if (isac.equalsIgnoreCase("yes")) {
							cgst_amount = Float.parseFloat(df
									.format((seat_fare1 * cgst) / 100));
							sgst_amount = Float.parseFloat(df
									.format((seat_fare1 * sgst) / 100));
							tcs_amount = Float.parseFloat(df
									.format((seat_fare1 * tcs) / 100));
						}
						
						if(service_tax == 0 || service_tax == 0.0) {
							fare = Float.parseFloat(df.format(seat_fare1))
									+ service_tax_amount + convenience_charge1 + cgst_amount + sgst_amount
									- Float.parseFloat(df.format(discount_amount));
						} else {
							fare = Float.parseFloat(df.format(seat_fare1))
									+ service_tax_amount + convenience_charge1
									- Float.parseFloat(df.format(discount_amount));
						}
						
						//System.out.println("fare"+fare);
						seatObj.setAvailable(seat_availability);
						seatObj.setIs_ladies_seat(is_ladies_seat);
						seatObj.setNumber(seat_name);
						seatObj.setFare(String.valueOf(df.format(fare)));
						seatObj.setBase_fare(seat_fare1);
						seatObj.setService_tax_amount(service_tax_amount);
						seatObj.setDiscount_amount(discount_amount);
						seatObj.setconvenience_charge(convenience_charge1);
						/*seatObj.setcgst(cgst_amount);
						seatObj.setsgst(sgst_amount);*/
						seatObj.setRow_id(rs2.getInt("row"));
						seatObj.setCol_id(rs2.getInt("col"));
						seatObj.setType(rs2.getString("seat_type"));
						sdetObj.setSeat(seatObj);
						seats_list.add(sdetObj);
					}

					clObj.setSeat_details(seats_list);
					ldObj.setCoach_layout(clObj);
					ld.add(ldObj);

				}
				feedObject.setService_details(ld);
				return feedObject;
			} catch (SQLException sql) {
				// log.error("Exception in getSeatingArgmtDb() from"+sql.getMessage()+"#ip:"+ip+" and key"+key);
				System.out.println("Error Code" + sql.getErrorCode());
				System.out.println("Error Message" + sql.getMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error");
				feedObject.setResponseCodes(response);
				return feedObject;
			} catch (Exception e) {
				// log.error("Exception in getSeatingArgmtDb() from"+e.getMessage()+"#ip:"+ip+" and key"+key);
				System.out.println("Error Message" + e.getMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error");
				feedObject.setResponseCodes(response);
				e.printStackTrace();
				return feedObject;
			}
		} else {
			// log.info("Authentication Failed For getSeatingArgmtDb() from ip:"+ip+" and api:"+key);
			response.setCode(401);
			response.setMessage("Authentication failed");
			feedObject.setResponseCodes(response);
			return feedObject;
		}

	}

	/**
	 * This method send the seating arrangement for agent.
	 * 
	 * @author Mahendar
	 * @version 1.1
	 * @since 2014 Oct
	 * @return seats and row cols.
	 */
	public GetSeatingArrangementAgent getSeatingArgmtDbAgent(
			Connection connection, String key, int from, int to, String date,
			String srvno, HttpServletRequest request) throws Exception,
			SQLException {
		/*
		 * String ipAdd=request.getRemoteAddr(); InetAddress ip1; ip1 =
		 * InetAddress.getLocalHost(); String ip = ip1.getHostAddress();
		 */
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// logging information storing into logs_api table
		String m = "getSeatingAgent()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		//int res = psl.executeUpdate();
		// log.info("getSeatingArgmtDbAgent() Called from ip:"+ip+" api:"+key);
		boolean valid = this.authenticate(key, ip);
		GetSeatingArrangementAgent feedObject = new GetSeatingArrangementAgent();
		LayoutDetailsAgent ldObj = null;
		CoachLayoutAgent clObj = null;
		SeatDetailsAgent sdetObj = null;
		SeatAgent seatObj = null;
		boolean seat_availability = false;
		boolean is_ladies_seat = false;
		Origin origin = null;
		Destination destination = null;
		ResponseCodes response = new ResponseCodes();
		if (valid) {// valid call
			try {

				// checking the origin and destination id are same or not
				if (from == to) {
					response.setCode(413);
					response.setMessage("The origin and destination should not be same");
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				/*
				 * if(from==0 || to==0) { response.setCode(423);
				 * response.setMessage("Invalid Origin or Destination");
				 * sd.setResponseCodes(response); return sd; }
				 */
				if (date == null || from == 0 || to == 0 || srvno == null) {
					response.setCode(400);
					response.setMessage("Invalid Parameter Name or Parameter value is NULL");
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				// convering input date string type to date format
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date input_date;
				// checking the input date format
				try {
					formatter.applyPattern("yyyy-MM-dd");
					formatter.setLenient(false);
					input_date = formatter.parse(date);
				} catch (ParseException e) {
					response.setCode(422);
					response.setMessage("Invalid Date or please check the date format yyyy-mm-dd");
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				// current date
				Date cdate = new Date();
				String tdate = formatter.format(cdate);
				Date today_date = formatter.parse(tdate);
				// if input date is lesser than current date
				if (input_date.compareTo(today_date) < 0) {
					response.setCode(415);
					response.setMessage("Date should be Equal or  Greater than "
							+ tdate);
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				PreparedStatement psd = connection
						.prepareStatement("select t1.fwd from registered_operators t1,master_buses t2 where t2.service_num=? and t1.travel_id=t2.travel_id");
				psd.setString(1, srvno);
				// System.out.println("psd"+psd.toString());
				ResultSet rsd = psd.executeQuery();
				int fwd = 0;
				if (rsd.next()) {
					fwd = rsd.getInt("fwd");
				}
				// getting no of days in between two days
				Calendar cal1 = Calendar.getInstance();
				Calendar cal2 = Calendar.getInstance();
				cal1.setTime(today_date);
				cal2.setTime(input_date);

				int numberOfDays = 0;
				while (cal1.before(cal2)) {
					numberOfDays++;
					cal1.add(Calendar.DATE, 1);

				}
				// System.out.println("number of days is   "+numberOfDays);
				// System.out.println("Forward booking days are "+fwd);
				if (fwd > numberOfDays) {

				} else {
					response.setCode(402);
					response.setMessage("No Services!"); // if(!rs.isBeforeFirst())
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				PreparedStatement ps = connection
						.prepareStatement("select t1.travel_id,t1.from_id,t1.from_name,t1.to_id,t1.to_name,t1.start_time,t1.journey_time,t1.model,t1.bus_type,t1.seat_nos,t2.seat_fare,t2.lberth_fare,t2.uberth_fare,t2.available_seats,t2.journey_date,t1.title,t1.service_tax,t1.isac from master_buses t1,buses_list t2 where t1.service_num=? and t1.from_id=? and t1.to_id=? and t2.service_num=? and t2.from_id=? and t2.to_id=? and t2.journey_date=? and t1.status=1 and t2.status=1 and t1.service_num=t2.service_num");
				ps.setString(1, srvno);
				ps.setInt(2, from);
				ps.setInt(3, to);
				ps.setString(4, srvno);
				ps.setInt(5, from);
				ps.setInt(6, to);
				ps.setString(7, date);
				ResultSet rs = ps.executeQuery();
				// System.out.println("ps"+ps.toString());
				int travel_id = 0;
				String journey_date = null;
				String title = null;
				float convenience_charge = 0;
				float cgst = 0;
				float sgst = 0;
				float tcs = 0;
				String isac = null;
				float convenience_charge1 = 0;
				float cgst_amount = 0;
				float sgst_amount = 0;
				float tcs_amount = 0;
				float margin = 0;
				String comm_type = null;
				float agent_commission = 0;
				
				if (rs.next()) {
					travel_id = (rs.getInt("travel_id"));
					journey_date = rs.getString("journey_date");
					title = rs.getString("title");
					isac = rs.getString("isac");
				} else {
					response.setCode(402);
					response.setMessage("No Services!"); // if(!rs.isBeforeFirst())
					feedObject.setResponseCodes(response);
					return feedObject;
				}
				rs.previous();
				PreparedStatement ps2 = connection
						.prepareStatement("select seat_name,row,col,seat_type,window,is_ladies,seat_status,blocked_time,available,available_type from layout_list where service_num=? and journey_date=?");
				ps2.setString(1, srvno);
				ps2.setString(2, date);
				ResultSet rs2 = ps2.executeQuery();
				// System.out.println("ps2"+ps2.toString());
				PreparedStatement ps3 = connection
						.prepareStatement("select operator_title,convenience_charge,cgst,sgst,tcs from registered_operators where travel_id=? ");
				ps3.setInt(1, travel_id);
				String travel_name = null;
				ResultSet rs3 = ps3.executeQuery();
				// System.out.println("ps3"+ps3.toString());
				if (rs3.next()) {
					convenience_charge = rs3.getFloat("convenience_charge");
					cgst = rs3.getFloat("cgst");
					sgst = rs3.getFloat("sgst");
					tcs = rs3.getFloat("tcs");
					
					if (title != null)
						travel_name = rs3.getString("operator_title") + "-"
								+ title;
					else
						travel_name = rs3.getString("operator_title");
				}

				ArrayList<LayoutDetailsAgent> ld = new ArrayList<LayoutDetailsAgent>();
				ArrayList<SeatDetailsAgent> seats_list = new ArrayList<SeatDetailsAgent>();
				while (rs.next()) {
					feedObject = new GetSeatingArrangementAgent();

					ldObj = new LayoutDetailsAgent();
					origin = new Origin();
					destination = new Destination();
					ldObj.setTravel_id(travel_id);
					ldObj.setTravel_name(travel_name);
					ldObj.setService_num(srvno);
					ldObj.setTravel_date(journey_date);
					ldObj.setBus_model(rs.getString("model"));
					ldObj.setBus_type(rs.getString("bus_type"));
					origin.setId(rs.getInt("from_id"));
					origin.setName(rs.getString("from_name"));
					ldObj.setOrigin(origin);
					destination.setId(rs.getInt("to_id"));
					destination.setName(rs.getString("to_name"));
					ldObj.setDestination(destination);
					clObj = new CoachLayoutAgent();
					clObj.setAvailable_seats(rs.getInt("available_seats"));
					clObj.setTotal_seats(rs.getInt("seat_nos"));
					String status = null;
					while (rs2.next()) {
						sdetObj = new SeatDetailsAgent();
						seatObj = new SeatAgent();
						// String blocked_time=rs.getString("blocked_time");
						// SimpleDateFormat formatter = new
						// SimpleDateFormat("yyyy-MM-dd");
						// difference bw blocked time and current time

						// seat status is available
						if (rs2.getInt("seat_status") == 0) {
							seat_availability = true;
							status = "avail";
						}
						// seat status is booked
						else if ((rs2.getInt("seat_status") == 1)) {
							seat_availability = false;
							status = "confirm";
						}
						// seat status is pending
						else if ((rs2.getInt("seat_status") == 3)) {
							seat_availability = false;
							status = "pend";
						}
						// seat has blocked and not reserved for any agent
						else if ((rs2.getInt("seat_status") == 2)) {
							// release the blocking seat if difference b/w
							// current and blocked time is more than 10 mints
							// calling method
							boolean ck = this.releaseBlockedSeat(rs2
									.getString("blocked_time"));
							if (ck == true) {
								// updating seat status,blocked time as 0 that
								// means removing seat from blocked status
								PreparedStatement psupdt = connection
										.prepareStatement("update layout_list set is_ladies=?,seat_status=?,pnr=? where travel_id=? and  seat_name=? and service_num=? and journey_date=?");
								psupdt.setInt(1, 0);
								psupdt.setInt(2, 0);
								psupdt.setInt(3, 0);
								psupdt.setInt(4, travel_id);
								psupdt.setString(5, rs2.getString("seat_name"));
								psupdt.setString(6, srvno);
								psupdt.setString(7, journey_date);
								if (psupdt.executeUpdate() == 1)// updated
																// successfully
								{
									seat_availability = ck;// true
									status = "avail";
								} else {// problem in updating
									seat_availability = false;
									status = "block";
								}
							} else {
								seat_availability = ck; // false
								status = "block";
							}
						} else {
							seat_availability = false;
							status = "block";
						}

						if (rs2.getInt("is_ladies") == 0) {
							is_ladies_seat = false;
						} else if (rs2.getInt("is_ladies") == 1) {
							is_ladies_seat = true;
						}
						// getting fares
						String seat_type = rs2.getString("seat_type");
						String seater = "s";
						String lsleeper = "l";
						String lsleeper1 = "l:s";
						String lsleeper2 = "l:b";
						String usleeper = "u";
						float seat_fare = 0;
						float seat_fare1 = 0;
						String changedSeatFare = null;
						String seat_name = rs2.getString("seat_name");
						float discount_amount = 0;
						float fare = 0;
						float service_tax = 0;
						float service_tax_amount = 0;						

						// getting updated fare from master_price
						PreparedStatement ps4 = connection
								.prepareStatement("select * from master_price where service_num=? and travel_id=? and journey_date=? and from_id=? and to_id=?");
						ps4.setString(1, srvno);
						ps4.setInt(2, travel_id);
						ps4.setString(3, date);
						ps4.setInt(4, from);
						ps4.setInt(5, to);

						ResultSet rs4 = ps4.executeQuery();
						// System.out.println("ps4"+ps4.toString());
						if (rs4.next()) {
							if (seat_type.equalsIgnoreCase(seater)
									|| seat_type.equalsIgnoreCase(lsleeper1)) {
								changedSeatFare = rs4
										.getString("seat_fare_changed");
								seat_fare = rs4.getFloat("seat_fare");
							}
							if (seat_type.equalsIgnoreCase(lsleeper)
									|| seat_type.equalsIgnoreCase(lsleeper2)) {
								changedSeatFare = rs4
										.getString("lberth_fare_changed");
								seat_fare = rs4.getFloat("lberth_fare");
							}
							if (seat_type.equalsIgnoreCase(usleeper)) {
								changedSeatFare = rs4
										.getString("uberth_fare_changed");
								seat_fare = rs4.getFloat("uberth_fare");
							}

							/*
							 * seat_fare_changed =
							 * rs4.getString("seat_fare_changed");
							 * lberth_fare_changed =
							 * rs4.getString("lberth_fare_changed");
							 * uberth_fare_changed =
							 * rs4.getString("uberth_fare_changed");
							 */
							// System.out.println("fare"+seat_fare+"/"+changedSeatFare);
						} else {
							PreparedStatement ps6 = connection
									.prepareStatement("select * from master_price where service_num=? and travel_id=? and from_id=? and to_id=? and journey_date IS NULL");
							ps6.setString(1, srvno);
							ps6.setInt(2, travel_id);
							ps6.setInt(3, from);
							ps6.setInt(4, to);
							// System.out.println("ps6"+ps6.toString());
							ResultSet rs6 = ps6.executeQuery();

							if (rs6.next()) {
								if (seat_type.equalsIgnoreCase(seater)
										|| seat_type
												.equalsIgnoreCase(lsleeper1)) {
									changedSeatFare = rs6
											.getString("seat_fare_changed");
									seat_fare = rs6.getFloat("seat_fare");
								}
								if (seat_type.equalsIgnoreCase(lsleeper)
										|| seat_type
												.equalsIgnoreCase(lsleeper2)) {
									changedSeatFare = rs6
											.getString("lberth_fare_changed");
									seat_fare = rs6.getFloat("lberth_fare");
								}
								if (seat_type.equalsIgnoreCase(usleeper)) {
									changedSeatFare = rs6
											.getString("uberth_fare_changed");
									seat_fare = rs6.getFloat("uberth_fare");
								}
								// System.out.println("faredf"+seat_fare+"/"+changedSeatFare);
							}
						}
						if (changedSeatFare != null
								&& !changedSeatFare.isEmpty()) {
							String[] faredetarray = changedSeatFare
									.split("@");
							for (int j = 0; j < faredetarray.length; j++) {
								String[] faredetarray1 = faredetarray[j]
										.split("#");

								String fseatname = faredetarray1[0];
								String changedfare = faredetarray1[1];

								if (seat_name.equalsIgnoreCase(fseatname)) {
									seat_fare1 = Float
											.parseFloat(changedfare);
									break;
								} else {
									seat_fare1 = seat_fare;
								}
							}
						} else {
							seat_fare1 = seat_fare;
						}
						
						// System.out.println("seat_fare1"+seat_fare1);
						// getting discount type,discount from master_discount
						PreparedStatement ps7 = connection
								.prepareStatement("select * from master_discount where service_num=? and travel_id=? and discount_date=?");
						ps7.setString(1, srvno);
						ps7.setInt(2, travel_id);
						ps7.setString(3, date);
						ResultSet rs7 = ps7.executeQuery();
						String discount_type = null;
						String discount = null;
						String discount_for = null;
						// System.out.println("ps7"+ps7.toString());
						if (rs7.next()) {
							discount_type = rs7.getString("discount_type");
							discount = rs7.getString("discount");
							discount_for = rs7.getString("discount_for");
						} else {
							PreparedStatement ps8 = connection
									.prepareStatement("select * from master_discount where service_num=? and travel_id=? and discount_date IS NULL");
							ps8.setString(1, srvno);
							ps8.setInt(2, travel_id);
							// System.out.println("ps8"+ps8.toString());
							ResultSet rs8 = ps8.executeQuery();

							if (rs8.next()) {
								discount_type = rs8.getString("discount_type");
								discount = rs8.getString("discount");
								discount_for = rs8.getString("discount_for");
							}

						}
						// System.out.println("discount_type"+discount_type+"+"+discount);
						if (discount_for != null) {
							if (!discount_for.equalsIgnoreCase("web")) {
								if (discount_type != null
										&& !discount_type.isEmpty()) {
									if (discount_type
											.equalsIgnoreCase("percent")) {
										discount_amount = (seat_fare1 * Float
												.parseFloat(discount)) / 100;
									} else {
										discount_amount = Float
												.parseFloat(discount);
									}
								}
							} else {
								discount_amount = Float.parseFloat("0.00");
							}
						} else {
							discount_amount = Float.parseFloat("0.00");
						}
						
						DecimalFormat df = new DecimalFormat("#.##");

						service_tax = rs.getFloat("service_tax");
						service_tax_amount = Float.parseFloat(df
								.format((seat_fare1 * service_tax) / 100));
						
						PreparedStatement ps11 = connection
								.prepareStatement("select margin,comm_type from agents_operator where api_key=? and status=? and agent_type=?");
						ps11.setString(1, key);
						ps11.setInt(2, 1);
						ps11.setInt(3, 3);
						// System.out.println("ps8"+ps8.toString());
						ResultSet rs11 = ps11.executeQuery();

						if (rs11.next()) {
							margin = rs11.getFloat("margin");
							comm_type = rs11.getString("comm_type");							
						}
						
						if(comm_type.equalsIgnoreCase("percent")) {
							agent_commission = Float.parseFloat(df
									.format((seat_fare1 * margin) / 100));
							convenience_charge1 = Float
									.parseFloat(df
											.format((agent_commission * convenience_charge) / 100));
						} else if(comm_type.equalsIgnoreCase("rupees")) {
							agent_commission = Float.parseFloat(df
									.format(margin));
							convenience_charge1 = Float
									.parseFloat(df
											.format((agent_commission * convenience_charge) / 100));
						}
						/*convenience_charge1 = Float
								.parseFloat(df
										.format((seat_fare1 * convenience_charge) / 100));*/
						
						if (isac.equalsIgnoreCase("yes")) {
							cgst_amount = Float.parseFloat(df
									.format((seat_fare1 * cgst) / 100));
							sgst_amount = Float.parseFloat(df
									.format((seat_fare1 * sgst) / 100));
							tcs_amount = Float.parseFloat(df
									.format((seat_fare1 * tcs) / 100));
						}
						
						if(service_tax == 0 || service_tax == 0.0) {
							fare = Float.parseFloat(df.format(seat_fare1))
									+ service_tax_amount + convenience_charge1 + cgst_amount + sgst_amount
									- Float.parseFloat(df.format(discount_amount));
						} else {
							fare = Float.parseFloat(df.format(seat_fare1))
									+ service_tax_amount + convenience_charge1
									- Float.parseFloat(df.format(discount_amount));
						}
												
						// System.out.println("faredhv"+seat_fare1+"/"+changedSeatFare);
						seatObj.setAvailable(seat_availability);
						seatObj.setAvailable_type(rs2.getInt("available_type"));
						seatObj.setAvailable_for(rs2.getInt("available"));
						seatObj.setIs_ladies_seat(is_ladies_seat);
						seatObj.setNumber(seat_name);

						seatObj.setFare(fare);
						seatObj.setBase_fare(seat_fare1);
						seatObj.setService_tax_amount(service_tax_amount);
						seatObj.setDiscount_amount(discount_amount);
						seatObj.setconvenience_charge(convenience_charge1);
						seatObj.setcgst(cgst_amount);
						seatObj.setsgst(sgst_amount);
						seatObj.setRow_id(rs2.getInt("row"));
						seatObj.setCol_id(rs2.getInt("col"));
						seatObj.setType(rs2.getString("seat_type"));
						seatObj.setStatus(status);
						sdetObj.setSeat(seatObj);
						seats_list.add(sdetObj);
					}

					clObj.setSeat_details(seats_list);
					ldObj.setCoach_layout(clObj);
					ld.add(ldObj);

				}
				feedObject.setService_details(ld);
				return feedObject;
			} catch (SQLException sql) {
				// log.error("Exception in getSeatingArgmtDbAgent() from"+sql.getMessage()+"#ip:"+ip+" and key"+key);
				System.out.println("Error Code" + sql.getErrorCode());
				System.out.println("Error Message" + sql.getMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error");
				feedObject.setResponseCodes(response);
				return feedObject;
			} catch (Exception e) {
				// log.error("Exception in getSeatingArgmtDbAgent() from"+e.getMessage()+"#ip:"+ip+" and key"+key);
				e.printStackTrace();
				System.out.println("Error Message" + e.getMessage());
				response.setCode(500);
				response.setMessage("Internal Server Error");
				feedObject.setResponseCodes(response);
				return feedObject;
			}
		} else {
			// log.info("Authentication Failed For getSeatingArgmtDbAgent() from ip:"+ip+" and api:"+key);
			response.setCode(401);
			response.setMessage("Authentication failed");
			feedObject.setResponseCodes(response);
			return feedObject;
		}

	}

	/**
	 * This method releases the seat if the blocking time is more than 10
	 * minutes. returns true if time difference is more than 10 mints
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return true or false.
	 */
	public boolean releaseBlockedSeat(String blockedTime) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String currentDateTime = dateFormat.format(date);
		Date d1 = null;
		Date d2 = null;
		// System.out.println("blockedTime"+blockedTime);
		try {
			d1 = dateFormat.parse(currentDateTime);
			d2 = dateFormat.parse(blockedTime);
			/*
			 * Another way to find the difference time //in milliseconds long
			 * diff = d1.getTime() - d2.getTime(); long diffMinutes = diff / (60
			 * * 1000) % 60;
			 */
			DateTime dt1 = new DateTime(d2);
			DateTime dt2 = new DateTime(d1);
			int minutes = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60;
			int days = Days.daysBetween(dt1, dt2).getDays();
			int hours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
			// System.out.print("current Time : "+dateFormat.format(date)+"##Blocking Time : "+blockedTime+"##Diff.minutes"+minutes
			// + "##days : "+days+"## hours : "+hours+"@@");
			if (minutes > 10 || days > 0 || hours > 0) {
				return true;// means seat has blocked before 10 mintues and not
							// yet booked so release the seat
			} else
				return false; // seat has blocked
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * This method book the requested seats.
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return true or false.
	 */
	@SuppressWarnings("unused")
	public SeatBookingResponse getSeatConfirmation(Connection connection,
			String key, int from, int to, String date, String srvno,
			HttpServletRequest request, SeatBookingRequest obj)
			throws Exception, SQLException {

		String tdate = null;
		String bpName = null;
		String dropPoint = null;
		String[] bpArray;
		SeatBookingResponse seatRes = new SeatBookingResponse();
		ResponseCodes response = new ResponseCodes();
		TicketDetails tdet = new TicketDetails();

		/*
		 * String ipAdd=request.getRemoteAddr(); InetAddress ip1; ip1 =
		 * InetAddress.getLocalHost(); String ip = ip1.getHostAddress();
		 */
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// logging information storing into logs_api table
		String m = "seatBooking()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement rl = connection
				.prepareStatement("insert into response_logs (date,date_time,method_called,series,response,ip,api_key) values (?,?,?,?,?,?,?)");

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		int resapi = psl.executeUpdate();

		// log.info("dao-getSeatConfirmation() Called from ip:"+ip+" api:"+key);
		boolean valid = this.authenticate(key, ip);
		if (valid) {
			String genders = null;
			String seats = null;
			String names = null;
			String ages = null;
			String mobile = null;
			String email = null;
			String seatsArray[];
			String namesArray[];
			int seatsCount = 0;
			String bpid = null;
			String dpid = null;
			float fare = 0;
			int idType = 0;
			String idNumber = null;
			String altMobile = null;
			String address = null;
			int pnr = 0;
			String genderArray[];
			/* getting seat information */
			genders = obj.getSex();
			seats = obj.getSeat();
			names = obj.getName();
			ages = obj.getAge();
			mobile = obj.getMobile();
			email = obj.getEmail();
			seatsArray = seats.split(",");
			seatsCount = obj.getNo_of_seats();
			bpid = obj.getBpid();
			dpid = obj.getDpid();
			fare = obj.getFare();
			idType = obj.getId_type();
			idNumber = obj.getId_number();
			altMobile = obj.getAlt_mobile();
			address = obj.getAddress();
			pnr = obj.getPnr();
			genderArray = genders.split(",");
			namesArray = names.split(",");
			// System.out.println("fare"+fare);
			try { /* validations */
				if (from == to) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 1);
					rl.setString(5,
							"The origin and destination should not be same");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(423);
					response.setMessage("The origin and destination should not be same");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (email == null) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 2);
					rl.setString(5, "Email ID is Missing !");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(417);
					response.setMessage("Email ID is Missing !");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (mobile == null) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 3);
					rl.setString(5, "Mobile Number is Missing !");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(418);
					response.setMessage("Mobile Number is Missing !");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (ages == null) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 4);
					rl.setString(5, "Passenger Age(s) missing !");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(419);
					response.setMessage("Passenger Age(s) missing !");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (names == null) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 5);
					rl.setString(5, "Passenger Names(s) missing !");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(420);
					response.setMessage("Passenger Names(s) missing !");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (altMobile == null) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 6);
					rl.setString(5, "Alternate Mobile Number is Missing !");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(421);
					response.setMessage("Alternate Mobile Number is Missing !");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (pnr == 0) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 7);
					rl.setString(5, "Invalid PNR !");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(416);
					response.setMessage("Invalid PNR !");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (from == 0 || to == 0) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 8);
					rl.setString(5, "Invalid Origin or Destination");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(413);
					response.setMessage("Invalid Origin or Destination");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (date == null || from == 0 || to == 0 || key == null
						|| srvno == null || seatsCount == 0) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 9);
					rl.setString(5,
							"Invalid Parameter Name or Parameter value is NULL");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(400);
					response.setMessage("Invalid Parameter Name or Parameter value is NULL");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (fare == 0) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 10);
					rl.setString(5, "Invalid Parameter or Ticket Fare is Zero!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(408);
					response.setMessage("Invalid Parameter or Ticket Fare is Zero!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (bpid == null || bpid == "") {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 11);
					rl.setString(5,
							"Invalid Parameter Name or Invalid Boarding Point ID!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(426);
					response.setMessage("Invalid Parameter Name or Invalid Boarding Point ID!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (seats == null) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 12);
					rl.setString(5, "Invalid Parameter!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(412);
					response.setMessage("Invalid Parameter!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (genders == null) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 13);
					rl.setString(5, "Invalid Parameter!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(425);
					response.setMessage("Invalid Parameter!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				// convering input date string type to date format
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date input_date;
				// checking the input date format
				try {
					formatter.applyPattern("yyyy-MM-dd");
					formatter.setLenient(false);
					input_date = formatter.parse(date);
				} catch (ParseException e) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 14);
					rl.setString(5,
							"Invalid Date or please check the date format yyyy-mm-dd");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(422);
					response.setMessage("Invalid Date or please check the date format yyyy-mm-dd");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				// current date
				Date cdate = new Date();
				tdate = formatter.format(cdate);
				Date today_date = formatter.parse(tdate);
				// if input date is lesser than current date
				if (input_date.compareTo(today_date) < 0) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 15);
					rl.setString(5, "Date should be Equal or Greater than");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(415);
					response.setMessage("Date should be Equal or Greater than "
							+ tdate);
					seatRes.setResponseCodes(response);
					return seatRes;
				}

				PreparedStatement ps123 = connection
						.prepareStatement("select status from buses_list where service_num=? and from_id=? and to_id=? and journey_date=?");
				ps123.setString(1, srvno);
				ps123.setInt(2, from);
				ps123.setInt(3, to);
				ps123.setString(4, date);
				ResultSet rs123 = ps123.executeQuery();
				String service_status = null;
				if (rs123.next()) {
					service_status = rs123.getString("status");

				}
				if (service_status.equals("2")) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 16);
					rl.setString(5, "Service is Temporarily not available");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(415);
					response.setMessage("Service is Temporarily not available");
					seatRes.setResponseCodes(response);
					return seatRes;
				} else if (service_status.equals("0")) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 17);
					rl.setString(5, "Service not Available");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(415);
					response.setMessage("Service not Available");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
			} catch (Exception e) {
				// log.error("Error In : DAO-getSeatConfirmation() method : "+e.getMessage()+" at Line "+Thread.currentThread().getStackTrace()[2].getLineNumber()+"from api:"+key+" IP:"+ip);
			}
			if (seatsArray.length == seatsCount
					&& genderArray.length == seatsCount) {
				int travel_id = 0;
				ArrayList<Integer> myList = new ArrayList<Integer>();
				ArrayList<String> bookedSeatsArray = new ArrayList<String>();
				ArrayList<String> blockedSeatsArray = new ArrayList<String>();
				String seatType = null;
				float totalSeatFare = 0;
				float totalSeatFare1 = 0;
				float total_discount_amt = 0;
				float total_service_tax_amt = 0;
				float total_fare = 0;
				float singleSeatFare = 0;

				// int seat_fare=0;
				String seater = "s";
				String lsleeper = "l";
				String lsleeper1 = "l:s";
				String lsleeper2 = "l:b";
				String usleeper = "u";
				float seat_fare = 0;
				float seat_fare1 = 0;
				String changedSeatFare = null;
				float discount_amount = 0;
				float fare1 = 0;
				float service_tax = 0;
				float service_tax_amount = 0;
				float total_base_fare1 = 0;
				float total_base_fare = 0;
				float total_convenience_charge = 0;
				String isac = null;
				float cgst = 0;
				float sgst = 0;
				float tcs = 0;
				float cgst_amount = 0;
				float sgst_amount = 0;
				float tcs_amount = 0;
				float total_cgst_amount = 0;
				float total_sgst_amount = 0;
				float total_tcs_amount = 0;
				float margin = 0;
				float agent_commission = 0;
				String comm_type = null;
				//added for stagewise booking  :praneeth
				String availableStagesList=null;
				String bookedStagesList=null;
				String blockedStagesList=null;
				String blockedPnrList = null;
				for (int i = 0; i < seatsArray.length; i++) {
					seatType = null;
					// gender should be M or F -- checking
					if (genderArray[i].equalsIgnoreCase("M")
							|| genderArray[i].equalsIgnoreCase("F")) {
						PreparedStatement ps = connection
								.prepareStatement("select * from layout_list where seat_name=? and service_num=? and journey_date=?");
						ps.setString(1, seatsArray[i]);
						ps.setString(2, srvno);
						ps.setString(3, date);
						ResultSet rs = ps.executeQuery();
						if (rs.next() == false) {
							rl.setString(1, todayDateApi);
							rl.setString(2, todayDateTimeApi);
							rl.setString(3, m);
							rl.setInt(4, 18);
							rl.setString(5,
									"Invalid Parameter !" + ps.toString());
							rl.setString(6, ip);
							rl.setString(7, key);
							rl.executeUpdate();
							response.setCode(414);
							response.setMessage("Invalid Parameter !");
							seatRes.setResponseCodes(response);
							return seatRes;
						}
						rs.previous();
						while (rs.next()) {
							seatType = rs.getString("seat_type");
							travel_id = rs.getInt("travel_id");
							//int pnrFromDb = rs.getInt("pnr");
							boolean isPnrValid = false;
							//added for stagewise booking  :praneeth
							availableStagesList = rs.getString("available_stages");
							bookedStagesList = rs.getString("booked_stages");
							blockedStagesList = rs.getString("blocked_stages");
							blockedPnrList = rs.getString("blocked_pnrs");
							if(blockedPnrList != null || blockedPnrList!="") isPnrValid = blockedPnrList.contains(String.valueOf(pnr));
							if (!isPnrValid) {
								rl.setString(1, todayDateApi);
								rl.setString(2, todayDateTimeApi);
								rl.setString(3, m);
								rl.setInt(4, 19);
								rl.setString(5, "Invalid PNR Number!");
								rl.setString(6, ip);
								rl.setString(7, key);
								rl.executeUpdate();
								response.setCode(424);
								response.setMessage("Invalid PNR Number!");
								seatRes.setResponseCodes(response);
								return seatRes;
							}
							
							// getting updated fare from master_price
							PreparedStatement ps4 = connection
									.prepareStatement("select * from master_price where service_num=? and travel_id=? and journey_date=? and from_id=? and to_id=?");
							ps4.setString(1, srvno);
							ps4.setInt(2, travel_id);
							ps4.setString(3, date);
							ps4.setInt(4, from);
							ps4.setInt(5, to);
							// System.out.println(ps4+"ps4");
							ResultSet rs4 = ps4.executeQuery();

							if (rs4.next()) {
								if (seatType.equalsIgnoreCase(seater)
										|| seatType.equalsIgnoreCase(lsleeper1)) {
									changedSeatFare = rs4
											.getString("seat_fare_changed");
									seat_fare = rs4.getFloat("seat_fare");
								}
								if (seatType.equalsIgnoreCase(lsleeper)
										|| seatType.equalsIgnoreCase(lsleeper2)) {
									changedSeatFare = rs4
											.getString("lberth_fare_changed");
									seat_fare = rs4.getFloat("lberth_fare");
								}
								if (seatType.equalsIgnoreCase(usleeper)) {
									changedSeatFare = rs4
											.getString("uberth_fare_changed");
									seat_fare = rs4.getFloat("uberth_fare");
								}								
								// System.out.println("fare"+seat_fare+"/"+changedSeatFare);
							} else {
								PreparedStatement ps6 = connection
										.prepareStatement("select * from master_price where service_num=? and travel_id=? and from_id=? and to_id=? and journey_date IS NULL");
								ps6.setString(1, srvno);
								ps6.setInt(2, travel_id);
								ps6.setInt(3, from);
								ps6.setInt(4, to);

								ResultSet rs6 = ps6.executeQuery();

								if (rs6.next()) {
									if (seatType.equalsIgnoreCase(seater)
											|| seatType
													.equalsIgnoreCase(lsleeper1)) {
										changedSeatFare = rs6
												.getString("seat_fare_changed");
										seat_fare = rs6.getFloat("seat_fare");
									}
									if (seatType.equalsIgnoreCase(lsleeper)
											|| seatType
													.equalsIgnoreCase(lsleeper2)) {
										changedSeatFare = rs6
												.getString("lberth_fare_changed");
										seat_fare = rs6
												.getFloat("lberth_fare");
									}
									if (seatType.equalsIgnoreCase(usleeper)) {
										changedSeatFare = rs6
												.getString("uberth_fare_changed");
										seat_fare = rs6
												.getFloat("uberth_fare");
									}
									// System.out.println("faredf"+seat_fare+"/"+changedSeatFare);
								}
							}
							if (changedSeatFare != null
									&& !changedSeatFare.isEmpty()) {
								String[] faredetarray = changedSeatFare
										.split("@");
								for (int j = 0; j < faredetarray.length; j++) {
									String[] faredetarray1 = faredetarray[j]
											.split("#");

									String fseatname = faredetarray1[0];
									String changedfare = faredetarray1[1];

									if (seatsArray[i]
											.equalsIgnoreCase(fseatname)) {
										seat_fare1 = Float
												.parseFloat(changedfare);
										break;
									} else {
										seat_fare1 = seat_fare;
									}
								}
							} else {
								seat_fare1 = seat_fare;
							}
							// getting discount type,discount from
							// master_discount
							PreparedStatement ps7 = connection
									.prepareStatement("select * from master_discount where service_num=? and travel_id=? and discount_date=?");
							ps7.setString(1, srvno);
							ps7.setInt(2, travel_id);
							ps7.setString(3, date);
							ResultSet rs7 = ps7.executeQuery();
							String discount_type = null;
							float discount = 0;
							float convenience_charge = 0;
							float convenience_charge1 = 0;
							String discount_for = null;

							if (rs7.next()) {
								discount_type = rs7.getString("discount_type");
								discount = rs7.getFloat("discount");
								discount_for = rs7.getString("discount_for");
							} else {
								PreparedStatement ps8 = connection
										.prepareStatement("select * from master_discount where service_num=? and travel_id=? and discount_date IS NULL");
								ps8.setString(1, srvno);
								ps8.setInt(2, travel_id);
								// System.out.println("ps8"+ps8.toString());
								ResultSet rs8 = ps8.executeQuery();

								if (rs8.next()) {
									discount_type = rs8
											.getString("discount_type");
									discount = rs8.getFloat("discount");
									discount_for = rs8
											.getString("discount_for");
								}

							}

							if (discount_for != null) {
								if (!discount_for.equalsIgnoreCase("web")) {
									if (discount_type != null
											&& !discount_type.isEmpty()) {
										if (discount_type
												.equalsIgnoreCase("percent")) {
											discount_amount = (seat_fare1 * discount) / 100;
										} else {
											discount_amount = discount;
										}
									}
								} else {
									discount_amount = Float.parseFloat("0.00");
								}
							} else {
								discount_amount = Float.parseFloat("0.00");
							}							
							
							PreparedStatement ps9 = connection
									.prepareStatement("select * from master_buses where service_num=? and travel_id=?");
							ps9.setString(1, srvno);
							ps9.setInt(2, travel_id);

							ResultSet rs9 = ps9.executeQuery();

							if (rs9.next()) {
								service_tax = rs9.getFloat("service_tax");
								isac = rs9.getString("isac");
							}																					
							
							PreparedStatement ps10 = connection
									.prepareStatement("select convenience_charge,cgst,sgst,tcs from registered_operators where travel_id=?");
							ps10.setInt(1, travel_id);
							ResultSet rs10 = ps10.executeQuery();
							if (rs10.next()) {
								convenience_charge = rs10
										.getFloat("convenience_charge");
								cgst = rs10.getFloat("cgst");
								sgst = rs10.getFloat("sgst");
								tcs = rs10.getFloat("tcs");
							}

							DecimalFormat df = new DecimalFormat("#.##");																										
														
							service_tax_amount = Float.parseFloat(df
									.format((seat_fare1 * service_tax) / 100));
							
							PreparedStatement ps11 = connection
									.prepareStatement("select margin,comm_type from agents_operator where api_key=? and status=? and agent_type=?");
							ps11.setString(1, key);
							ps11.setInt(2, 1);
							ps11.setInt(3, 3);
							// System.out.println("ps8"+ps8.toString());
							ResultSet rs11 = ps11.executeQuery();

							if (rs11.next()) {
								margin = rs11.getFloat("margin");
								comm_type = rs11.getString("comm_type");							
							}
							
							if(comm_type.equalsIgnoreCase("percent")) {
								agent_commission = Float.parseFloat(df
										.format((seat_fare1 * margin) / 100));
								convenience_charge1 = Float
										.parseFloat(df
												.format((agent_commission * convenience_charge) / 100));
							} else if(comm_type.equalsIgnoreCase("rupees")) {
								agent_commission = Float.parseFloat(df
										.format(margin));
								convenience_charge1 = Float
										.parseFloat(df
												.format((agent_commission * convenience_charge) / 100));
							}
							/*convenience_charge1 = Float
									.parseFloat(df
											.format((seat_fare1 * convenience_charge) / 100));*/
							
							if (isac.equalsIgnoreCase("yes")) {
								cgst_amount = Float.parseFloat(df
										.format((seat_fare1 * cgst) / 100));
								sgst_amount = Float.parseFloat(df
										.format((seat_fare1 * sgst) / 100));
								tcs_amount = Float.parseFloat(df
										.format((seat_fare1 * tcs) / 100));
							}
							
							
							if(service_tax == 0 || service_tax == 0.0) {
								fare1 = Float.parseFloat(df.format(seat_fare1))
										+ service_tax_amount
										+ convenience_charge1
										+ cgst_amount
										+ sgst_amount
										- Float.parseFloat(df
												.format(discount_amount));
							} else {
								fare1 = Float.parseFloat(df.format(seat_fare1))
										+ service_tax_amount
										+ convenience_charge1										
										- Float.parseFloat(df
												.format(discount_amount));
							}
							
							//totalSeatFare = totalSeatFare + fare1;// fare
																	// validation												
							
							totalSeatFare = totalSeatFare + fare1;
							totalSeatFare1 = Float.parseFloat(df
									.format(totalSeatFare));
							// end of update fare
							// base_fare
							total_base_fare1 = total_base_fare1 + seat_fare1
									- discount_amount;// base_fare after
														// discount
							total_service_tax_amt = total_service_tax_amt
									+ service_tax_amount;
							total_discount_amt = total_discount_amt
									+ discount_amount;
							total_convenience_charge = total_convenience_charge
									+ convenience_charge1;
							total_cgst_amount = total_cgst_amount + cgst_amount;
							total_sgst_amount = total_sgst_amount + sgst_amount;
							total_tcs_amount = total_tcs_amount + tcs_amount;
							// total ticket fare including tax
							// total ticket fare including tax and convenience
							// charge
							total_fare = total_base_fare1
									+ total_service_tax_amt
									+ total_convenience_charge
									+ total_cgst_amount
									+ total_sgst_amount;

							int busStatus = rs.getInt("status");
							int SeatStatus = rs.getInt("seat_status");
							int Available = rs.getInt("available");
							String BlockedTime = rs.getString("blocked_time");
							if (busStatus != 1) {
								rl.setString(1, todayDateApi);
								rl.setString(2, todayDateTimeApi);
								rl.setString(3, m);
								rl.setInt(4, 20);
								rl.setString(5, "No services!");
								rl.setString(6, ip);
								rl.setString(7, key);
								rl.executeUpdate();
								response.setCode(402);
								response.setMessage("No services!");
								seatRes.setResponseCodes(response);
								return seatRes;
							}
							if (SeatStatus == 1 || Available != 0) {
								bookedSeatsArray.add(seatsArray[i]);
							}

							if ((SeatStatus == 0) || (SeatStatus == 2)) {// seat
																			// is
																			// available
								// gender checking
								int rowno = rs.getInt("col");
								int colno = rs.getInt("row");
								int window = rs.getInt("window");
								int rowinc = rowno + 1;
								int rowdec = rowno - 1;
								PreparedStatement ps3 = connection
										.prepareStatement("select MIN(col) as mincol,MAX(col) as maxcol from layout_list where service_num=? and journey_date=?");
								ps3.setString(1, srvno);
								ps3.setString(2, date);
								int rowmax = 0;
								int rowmin = 0;
								int rowmininc = 0;
								ResultSet rs3 = ps3.executeQuery();
								while (rs3.next()) {
									rowmax = rs3.getInt("maxcol");
									rowmin = rs3.getInt("mincol");
									rowmininc = rowmin + 1;
								}

								/*PreparedStatement ps2 = connection
										.prepareStatement("select is_ladies from layout_list where service_num=? and journey_date=? and row=? and col=? and seat_type=?");
								ps2.setString(1, srvno);
								ps2.setString(2, date);
								ps2.setInt(3, colno);
								if (window == 1 && rowno != 1) {
									ps2.setInt(4, rowdec);
								} else if (window != 1 && rowno == rowmininc) {
									ps2.setInt(4, rowdec);
								} else if (window == 1 && rowno == rowmax) {
									ps2.setInt(4, rowdec);
								} else {
									ps2.setInt(4, rowinc);
								}
								ps2.setString(5, seatType);
								ResultSet rs2 = ps2.executeQuery();
								while (rs2.next()) {
									int Ladies = rs2.getInt("is_ladies");
									myList.add(Ladies);
								}*/

							}
						}
					} else {// gender mistake
						rl.setString(1, todayDateApi);
						rl.setString(2, todayDateTimeApi);
						rl.setString(3, m);
						rl.setInt(4, 21);
						rl.setString(5, "Invalid Gender Format!");
						rl.setString(6, ip);
						rl.setString(7, key);
						rl.executeUpdate();
						response.setCode(407);
						response.setMessage("Invalid Gender Format!");
						seatRes.setResponseCodes(response);
						return seatRes;
					}
				}
				// fare mismathc
				int retval = Float.compare(totalSeatFare1, fare);

				if (retval != 0) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 22);
					rl.setString(5, "Ticket Fare mismatch!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(409);
					response.setMessage("Ticket Fare mismatch!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				/*
				 * if(totalSeatFare>fare){ response.setCode(409);
				 * response.setMessage("Ticket Fare mismatch!");
				 * seatRes.setResponseCodes(response); return seatRes; }
				 */
				/* Blocking */
				String SG = null;
				String BlockedSeat = null;
				String BookedSeat = null;
				try {
					int blockedCount = blockedSeatsArray.size();
					for (int y = 0; y < blockedCount; y++) {
						if (BlockedSeat == null)
							BlockedSeat = blockedSeatsArray.get(y);
						else
							BlockedSeat = BlockedSeat + ","
									+ blockedSeatsArray.get(y);
					}
					if (BlockedSeat != null) {
						response.setCode(405);
						response.setMessage("Seat number(s) " + BlockedSeat
								+ " already reserved by other customer!");
						seatRes.setResponseCodes(response);
						return seatRes;
					}
					/* booked seats */
					int bookedCount = bookedSeatsArray.size();
					for (int z = 0; z < bookedCount; z++) {
						if (BookedSeat == null)
							BookedSeat = bookedSeatsArray.get(z);
						else
							BookedSeat = BookedSeat + ","
									+ bookedSeatsArray.get(z);
					}
					if (BookedSeat != null) {
						response.setCode(404);
						response.setMessage("Seat number(s) " + BookedSeat
								+ " already booked by other customer!");
						seatRes.setResponseCodes(response);
						return seatRes;
					}
					/* gender checking 
					int ALCount = myList.size();

					for (int k = 0; k < ALCount; k++) {
						if ((genderArray[k].equalsIgnoreCase("M"))
								&& ((Integer) myList.get(k) == 1)) {
							if (SG == null) {
								SG = seatsArray[k];
							} else {
								SG = SG + "," + seatsArray[k];
							}
						}
					}*/
				} catch (Exception e) {
					// log.error("Error In : DAO-getSeatConfirmation() method : "+e.getMessage()+" at Line "+Thread.currentThread().getStackTrace()[2].getLineNumber()+"from api:"+key+" IP:"+ip);
					// TODO: handle exception
				}
				//if (SG == null) {
					try {
						synchronized (seats) {
							// getting boarding point
							PreparedStatement ps7 = connection
									.prepareStatement("select is_van,board_drop,contact from boarding_points where service_num=? and travel_id=? and city_id=? and board_or_drop_type=? and bpdp_id=? ");
							ps7.setString(1, srvno);
							ps7.setInt(2, travel_id);
							ps7.setInt(3, from);
							ps7.setString(4, "board");
							ps7.setString(5, bpid);
							ResultSet rs7 = ps7.executeQuery();
							String boardPointTotal = null;
							String contact = null;
							String isVan = null;
							if (rs7.next() == false) {
								rl.setString(1, todayDateApi);
								rl.setString(2, todayDateTimeApi);
								rl.setString(3, m);
								rl.setInt(4, 23);
								rl.setString(5,
										"Invalid Parameter Name or Invalid Boarding Point ID!");
								rl.setString(6, ip);
								rl.setString(7, key);
								rl.executeUpdate();
								response.setCode(426);
								response.setMessage("Invalid Parameter Name or Invalid Boarding Point ID!");
								seatRes.setResponseCodes(response);
								return seatRes;
							}
							rs7.previous();
							while (rs7.next()) {
								boardPointTotal = rs7.getString("board_drop");
								contact = rs7.getString("contact");
								isVan = rs7.getString("is_van");
							}
							bpArray = boardPointTotal.split("#");
							bpName = bpArray[0] + "-" + bpArray[1];
							// getting dropping point
							PreparedStatement ps9 = connection
									.prepareStatement("select board_drop from boarding_points where service_num=? and travel_id=? and board_or_drop_type=? and bpdp_id=? ");
							ps9.setString(1, srvno);
							ps9.setInt(2, travel_id);
							ps9.setString(3, "drop");
							ps9.setString(4, dpid);
							ResultSet rs9 = ps9.executeQuery();
							while (rs9.next()) {
								dropPoint = rs9.getString("board_drop");
							}
							/* Getting origin details */
							PreparedStatement ps5 = connection
									.prepareStatement("select city_name from master_cities where city_id=? ");
							ps5.setInt(1, from);
							ResultSet rs5 = ps5.executeQuery();
							String FromName = null;
							if (rs5.next() == false) {
								rl.setString(1, todayDateApi);
								rl.setString(2, todayDateTimeApi);
								rl.setString(3, m);
								rl.setInt(4, 24);
								rl.setString(5, "Invalid Origin ID!");
								rl.setString(6, ip);
								rl.setString(7, key);
								rl.executeUpdate();
								response.setCode(413);
								response.setMessage("Invalid Origin ID!");
								seatRes.setResponseCodes(response);
								return seatRes;
							}
							rs5.previous();
							while (rs5.next()) {
								FromName = rs5.getString("city_name");
							}
							/* Getting Destination details */
							PreparedStatement ps6 = connection
									.prepareStatement("select city_name from master_cities where city_id=? ");
							ps6.setInt(1, to);
							ResultSet rs6 = ps6.executeQuery();
							String ToName = null;
							if (rs6.next() == false) {
								rl.setString(1, todayDateApi);
								rl.setString(2, todayDateTimeApi);
								rl.setString(3, m);
								rl.setInt(4, 25);
								rl.setString(5, "Invalid Destination ID!");
								rl.setString(6, ip);
								rl.setString(7, key);
								rl.executeUpdate();
								response.setCode(413);
								response.setMessage("Invalid Destination ID!");
								seatRes.setResponseCodes(response);
								return seatRes;
							}
							rs6.previous();
							while (rs6.next()) {
								ToName = rs6.getString("city_name");
							}
							// checking the Agent Balance
							PreparedStatement ps8 = connection
									.prepareStatement("select id,operator_id,api_type,balance,bal_limit,margin,pay_type from agents_operator where api_key=? and status=? and agent_type=?");
							// ps8.setString(1, ip);
							ps8.setString(1, key);
							ps8.setInt(2, 1);
							ps8.setInt(3, 3);
							ResultSet rs8 = ps8.executeQuery();
							int operatorId = 0;
							String apiType = null;
							int agentId = 0;
							float marginAmt = 0;
							float ticketAmountAfterMargin = 0;
							float postPaidAmt = 0;
							float balance = 0;
							while (rs8.next()) {
								apiType = rs8.getString("api_type");
								operatorId = rs8.getInt("operator_id");
								agentId = rs8.getInt("id");
								balance = rs8.getFloat("balance");
								float balanceLimit = rs8.getFloat("bal_limit");
								margin = rs8.getFloat("margin");
								String payType = rs8.getString("pay_type");
								marginAmt = (total_base_fare1 * margin) / 100;
								// System.out.println("balance"+balance+"balanceLimit"+balanceLimit+"margin"+margin+"payType"+payType+"marginAmt"+marginAmt+"postPaidAmt"+postPaidAmt);
								if (payType.equalsIgnoreCase("prepaid")) {
									postPaidAmt = balance - total_base_fare1;
									ticketAmountAfterMargin = total_base_fare1;
									if ((balance < total_base_fare1)
											&& (balanceLimit == 0)) {
										rl.setString(1, todayDateApi);
										rl.setString(2, todayDateTimeApi);
										rl.setString(3, m);
										rl.setInt(4, 26);
										rl.setString(5, "Insufficient Balance!");
										rl.setString(6, ip);
										rl.setString(7, key);
										rl.executeUpdate();
										response.setCode(411);
										response.setMessage("Insufficient Balance!");
										seatRes.setResponseCodes(response);
										return seatRes;
									} else if ((balance < total_base_fare1)
											&& (postPaidAmt < balanceLimit)) {
										rl.setString(1, todayDateApi);
										rl.setString(2, todayDateTimeApi);
										rl.setString(3, m);
										rl.setInt(4, 27);
										rl.setString(5,
												"Your Balance Limit is Exceeded!");
										rl.setString(6, ip);
										rl.setString(7, key);
										rl.executeUpdate();
										response.setCode(412);
										response.setMessage("Your Balance Limit is Exceeded!");
										seatRes.setResponseCodes(response);
										return seatRes;
									}
								}
								if (payType.equalsIgnoreCase("postpaid")) {
									ticketAmountAfterMargin = total_base_fare1
											- marginAmt;
									postPaidAmt = balance
											- ticketAmountAfterMargin;
									if ((balance < ticketAmountAfterMargin)
											&& (postPaidAmt < balanceLimit)) {
										rl.setString(1, todayDateApi);
										rl.setString(2, todayDateTimeApi);
										rl.setString(3, m);
										rl.setInt(4, 28);
										rl.setString(5,
												"Your Balance Limit is Exceeded!");
										rl.setString(6, ip);
										rl.setString(7, key);
										rl.executeUpdate();
										response.setCode(412);
										response.setMessage("Your Balance Limit is Exceeded !");
										seatRes.setResponseCodes(response);
										return seatRes;
									}
								}
							}// while close

							// update status as
							// blocked(seat_status,blocked_time) in layout_list
							// table
							DateFormat dateFormat = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date CurrentDate = new Date();
							String currentDateTime = dateFormat
									.format(CurrentDate);
							int seatStatus = 1;
							//added for stagewise booking :praneeth
						//	if(availableStagesList == null || availableStagesList =="")
							seatStatus = findMainRouteOrStageBooking(connection,from,to,travel_id,srvno); //source destination request for complete route
							
							String availableStages="";
							List<Integer> orderIdList = getOrderIds(connection, from,  to,  travel_id,srvno);
							String stageOrder = orderIdList.get(0)+"-"+orderIdList.get(1);
							
							String bookedStages = null;
							if(StringUtils.isBlank(bookedStagesList))  bookedStages = stageOrder;
							else bookedStages = bookedStagesList+","+stageOrder;
							
							if(availableStagesList != null  || seatStatus == 4) //stage wise booking request
							{
								//get available stages list
								 availableStages = getAvailableStagesForNextBooking(connection,availableStagesList, orderIdList.get(0),orderIdList.get(1),travel_id,srvno);
							}
							
							if(blockedPnrList !=null && blockedPnrList!="")
							blockedPnrList = getRemovedStagewisePnr(stageOrder, blockedPnrList);
							
							if(blockedStagesList !=null && blockedStagesList!="")
								blockedStagesList = getRemovedBlockedStages(stageOrder, blockedStagesList);
							
							for (int x = 0; x < seatsArray.length; x++) {
								PreparedStatement psupdt = connection
										.prepareStatement("update layout_list set seat_status=?,is_ladies=?,available_stages=?,booked_stages=?,blocked_stages=?,blocked_pnrs=? where travel_id=? and  seat_name=? and service_num=? and journey_date=?");
								psupdt.setInt(1, seatStatus);

								if (genderArray[x].equalsIgnoreCase("F"))
									psupdt.setInt(2, 1);// female
								else
									psupdt.setInt(2, 0);// male

								psupdt.setString(3, availableStages);
								psupdt.setString(4, bookedStages);
								psupdt.setString(5, blockedStagesList);
								psupdt.setString(6, blockedPnrList);
								
								psupdt.setInt(7, travel_id);
								psupdt.setString(8, seatsArray[x]);
								psupdt.setString(9, srvno);
								psupdt.setString(10, date);
								if (psupdt.executeUpdate() == 0)// updated
																// successfully
								{
									rl.setString(1, todayDateApi);
									rl.setString(2, todayDateTimeApi);
									rl.setString(3, m);
									rl.setInt(4, 29);
									rl.setString(5, "Internal Server Error!"
											+ psupdt.toString());
									rl.setString(6, ip);
									rl.setString(7, key);
									rl.executeUpdate();
									response.setCode(500);
									response.setMessage("Internal Server Error!");
									seatRes.setResponseCodes(response);
									return seatRes;
								}
							}// for
								// insert into seat_blocking_det
								// get SELECT AUTO_INCREMENT FROM
								// information_schema.tables WHERE TABLE_NAME =
								// 'seat_blocking_det'
							/*
							 * PreparedStatement
							 * ps10=connection.prepareStatement(
							 * "SELECT AUTO_INCREMENT FROM information_schema.tables WHERE TABLE_NAME =?"
							 * ); ps10.setString(1, "master_booking");
							 */
							PreparedStatement ps10 = connection
									.prepareStatement("SELECT MAX(id) as id from master_booking");
							ResultSet rs10 = ps10.executeQuery();
							int autoId = 0;
							while (rs10.next()) {
								autoId = rs10.getInt("id");
							}
							int autoIdInc = autoId + 1;
							/* generating ticket number */
							String tktNo = null;
							if (apiType.equalsIgnoreCase("te") || apiType == "") {
								tktNo = "TE" + autoIdInc;
							} else if (apiType.equalsIgnoreCase("op")) {
								tktNo = "OPTE" + autoIdInc;
							}
							PreparedStatement ps11 = connection
									.prepareStatement("select operator_title from registered_operators where travel_id=?");
							ps11.setInt(1, travel_id);
							ResultSet rs11 = ps11.executeQuery();
							String operatorName = null;
							if (rs11.next()) {
								operatorName = rs11.getString("operator_title");
							}
							String startTime = null;
							String arrTime = null;
							String busTypeDb = null;
							String busModel = null;
							String journeyTime = null;
							PreparedStatement ps12 = connection
									.prepareStatement("select start_time,journey_time,arr_time,model,bus_type from master_buses where service_num=? and travel_id=? and from_id=? and to_id=?");
							ps12.setString(1, srvno);
							ps12.setInt(2, travel_id);
							ps12.setInt(3, from);
							ps12.setInt(4, to);
							ResultSet rs12 = ps12.executeQuery();
							while (rs12.next()) {
								startTime = rs12.getString("start_time");
								arrTime = rs12.getString("arr_time");
								busTypeDb = rs12.getString("bus_type");
								busModel = rs12.getString("model");
								journeyTime = rs12.getString("journey_time");
							}

							PreparedStatement ps4 = connection
									.prepareStatement("insert into master_booking(tkt_no,pnr,service_no,board_point,bpid,land_mark,source,dest,travels,bus_type,bdate,jdate,seats,gender,start_time,arr_time,paid,save,tkt_fare,base_fare,service_tax_amount,discount_amount,convenience_charge,pname,pemail,pmobile,age,refno,status,pass,travel_id,ip,time,id_type,id_num,padd,alter_ph,fid,tid,operator_agent_type,agent_id,bus_model,cgst,sgst,tcs)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							ps4.setString(1, tktNo);
							ps4.setInt(2, pnr);
							ps4.setString(3, srvno);
							ps4.setString(4, bpName);
							ps4.setString(5, bpid);
							ps4.setString(6, bpArray[2]);
							ps4.setString(7, FromName);
							ps4.setString(8, ToName);
							ps4.setString(9, operatorName);
							ps4.setString(10, busTypeDb);
							ps4.setString(11, tdate);
							ps4.setString(12, date);
							ps4.setString(13, seats);
							ps4.setString(14, genders);
							ps4.setString(15, startTime);
							ps4.setString(16, arrTime);
							ps4.setFloat(17, ticketAmountAfterMargin);
							ps4.setFloat(18, marginAmt);
							ps4.setFloat(19, total_fare);
							ps4.setFloat(20, total_base_fare1);
							ps4.setFloat(21, total_service_tax_amt);
							ps4.setFloat(22, total_discount_amt);
							ps4.setFloat(23, total_convenience_charge);
							ps4.setString(24, names);
							ps4.setString(25, email);
							ps4.setString(26, mobile);
							ps4.setString(27, ages);
							ps4.setInt(28, pnr);
							ps4.setString(29, "confirmed");
							ps4.setInt(30, seatsCount);
							ps4.setInt(31, travel_id);
							ps4.setString(32, ip);
							ps4.setString(33, currentDateTime);
							ps4.setInt(34, idType);
							ps4.setString(35, idNumber);
							ps4.setString(36, address);
							ps4.setString(37, altMobile);
							ps4.setInt(38, from);
							ps4.setInt(39, to);
							ps4.setInt(40, 3);
							ps4.setInt(41, agentId);
							ps4.setString(42, busModel);
							ps4.setFloat(43, total_cgst_amount);
							ps4.setFloat(44, total_sgst_amount);
							ps4.setFloat(45, total_tcs_amount);
							int res = ps4.executeUpdate();
							//System.out.println("res"+res);
							String m111 = null;
							PreparedStatement psop1 = connection
									.prepareStatement("select * from registered_operators where travel_id=?");
							psop1.setInt(1, travel_id);
							ResultSet rsop1 = psop1.executeQuery();
							String ph = null;
							String mobile_op = null;
							String sender_id = null;
							int is_api_sms = 0;

							while (rsop1.next()) {
								ph = rsop1.getString("other_contact");
								mobile_op = rsop1.getString("contact_no");
								sender_id = rsop1.getString("sender_id");
								is_api_sms = rsop1.getInt("is_api_sms");

								String txt = "TKT No: " + tktNo + "->"
										+ operatorName + "-" + FromName + "-"
										+ ToName + "->" + srvno + " , DOJ: "
										+ date + " , Seats: " + seats
										+ " , At-" + bpName + " , Ph: ";
								String txt1 = "TKT No: " + tktNo + " ,DOJ "
										+ date + "-" + FromName + "-" + ToName
										+ "->" + srvno + " seats:" + seats
										+ " pass:" + namesArray[0] + "-"
										+ mobile + "";
								String strMsg = java.net.URLEncoder.encode(txt,
										"UTF-8");
								String strMsg1 = java.net.URLEncoder.encode(
										txt1, "UTF-8");
								String sender_id1 = java.net.URLEncoder.encode(
										sender_id, "UTF-8");
								String mobile_op1 = java.net.URLEncoder.encode(
										mobile_op, "UTF-8");
								// System.out.println("Text Message"+txt);
								// System.out.println("Text mobile_op"+mobile_op);
								// System.out.println("Text sender_id"+sender_id);
								// System.out.println("Text is_api_sms"+is_api_sms);
								if (is_api_sms == 1) {
									// System.out.println("Text1 Message"+txt);
									// System.out.println("Text1 mobile_op"+mobile_op);
									// System.out.println("Text1 sender_id"+sender_id);
									// System.out.println("Text1 is_api_sms"+is_api_sms);
									String service_sms_contact = null;
									String strURL = null;
									PreparedStatement ps13 = connection
											.prepareStatement("select distinct contact from service_sms_contact where service_num=? and travel_id=?");
									ps13.setString(1, srvno);
									ps13.setInt(2, travel_id);									
									ResultSet rs13 = ps13.executeQuery();
									if (rs13.next()) {
										service_sms_contact = rs13.getString("contact");
										strURL = "http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=pridhvi@msn.com:activa1525@&senderID="
												+ sender_id1
												+ "&receipientno="
												+ service_sms_contact
												+ "&msgtxt="
												+ strMsg1
												+ "&state=4";
									} else {
										strURL = "http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=pridhvi@msn.com:activa1525@&senderID="
												+ sender_id1
												+ "&receipientno="
												+ mobile_op1
												+ "&msgtxt="
												+ strMsg1
												+ "&state=4";
									}																	

									// System.out.println(" URL is :"+strURL);
									String output = null;
									String exp = null;
									/*
									 * try { Client client = Client.create();
									 * WebResource webResource =
									 * client.resource(strURL); ClientResponse
									 * response1 =
									 * webResource.type("application/json"
									 * ).get(ClientResponse.class); output =
									 * response1.getEntity(String.class);
									 * //System.out.println(output);
									 * 
									 * } catch(Exception e) {
									 * exp=e.getMessage(); }
									 */
									String iStatus = null;
									try {
										java.net.URL obj1 = new java.net.URL(
												strURL);
										HttpURLConnection httpReq = (HttpURLConnection) obj1
												.openConnection();
										httpReq.setDoOutput(true);
										httpReq.setInstanceFollowRedirects(true);
										httpReq.setRequestMethod("GET");
										iStatus = httpReq.getResponseMessage();
										// System.out.println("iStatus: "+iStatus);

									} catch (MalformedURLException ex) {
										ex.printStackTrace();
									} catch (IOException ex) {
										ex.printStackTrace();
									} catch (Exception e) {
										e.printStackTrace();
									}
									// m111=output+"status"+strURL+"#######"+exp;
									m111 = iStatus + "status" + strURL
											+ "#######" + exp;
									// System.out.println("operatorName1 "+operatorName);
									PreparedStatement psl11 = connection
											.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
									psl11.setString(1, todayDateApi);
									psl11.setString(2, todayDateTimeApi);
									psl11.setString(3, m111);
									psl11.setString(4, ip);
									psl11.setString(5, key);
									int resapi11 = psl11.executeUpdate();

									if (rs13.next()) {
										service_sms_contact = rs13.getString("contact");
										PreparedStatement sms = connection
												.prepareStatement("insert into sms_api (date,date_time,sender_id,contact,msg,status) values (?,?,?,?,?,?)");
										sms.setString(1, todayDateApi);
										sms.setString(2, todayDateTimeApi);
										sms.setString(3, sender_id);
										sms.setString(4, service_sms_contact);
										sms.setString(5, strMsg1);
										sms.setString(6, iStatus);
										sms.execute();
									} else {
										PreparedStatement sms = connection
												.prepareStatement("insert into sms_api (date,date_time,sender_id,contact,msg,status) values (?,?,?,?,?,?)");
										sms.setString(1, todayDateApi);
										sms.setString(2, todayDateTimeApi);
										sms.setString(3, sender_id);
										sms.setString(4, mobile_op);
										sms.setString(5, strMsg1);
										sms.setString(6, iStatus);
										sms.execute();
									}																		

									// callURL("http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=user_id:password&senderID=sender_id&receipientno=9866740479&msgtxt="+txt+"&state=4");
									// /(http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=pridhvi@msn.com:activa1525@&senderID=SSSBUS&receipientno=9676197117&msgtxt="++"&state=4)

								}
							}

							if (res == 1) {
								/* updating agent balance */

								// float newbal=balance+ticketAmountAfterMargin;
								PreparedStatement ps13 = connection
										.prepareStatement("update agents_operator set balance=? where id=? and agent_type=?");
								ps13.setFloat(1, postPaidAmt);
								ps13.setInt(2, agentId);
								ps13.setInt(3, 3);
								int rs13 = ps13.executeUpdate();
								/*
								 * inserting into account table for report
								 * generation
								 */
								PreparedStatement ps14 = connection
										.prepareStatement("insert into master_pass_reports(tktno,pnr,pass_name,source,destination,date,transtype,tkt_fare,comm,net_amt,bal,dat,ip,agent_id,travel_id,status,jdate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
								ps14.setString(1, tktNo);
								ps14.setInt(2, pnr);
								ps14.setString(3, names);
								ps14.setString(4, FromName);
								ps14.setString(5, ToName);
								ps14.setString(6, currentDateTime);
								ps14.setString(7, "Debit");
								ps14.setFloat(8, fare);
								ps14.setFloat(9, marginAmt);
								ps14.setFloat(10, ticketAmountAfterMargin);
								ps14.setFloat(11, postPaidAmt);
								ps14.setString(12, date);
								ps14.setString(13, ip);
								ps14.setInt(14, agentId);
								ps14.setInt(15, travel_id);
								ps14.setString(16, "confirmed");
								ps14.setString(17, date);
								int rs14 = ps14.executeUpdate();
								// feeding to response obj
								tdet.setTicket_status("confirmed");
								tdet.setTicket_number(tktNo);
								tdet.setPnr_number(pnr);
								tdet.setTravel_id(travel_id);
								tdet.setTravel_name(operatorName);
								tdet.setOrigin(FromName);
								tdet.setDestination(ToName);
								tdet.setJourney_date(date);
								tdet.setNo_of_seats(seatsCount);
								tdet.setSeat_numbers(seats);
								tdet.setService_number(srvno);
								tdet.setBus_type(busModel);
								tdet.setDep_time(startTime);
								tdet.setBoarding_point(bpName);
								tdet.setLandmark(bpArray[2]);
								tdet.setDropping_point(dropPoint);
								tdet.setTotal_fare(fare);
								tdet.setGender(genders);
								tdet.setPassenger_name(names);
								tdet.setPassenger_mobile(mobile);
								tdet.setPassenger_email(email);
								tdet.setPassenger_age(ages);
								seatRes.setTicket_details(tdet);
								rl.setString(1, todayDateApi);
								rl.setString(2, todayDateTimeApi);
								rl.setString(3, m);
								rl.setInt(4, 30);
								String response_logs = "{ticket_details:{ticket_status:confirmed,boarding_point:"
										+ bpName
										+ ",landmark:"
										+ bpArray[2]
										+ ",ticket_number:"
										+ tktNo
										+ ",pnr_number:"
										+ pnr
										+ ",travel_id:"
										+ travel_id
										+ ",origin:"
										+ FromName
										+ ",destination:"
										+ ToName
										+ ",journey_date:"
										+ date
										+ ",no_of_seats:"
										+ seatsCount
										+ ",seat_numbers:" + seats + ",travel_name:"+ operatorName
										+ ",service_number:"
										+ srvno
										+ ",bus_type:"
										+ busModel
										+ ",dep_time:"
										+ startTime
										+ ",total_fare:"
										+ fare
										+ ",gender:"
										+ genders
										+ ",passenger_name:"
										+ names
										+ ",passenger_age:"
										+ ages
										+ ",passenger_mobile:"
										+ mobile +",passenger_email:"+ email +"}}";
								rl.setString(5, "success " + response_logs);
								rl.setString(6, ip);
								rl.setString(7, key);
								rl.executeUpdate();
								return seatRes;
							} else {
								rl.setString(1, todayDateApi);
								rl.setString(2, todayDateTimeApi);
								rl.setString(3, m);
								rl.setInt(4, 30);
								rl.setString(
										5,
										"Internal Server Error!"
												+ ps4.toString());
								rl.setString(6, ip);
								rl.setString(7, key);
								rl.executeUpdate();
								response.setCode(500);
								response.setMessage("Internal Server Error!");
								seatRes.setResponseCodes(response);
								return seatRes;
							}
						}// sync
					} catch (Exception e) {
						System.out
								.println("Error In : DAO-getSeatConfirmation() method : "
										+ e.getMessage()
										+ " at Line "
										+ Thread.currentThread()
												.getStackTrace()[2]
												.getLineNumber());
						// log.error("Error In : DAO-getSeatConfirmation() method : "+e.getMessage()+" at Line "+Thread.currentThread().getStackTrace()[2].getLineNumber()+"from api:"+key+" IP:"+ip);
						rl.setString(1, todayDateApi);
						rl.setString(2, todayDateTimeApi);
						rl.setString(3, m);
						rl.setInt(4, 31);
						rl.setString(5, "Invalid Parameter!");
						rl.setString(6, ip);
						rl.setString(7, key);
						rl.executeUpdate();
						response.setCode(412);
						response.setMessage("Invalid Parameter!");
						seatRes.setResponseCodes(response);
						return seatRes;
					}
				

			} else {// seats count mismatch
					// log.info("seats count mismatch dao-getPnrFortentativeBooking() from ip:"+ip+" and api:"+key);
				rl.setString(1, todayDateApi);
				rl.setString(2, todayDateTimeApi);
				rl.setString(3, m);
				rl.setInt(4, 33);
				rl.setString(5, "Seats count Mismatch!");
				rl.setString(6, ip);
				rl.setString(7, key);
				rl.executeUpdate();
				response.setCode(403);
				response.setMessage("Seats count Mismatch!");
				seatRes.setResponseCodes(response);
				return seatRes;
			}
			// return seatRes;
		} else {
			// log.info("Authentication Failed For DAO-isTicketCancellableDb() from ip:"+ip+" and api:"+key);
			rl.setString(1, todayDateApi);
			rl.setString(2, todayDateTimeApi);
			rl.setString(3, m);
			rl.setInt(4, 34);
			rl.setString(5, "Authentication failed");
			rl.setString(6, ip);
			rl.setString(7, key);
			rl.executeUpdate();
			response.setCode(401);
			response.setMessage("Authentication failed");
			seatRes.setResponseCodes(response);
			return seatRes;
		}
	}

	/**
	 * This method generates PNR number and blocks the requested seats.
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return pnr number or status code.
	 */

	@SuppressWarnings("unused")
	public SeatBlockingResponse getPnrFortentativeBooking(
			Connection connection, String key, int from, int to, String date,
			String srvno, HttpServletRequest request, SeatBlockingRequest obj)
			throws Exception, SQLException {

		SeatBlockingResponse seatRes = new SeatBlockingResponse();
		ResponseCodes response = new ResponseCodes();
		/*
		 * String ipAdd=request.getRemoteAddr(); InetAddress ip1; ip1 =
		 * InetAddress.getLocalHost(); String ip = ip1.getHostAddress();
		 */
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// logging information storing into logs_api table
		String m = "seatBlocking()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		int resapi = psl.executeUpdate();
		// log.info("DAO-getPnrFortentativeBooking() Called from ip:"+ip+" api:"+key);
		boolean valid = this.authenticate(key, ip);
		// System.out.println("ages are :"+obj.getAge());
		/* getting seat information */
		if (valid) {
			String seats = null;
			float fare = 0;
			int seatsCount = 0;
			String[] seatsArray;
			String[] genderArray;
			String genders = null;
			String pname = null;
			String pmobile = null;

			try {

				//saving passenger name,mobile no.
				pname = obj.getPname();
				pmobile = obj.getPmobile();
				//System.out.println("Pmobile  "+pmobile);
				//System.out.println("Pnme  "+pname);
				
				genders = obj.getSex();
				//System.out.println("genders "+genders);
				seats = obj.getSeat();
				//System.out.println("seats "+seats);
				seatsCount = obj.getNo_of_seats();
				//System.out.println("seatsCount "+seatsCount);
				fare = obj.getFare();
				//System.out.println("fare "+fare);
				seatsArray = seats.split(",");
				genderArray = genders.split(",");

				/* validations */
				/*if (pname == null) {
					response.setCode(423);
					response.setMessage("Invalid Parameter or Passenger Name is missing!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (pmobile == null) {
					response.setCode(423);
					response.setMessage("Invalid Parameter or Passenger Mobile Number is missing!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}*/
				if (from == to) {
					response.setCode(423);
					response.setMessage("The origin and destination should not be same");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (from == 0 || to == 0) {
					response.setCode(413);
					response.setMessage("Invalid Origin or Destination");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (date == null || from == 0 || to == 0 || key == null
						|| srvno == null || seatsCount == 0) {
					response.setCode(400);
					response.setMessage("Invalid Parameter Name or Parameter value is NULL");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (fare == 0) {
					response.setCode(408);
					response.setMessage("Invalid Parameter or Ticket Fare is Zero!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (seats == null) {
					response.setCode(412);
					response.setMessage("Invalid Parameter!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				if (genders == null) {
					response.setCode(414);
					response.setMessage("Invalid Parameter!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				// convering input date string type to date format
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date input_date;
				// checking the input date format
				try {
					formatter.applyPattern("yyyy-MM-dd");
					formatter.setLenient(false);
					input_date = formatter.parse(date);
				} catch (ParseException e) {
					response.setCode(422);
					response.setMessage("Invalid Date or please check the date format yyyy-mm-dd");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				// current date
				Date cdate = new Date();
				String tdate = formatter.format(cdate);
				Date today_date = formatter.parse(tdate);
				// if input date is lesser than current date
				if (input_date.compareTo(today_date) < 0) {
					response.setCode(415);
					response.setMessage("Date should be Equal or Greater than "
							+ tdate);
					seatRes.setResponseCodes(response);
					return seatRes;
				}

				PreparedStatement ps123 = connection
						.prepareStatement("select status from buses_list where service_num=? and from_id=? and to_id=? and journey_date=?");
				ps123.setString(1, srvno);
				ps123.setInt(2, from);
				ps123.setInt(3, to);
				ps123.setString(4, date);
				ResultSet rs123 = ps123.executeQuery();
				String service_status = null;
				if (rs123.next()) {
					service_status = rs123.getString("status");

				}
				if (service_status.equals("2")) {
					response.setCode(415);
					response.setMessage("Service is Temporarily not available");
					seatRes.setResponseCodes(response);
					return seatRes;
				} else if (service_status.equals("0")) {
					response.setCode(415);
					response.setMessage("Service not Available");
					seatRes.setResponseCodes(response);
					return seatRes;
				}

			} catch (NullPointerException e) {
				// log.info("Error In : DAO - getPnrFortentativeBooking()"+e.getMessage()+"#ip-"+ip+"#key-"+key);
				response.setCode(400);
				response.setMessage("Invalid parameter name !");
				seatRes.setResponseCodes(response);
				return seatRes;
			}
			/*System.out.println("length"+seatsArray.length);
			System.out.println("length"+seatsCount);
			System.out.println("length"+genderArray.length);*/
			
			if (seatsArray.length == seatsCount
					&& genderArray.length == seatsCount) {

				int travel_id = 0;
				ArrayList<Integer> myList = new ArrayList<Integer>();
				ArrayList<String> bookedSeatsArray = new ArrayList<String>();
				ArrayList<String> blockedSeatsArray = new ArrayList<String>();
				String seatType = null;
				float totalSeatFare = 0;
				float totalSeatFare1 = 0;
				int singleSeatFare = 0;

				/*
				 * float seat_fare=0; float ub_fare=0; float lb_fare=0;
				 */
				String seater = "s";
				String lsleeper = "l";
				String lsleeper1 = "l:s";
				String lsleeper2 = "l:b";
				String usleeper = "u";
				float seat_fare = 0;
				float seat_fare1 = 0;
				String changedSeatFare = null;
				float discount_amount = 0;
				float fare1 = 0;
				float service_tax = 0;
				float service_tax_amount = 0;
				String isac = null;
				float cgst = 0;
				float sgst = 0;
				float tcs = 0;
				float cgst_amount = 0;
				float sgst_amount = 0;
				float tcs_amount = 0;
				float margin = 0;
				float agent_commission = 0;
				String comm_type = null;
				List<Integer> cityOrderIdList = null;
				List<String> blockedStagesList = new ArrayList<String>();
				String stageOrder= null;
				String availableStages = null;
				int seatSts = 2;
				int p =1;
				String blockedPnr = null;
				
				DecimalFormat df = new DecimalFormat("#.##");

				for (int i = 0; i < seatsArray.length; i++) {
					//System.out.println("i"+i);
					seatType = null;
					// gender should be M or F -- checking
					if (genderArray[i].equalsIgnoreCase("M")
							|| genderArray[i].equalsIgnoreCase("F")) {

						PreparedStatement ps = connection
								.prepareStatement("select * from layout_list where seat_name=? and service_num=? and journey_date=? ");
						ps.setString(1, seatsArray[i]);
						ps.setString(2, srvno);
						ps.setString(3, date);
						// ps.setInt(4, 0);
						// ps.setInt(5, 2);
						 //System.out.println(ps +"ps");
						ResultSet rs = ps.executeQuery();
						
						if (rs.next() == false) {
							response.setCode(414);
							response.setMessage("Invalid Parameter !");
							seatRes.setResponseCodes(response);
							return seatRes;
						}
						rs.previous();
						while (rs.next()) {
							seatType = rs.getString("seat_type");
							travel_id = rs.getInt("travel_id");
							blockedStagesList.add(rs.getString("blocked_stages"));
							availableStages = rs.getString("available_stages");
							seatSts=rs.getInt("seat_status");
							blockedPnr = rs.getString("blocked_pnrs");
							
							if(p==1)
							{
								cityOrderIdList = getOrderIds( connection,from,to,travel_id,srvno);
								
							}
							p=2;							
							// System.out.println(seatType+"seatType");
							// getting updated fare from master_price
							PreparedStatement ps4 = connection
									.prepareStatement("select * from master_price where service_num=? and travel_id=? and journey_date=? and from_id=? and to_id=?");
							ps4.setString(1, srvno);
							ps4.setInt(2, travel_id);
							ps4.setString(3, date);
							ps4.setInt(4, from);
							ps4.setInt(5, to);
							 //System.out.println(ps4+"ps4");
							ResultSet rs4 = ps4.executeQuery();

							if (rs4.next()) {
								if (seatType.equalsIgnoreCase(seater)
										|| seatType.equalsIgnoreCase(lsleeper1)) {
									changedSeatFare = rs4
											.getString("seat_fare_changed");
									seat_fare = rs4.getFloat("seat_fare");
								}
								if (seatType.equalsIgnoreCase(lsleeper)
										|| seatType.equalsIgnoreCase(lsleeper2)) {
									changedSeatFare = rs4
											.getString("lberth_fare_changed");
									seat_fare = rs4.getFloat("lberth_fare");
								}
								if (seatType.equalsIgnoreCase(usleeper)) {
									changedSeatFare = rs4
											.getString("uberth_fare_changed");
									seat_fare = rs4.getFloat("uberth_fare");
								}

								/*
								 * seat_fare_changed =
								 * rs4.getString("seat_fare_changed");
								 * lberth_fare_changed =
								 * rs4.getString("lberth_fare_changed");
								 * uberth_fare_changed =
								 * rs4.getString("uberth_fare_changed");
								 */
								// System.out.println("fare"+seat_fare+"/"+changedSeatFare);
							} else {
								PreparedStatement ps6 = connection
										.prepareStatement("select * from master_price where service_num=? and travel_id=? and from_id=? and to_id=? and journey_date IS NULL");
								ps6.setString(1, srvno);
								ps6.setInt(2, travel_id);
								ps6.setInt(3, from);
								ps6.setInt(4, to);
								//System.out.println(ps6+"ps6");
								ResultSet rs6 = ps6.executeQuery();

								if (rs6.next()) {
									if (seatType.equalsIgnoreCase(seater)
											|| seatType
													.equalsIgnoreCase(lsleeper1)) {
										changedSeatFare = rs6
												.getString("seat_fare_changed");
										seat_fare = rs6.getFloat("seat_fare");
									}
									if (seatType.equalsIgnoreCase(lsleeper)
											|| seatType
													.equalsIgnoreCase(lsleeper2)) {
										changedSeatFare = rs6
												.getString("lberth_fare_changed");
										seat_fare = rs6
												.getFloat("lberth_fare");
									}
									if (seatType.equalsIgnoreCase(usleeper)) {
										changedSeatFare = rs6
												.getString("uberth_fare_changed");
										seat_fare = rs6
												.getFloat("uberth_fare");
									}
									// System.out.println("faredf"+seat_fare+"/"+changedSeatFare);
								}
							}
							if (changedSeatFare != null
									&& !changedSeatFare.isEmpty()) {
								String[] faredetarray = changedSeatFare
										.split("@");
								for (int j = 0; j < faredetarray.length; j++) {
									String[] faredetarray1 = faredetarray[j]
											.split("#");

									String fseatname = faredetarray1[0];
									String changedfare = faredetarray1[1];

									if (seatsArray[i]
											.equalsIgnoreCase(fseatname)) {
										seat_fare1 = Float
												.parseFloat(changedfare);
										break;
									} else {
										seat_fare1 = seat_fare;
									}
								}
							} else {
								seat_fare1 = seat_fare;
							}

							// getting discount type,discount from
							// master_discount
							PreparedStatement ps7 = connection
									.prepareStatement("select * from master_discount where service_num=? and travel_id=? and discount_date=?");
							ps7.setString(1, srvno);
							ps7.setInt(2, travel_id);
							ps7.setString(3, date);
							//System.out.println(ps7+"ps7");
							ResultSet rs7 = ps7.executeQuery();
							String discount_type = null;
							float discount = 0;
							float convenience_charge = 0;
							float convenience_charge1 = 0;
							String discount_for = null;

							if (rs7.next()) {
								discount_type = rs7.getString("discount_type");
								discount = rs7.getFloat("discount");
								discount_for = rs7.getString("discount_for");
							} else {
								PreparedStatement ps8 = connection
										.prepareStatement("select * from master_discount where service_num=? and travel_id=? and discount_date IS NULL");
								ps8.setString(1, srvno);
								ps8.setInt(2, travel_id);
								// System.out.println("ps8"+ps8.toString());
								//System.out.println(ps8+"ps8");
								ResultSet rs8 = ps8.executeQuery();

								if (rs8.next()) {
									discount_type = rs8
											.getString("discount_type");
									discount = rs8.getFloat("discount");
									discount_for = rs8
											.getString("discount_for");
								}

							}

							if (discount_for != null) {
								if (!discount_for.equalsIgnoreCase("web")) {
									if (discount_type != null
											&& !discount_type.isEmpty()) {
										if (discount_type
												.equalsIgnoreCase("percent")) {
											discount_amount = (seat_fare1 * discount) / 100;
										} else {
											discount_amount = discount;
										}
									}
								} else {
									discount_amount = Float.parseFloat("0.00");
								}
							} else {
								discount_amount = Float.parseFloat("0.00");
							}

							PreparedStatement ps9 = connection
									.prepareStatement("select * from master_buses where service_num=? and travel_id=?");
							ps9.setString(1, srvno);
							ps9.setInt(2, travel_id);
							//System.out.println(ps9+"ps9");
							ResultSet rs9 = ps9.executeQuery();
							if (rs9.next()) {
								service_tax = rs9.getFloat("service_tax");
								isac = rs9.getString("isac");
							}
							
							PreparedStatement ps10 = connection
									.prepareStatement("select convenience_charge,cgst,sgst,tcs from registered_operators where travel_id=?");
							ps10.setInt(1, travel_id);
							//System.out.println(ps10+"ps10");
							ResultSet rs10 = ps10.executeQuery();
							if (rs10.next()) {
								convenience_charge = rs10
										.getFloat("convenience_charge");
								cgst = rs10.getFloat("cgst");
								sgst = rs10.getFloat("sgst");
								tcs = rs10.getFloat("tcs");
							}						
							
							service_tax_amount = Float.parseFloat(df
									.format((seat_fare1 * service_tax) / 100));
							
							//System.out.println(service_tax_amount+"service_tax_amount");
							
							/*convenience_charge1 = Float
									.parseFloat(df
											.format((seat_fare1 * convenience_charge) / 100));*/
							
							//System.out.println(convenience_charge1+"convenience_charge1");
							
							PreparedStatement ps11 = connection
									.prepareStatement("select margin,comm_type from agents_operator where api_key=? and status=? and agent_type=?");
							ps11.setString(1, key);
							ps11.setInt(2, 1);
							ps11.setInt(3, 3);
							
							ResultSet rs11 = ps11.executeQuery();

							if (rs11.next()) {
								margin = rs11.getFloat("margin");
								comm_type = rs11.getString("comm_type");							
							}

							
							if(comm_type.equalsIgnoreCase("percent")) {
								agent_commission = Float.parseFloat(df
										.format((seat_fare1 * margin) / 100));
								convenience_charge1 = Float
										.parseFloat(df
												.format((agent_commission * convenience_charge) / 100));
							} else if(comm_type.equalsIgnoreCase("rupees")) {
								agent_commission = Float.parseFloat(df
										.format(margin));
								convenience_charge1 = Float
										.parseFloat(df
												.format((agent_commission * convenience_charge) / 100));
							}
							
							if (isac.equalsIgnoreCase("yes")) {
								cgst_amount = Float.parseFloat(df
										.format((seat_fare1 * cgst) / 100));
								sgst_amount = Float.parseFloat(df
										.format((seat_fare1 * sgst) / 100));
								tcs_amount = Float.parseFloat(df
										.format((seat_fare1 * tcs) / 100));
							}
							
							
							if(service_tax == 0 || service_tax == 0.0) {
								fare1 = Float.parseFloat(df.format(seat_fare1))
										+ service_tax_amount
										+ convenience_charge1
										+ cgst_amount
										+ sgst_amount
										- Float.parseFloat(df
												.format(discount_amount));
							} else {
								fare1 = Float.parseFloat(df.format(seat_fare1))
										+ service_tax_amount
										+ convenience_charge1										
										- Float.parseFloat(df
												.format(discount_amount));
							}
							//System.out.println(fare1+"fare1");
							// System.out.println(seat_fare1+"seat_fare");													

							totalSeatFare = totalSeatFare + fare1;
							//System.out.println(Float.parseFloat(df.format(totalSeatFare))+"totalSeatFare");
							totalSeatFare1 = Float.parseFloat(df
									.format(totalSeatFare));
							//System.out.println("seatsArray[i]"+seatsArray[i]+"seat_fare1 "+seat_fare1+" totalSeatFare "+totalSeatFare1+"fare"+fare);
							// travel_id= rs.getInt("travel_id");
							int busStatus = rs.getInt("status");
							int SeatStatus = rs.getInt("seat_status");
							int Available = rs.getInt("available");
							if (busStatus != 1) {
								response.setCode(402);
								response.setMessage("No services!");
								seatRes.setResponseCodes(response);
								return seatRes;
							}
							if (SeatStatus == 1 || Available != 0) {
								bookedSeatsArray.add(seatsArray[i]);
							}
							String BlockedTime = rs.getString("blocked_time");
							if(BlockedTime == null) {
								BlockedTime = "00-00-00 00:00:00";
							}
							boolean ck = this.releaseBlockedSeat(BlockedTime);
							// System.out.println(ck+" ck");
							if (SeatStatus == 2 && ck == false) {
								blockedSeatsArray.add(seatsArray[i]);
							}
							if ((SeatStatus == 0)
									|| (SeatStatus == 2 && ck == true)) {// seat
																			// is
																			// available
								// gender checking
								int rowno = rs.getInt("col");
								int colno = rs.getInt("row");
								int window = rs.getInt("window");
								int rowinc = rowno + 1;
								int rowdec = rowno - 1;
								PreparedStatement ps3 = connection
										.prepareStatement("select MIN(col) as mincol,MAX(col) as maxcol from layout_list where service_num=? and journey_date=?");
								ps3.setString(1, srvno);
								ps3.setString(2, date);
								int rowmax = 0;
								int rowmin = 0;
								int rowmininc = 0;
								ResultSet rs3 = ps3.executeQuery();
								while (rs3.next()) {
									rowmax = rs3.getInt("maxcol");
									rowmin = rs3.getInt("mincol");
									rowmininc = rowmin + 1;
								}

								// getting
								// 2#2#0#3#1#2#2#3
								// System.out.println(rowno+"#"+colno+"#"+window+"#"+rowinc+"#"+rowdec+"#"+rowmax+"#"+rowmin+"#"+rowmininc);
								// System.out.println("select is_ladies from layout_list where seat_name=? and service_num=? and journey_date=? and row=? and col=?");
								/*PreparedStatement ps2 = connection
										.prepareStatement("select is_ladies from layout_list where service_num=? and journey_date=? and row=? and col=? and seat_type=?");
								ps2.setString(1, srvno);
								ps2.setString(2, date);
								ps2.setInt(3, colno);
								if (window == 1 && rowno != 1) {
									ps2.setInt(4, rowdec);
								} else if (window != 1 && rowno == rowmininc) {
									ps2.setInt(4, rowdec);
								} else if (window == 1 && rowno == rowmax) {
									ps2.setInt(4, rowdec);
								} else {
									ps2.setInt(4, rowinc);
								}
								ps2.setString(5, seatType);
								ResultSet rs2 = ps2.executeQuery();
								while (rs2.next()) {
									int Ladies = rs2.getInt("is_ladies");
									myList.add(Ladies);
								}*/

							}
						}
					} else {// gender mistake
						response.setCode(407);
						response.setMessage("Invalid Gender Format!");
						seatRes.setResponseCodes(response);
						return seatRes;
					}
				}
				// fare mismathch
				int retval = Float.compare(totalSeatFare1, fare);
				// System.out.println("retval"+retval+"/"+totalSeatFare+"/"+fare);
				if (retval != 0) {
					response.setCode(409);
					response.setMessage("Ticket Fare mismatch!");
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				/*
				 * if(totalSeatFare>fare){ response.setCode(409);
				 * response.setMessage("Ticket Fare mismatch!");
				 * seatRes.setResponseCodes(response); return seatRes; }
				 */
				/* Blocking */
				String SG = null;
				String BlockedSeat = null;
				String BookedSeat = null;
				try {
					int blockedCount = blockedSeatsArray.size();
					// System.out.println("blockedCount"+blockedCount);
					for (int y = 0; y < blockedCount; y++) {
						if (BlockedSeat == null)
							BlockedSeat = blockedSeatsArray.get(y);
						else
							BlockedSeat = BlockedSeat + ","
									+ blockedSeatsArray.get(y);
					}
					if (BlockedSeat != null) {
						response.setCode(405);
						response.setMessage("Seat number(s) " + BlockedSeat
								+ " already reserved by other customer!");
						seatRes.setResponseCodes(response);
						return seatRes;
					}
					/* booked seats */
					int bookedCount = bookedSeatsArray.size();
					for (int z = 0; z < bookedCount; z++) {
						if (BookedSeat == null)
							BookedSeat = bookedSeatsArray.get(z);
						else
							BookedSeat = BookedSeat + ","
									+ bookedSeatsArray.get(z);
					}
					if (BookedSeat != null) {
						response.setCode(404);
						response.setMessage("Seat number(s) " + BookedSeat
								+ " already booked by other customer!");
						seatRes.setResponseCodes(response);
						return seatRes;
					}
					/* gender checking 
					int ALCount = myList.size();

					for (int k = 0; k < ALCount; k++) {
						if ((genderArray[k].equalsIgnoreCase("M"))
								&& ((Integer) myList.get(k) == 1)) {
							if (SG == null) {
								SG = seatsArray[k];
							} else {
								SG = SG + "," + seatsArray[k];
							}
						}
					}*/
				} catch (Exception e) {
					System.out.println("Exc" + e.getMessage());
					log.info("Error In : DAO - getPnrFortentativeBooking()"
							+ e.getMessage() + "#ip-" + ip + "#key-" + key);

					// TODO: handle exception
				}
				//if (SG == null) {
					try {
						synchronized (seats) {
							// checking the Agent Balance
							PreparedStatement ps8 = connection
									.prepareStatement("select id,operator_id,api_type,balance,bal_limit,margin,pay_type from agents_operator where api_key=? and status=? and agent_type=?");
							// ps8.setString(1, ip);
							ps8.setString(1, key);
							ps8.setInt(2, 1);
							ps8.setInt(3, 3);
							ResultSet rs8 = ps8.executeQuery();
							int operatorId = 0;
							String apiType = null;
							int agentId = 0;
							while (rs8.next()) {
								apiType = rs8.getString("api_type");
								operatorId = rs8.getInt("operator_id");
								agentId = rs8.getInt("id");
								float balance = rs8.getFloat("balance");
								float balanceLimit = rs8.getFloat("bal_limit");
								margin = rs8.getFloat("margin");
								String payType = rs8.getString("pay_type");
								float marginAmt = (fare * margin) / 100;
								float ticketAmountAfterMargin = 0;
								float postPaidAmt = 0;
								if (payType.equalsIgnoreCase("prepaid")) {
									postPaidAmt = balance - fare;
									if ((balance < fare) && (balanceLimit == 0)) {
										response.setCode(411);
										response.setMessage("Insufficient Balance!");
										seatRes.setResponseCodes(response);
										return seatRes;
									} else if ((balance < fare)
											&& (postPaidAmt < balanceLimit)) {
										response.setCode(412);
										response.setMessage("Your Balance Limit is Exceeded!");
										seatRes.setResponseCodes(response);
										return seatRes;
									}
								}
								if (payType.equalsIgnoreCase("postpaid")) {
									ticketAmountAfterMargin = fare - marginAmt;
									postPaidAmt = balance
											- ticketAmountAfterMargin;
									if ((balance < ticketAmountAfterMargin)
											&& (postPaidAmt < balanceLimit)) {
										response.setCode(412);
										response.setMessage("Your Balance Limit is Exceeded !");
										seatRes.setResponseCodes(response);
										return seatRes;
									}
								}
							}// while close
								// update status as
								// blocked(seat_status,blocked_time) in
								// layout_list
								// table
							DateFormat dateFormat = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date CurrentDate = new Date();
							String currentDateTime = dateFormat
									.format(CurrentDate);
							// get SELECT AUTO_INCREMENT FROM
							// information_schema.tables WHERE TABLE_NAME =
							// 'seat_blocking_det'
							// PreparedStatement
							// ps10=connection.prepareStatement("SELECT AUTO_INCREMENT FROM information_schema.tables WHERE TABLE_NAME =?");
							// ps10.setString(1, "seat_blocking_det");
							PreparedStatement ps10 = connection
									.prepareStatement("SELECT MAX(pnr) as pnr FROM seat_blocking_det");
							ResultSet rs10 = ps10.executeQuery();
							int autoId = 0;
							while (rs10.next()) {
								autoId = rs10.getInt("pnr");
							}
							int pnr = autoId + 1;
							String blockedStages =null;
							//get blocked stages list
							String blockeStages2 = getBlockedStagesForTentativeBooking(cityOrderIdList, connection, travel_id,availableStages,srvno);
							stageOrder = cityOrderIdList.get(0)+"-"+cityOrderIdList.get(1);
							
							if(StringUtils.isBlank(blockedPnr))
								blockedPnr = stageOrder+"#"+pnr;
							else
							blockedPnr = blockedPnr+"|"+stageOrder+"#"+pnr;
							
							for (int x = 0; x < seatsArray.length; x++) {
								
								
								PreparedStatement psupdt = connection
										.prepareStatement("update layout_list set is_ladies=?,seat_status=?,blocked_stages=?,blocked_pnrs=? where travel_id=? and  seat_name=? and service_num=? and journey_date=?");

								if (genderArray[x].equalsIgnoreCase("F"))// updating
																			// as
																			// female
																			// seat
									psupdt.setInt(1, 0);// female
								else
									psupdt.setInt(1, 0);// male

								if(seatSts == 4) psupdt.setInt(2, 4);
								else if(seatSts == 3)psupdt.setInt(2, 3);
								else psupdt.setInt(2, 2);
								
								if(StringUtils.isBlank(blockedStagesList.get(x)))
									blockedStages=blockeStages2+"#"+currentDateTime;
								else blockedStages = blockedStagesList.get(x)+"|"+blockeStages2+"#"+currentDateTime;
									
								psupdt.setString(3, blockedStages);
								//psupdt.setString(4, currentDateTime);
								psupdt.setString(4, blockedPnr);
								psupdt.setInt(5, travel_id);
								psupdt.setString(6, seatsArray[x]);
								psupdt.setString(7, srvno);
								psupdt.setString(8, date);
								if (psupdt.executeUpdate() == 0)// updated
																// successfully
								{
									response.setCode(500);
									response.setMessage("Internal Server Error!");
									seatRes.setResponseCodes(response);
									return seatRes;
								}
							}// for
							PreparedStatement ps5 = connection
									.prepareStatement("select city_name from master_cities where city_id=? ");
							ps5.setInt(1, from);
							ResultSet rs5 = ps5.executeQuery();
							String FromName = null;
							while (rs5.next()) {
								FromName = rs5.getString("city_name");
							}
							PreparedStatement ps6 = connection
									.prepareStatement("select city_name from master_cities where city_id=? ");
							ps6.setInt(1, to);
							ResultSet rs6 = ps6.executeQuery();
							String ToName = null;
							while (rs6.next()) {
								ToName = rs6.getString("city_name");
							}
							// insert into seat_blocking_det
							PreparedStatement ps4 = connection
									.prepareStatement("insert into seat_blocking_det(api_key,ip,from_id,to_id,from_name,to_name,service_num,journey_date,seats,gender,travel_id,agent_id,api_type,status,fare,pname,pmobile)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							ps4.setString(1, key);
							ps4.setString(2, ip);
							ps4.setInt(3, from);
							ps4.setInt(4, to);
							ps4.setString(5, FromName);
							ps4.setString(6, ToName);
							ps4.setString(7, srvno);
							ps4.setString(8, date);
							ps4.setString(9, seats);
							ps4.setString(10, genders);
							ps4.setInt(11, travel_id);
							ps4.setInt(12, agentId);
							ps4.setString(13, apiType);
							ps4.setString(14, "blocked");
							ps4.setFloat(15, fare);
							ps4.setString(16, pname);
							ps4.setString(17, pmobile);
							int res = ps4.executeUpdate();
							if (res == 1 && pnr != 0) {
								SendPNR pnrdet = new SendPNR();
								response.setCode(200);
								response.setMessage("success");
								pnrdet.setPnr(pnr);
								pnrdet.setStatus("blocked");
								seatRes.setPnrDetails(pnrdet);
								seatRes.setResponseCodes(response);

								return seatRes;
							} else {

								response.setCode(500);
								response.setMessage("Internal Server Error!");
								seatRes.setResponseCodes(response);
								return seatRes;
							}
						}// sync
					} catch (Exception e) {
						System.out
								.println("Error In : DAO - getPnrFortentativeBooking()"
										+ e.getMessage());
						// log.info("Error In : DAO - getPnrFortentativeBooking()"+e.getMessage()+"#ip-"+ip+"#key-"+key);
						response.setCode(412);
						response.setMessage("Invalid Parameter!");
						seatRes.setResponseCodes(response);
						return seatRes;
					}
				

			} else {// seats count mismatch
					// log.info("seats count mismatch dao-getPnrFortentativeBooking() from ip:"+ip+" and api:"+key);
				response.setCode(403);
				response.setMessage("Seats count Mismatch!");
				seatRes.setResponseCodes(response);
				return seatRes;
			}
		} else {
			// log.info("Authentication Failed For DAO-isTicketCancellableDb() from ip:"+ip+" and api:"+key);
			response.setCode(401);
			response.setMessage("Authentication failed");
			seatRes.setResponseCodes(response);
			return seatRes;
		}
		// return seatRes;

	}

	/**
	 * This method checks the cancellation details.
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return cancellation chares details or status code.
	 */
	public IsTicketCancellableResponse isTicketCancellableDb(
			Connection connection, String key, String tktno, String jDate,
			HttpServletRequest request) throws Exception {

		/*
		 * String ipAdd=request.getRemoteAddr(); InetAddress ip1; ip1 =
		 * InetAddress.getLocalHost(); String ip = ip1.getHostAddress();
		 */
		CancellationDetails cdet = new CancellationDetails();
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		IsTicketCancellableResponse sd = new IsTicketCancellableResponse();
		// logging information storing into logs_api table
		String m = "isCancellable()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement rl = connection
				.prepareStatement("insert into response_logs (date,date_time,method_called,series,response,ip,api_key) values (?,?,?,?,?,?,?)");

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		int resapi = psl.executeUpdate();

		// log.info("DAO--isTicketCancellableDb() Called from ip:"+ip+" api:"+key);
		ResponseCodes response = new ResponseCodes();
		boolean valid = this.authenticate(key, ip);
		if (valid)// valid call
		{
			try {

				if (jDate == null) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 1);
					rl.setString(5,
							"Invalid Parameter Name or Parameter value is NULL");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(400);
					response.setMessage("Invalid Parameter Name or Parameter value is NULL");
					sd.setResponseCodes(response);
					return sd;
				}
				// convering input date string type to date format
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
				Date input_date;
				// checking the input date format
				try {
					formatter.applyPattern("yyyy-MM-dd");
					formatter.setLenient(false);
					input_date = formatter.parse(jDate);
				} catch (ParseException e) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 2);
					rl.setString(5,
							"Invalid Date or please check the date format yyyy-mm-dd");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(422);
					response.setMessage("Invalid Date or please check the date format yyyy-mm-dd");
					sd.setResponseCodes(response);
					return sd;
				}
				// current date
				Date cdate = new Date();
				String tdate = formatter.format(cdate);
				Date today_date = formatter.parse(tdate);
				// if input date is lesser than current date
				if (input_date.compareTo(today_date) < 0) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 3);
					rl.setString(5, "Date should be Equal or Greater than "
							+ tdate);
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(415);
					response.setMessage("Date should be Equal or Greater than "
							+ tdate);
					sd.setResponseCodes(response);
					return sd;
				}
				/* Getting Journey Details from master_booking */
				PreparedStatement ps = connection
						.prepareStatement("SELECT * FROM master_booking where tkt_no=? and  jdate=?");
				ps.setString(1, tktno);
				ps.setString(2, jDate);
				ResultSet rs = ps.executeQuery();
				int travelID = 0;
				String journeyDate = null;
				String startTime = null;
				String seats = null;
				String reschedule = null;
				String service_no = null;
				float fare = 0;
				float base_fare = 0;
				float discount_amount = 0;

				if (rs.next() == false) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 4);
					rl.setString(5, "Invalid Ticket Number or Journey Date!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(427);
					response.setMessage("Invalid Ticket Number or Journey Date!"); // if(!rs.isBeforeFirst())
					sd.setResponseCodes(response);
					return sd;
				}
				rs.previous();
				while (rs.next()) {
					travelID = rs.getInt("travel_id");
					journeyDate = rs.getString("jdate");
					startTime = rs.getString("start_time");
					seats = rs.getString("seats");
					fare = rs.getFloat("tkt_fare");
					base_fare = rs.getFloat("base_fare");
					discount_amount = rs.getFloat("discount_amount");
					reschedule = rs.getString("reschedule");
					service_no = rs.getString("service_no");
				}

				if (reschedule == "reschedule") {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 5);
					rl.setString(
							5,
							"No Cancellation For this Ticket due to Rescheduled,contact operator for clarification!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(401);
					response.setMessage("No Cancellation For this Ticket due to Rescheduled,contact operator for clarification!"); // if(!rs.isBeforeFirst())
					sd.setResponseCodes(response);
					return sd;
				}

				/*
				 * if(travelID == 38 && journeyDate.equals("2015-09-20")) {
				 * response.setCode(427); response.setMessage(
				 * "Cancellation Not Allowed For This Service on "+journeyDate);
				 * // if(!rs.isBeforeFirst()) sd.setResponseCodes(response);
				 * return sd; }
				 */

				PreparedStatement ps2 = connection
						.prepareStatement("SELECT * FROM registered_operators where travel_id=?");
				ps2.setInt(1, travelID);
				ResultSet rs2 = ps2.executeQuery();
				String cancTerms = null;
				String jTime = journeyDate + " " + startTime;

				PreparedStatement mt = connection
						.prepareStatement("select distinct canc_terms from master_terms where service_num=? and travel_id=? and terms_date=?");
				mt.setString(1, service_no);
				mt.setInt(2, travelID);
				mt.setString(3, journeyDate);

				ResultSet mtrs = mt.executeQuery();

				if (mtrs.next()) {
					cancTerms = mtrs.getString("canc_terms");
				} else {
					PreparedStatement mt1 = connection
							.prepareStatement("select distinct canc_terms from master_terms where service_num=? and travel_id=? and terms_date IS NULL");
					mt1.setString(1, service_no);
					mt1.setInt(2, travelID);

					// System.out.println("Before : " + mt1.toString());
					ResultSet mtrs1 = mt1.executeQuery();

					if (mtrs1.next()) {
						cancTerms = mtrs1.getString("canc_terms");
					} else if (rs2.next()) {
						cancTerms = rs2.getString("canc_terms");
					}
				}
				if (cancTerms.equalsIgnoreCase("no")) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 6);
					rl.setString(5,
							"Cancellation Not Allowed For This Service On "
									+ journeyDate);
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(427);
					response.setMessage("Cancellation Not Allowed For This Service On "
							+ journeyDate); // if(!rs.isBeforeFirst())
					sd.setResponseCodes(response);
					return sd;
				} else {
					int canCharge2 = this.getTimeDiff(jTime, cancTerms);
					String canCharge = canCharge2 + "%";
					float canAmt = (canCharge2 * (base_fare + discount_amount)) / 100;
					float refAmt = base_fare - canAmt;
					response.setCode(200);
					response.setMessage("success");
					cdet.setRefund_amount(refAmt);
					cdet.setCancellation_amount(canAmt);
					cdet.setCancellation_charges(canCharge);
					cdet.setSeat_numbers(seats);
					sd.setResponseCodes(response);
					sd.setCancel_details(cdet);
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 7);
					String response_logs = "{response:{code:200,message:success,cancel_details:{refund_amount:"
							+ refAmt
							+ ",cancellation_amount:"
							+ canAmt
							+ ",cancellation_charges:"
							+ canCharge
							+ ",seat_numbers:"
							+ seats +"}}";
					rl.setString(5, "success " + response_logs);
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					return sd;
				}
			} catch (SQLException ie) {
				// log.error("SQLException in DAO-isTicketCancellableDb() from ip:"+ip+" and key"+key);
				// System.out.println("Error Code" + ie.getErrorCode());
				// System.out.println("Error Message" + ie.getMessage());
				rl.setString(1, todayDateApi);
				rl.setString(2, todayDateTimeApi);
				rl.setString(3, m);
				rl.setInt(4, 8);
				rl.setString(5, "Internal Server Error " + ie.getMessage());
				rl.setString(6, ip);
				rl.setString(7, key);
				rl.executeUpdate();
				response.setCode(500);
				response.setMessage("Internal Server Error");
				sd.setResponseCodes(response);
				return sd;
			} catch (Exception e) {
				// log.error("Exception in DAO-isTicketCancellableDb() from ip:"+ip+" and key"+key);
				// System.out.println("Error Message" + e.getMessage());
				rl.setString(1, todayDateApi);
				rl.setString(2, todayDateTimeApi);
				rl.setString(3, m);
				rl.setInt(4, 9);
				rl.setString(5, "Internal Server Error " + e.getMessage());
				rl.setString(6, ip);
				rl.setString(7, key);
				rl.executeUpdate();
				response.setCode(500);
				response.setMessage("Internal Server Error!");
				sd.setResponseCodes(response);
				return sd;
			}
		}// close if
		else {
			// log.info("Authentication Failed For DAO-isTicketCancellableDb() from ip:"+ip+" and api:"+key);
			rl.setString(1, todayDateApi);
			rl.setString(2, todayDateTimeApi);
			rl.setString(3, m);
			rl.setInt(4, 10);
			rl.setString(5, "Authentication failed");
			rl.setString(6, ip);
			rl.setString(7, key);
			rl.executeUpdate();
			response.setCode(401);
			response.setMessage("Authentication failed");
			sd.setResponseCodes(response);
			return sd;
		}// close else
	}// close getServices(---)

	/**
	 * This method returns the cancellation charge percentage based on current
	 * and journey time. returns cancellation charge
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return true or false.
	 */
	public int getTimeDiff(String jTime, String cancTerms) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		String currentDateTime = dateFormat.format(date);
		Date d1 = null;
		Date d2 = null;
		// 00#03#100@03#12#50@12#24#10
		String[] cancTermsArray = cancTerms.split("@");
		String[] cancTermsArray2;
		// int fromTime=0;
		// int toTime=0;
		int canCharge = 100;
		try {
			d2 = dateFormat.parse(currentDateTime);
			d1 = dateFormat.parse(jTime);
			DateTime dt1 = new DateTime(d2);
			DateTime dt2 = new DateTime(d1);
			int minutes = Minutes.minutesBetween(dt1, dt2).getMinutes() % 60;
			int days = Days.daysBetween(dt1, dt2).getDays();
			int hours = Hours.hoursBetween(dt1, dt2).getHours() % 24;
			int totalTime = minutes + (hours * 60) + (days * 24 * 60);
			// 00#03#100@03#12#50@12#24#10
			for (int i = 0; i < cancTermsArray.length; i++) {// 00#03#100
				cancTermsArray2 = cancTermsArray[i].split("#"); // 00 03 100
				int fromTime = Integer.parseInt(cancTermsArray2[0]) * 60;// in
																			// minutes
				int toTime = Integer.parseInt(cancTermsArray2[1]) * 60;// in
																		// minutes
				canCharge = Integer.parseInt(cancTermsArray2[2]);
				// System.out.println(fromTime+"-"+"-"+toTime+"-"+totalTime+"-"+canCharge);//180-720-336
				if (fromTime < totalTime && totalTime < toTime) {
					return canCharge;					
				}
			}
			return canCharge;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return canCharge;
	}

	/**
	 * This method checks the cancellation details and cancels the ticket.
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return cancellation chares details and cancel the ticket or status code.
	 */
	public CancelTicketResponse cancelTicketDb(Connection connection,
			String key, String tktno, String jDate, HttpServletRequest request)
			throws Exception {
		/*
		 * String ipAdd=request.getRemoteAddr(); InetAddress ip1; ip1 =
		 * InetAddress.getLocalHost(); String ip = ip1.getHostAddress();
		 */

		CancellationDetails cdet = new CancellationDetails();
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		CancelTicketResponse sd = new CancelTicketResponse();
		// logging information storing into logs_api table
		String m = "cancelTicket()";
		DateFormat dateFormatApi = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateFormatApi1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// get current date time with Date()
		Date dateForApi = new Date();
		String todayDateApi = dateFormatApi.format(dateForApi);
		String todayDateTimeApi = dateFormatApi1.format(dateForApi);
		// System.out.println("date is "+todayDateApi);
		// System.out.println("date and time is "+todayDateTimeApi);

		PreparedStatement rl = connection
				.prepareStatement("insert into response_logs (date,date_time,method_called,series,response,ip,api_key) values (?,?,?,?,?,?,?)");

		PreparedStatement psl = connection
				.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
		psl.setString(1, todayDateApi);
		psl.setString(2, todayDateTimeApi);
		psl.setString(3, m);
		psl.setString(4, ip);
		psl.setString(5, key);
		int resapi = psl.executeUpdate();
		// log.info("DAO-cancelTicketDb() Called from ip:"+ip+" api:"+key);
		// logs
		// current date
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date cdate = new Date();
		String tdate = formatter.format(cdate);
		Date today_date = formatter.parse(tdate);
		PreparedStatement ps0 = connection
				.prepareStatement("insert into cancellation_logs (date,method,tktno,jdate,ip)values(?,?,?,?,?)");
		ps0.setString(1, tdate);
		ps0.setString(2, "DAO-cancelTicketDb");
		ps0.setString(3, tktno);
		ps0.setString(4, jDate);
		ps0.setString(5, ip);
		ps0.executeUpdate();
		ResponseCodes response = new ResponseCodes();
		boolean valid = this.authenticate(key, ip);
		List<Integer> cityOrderIdList;
		String stageorder="";
		if (valid)// valid call
		{
			try {

				if (jDate == null) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 1);
					rl.setString(5,
							"Invalid Parameter Name or Parameter value is NULL");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(400);
					response.setMessage("Invalid Parameter Name or Parameter value is NULL");
					sd.setResponseCodes(response);
					return sd;
				}
				// convering input date string type to date format
				Date input_date;
				// checking the input date format
				try {
					formatter.applyPattern("yyyy-MM-dd");
					formatter.setLenient(false);
					input_date = formatter.parse(jDate);
				} catch (ParseException e) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 2);
					rl.setString(5,
							"Invalid Date or please check the date format yyyy-mm-dd");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(422);
					response.setMessage("Invalid Date or please check the date format yyyy-mm-dd");
					sd.setResponseCodes(response);
					return sd;
				}

				// if input date is lesser than current date
				if (input_date.compareTo(today_date) < 0) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 3);
					rl.setString(5, "Date should be Equal or  Greater than "
							+ tdate);
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(415);
					response.setMessage("Date should be Equal or  Greater than "
							+ tdate);
					sd.setResponseCodes(response);
					return sd;
				}
				/* Getting Journey Details from master_booking */
				PreparedStatement ps = connection
						.prepareStatement("SELECT * FROM master_booking where tkt_no=? and  jdate=?");
				ps.setString(1, tktno);
				ps.setString(2, jDate);
				ResultSet rs = ps.executeQuery();
				int travelID = 0;
				String journeyDate = null;
				String startTime = null;
				String seats = null;
				String tktNo = null;
				String pnr = null;
				String serviceNumber = null;
				String boardPoint = null;
				String bpid = null;
				String landmark = null;
				String source = null;
				String dest = null;
				String travels = null;
				String busType = null;
				String bDate = null;
				String gender = null;
				String arrTime = null;
				float paid = 0;
				float save = 0;
				String passengerName = null;
				String passengerEmail = null;
				String passengerMobile = null;
				String refno = null;
				String age = null;
				int pass = 0;
				String time = null;
				String idType = null;
				String idNumber = null;
				String address = null;
				String alternateMobile = null;
				String status = null;
				int fid = 0;
				int tid = 0;
				int operator_agent_type = 0;
				int agent_id = 0;
				String bus_model = null;
				String reschedule = null;

				float fare = 0;
				float base_fare = 0;
				float service_tax_amount = 0;
				float discount_amount = 0;
				float convenience_charge = 0;
				float cgst = 0;
				float sgst = 0;
				float tcs = 0;

				if (rs.next() == false) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 4);
					rl.setString(5, "Invalid Ticket Number or Journey Date!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(427);
					response.setMessage("Invalid Ticket Number or Journey Date!"); // if(!rs.isBeforeFirst())
					sd.setResponseCodes(response);
					return sd;
				}
				rs.previous();
				while (rs.next()) {
					tktNo = rs.getString("tkt_no");
					pnr = rs.getString("pnr");
					serviceNumber = rs.getString("service_no");
					boardPoint = rs.getString("board_point");
					bpid = rs.getString("bpid");
					landmark = rs.getString("land_mark");
					source = rs.getString("source");
					dest = rs.getString("dest");
					travels = rs.getString("travels");
					busType = rs.getString("bus_type");
					bDate = rs.getString("bdate");
					journeyDate = rs.getString("jdate");
					seats = rs.getString("seats");
					gender = rs.getString("gender");
					startTime = rs.getString("start_time");
					arrTime = rs.getString("arr_time");
					paid = rs.getFloat("paid");
					save = rs.getFloat("save");
					fare = rs.getFloat("tkt_fare");
					base_fare = rs.getFloat("base_fare");
					service_tax_amount = rs.getFloat("service_tax_amount");
					discount_amount = rs.getFloat("discount_amount");
					convenience_charge = rs.getFloat("convenience_charge");
					passengerName = rs.getString("pname");
					passengerEmail = rs.getString("pemail");
					passengerMobile = rs.getString("pmobile");
					age = rs.getString("age");
					refno = rs.getString("refno");
					status = rs.getString("status");
					pass = rs.getInt("pass");
					travelID = rs.getInt("travel_id");
					// ip
					time = rs.getString("time");
					idType = rs.getString("id_type");
					idNumber = rs.getString("id_num");
					address = rs.getString("padd");
					alternateMobile = rs.getString("alter_ph");
					fid = rs.getInt("fid");
					tid = rs.getInt("tid");
					operator_agent_type = rs.getInt("operator_agent_type");
					agent_id = rs.getInt("agent_id");
					bus_model = rs.getString("bus_model");
					reschedule = rs.getString("reschedule");
					cgst = rs.getFloat("cgst");
					sgst = rs.getFloat("sgst");
					tcs = rs.getFloat("tcs");
				}

				if (reschedule == "reschedule") {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 5);
					rl.setString(
							5,
							"No Cancellation For this Ticket due to Rescheduled,contact operator for clarification!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(401);
					response.setMessage("No Cancellation For this Ticket due to Rescheduled,contact operator for clarification!"); // if(!rs.isBeforeFirst())
					sd.setResponseCodes(response);
					return sd;
				}

				if (status.equalsIgnoreCase("cancelled") || status == null
						|| status.equalsIgnoreCase("cancel")) {
					// log.error("Exception in DAO-cancelTicketDb() from ip:"+ip+" and key"+key);
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 6);
					rl.setString(5, "Ticket Number already cancelled!");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(428);
					response.setMessage("Ticket Number already cancelled!");
					sd.setResponseCodes(response);
					return sd;
				}				

				String m111 = null;

				PreparedStatement ps2 = connection
						.prepareStatement("SELECT * FROM registered_operators where travel_id=?");
				ps2.setInt(1, travelID);
				ResultSet rs2 = ps2.executeQuery();
				String cancTerms = null;
				String jTime = journeyDate + " " + startTime;
				String ph = null;
				String mobile_op = null;
				String sender_id = null;
				int api_can_sms = 0;

				while (rs2.next()) {
					cancTerms = rs2.getString("canc_terms");
					ph = rs2.getString("other_contact");
					mobile_op = rs2.getString("contact_no");
					sender_id = rs2.getString("sender_id");
					api_can_sms = rs2.getInt("api_can_sms");
				}

				PreparedStatement mt = connection
						.prepareStatement("select distinct canc_terms from master_terms where service_num=? and travel_id=? and terms_date=?");
				mt.setString(1, serviceNumber);
				mt.setInt(2, travelID);
				mt.setString(3, journeyDate);

				ResultSet mtrs = mt.executeQuery();

				if (mtrs.next()) {
					cancTerms = mtrs.getString("canc_terms");
				} else {
					PreparedStatement mt1 = connection
							.prepareStatement("select distinct canc_terms from master_terms where service_num=? and travel_id=? and terms_date IS NULL");
					mt1.setString(1, serviceNumber);
					mt1.setInt(2, travelID);

					// System.out.println("Before : " + mt1.toString());
					ResultSet mtrs1 = mt1.executeQuery();

					if (mtrs1.next()) {
						cancTerms = mtrs1.getString("canc_terms");
					} else if (rs2.next()) {
						cancTerms = rs2.getString("canc_terms");
					}
				}

				String canCharge = null;
				float canAmt = 0;
				float refAmt = 0;

				if (cancTerms.equalsIgnoreCase("no")) {
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 7);
					rl.setString(5,
							"Cancellation Not Allowed For This Service On "
									+ journeyDate);
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(427);
					response.setMessage("Cancellation Not Allowed For This Service On "
							+ journeyDate); // if(!rs.isBeforeFirst())
					sd.setResponseCodes(response);
					return sd;
				} else {
					int canCharge2 = this.getTimeDiff(jTime, cancTerms);
					/*
					 * if(canCharge2==100){ response.setCode(429);
					 * response.setMessage("Your Ticket cannot be cancelled!");
					 * //if(!rs.isBeforeFirst()) sd.setResponseCodes(response);
					 * return sd; }
					 */
					canCharge = canCharge2 + "%";
					canAmt = (canCharge2 * (base_fare + discount_amount)) / 100;
					refAmt = base_fare - canAmt;
				}

				DateFormat dateFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date CurrentDate = new Date();
				String currentDateTime = dateFormat.format(CurrentDate);
				// insert cancellation details into master_booking
				PreparedStatement ps4 = connection
						.prepareStatement("insert into master_booking(tkt_no,pnr,service_no,board_point,bpid,land_mark,source,dest,travels,bus_type,bdate,jdate,seats,gender,start_time,arr_time,paid,save,tkt_fare,base_fare,service_tax_amount,discount_amount,convenience_charge,pname,pemail,pmobile,age,refno,status,pass,cseat,ccharge,camt,refamt,travel_id,ip,time,cdate,ctime,id_type,id_num,padd,alter_ph,fid,tid,operator_agent_type,agent_id,bus_model,cgst,sgst,tcs)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps4.setString(1, tktNo);
				ps4.setString(2, pnr);
				ps4.setString(3, serviceNumber);
				ps4.setString(4, boardPoint);
				ps4.setString(5, bpid);
				ps4.setString(6, landmark);
				ps4.setString(7, source);
				ps4.setString(8, dest);
				ps4.setString(9, travels);
				ps4.setString(10, busType);
				ps4.setString(11, bDate);
				ps4.setString(12, journeyDate);
				ps4.setString(13, seats);
				ps4.setString(14, gender);
				ps4.setString(15, startTime);
				ps4.setString(16, arrTime);
				ps4.setFloat(17, paid);
				ps4.setFloat(18, save);
				ps4.setFloat(19, fare);
				ps4.setFloat(20, base_fare);
				ps4.setFloat(21, service_tax_amount);
				ps4.setFloat(22, discount_amount);
				ps4.setFloat(23, convenience_charge);
				ps4.setString(24, passengerName);
				ps4.setString(25, passengerEmail);
				ps4.setString(26, passengerMobile);
				ps4.setString(27, age);
				ps4.setString(28, refno);
				ps4.setString(29, "cancelled");
				ps4.setInt(30, pass);
				ps4.setString(31, seats);
				ps4.setString(32, canCharge);
				ps4.setFloat(33, canAmt);
				ps4.setFloat(34, refAmt);
				ps4.setInt(35, travelID);
				ps4.setString(36, ip);
				ps4.setString(37, time);
				ps4.setString(38, tdate);
				ps4.setString(39, currentDateTime);
				ps4.setString(40, idType);
				ps4.setString(41, idNumber);
				ps4.setString(42, address);
				ps4.setString(43, alternateMobile);
				ps4.setInt(44, fid);
				ps4.setInt(45, tid);
				ps4.setInt(46, operator_agent_type);
				ps4.setInt(47, agent_id);
				ps4.setString(48, bus_model);
				ps4.setFloat(49, cgst);
				ps4.setFloat(50, sgst);
				ps4.setFloat(51, tcs);
				int res = ps4.executeUpdate();

				// update balance of agent
				PreparedStatement ps5 = connection
						.prepareStatement("select * from agents_operator where id=?");
				ps5.setInt(1, agent_id);
				ResultSet rs5 = ps5.executeQuery();
				float oldBalance = 0;
				while (rs5.next()) {
					oldBalance = rs5.getFloat("balance");
				}
				float newBalance = oldBalance + refAmt;
				PreparedStatement ps6 = connection
						.prepareStatement("update agents_operator set balance=? where id=?");
				ps6.setFloat(1, newBalance);
				ps6.setInt(2, agent_id);
				int rs6 = ps6.executeUpdate();
				/* inserting into account table for report generation */
				PreparedStatement ps14 = connection
						.prepareStatement("insert into master_pass_reports(tktno,pnr,pass_name,source,destination,date,transtype,tkt_fare,comm,can_fare,refamt,net_amt,bal,dat,ip,agent_id,travel_id,status,jdate)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				ps14.setString(1, tktNo);
				ps14.setString(2, pnr);
				ps14.setString(3, passengerName);
				ps14.setString(4, source);
				ps14.setString(5, dest);
				ps14.setString(6, currentDateTime);
				ps14.setString(7, "credit");
				ps14.setFloat(8, fare);
				ps14.setFloat(9, save);
				ps14.setFloat(10, fare);
				ps14.setFloat(11, canAmt);
				ps14.setFloat(12, refAmt);
				ps14.setFloat(13, newBalance);
				ps14.setString(14, tdate);
				ps14.setString(15, ip);
				ps14.setInt(16, agent_id);
				ps14.setInt(17, travelID);
				ps14.setString(18, "cancelled");
				ps14.setString(19, journeyDate);
				int rs14 = ps14.executeUpdate();
				// updating seats status as available
				String[] seatsArray = seats.split(",");

				cityOrderIdList = getOrderIds( connection,fid,tid,travelID,serviceNumber);
				if(cityOrderIdList!=null)
				{
					stageorder = cityOrderIdList.get(0)+"-"+cityOrderIdList.get(1);
				}
				String updatedBookedStages = "";
				int seatStauts = 4;
				for (int i = 0; i < seatsArray.length; i++) {
					
					PreparedStatement pstmt = connection
							.prepareStatement("select * from layout_list where seat_name=? and travel_id=? and service_num=? and journey_date=?");
					pstmt.setString(1, seatsArray[i]);
					pstmt.setInt(2, travelID);
					pstmt.setString(3, serviceNumber);
					pstmt.setString(4, journeyDate);
					
					ResultSet rss = pstmt.executeQuery();
					while(rss.next())
					{
						String availableStages = rss.getString("available_stages");
						String bookedStages = rss.getString("booked_stages");
						seatStauts = rss.getInt("seat_status");
						
						String availList = getAvailableStagesForCancellingSeat(connection,cityOrderIdList,travelID,serviceNumber);
						
						if(StringUtils.isBlank(availList)) availList = availableStages+","+stageorder;
						else 
						 {
								if(StringUtils.isBlank(availableStages))
								availList = availList;
								else
								availList = availList+","+availableStages;
								
							}
						
						if(bookedStages != null || bookedStages!="")
						{
							String key1 = stageorder+",";
							String key2 = ","+stageorder;
							if(bookedStages.contains(key1)) updatedBookedStages = bookedStages.replace(key1,"");
							else if(bookedStages.contains(key2)) updatedBookedStages = bookedStages.replace(key2,"");
							else if(bookedStages.contains(stageorder)) updatedBookedStages = bookedStages.replace(stageorder,"");
							
						}
						
						
						
						
						PreparedStatement ps15 = connection
								.prepareStatement("update layout_list set seat_status=?,is_ladies=?,available_stages=?,booked_stages=? where seat_name=? and travel_id=? and service_num=? and journey_date=?");
					
						if(seatStauts == 4) ps15.setInt(1, 4);
						else if(seatStauts == 2) ps15.setInt(1, 2);
						else if(seatStauts == 3) ps15.setInt(1, 3);
						else ps15.setInt(1, 0);
						
						ps15.setInt(2, 0);
						ps15.setString(3, availList);
						ps15.setString(4, updatedBookedStages);
						ps15.setString(5, seatsArray[i]);
						ps15.setInt(6, travelID);
						ps15.setString(7, serviceNumber);
						ps15.setString(8, journeyDate);
						ps15.executeUpdate();
						
					}
					
					
				}
				if (res == 1) {
					String namesArray[];
					namesArray = passengerName.split(",");
					String txt = "";
					String txt1 = "TKT No: " + tktNo + " ,DOJ " + journeyDate
							+ "-" + source + "-" + dest + "->" + serviceNumber
							+ " seats:" + seats + " pass:" + namesArray[0]
							+ "-" + passengerMobile + " is cancelled";
					String strMsg = java.net.URLEncoder.encode(txt, "UTF-8");
					String strMsg1 = java.net.URLEncoder.encode(txt1, "UTF-8");
					String sender_id1 = java.net.URLEncoder.encode(sender_id,
							"UTF-8");
					String mobile_op1 = java.net.URLEncoder.encode(mobile_op,
							"UTF-8");

					if (api_can_sms == 1) {
						String strURL = "http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=pridhvi@msn.com:activa1525@&senderID="
								+ sender_id1
								+ "&receipientno="
								+ mobile_op1
								+ "&msgtxt=" + strMsg1 + "&state=4";
						// System.out.println(" URL is :"+strURL);
						String output = null;
						String exp = null;
						String iStatus = null;

						try {
							java.net.URL obj1 = new java.net.URL(strURL);
							HttpURLConnection httpReq = (HttpURLConnection) obj1
									.openConnection();
							httpReq.setDoOutput(true);
							httpReq.setInstanceFollowRedirects(true);
							httpReq.setRequestMethod("GET");
							iStatus = httpReq.getResponseMessage();
							// System.out.println("iStatus: "+iStatus);

						} catch (MalformedURLException ex) {
							ex.printStackTrace();
						} catch (IOException ex) {
							ex.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}

						// m111=output+"status"+strURL+"#######"+exp;
						m111 = iStatus + "status" + strURL + "#######" + exp;
						// System.out.println("operatorName1 "+operatorName);
						PreparedStatement psl11 = connection
								.prepareStatement("insert into logs_api (date,date_time,method_called,ip,api_key) values (?,?,?,?,?)");
						psl11.setString(1, todayDateApi);
						psl11.setString(2, todayDateTimeApi);
						psl11.setString(3, m111);
						psl11.setString(4, ip);
						psl11.setString(5, key);
						int resapi11 = psl11.executeUpdate();

						PreparedStatement sms = connection
								.prepareStatement("insert into sms_api (date,date_time,sender_id,contact,msg,status) values (?,?,?,?,?,?)");
						sms.setString(1, todayDateApi);
						sms.setString(2, todayDateTimeApi);
						sms.setString(3, sender_id);
						sms.setString(4, mobile_op);
						sms.setString(5, strMsg1);
						sms.setString(6, iStatus);
						sms.execute();

						// callURL("http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=user_id:password&senderID=sender_id&receipientno=9866740479&msgtxt="+txt+"&state=4");
						// /(http://api.mVaayoo.com/mvaayooapi/MessageCompose?user=pridhvi@msn.com:activa1525@&senderID=SSSBUS&receipientno=9676197117&msgtxt="++"&state=4)
					}

					// response
					response.setCode(200);
					response.setMessage("success");
					cdet.setStatus("cancelled");
					cdet.setRefund_amount(refAmt);
					cdet.setCancellation_amount(canAmt);
					cdet.setCancellation_charges(canCharge);
					sd.setResponseCodes(response);
					sd.setCancel_details(cdet);
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 8);
					String response_logs = "{response:{code:200,message:success,cancel_details:{refund_amount:"
							+ refAmt
							+ ",cancellation_amount:"
							+ canAmt
							+ ",cancellation_charges:"
							+ canCharge
							+ ",status:cancelled}}";
					rl.setString(5, "success " + response_logs);
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					return sd;
				} else {
					// log.error("Exception in DAO-cancelTicketDb() from ip:"+ip+" and key"+key);
					// /log.error("Exception in DAO-cancelTicketDb()-not inserted in master_booking ");
					rl.setString(1, todayDateApi);
					rl.setString(2, todayDateTimeApi);
					rl.setString(3, m);
					rl.setInt(4, 9);
					rl.setString(5,
							"Internal Server Error! not inserted in master_booking");
					rl.setString(6, ip);
					rl.setString(7, key);
					rl.executeUpdate();
					response.setCode(500);
					response.setMessage("Internal Server Error!");
					sd.setResponseCodes(response);
					return sd;
				}
			} catch (SQLException ie) {
				// log.error("SQLException in DAO-cancelTicketDb() from ip:"+ip+" and key"+key);
				// System.out.println("Error Code" + ie.getErrorCode());
				// System.out.println("Error Message" + ie.getMessage());
				rl.setString(1, todayDateApi);
				rl.setString(2, todayDateTimeApi);
				rl.setString(3, m);
				rl.setInt(4, 10);
				rl.setString(5, "Internal Server Error!" + ie.getMessage());
				rl.setString(6, ip);
				rl.setString(7, key);
				rl.executeUpdate();
				response.setCode(500);
				response.setMessage("Internal Server Error");
				sd.setResponseCodes(response);
				return sd;
			} catch (Exception e) {
				// log.error("Exception in DAO-cancelTicketDb() from ip:"+ip+" and key"+key);
				// System.out.println("Error Message" + e.getMessage());
				rl.setString(1, todayDateApi);
				rl.setString(2, todayDateTimeApi);
				rl.setString(3, m);
				rl.setInt(4, 11);
				rl.setString(5, "Internal Server Error!" + e.getMessage());
				rl.setString(6, ip);
				rl.setString(7, key);
				rl.executeUpdate();
				response.setCode(500);
				response.setMessage("Internal Server Error!");
				sd.setResponseCodes(response);
				return sd;
			}
		}// close if
		else {
			// log.info("Authentication Failed For DAO-cancelTicketDb() from ip:"+ip+" and api:"+key);
			rl.setString(1, todayDateApi);
			rl.setString(2, todayDateTimeApi);
			rl.setString(3, m);
			rl.setInt(4, 12);
			rl.setString(5, "Authentication failed");
			rl.setString(6, ip);
			rl.setString(7, key);
			rl.executeUpdate();
			response.setCode(401);
			response.setMessage("Authentication failed");
			sd.setResponseCodes(response);
			return sd;
		}// close else
	}// close getServices(---)

	/**
	 * This method to get the journey details.
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return journey details.
	 */
	public SeatBookingResponse getTicketDetailsDb(Connection connection,
			String key, String tktno, HttpServletRequest request)
			throws Exception {

		SeatBookingResponse seatRes = new SeatBookingResponse();
		ResponseCodes response = new ResponseCodes();
		TicketDetails tdet = new TicketDetails();
		if (tktno == null) {
			response.setCode(400);
			response.setMessage("Invalid Parameter Name OR Null");
			seatRes.setResponseCodes(response);
			return seatRes;
		}
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// log.info("dao-getTicketDetailsDb() Called from ip:"+ip+" api:"+key);
		boolean valid = this.authenticate(key, ip);
		if (valid) {

			PreparedStatement ps2 = connection
					.prepareStatement("select * from master_booking where tkt_no=? and status=?");
			ps2.setString(1, tktno);
			ps2.setString(2, "cancelled");
			ResultSet rs2 = ps2.executeQuery();
			if (rs2.next() == false) {
				PreparedStatement ps = connection
						.prepareStatement("select * from master_booking where tkt_no=?");
				ps.setString(1, tktno);
				ResultSet rs = ps.executeQuery();
				if (rs.next() == false) {
					response.setCode(430);
					response.setMessage("Invalid Ticket Number!"); // if(!rs.isBeforeFirst())
					seatRes.setResponseCodes(response);
					return seatRes;
				}
				rs.previous();
				while (rs.next()) {
					String status = rs.getString("status");
					String jdate = rs.getString("jdate");
					// convering input date string type to date format
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd");
					Date journey_date = formatter.parse(jdate);
					// current date
					Date cdate = new Date();
					String tdate = formatter.format(cdate);
					Date today_date = formatter.parse(tdate);
					// if journey date is lesser than current date
					if (journey_date.compareTo(today_date) < 0) {
						response.setCode(432);
						response.setMessage("Journey Date is past!");
						seatRes.setResponseCodes(response);
						return seatRes;
					}
					// feeding to response obj
					tdet.setTicket_status(status);
					tdet.setTicket_number(rs.getString("tkt_no"));
					tdet.setPnr_number(rs.getInt("pnr"));
					tdet.setTravel_id(rs.getInt("travel_id"));
					tdet.setTravel_name(rs.getString("travels"));
					tdet.setOrigin(rs.getString("source"));
					tdet.setDestination(rs.getString("dest"));
					tdet.setJourney_date(jdate);
					tdet.setNo_of_seats(rs.getInt("pass"));
					tdet.setSeat_numbers(rs.getString("seats"));
					tdet.setService_number(rs.getString("service_no"));
					tdet.setBus_type(rs.getString("bus_model"));
					tdet.setDep_time(rs.getString("start_time"));
					tdet.setBoarding_point(rs.getString("board_point"));
					tdet.setLandmark(rs.getString("land_mark"));
					tdet.setDropping_point(rs.getString("drop_point"));
					tdet.setTotal_fare(rs.getFloat("tkt_fare"));
					tdet.setGender(rs.getString("gender"));
					tdet.setPassenger_name(rs.getString("pname"));
					tdet.setPassenger_mobile(rs.getString("pmobile"));
					tdet.setPassenger_email(rs.getString("pemail"));
					tdet.setPassenger_age(rs.getString("age"));
					seatRes.setTicket_details(tdet);
				}

			}// close if
			else {
				response.setCode(431);
				response.setMessage("Cancelled Ticket!"); // if(!rs.isBeforeFirst())
				seatRes.setResponseCodes(response);
				return seatRes;
			}
		} else {
			// log.info("Authentication Failed For DAO-getTicketDetailsDb() from ip:"+ip+" and api:"+key);
			response.setCode(401);
			response.setMessage("Authentication failed");
			seatRes.setResponseCodes(response);
			return seatRes;
		}// close else
		return seatRes;
	}

	/**
	 * This method is to get the balance of an Agent .
	 * 
	 * @author Praneeth
	 * @version 1.0
	 * @since 2013 Dec
	 * @return balance.
	 */
	public BalanceResponse getBalanceDb(Connection connection, String key,
			HttpServletRequest request) {
		BalanceResponse bresp = new BalanceResponse();
		AgentBalance ab = new AgentBalance();
		ResponseCodes response = new ResponseCodes();
		String ip = request.getHeader("X-FORWARDED-FOR");
		if (ip == null) {
			ip = request.getRemoteAddr();
		}
		// log.info("dao-getBalanceDb() Called from ip:"+ip+" api:"+key);
		boolean valid = this.authenticate(key, ip);
		if (valid) {
			try {
				PreparedStatement ps = connection
						.prepareStatement("select * from agents_operator where api_key=?");
				ps.setString(1, key);
				ResultSet rs = ps.executeQuery();
				if (rs.next() == false) {
					response.setCode(433);
					response.setMessage("No Agent Exist with API Key!");
					bresp.setResponseCodes(response);
					return bresp;
				}
				rs.previous();
				while (rs.next()) {

					String name = rs.getString("name");
					double balance = rs.getDouble("balance");
					double limit = rs.getDouble("bal_limit");
					ab.setName(name);
					ab.setBalance(balance);
					ab.setLimit(limit);
					bresp.setBalance_details(ab);
					return bresp;

				}
			} catch (SQLException e) {
				// log.info("SQL Exception in DAO-getBalanceDb()"+e.getMessage()+" from ip:"+ip+" and api:"+key);
				System.out.println("SQL Exception in DAO-getBalanceDb()"
						+ e.getMessage());
				e.printStackTrace();
			}
		} else {
			// log.info("Authentication Failed For DAO-getBalanceDb() from ip:"+ip+" and api:"+key);
			response.setCode(401);
			response.setMessage("Authentication failed");
			bresp.setResponseCodes(response);
			return bresp;
		}// close else
		return bresp;
	}
	
	public int findMainRouteOrStageBooking(Connection connection,int fromId, int toId, int trvelId,String serviceNum) throws SQLException
	{
		//get route id
		PreparedStatement ps = connection
				.prepareStatement("select * from master_routes_international where source_id=? and destination_id=? and operator_id=? and service_num=? ");
		ps.setInt(1, fromId);
		ps.setInt(2, toId);
		ps.setInt(3, trvelId);
		ps.setString(4, serviceNum);
		ResultSet rs = ps.executeQuery();
		int seatStatus = 4;
		while (rs.next()) {
			seatStatus = 1;
		}
		
		return seatStatus;	
	}
	
	
	public List<Integer> getOrderIds(Connection connection,int from, int to,int operId, String serviceNum) throws SQLException
	{
		List<Integer> list = new ArrayList<Integer>();
		PreparedStatement ps = connection
				.prepareStatement("select stage_order,route_id from master_operator_stages where  operator_id=? and stage_city_id=? and service_num=? ");
		ps.setInt(1, operId);
		ps.setInt(2, from);
		ps.setString(3, serviceNum);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			list.add(rs.getInt("stage_order"));
			//setRouteId(rs.getInt("route_id"));
		}
		
		ps.setInt(2, to);
		ResultSet rs2 = ps.executeQuery();
		while (rs2.next()) {
			list.add(rs2.getInt("stage_order"));
		}
		return list;
	}
	public String getAvailableStagesForNextBooking(Connection connection,String availableStagesList, int fromOrderId, int toOrderId, int operId, String srvnum) throws SQLException
	{
		//get order id's
		HashSet<List<Integer>> totalStages;
		if(StringUtils.isBlank(availableStagesList))
		{
			totalStages = getTotalStageCombinations(connection,operId,srvnum);
		}
		else
		{
			String[] strArray = availableStagesList.split(",");
			totalStages = new HashSet<List<Integer>>();
			for(int i=0;i<strArray.length;i++)
			{
				List<Integer> myList = new ArrayList<Integer>();
				String[] strArray2 = strArray[i].split("-");
				myList.add(Integer.parseInt(strArray2[0]));
				myList.add(Integer.parseInt(strArray2[1]));
				totalStages.add(myList);
				
			}
		}
		
		Set<List<Integer>> set = getAvailableStages(totalStages, fromOrderId, toOrderId);
		StringBuffer sb = new StringBuffer();
		 int size  = set.size();
        int i=0;
        
        for(List<Integer> list:set)
        {  
        	i++;
        	if(size==i)
        	sb.append(list.get(0)+"-"+list.get(1));
        	else
        	sb.append(list.get(0)+"-"+list.get(1)+",");
        	
        	
        }
        
        return sb.toString();
		
	}
	
	
	
	public String getAvailableStagesForCancellingSeat(Connection connection,
			List<Integer> cityOrderIdList, int operId, String srvnum) throws SQLException
	{
		//get order id's
		HashSet<List<Integer>> totalStages;
		totalStages = getTotalStageCombinations(connection,operId,srvnum);
				
		Set<List<Integer>> set = getBlockedStages(totalStages,cityOrderIdList);
		StringBuffer sb = new StringBuffer();
		 int size  = set.size();
        int i=0;
        
        for(List<Integer> list:set)
        {  
        	i++;
        	if(size==i)
        	sb.append(list.get(0)+"-"+list.get(1));
        	else
        	sb.append(list.get(0)+"-"+list.get(1)+",");
        	
        	
        }
        
        return sb.toString();
		
	}
	
	
	
	  public Set<List<Integer>> getAvailableStages(Set<List<Integer>> totalStages, int from, int to)
	    {
	       ArrayList<Integer>  fromTo= new ArrayList<Integer>();
	       fromTo.add(from);
	       fromTo.add(to);
	      
	       Set<List<Integer>> availableStages=new HashSet<List<Integer>>();
	       
	       
	       if(totalStages.contains(fromTo))
	       {
	    	   for(List<Integer> list: totalStages)
	    	   {
	    		   
	    		   if( (list.get(0)!=from) &&  (list.get(1)!=to)  && !(list.get(0)>from && list.get(0)<to) 
	    				   && !(list.get(1)>from && list.get(1)<to) && !(list.get(0)<from && to<list.get(1)))
	    		   {
	    			   availableStages.add(list);
	    		   }
	    	   }
	    	   
	       }
	    	
	    	return availableStages;
	    }

	
	public  HashSet<List<Integer>>  getTotalStageCombinations(Connection connection,int operatorId,String srvnum) throws SQLException
	{
				int noOfStages = 0;
				PreparedStatement ps = connection
						.prepareStatement("select max(stage_order) from master_operator_stages where service_num=? and operator_id=?");
				ps.setString(1, srvnum);
				ps.setInt(2, operatorId);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					noOfStages = rs.getInt(1);
				}
		Integer[] input =new Integer[noOfStages]; 
		 for(int k=0; k<noOfStages;k++)
		 {
			 input[k] = k+1;
		 }
			
	        HashSet<List<Integer>> totalStages=new HashSet<List<Integer>>();
	        totalStages=getTotalCombinations(input, 0, new int[2], 0, totalStages);//it gives total stage combinations
	        
	     
			return totalStages;
		
	}
	
	public HashSet<List<Integer>> getTotalCombinations(Integer[] arr,int startId, int[] branch, int numElem,HashSet arrSet)
    {
    	int k = 2;
        if (numElem == k)
        {
            List<Integer> mySet = new ArrayList<Integer>();
            for(int i=0;i<branch.length;i++)
            {
                mySet.add(branch[i]);
            }
            arrSet.add(mySet);
            return arrSet;
        }

        for (int i = startId; i < arr.length; ++i)
        {
            branch[numElem++]=arr[i];
            getTotalCombinations(arr, ++startId, branch, numElem, arrSet);
            --numElem;
        }
        return arrSet;
    }
	
	public String getBlockedStagesForTentativeBooking(List<Integer> cityOrderIdList, Connection connection, int operId, String availableStagesList,String srvnum) throws SQLException
	{
		HashSet<List<Integer>> totalStages;
			if(StringUtils.isBlank(availableStagesList))
			{
				totalStages = getTotalStageCombinations(connection,operId,srvnum);
			}
			else
			{
				String[] strArray = availableStagesList.split(",");
				totalStages = new HashSet<List<Integer>>();
				for(int i=0;i<strArray.length;i++)
				{
					List<Integer> myList = new ArrayList<Integer>();
					String[] strArray2 = strArray[i].split("-");
					myList.add(Integer.parseInt(strArray2[0]));
					myList.add(Integer.parseInt(strArray2[1]));
					totalStages.add(myList);
					
				}
			}
			
		Set<List<Integer>> set = getBlockedStages(totalStages, cityOrderIdList);
				StringBuffer sb = new StringBuffer();
				 int size  = set.size();
		        int i=0;
		        
		        for(List<Integer> list:set)
		        {  
		        	i++;
		        	if(size==i)
		        	sb.append(list.get(0)+"-"+list.get(1));
		        	else
		        	sb.append(list.get(0)+"-"+list.get(1)+",");
		        	
		        	
		        }
		        
		        return sb.toString();
	}
	
	public Set<List<Integer>> getBlockedStages(Set<List<Integer>> totalStages, List<Integer> cityOrderIdList)
    {
		
		
		
       int from = 0;
       int to = 0;
       if(cityOrderIdList!=null)
       {
    	   from = cityOrderIdList.get(0);
    	   to=cityOrderIdList.get(1);
       }
       Set<List<Integer>> blockedStages=new HashSet<List<Integer>>();
       if(totalStages.contains(cityOrderIdList))
       {
    	   for(List<Integer> list: totalStages)
    	   {
    		   
    		   if( (list.get(0)!=from) &&  (list.get(1)!=to)  && !(list.get(0)>from && list.get(0)<to) 
    				   && !(list.get(1)>from && list.get(1)<to) && !(list.get(0)<from && to<list.get(1)))
    		   {
    			   //nothing to do
    		   }
    		   else blockedStages.add(list);
    	   }
    	   
       }
    	
    	return blockedStages;
    }
	
	
	public boolean updateToAvailableFromBlocked(Connection connection, String blockedStagesFrmDB, String stageOrder, int travelId , 
			String seatName, String srvNo, String jDate, int seatStatus, String blockedPnrs) throws SQLException
	{
		boolean ck = false;
		String pnrToUpdateInDB = "";
		String blockedStagesToUpdateInDB=null;
		boolean contains = blockedStagesFrmDB.contains("|");
		int p = 0;
		int k = 0;
		if(contains == true)
		{
			String[] strArray = blockedStagesFrmDB.split("\\|");
			
			for(int q=0;q<strArray.length;q++)
			{
				ck = false;
				String[] strArray2 = strArray[q].split("#");
				if(strArray2[0].contains(stageOrder))
				{
					ck = this.releaseBlockedSeat(strArray2[1]);
					if(ck == true) p =1;
				}
				if(ck == false)
				{
					if(k ==0)blockedStagesToUpdateInDB = strArray[q];
					else blockedStagesToUpdateInDB = blockedStagesToUpdateInDB+"|"+strArray[q];
					k++;
				}
				
			}
			
		}else if(contains==false)
		{
			String[] strArray2 = blockedStagesFrmDB.split("#");
			if(strArray2[0].contains(stageOrder))
			{
				ck = this.releaseBlockedSeat(strArray2[1]);
			}
			if(ck == false) blockedStagesToUpdateInDB = blockedStagesFrmDB;
		}
		
		if(p==1 || ck==true) 
			{
				ck = true;
				if(blockedPnrs != null && blockedPnrs.contains(stageOrder))
				{
					pnrToUpdateInDB = getRemovedStagewisePnr(stageOrder,blockedPnrs);
				}
			}
		if(ck == true)
		{
			PreparedStatement psupdt = connection
					.prepareStatement("update layout_list set is_ladies=?,blocked_stages=?,blocked_pnrs=? where travel_id=? and  seat_name=? and service_num=? and journey_date=?");
			psupdt.setInt(1, 0);
			//if(seatStatus == 4) psupdt.setInt(2, 4);
			//else if(seatStatus == 2) psupdt.setInt(2, 2);
			//else if(seatStatus == 2) psupdt.setInt(2, 2);
			psupdt.setString(2, blockedStagesToUpdateInDB);
			psupdt.setString(3, pnrToUpdateInDB);
			psupdt.setInt(4, travelId);
			psupdt.setString(5, seatName);
			psupdt.setString(6, srvNo);
			psupdt.setString(7, jDate);
			if (psupdt.executeUpdate() == 1) ck = true;
			else ck = false; //problem occured while updating.
		}
		
		 return ck;
	}
	
	//
	public String getRemovedStagewisePnr(String stageOrder , String blockedPnrs)
	{
		String updatedPnr = "";
		int k = 0;
		if(blockedPnrs.contains("|"))
		{
			String[] strArray = blockedPnrs.split("\\|");
			
			for(int q=0;q<strArray.length;q++)
			{
				String[] strArray2 = strArray[q].split("#");
				if(!strArray2[0].contains(stageOrder))
				{
					
					if(k ==0)updatedPnr = strArray[q];
					else updatedPnr = updatedPnr+"|"+strArray[q];
					k++;
				}
				
			}
			
		}else
		{
			String[] strArray2 = blockedPnrs.split("#");
			if(strArray2[0].contains(stageOrder))
			{
				updatedPnr = "";
			} else updatedPnr = blockedPnrs;
		}
		
		return updatedPnr;
	}
	
	public String getRemovedBlockedStages(String stageOrder , String blockedStages)
	{
		String updatedBlockedStages =  "";
		int k = 0;
		if(blockedStages.contains("|"))
		{
			String[] strArray = blockedStages.split("\\|");
			
			for(int q=0;q<strArray.length;q++)
			{
				String[] strArray2 = strArray[q].split("#");
				if(!strArray2[0].contains(stageOrder))
				{
					
					if(k ==0)updatedBlockedStages = strArray[q];
					else updatedBlockedStages = updatedBlockedStages+"|"+strArray[q];
					k++;
				}
				
			}
			
		}else
		{
			String[] strArray2 = blockedStages.split("#");
			if(strArray2[0].contains(stageOrder))
			{
				updatedBlockedStages = "";
			}
			else updatedBlockedStages = blockedStages;
		}
		
		return updatedBlockedStages;
	}
	
	
	
}
