package model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import dto.CitiesPair;
import dao.DbConnection;
import dao.DbLogic;
import dto.BalanceResponse;
import dto.CancelTicketResponse;
import dto.Cities;
import dto.GetCitiesRequest;
import dto.GetSeatingArrangement;
import dto.GetSeatingArrangementAgent;
import dto.GetServicesRequest;
import dto.IsTicketCancellableResponse;
import dto.SeatBlockingRequest;
import dto.SeatBlockingResponse;
import dto.SeatBookingRequest;
import dto.SeatBookingResponse;
//import dto.SeatBlocking;
import dto.ServiceDetails;

public class ServiceModel {
	
	public CitiesPair getCitiesPair(String key, HttpServletRequest request) throws Exception {
		CitiesPair feeds = null;
		DbConnection database = new DbConnection();
		Connection connection = null;
		try
		{
			
			connection =database.Get_Connection();
			DbLogic db = new DbLogic();
			feeds = db.getCitiesPair(connection,key,request);		
		}
		catch(Exception e)
		{
			throw e;
		}
		finally{
			connection.close();
			System.out.println("conn closed in getCitiesPair()");
		}
		return feeds;
	}
	
	
	public Cities getCities(String key, HttpServletRequest request)throws Exception {
		Cities feeds = null;
		DbConnection database= new DbConnection();
		Connection connection = null;
		try {			
			    connection = database.Get_Connection();
				DbLogic db= new DbLogic();
				feeds=db.getCities(connection,key,request);
		
		} catch (Exception e) {
			throw e;
		}
		finally{
			connection.close();
			System.out.println("conn closed in getCities()");
		}
		return feeds;
	}
	public ServiceDetails getServices(String key,int from,int to,String date,HttpServletRequest request)throws Exception {
		ServiceDetails feeds = null;
		DbConnection database= new DbConnection();
		Connection connection = null;
		try {
			    connection = database.Get_Connection();
				DbLogic db= new DbLogic();
				feeds=db.getServices(connection,key,from,to,date,request);
		
		} catch (Exception e) {
			throw e;
		}finally{
			connection.close();
			System.out.println("conn closed in getServices()");
		}
		return feeds;
	}
	public ServiceDetails getServicesList(String key,HttpServletRequest request)throws Exception
	{
		ServiceDetails feeds = null;
		DbConnection database= new DbConnection();
		Connection connection = null;
		try {			
			    connection = database.Get_Connection();
				DbLogic db= new DbLogic();
				feeds=db.getServicesList(connection,key,request);
		
		} catch (Exception e) {
			throw e;
		}finally{
			connection.close();
			System.out.println("conn closed in getServicesList()");
		}
		return feeds;
	}
	public GetSeatingArrangement getSeatingArgmt(String key,int from,int to,String date,String srvno,HttpServletRequest request) throws Exception
	{
		GetSeatingArrangement feeds = null;
		DbConnection database= new DbConnection();
		Connection connection = null;
		try {			
			    connection = database.Get_Connection();
				DbLogic db= new DbLogic();
				feeds=db.getSeatingArgmtDb(connection,key,from,to,date,srvno,request);
		
		} catch (Exception e) {
			throw e;
		}finally{
			connection.close();
			System.out.println("conn closed in getSeatingArgmt()");
		}
		return feeds;	
	}
	public GetSeatingArrangementAgent getSeatingArgmtAgent(String key,int from,int to,String date,String srvno,HttpServletRequest request) throws Exception
	{
		GetSeatingArrangementAgent feeds = null;
		DbConnection database= new DbConnection();
		Connection connection = null;
		try {				
			    connection = database.Get_Connection();
				DbLogic db= new DbLogic();
				feeds=db.getSeatingArgmtDbAgent(connection,key,from,to,date,srvno,request);
		
		} catch (Exception e) {
			throw e;
		}finally{
			connection.close();
			System.out.println("conn closed in getSeatingArgmtAgent()");
		}
		return feeds;	
	}
	 public SeatBlockingResponse tentativeBooking(String key,int from,int to,String date,String srvno,HttpServletRequest request,SeatBlockingRequest obj) throws Exception
	{
		 SeatBlockingResponse feeds = null;
		 //System.out.println(passInfo);
		 DbConnection database= new DbConnection();
		 Connection connection = null;
		try {				
			    connection = database.Get_Connection();
				DbLogic db= new DbLogic();
				feeds=db.getPnrFortentativeBooking(connection,key,from,to,date,srvno,request,obj);
		
		} catch (Exception e) {
			throw e;
		}finally{
			connection.close();
			System.out.println("conn closed in tentativeBooking()");
		}
		return feeds;	
	} 
	 public SeatBookingResponse cinfirmBooking(String key,int from,int to,String date,String srvno,HttpServletRequest request,SeatBookingRequest obj) throws Exception
		{
		 //cinfirmBooking(key,from_id,to_id,date,srvno,no_of_seats,bpid,dpid,fare,request,obj);
		  SeatBookingResponse feeds = null;
			 //System.out.println(passInfo);
		  DbConnection database= new DbConnection();
		  Connection connection = null;
			try {					
				    connection = database.Get_Connection();
					DbLogic db= new DbLogic();
					feeds=db.getSeatConfirmation(connection,key,from,to,date,srvno,request,obj);
			
			} catch (Exception e) {
				throw e;
			}finally{
				connection.close();
				System.out.println("conn closed in tentativeBooking()");
			}
			return feeds;	
		} 
	 
