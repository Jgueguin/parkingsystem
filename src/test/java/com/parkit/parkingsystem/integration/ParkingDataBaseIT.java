package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;

import com.parkit.parkingsystem.dao.TicketDAO;


import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

//added
import com.parkit.parkingsystem.model.Ticket;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import java.util.Date;
import java.util.Locale;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;


    private Ticket ticket;

    @Mock
    private static InputReaderUtil inputReaderUtil;
    private static TicketDAO ticketDaoMock;

    @BeforeAll
    private static void setUp() throws Exception{
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.dataBaseConfig = dataBaseTestConfig;
        ticketDAO = new TicketDAO();
        ticketDAO.dataBaseConfig = dataBaseTestConfig;
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();

        // when (ticketDaoMock.getTicket(ticket.getVehicleRegNumber())).thenReturn(ticket);

    }

    @AfterAll
    private static void tearDown(){

    }

   // @Test
    public void testParkingACar(){

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability

        //Assertions
        // Is car "ABCDEF" saved in DB
        assertEquals(
                "ABCDEF",ticketDAO.getTicket("ABCDEF").getVehicleRegNumber()
        );

        // Is ParkingSpot Avalaibility updated in DB
        assertEquals(
                false,ticketDAO.getTicket("ABCDEF").getParkingSpot().isAvailable()

        );

    }

    @Test
    public void testParkingLotExit(){

        String pattern = "yyyy-MM-dd HH:mm:ss.0";
        SimpleDateFormat inTime = new SimpleDateFormat(pattern);
        String inDate = inTime.format(new Date());
        Date outTime = new Date();
        System.out.println(inDate);

        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processExitingVehicle();

        //TODO: check that the fare generated and out time are populated correctly in the database

        // Assertions
        assertNotNull(
                ticketDAO.getTicket("ABCDEF").getInTime()
        );
        assertNotNull(
                ticketDAO.getTicket("ABCDEF").getOutTime()
        );

        assertEquals(
                inDate.toString(),
                ticketDAO.getTicket("ABCDEF").getInTime().toString()
                );

        assertEquals(
                false,
                ParkingService.getFidelity()
        );

    }

    @Test
    public void testRecurrentUser(){

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        Date inTime = new Date();
        inTime.setTime( System.currentTimeMillis() - ( 2* 60 * 60 * 1000) );
        Date outTime = new Date();

        Ticket ticket = new Ticket();


        System.out.println("");
        System.out.println("Test Recurent User");
        System.out.println("");

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);



        //ticket.getParkingSpot().getId();
        //ticket.setParkingSpot();
/*
        ticket.getVehicleRegNumber();
        ticket.getPrice();
        ticket.getInTime().getTime();
        ticket.getOutTime().getTime();
*/



        System.out.println(inTime + " // "+outTime);


       // ticketDAO.saveTicket(ticket);













        testParkingACar();

        parkingService.processExitingVehicle();

        testParkingACar();
        parkingService.processExitingVehicle();

        //TODO: check that the fare generated and out time are populated correctly in the database

        // Assertions

        assertEquals(
                true,
                ParkingService.getFidelity()
        );



        }




}
