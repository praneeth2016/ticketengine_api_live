package webService;
import interfaces.BusinessInterface;

import java.text.ParseException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import model.ServiceModel;

import com.google.gson.Gson;

import dto.BalanceResponse;
import dto.CancelTicketResponse;
import dto.Cities;
import dto.CitiesPair;
import dto.GetSeatingArrangement;
import dto.GetSeatingArrangementAgent;
import dto.IsTicketCancellableResponse;
import dto.SeatBlockingRequest;
import dto.SeatBlockingResponse;
import dto.SeatBookingRequest;
import dto.SeatBookingResponse;

//import dto.SeatDetailForBlock;

//import dto.SeatBlockingInputs;
import dto.ServiceDetails;
@Path("/")
public class BusinessClass implements BusinessInterface{
	
	@GET
	@Path("/getSourceDestinationPairs.json")
	@Produces("application/json")	
	public String getSourceDestinationPairsList(@QueryParam("api_key") String api_key,@Context HttpServletRequest request)
	{
		String ip  = request.getHeader("X-FORWARDED-FOR");  
        if(ip == null)  
        {  
          ip = request.getRemoteAddr();  
        } 
		//logging
		String citiespair  = null;
		try 
		{
			CitiesPair sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.getCitiesPair(api_key,request);
			//serializing
			Gson gson = new Gson();
			//System.out.println(gson.toJson(sendData));
			citiespair = gson.toJson(sendData);

		}
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-getSourceDestinationPairsList() of BusinessClass - Error Message"+e.getMessage());
		}
		return citiespair;
		
		
	}//getSourceDestinationPairs()
	
	@GET
	@Path("/getCities.json")
	@Produces("application/json")
	/*
	 * This method is for getting all possible cities list.
	 * @see interfaces.BusinessInterface#getCitiesList(java.lang.String, javax.servlet.http.HttpServletRequest)
	 */
	public String getCitiesList(@QueryParam("api_key") String api_key,@Context HttpServletRequest request)
	//String key,HttpServletRequest request
	{

		/* String ipAdd=request.getRemoteAddr();
		InetAddress ip1;
		ip1 = InetAddress.getLocalHost();
		String ip = ip1.getHostAddress(); */
	 String ip  = request.getHeader("X-FORWARDED-FOR");  
        if(ip == null)  
        {  
          ip = request.getRemoteAddr();  
        } 
		//logging
		String cities  = null;
		try 
		{
			Cities sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.getCities(api_key,request);
			//serializing
			Gson gson = new Gson();
			//System.out.println(gson.toJson(sendData));
			cities = gson.toJson(sendData);

		}
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-getCitiesList() of BusinessClass - Error Message"+e.getMessage());
		}
		return cities;
	}//close getCitiesList(--)
	@GET
	@Path("/getServices.json")
	@Produces("application/json")
	/*
	 * (this method is for getting the services list for particular date and route)
	 * @see interfaces.BusinessInterface#getServicesList(java.lang.String, int, int, java.util.Date, javax.servlet.http.HttpServletRequest)
	 */
	public String getServicesList(@QueryParam("api_key") String key,@QueryParam("from_id") int from_id,@QueryParam("to_id") int to_id,@QueryParam("date") String date,@Context HttpServletRequest request)
	{
		String services  = null;	    
		try 
		{
			ServiceDetails sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.getServices(key,from_id,to_id,date,request);
			Gson gson = new Gson();
			services = gson.toJson(sendData);

		} 
		catch(ParseException pe){
			 System.out.println("Error in BusinessClass-getServicesList() of BusinessClass - Error Message"+pe.getMessage());
		}
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-getServicesList() of BusinessClass - Error Message"+e.getMessage());
		}
		return services;
	}//getServicesList
	@GET
	@Path("/getServiceDetailsList.json")
	@Produces("application/json")
	/*
	 * (this method is for getting the all services list )
	 * @see interfaces.BusinessInterface#getAllServicesList(java.lang.String,javax.servlet.http.HttpServletRequest)
	 */
	public String getAllServicesList(@QueryParam("api_key") String key,@Context HttpServletRequest request)
	{
		String servicesList  = null;		
		try 
		{
			ServiceDetails sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.getServicesList(key,request);
			Gson gson = new Gson();
			servicesList = gson.toJson(sendData);

		} 
		catch(ParseException pe){
			 System.out.println("Error in BusinessClass-getServicesList() of BusinessClass - Error Message"+pe.getMessage());
		}
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-getServicesList() of BusinessClass - Error Message"+e.getMessage());
		}
		return servicesList;
		
	}//getAllServicesList
	@GET
	@Path("/getSeating.json")
	@Produces("application/json")
	/*
	 * (this method is for getting the services list for particular date and route)
	 * @see interfaces.BusinessInterface#getServicesList(java.lang.String, int, int, java.util.Date, javax.servlet.http.HttpServletRequest)
	 */
	public String getSeating(@QueryParam("api_key") String key,@QueryParam("from_id") int from_id,@QueryParam("to_id") int to_id,@QueryParam("date") String date,@QueryParam("srvno") String srvno,@Context HttpServletRequest request)
	{
		String seats  = null;	    
		try 
		{
			
			GetSeatingArrangement sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.getSeatingArgmt(key,from_id,to_id,date,srvno,request);
			Gson gson = new Gson();
			//System.out.println(gson.toJson(sendData));
			seats = gson.toJson(sendData);

		} 
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-getSeating() of BusinessClass - Error Message"+e.getMessage());
		}
		return seats;
	}//getSeating
	
	@GET
	@Path("/getSeatingAgent.json")
	@Produces("application/json")
	/*
	 * (this method is for getting the services list for particular date and route)
	 * @see interfaces.BusinessInterface#getServicesList(java.lang.String, int, int, java.util.Date, javax.servlet.http.HttpServletRequest)
	 */
	public String getSeatingAgent(@QueryParam("api_key") String key,@QueryParam("from_id") int from_id,@QueryParam("to_id") int to_id,@QueryParam("date") String date,@QueryParam("srvno") String srvno,@Context HttpServletRequest request)
	{
		String seats  = null;	    
		try 
		{
			
			GetSeatingArrangementAgent sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.getSeatingArgmtAgent(key,from_id,to_id,date,srvno,request);
			Gson gson = new Gson();
			//System.out.println(gson.toJson(sendData));
			seats = gson.toJson(sendData);

		} 
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-getSeating() of BusinessClass - Error Message"+e.getMessage());
		}
		return seats;
	}//getSeating
	

	@POST
	@Path("/seatBlocking.json")
	@Consumes("application/json")
	@Produces("application/json")
