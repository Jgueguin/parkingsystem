package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Executable;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseExitingBikeIT {

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
        //when(inputReaderUtil.readSelection()).thenReturn(2);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn("ABCDEF");
        //dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown(){

    }

    //@Test
    public void testExitingABike(){

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        System.out.println("1");

        try {
            parkingService.processExitingVehicle();
        }

        catch(NullPointerException aExp)
        {
        assert(aExp.getMessage().contains("quot" ));

        }

        System.out.println("2");

        //assertThrows(NullPointerException.class, () -> parkingService.processExitingVehicle());

        System.out.println("3");

        NullPointerException thrown =
                assertThrows(NullPointerException.class,
                        () -> parkingService.processExitingVehicle(),
                        "Expected doThing() to throw, but it didn't");

        assertTrue(thrown.getMessage().contains("Stuff"));


    }

    //@Test
    public void testExitingLotExit(){

        String pattern = "yyyy-MM-dd HH:mm:ss.0";
        SimpleDateFormat inTime = new SimpleDateFormat(pattern);
        String inDate = inTime.format(new Date());
        Date outTime = new Date();
        System.out.println(inDate);

        try {

            //ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
            testExitingABike();


            // Assertions

// >>>> Comment récupérer l'exception et s'en servir dans un assert ????

        }

        catch (Exception e) {

            System.out.println(e);
        }

        //assertThrows(NullPointerException.class, () -> parkingService.processExitingVehicle());



    }

}
