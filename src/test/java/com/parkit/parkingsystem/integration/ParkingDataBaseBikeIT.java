package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseBikeIT {

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
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

   // @Test
    public void testParkingABike(){

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability

        //Assertions
        // Is bike "ABCDEF" saved in DB
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

        String pattern = "yyyy-MM-dd HH:mm";
        SimpleDateFormat inTime = new SimpleDateFormat(pattern);
        String inDate = inTime.format(new Date());
        Date outTime = new Date();
        System.out.println(inDate);

        testParkingABike();
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

        System.out.println("");
        System.out.println("Test Recurent User");
        System.out.println("");


        testParkingABike();
        parkingService.processExitingVehicle();

        testParkingABike();
        parkingService.processExitingVehicle();

        //TODO: check that the fare generated and out time are populated correctly in the database

        // Assertions

        assertEquals(
                true,
                ParkingService.getFidelity()
        );

        }


}
