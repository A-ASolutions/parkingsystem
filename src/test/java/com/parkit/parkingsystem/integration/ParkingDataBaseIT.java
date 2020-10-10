package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;

import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;

    @Mock
    private static InputReaderUtil inputReaderUtil;

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
    }

    @AfterAll
    private static void tearDown(){

    }
    //TODO: check that a ticket is actually saved in DB and Parking table is updated with availability

    /** To make this test, we are going tp create a variable and call it parkingNumber
     * this variable represents the parking spot.
     * After getting the car in the parking spot, we will make the parking spot unavailable by setting
     * the method set available to false for the parking spot that we used.
     *
     * @throws Exception
     */

    @Test
    public void testParkingACar() throws Exception {

        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        int parkingNumber = parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR);
        ParkingSpot parkingSpot = new ParkingSpot(parkingNumber, ParkingType.CAR, true);
        String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();
        parkingSpot.setAvailable(false);

        assertEquals(vehicleRegNumber, ticketDAO.getTicket(vehicleRegNumber).getVehicleRegNumber());
        assertFalse(parkingSpot.isAvailable());




    }
    //TODO: check that the fare generated and out time are populated correctly in the database

    /** To make this test we will get the VehicleRegistrationNumber and add
     * 3 seconds delay then assert that the generated fare and the out time
     * are not null.
     *
     * @throws Exception
     */

    @Test
    public void testParkingLotExit() throws Exception {
        testParkingACar();
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);


        String vehicleRegNumber = inputReaderUtil.readVehicleRegistrationNumber();

        Thread.sleep(3000);
        parkingService.processExitingVehicle();

        assertNotEquals(null, ticketDAO.getTicket(vehicleRegNumber).getPrice());
        assertNotEquals(null, ticketDAO.getTicket(vehicleRegNumber).getOutTime());





    }

}