public String seatBlocking(String passInfo,
	@QueryParam("api_key") String key,
	@QueryParam("from_id") int from_id,
	@QueryParam("to_id") int to_id,
	@QueryParam("date") String date,
	@QueryParam("srvno") String srvno,
	@Context HttpServletRequest request)
{
		 String response=null;
		 //System.out.println("blocking");
		try 
		{
			Gson gson = new Gson();
			SeatBlockingResponse sendData = null;
			ServiceModel model= new ServiceModel();
			SeatBlockingRequest obj=gson.fromJson(passInfo, SeatBlockingRequest.class);
			sendData = model.tentativeBooking(key,from_id,to_id,date,srvno,request,obj);
			response = gson.toJson(sendData);

		} 
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-seatBlocking() of BusinessClass - Error Message"+e.getMessage());
		} 
		//System.out.println(response);
		return response;
		
}
	
	@POST
	@Path("/seatBooking.json")
	@Consumes("application/json")
	@Produces("application/json")
	public String seatBooking(String passInfo,
	@QueryParam("api_key") String key,
	@QueryParam("from_id") int from_id,
	@QueryParam("to_id") int to_id,
	@QueryParam("date") String date,
	@QueryParam("srvno") String srvno,
	@Context HttpServletRequest request)
{
		 String response=null;
		try 
		{
			Gson gson = new Gson();
			SeatBookingResponse sendData = null;
			ServiceModel model= new ServiceModel();
			SeatBookingRequest obj=gson.fromJson(passInfo, SeatBookingRequest.class);
			//System.out.println("agee"+obj.getAge());
			sendData = model.cinfirmBooking(key,from_id,to_id,date,srvno,request,obj);
			response = gson.toJson(sendData);
			//System.out.println("output "+pnr);

		} 
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-seatBooking() of BusinessClass - Error Message"+e.getMessage());

		} 
		return response;
}
	@GET
	@Path("/isCancellable.json")
	@Produces("application/json")
	public String isCancellable(
	@QueryParam("api_key") String key,
	@QueryParam("tktno") String tktno,
	@QueryParam("journey_date") String jDate,
	@Context HttpServletRequest request)
{
		 String response=null;
		try 
		{
			Gson gson = new Gson();
			IsTicketCancellableResponse sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.isTicketCancellable(key,tktno,jDate,request);
			response = gson.toJson(sendData);
			System.out.println("output "+response);

		} 
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-isCancellable() of BusinessClass - Error Message"+e.getMessage());

		} 
		return response;
		
}

	@GET
	@Path("/cancelTicket.json")
	@Produces("application/json")
	public String cancelTicket(
	@QueryParam("api_key") String key,
	@QueryParam("tktno") String tktno,
	@QueryParam("journey_date") String jDate,
	@Context HttpServletRequest request)
{
		 String response=null;
		try 
		{
			Gson gson = new Gson();
			CancelTicketResponse sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.cancelTicketModel(key,tktno,jDate,request);
			response = gson.toJson(sendData);
			System.out.println("output "+response);

		} 
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-cancelTicket() of BusinessClass - Error Message"+e.getMessage());

		} 
		return response;
		
}
	@GET
	@Path("/getBalance.json")
	@Produces("application/json")
	public String getBalance(
	@QueryParam("api_key") String key,
	@Context HttpServletRequest request)
{
		 String response=null;
		try 
		{
			Gson gson = new Gson();
			BalanceResponse sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.getBalanceModel(key,request);
			response = gson.toJson(sendData);
			System.out.println("output "+response);

		} 
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-cancelTicket() of BusinessClass - Error Message"+e.getMessage());

		} 
		return response;
		
}
	@GET
	@Path("/getTicketDetails.json")
	@Produces("application/json")
	public String getTicketDetails(
	@QueryParam("api_key") String key,
	@QueryParam("tktno") String tktno,
	@Context HttpServletRequest request)
{
		 String response=null;
		try 
		{
			Gson gson = new Gson();
			SeatBookingResponse sendData = null;
			ServiceModel model= new ServiceModel();
			sendData = model.getTicketDetailsModel(key,tktno,request);
			response = gson.toJson(sendData);
			System.out.println("output "+response);

		} 
		catch (Exception e)
		{
			 System.out.println("Error in BusinessClass-cancelTicket() of BusinessClass - Error Message"+e.getMessage());

		} 
		return response;
		
}

}