	 public IsTicketCancellableResponse isTicketCancellable(String key,String tktno ,String jDate,HttpServletRequest request) throws Exception
		{
		 IsTicketCancellableResponse feeds = null;
		 DbConnection database= new DbConnection();
		 Connection connection = null;
			try {					
				    connection = database.Get_Connection();
					DbLogic db= new DbLogic();
					feeds=db.isTicketCancellableDb(connection,key,tktno,jDate,request);
			
			} catch (Exception e) {
				throw e;
			}finally{
				connection.close();
				System.out.println("conn closed in isTicketCancellable()");
			}
			return feeds;	
		} 
	
	 public CancelTicketResponse cancelTicketModel(String key,String tktno ,String jDate,HttpServletRequest request) throws Exception
		{
		 CancelTicketResponse feeds = null;
		 DbConnection database= new DbConnection();
		 Connection connection = null;
			try {				
				    connection = database.Get_Connection();
					DbLogic db= new DbLogic();
					feeds=db.cancelTicketDb(connection,key,tktno,jDate,request);
			
			} catch (Exception e) {
				throw e;
			}finally{
				connection.close();
				System.out.println("conn closed in cancelTicketModel()");
			}
			return feeds;	
		} 
	 public SeatBookingResponse getTicketDetailsModel(String key,String tktno ,HttpServletRequest request) throws Exception
		{
		 SeatBookingResponse feeds = null;
		 DbConnection database= new DbConnection();
		 Connection connection = null;
			try {					
				    connection = database.Get_Connection();
					DbLogic db= new DbLogic();
					feeds=db.getTicketDetailsDb(connection,key,tktno,request);
			
			} catch (Exception e) {
				throw e;
			}finally{
				connection.close();
				System.out.println("conn closed in getTicketDetailsModel()");
			}
			return feeds;	
		} 
	 public BalanceResponse getBalanceModel(String key,HttpServletRequest request) throws Exception
		{
		 BalanceResponse feeds = null;
		 DbConnection database= new DbConnection();
		 Connection connection = null;
			try {					
				    connection = database.Get_Connection();
					DbLogic db= new DbLogic();
					feeds=db.getBalanceDb(connection,key,request);
			
			} catch (Exception e) {
				throw e;
			}finally{
				connection.close();
				System.out.println("conn closed in getBalanceModel()");
			}
			return feeds;	
		}
	 
}
