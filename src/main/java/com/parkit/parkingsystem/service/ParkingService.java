package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.JsonUtils;
import sun.util.calendar.BaseCalendar;
import sun.util.calendar.CalendarUtils;
// import sun.util.calendar.BaseCalendar;
// import sun.util.calendar.CalendarUtils;


import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Date.convertToAbbr;

public class ParkingService {

    private static final Logger logger = LogManager.getLogger("ParkingService");

    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();
    private static Boolean fidelity=false;

    private InputReaderUtil inputReaderUtil;
    private ParkingSpotDAO parkingSpotDAO;
    private  TicketDAO ticketDAO;


    public ParkingService(InputReaderUtil inputReaderUtil, ParkingSpotDAO parkingSpotDAO, TicketDAO ticketDAO){
        this.inputReaderUtil = inputReaderUtil;
        this.parkingSpotDAO = parkingSpotDAO;
        this.ticketDAO = ticketDAO;
    }

    public void processIncomingVehicle() {
        try{
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if(parkingSpot !=null && parkingSpot.getId() > 0) {
                String vehicleRegNumber = getVehichleRegNumber();

                TicketDAO ticketDAO2 = new TicketDAO();

                // Est-ce qu'il y a un ticket avec le numero du véhicule ?
                // si oui on vérifie si il est en préparation ou qu'il a été validé
                // sinon on prépare un nouveau ticket

                if (ticketDAO2.getTicket(vehicleRegNumber) == null) {
                    System.out.println(
                            "Préparation d'un nouveau ticket");


                    // préparation d'un nouveau ticket
                    parkingSpot.setAvailable(false);
                    parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                    Date inTime = new Date();
                    Ticket ticket = new Ticket();

                    //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                    //ticket.setId(ticketID);
                    ticket.setParkingSpot(parkingSpot);
                    ticket.setVehicleRegNumber(vehicleRegNumber);

                    ticket.setPrice(0);
                    ticket.setInTime(inTime);
                    ticket.setOutTime(null);
                    ticketDAO.saveTicket(ticket);
                    System.out.println("Generated Ticket and saved in DB");
                    System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
                    System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);

                } else {

                    if (ticketDAO2.getTicket(vehicleRegNumber).getOutTime()!=null) {
                        //cas où le véhicule a déjà validé une entrée et une sortie et il revient dans le parking

                        System.out.println();
                        System.out.println(" Hey ! Happy to see you again !");
                        System.out.println();

                        parkingSpot.setAvailable(false);
                        parkingSpotDAO.updateParking(parkingSpot);//allot this parking space and mark it's availability as false

                        Date inTime = new Date();
                        Ticket ticket = new Ticket();
                        //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                        //ticket.setId(ticketID);
                        ticket.setParkingSpot(parkingSpot);
                        ticket.setVehicleRegNumber(vehicleRegNumber);

                        ticket.setPrice(0);
                        ticket.setInTime(inTime);
                        ticket.setOutTime(null);
                        ticketDAO.saveTicket(ticket);
                        System.out.println("Generated Ticket and saved in DB");
                        System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
                        System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);

                    } else {
                        // on essai de faire rentré un véhicule qui est déjà présent
                        System.out.println();
                        System.out.println(
                                "--- On ne peut pas rentrer un Véhicule qui est déjà présent --- "
                        );
                        System.out.println();

                    }
                }
            }

        }catch(Exception e){
            logger.error("Unable to process incoming vehicle",e);
        }
    }

    private String getVehichleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    public ParkingSpot getNextParkingNumberIfAvailable(){
        int parkingNumber=0;
        ParkingSpot parkingSpot = null;
        try{
            ParkingType parkingType = getVehichleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if(parkingNumber > 0){
                parkingSpot = new ParkingSpot(parkingNumber,parkingType, true);
            }else{
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        }catch(IllegalArgumentException ie){
            logger.error("Error parsing user input for type of vehicle", ie);
        }catch(Exception e){
            logger.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    private ParkingType getVehichleType(){
        System.out.println("Please select vehicle type from menu");
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch(input){
            case 1: {
                return ParkingType.CAR;
            }
            case 2: {
                return ParkingType.BIKE;
            }
            default: {
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
            }
        }
    }

    public void processExitingVehicle() {
        try{
            String vehicleRegNumber = getVehichleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            Ticket ticket2 = ticketDAO.getTicket2(vehicleRegNumber);


            try {
                if (ticket.getOutTime() != null // il y a un ticket déjà généré avec le véhicule
                ) {
                    System.out.println("Ce véhicule est déjà venu");

                    try {
                        int id = ticketDAO.getTicket2(vehicleRegNumber).getId();
                        ticket.setId(id);

                        ticket.setFidelity(true);
                        setFidelity(true);

                        Date outTime = new Date();
                        ticket.setOutTime(outTime);
                        fareCalculatorService.calculateFare(ticket);
                        if (ticketDAO.updateTicket(ticket)) {
                            ParkingSpot parkingSpot = ticket.getParkingSpot();
                            parkingSpot.setAvailable(true);
                            parkingSpotDAO.updateParking(parkingSpot);
                            System.out.println("Please pay the parking fare:" + ticket.getPrice());
                            System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
                            System.out.println();
                        } else {
                            System.out.println("Unable to update ticket information. Error occurred");
                        }
                    } catch (Exception e) {
                        System.out.println("Exception : ");
                        System.out.println(e);
                    }
                } else {
                    System.out.println("premiere venue du véhicule");

                    Date outTime = new Date();
                    ticket.setOutTime(outTime);
                    fareCalculatorService.calculateFare(ticket);
                    if (ticketDAO.updateTicket(ticket)) {
                        ParkingSpot parkingSpot = ticket.getParkingSpot();
                        parkingSpot.setAvailable(true);
                        parkingSpotDAO.updateParking(parkingSpot);
                        System.out.println("Please pay the parking fare:" + ticket.getPrice());
                        System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
                        System.out.println();
                    } else {
                        System.out.println("Unable to update ticket information. Error occurred");
                    }
                }
            }
            catch (Exception e)
            {
                System.out.println("le ticket n'existe pas");
            }



        }catch(Exception e){
            logger.error("Unable to process exiting vehicle",e);
        }
    }

    public static Boolean getFidelity() {
        return fidelity;
    }

    public void setFidelity(Boolean fidelity) {
        this.fidelity = fidelity;
    }


    @Override
    public String toString() {
        // "EEE MMM dd HH:mm:ss zzz yyyy";
        BaseCalendar.Date date = normalize();
        StringBuilder sb = new StringBuilder(28);
        int index = date.getDayOfWeek();
        if (index == BaseCalendar.SUNDAY) {
            index = 8;
        }
        convertToAbbr(sb, wtb[index]).append(' ');                        // EEE
        convertToAbbr(sb, wtb[date.getMonth() - 1 + 2 + 7]).append(' ');  // MMM
        CalendarUtils.sprintf0d(sb, date.getDayOfMonth(), 2).append(' '); // dd

        CalendarUtils.sprintf0d(sb, date.getHours(), 2).append(':');   // HH
        CalendarUtils.sprintf0d(sb, date.getMinutes(), 2).append(':'); // mm
        // CalendarUtils.sprintf0d(sb, date.getSeconds(), 2).append(' '); // ss
        TimeZone zi = date.getZone();
        if (zi != null) {
            sb.append(zi.getDisplayName(date.isDaylightTime(), TimeZone.SHORT, Locale.US)); // zzz
        } else {
            sb.append("GMT");
        }
        sb.append(' ').append(date.getYear());  // yyyy
        return sb.toString();
    }




}
