package by.epam.grodno.pronych.port;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import by.epam.grodno.pronych.port.entity.Dock;
import by.epam.grodno.pronych.port.entity.Ship;
import by.epam.grodno.pronych.port.service.Port;

public class AppTest 
{

	@Test
	public void testUnloadShips() {
        Port.docks.clear();
        Port.cargo = 0;
        for (int i = 0; i < 2; i++) {
            Dock dock = new Dock(i);
            Port.docks.add(dock);
        }

        List<Ship> listShip = new LinkedList<Ship>();
        for (int i = 0; i < 3; i++) {
            boolean toLoad = false;
        	int capacity = 50;
        	int cargo = 25;
        	int waitingTime = 500;
        	
        	Ship ship = new Ship(i, capacity, cargo, waitingTime, toLoad);
            listShip.add(ship);
        }

        Port.docks.forEach(Thread::start);
        listShip.forEach(Thread::start);
        listShip.forEach(Port.shipQueue::add);
		
        try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		Assert.assertEquals(0, listShip.get(0).getCargo());
		Assert.assertEquals(0, listShip.get(1).getCargo());
		Assert.assertEquals(0, listShip.get(2).getCargo());
		
		Assert.assertEquals(75, Port.cargo);
	}
    
	@Test
	public void testLoadShips() {
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        Port.docks.clear();
        Port.cargo = 0;
        for (int i = 0; i < 2; i++) {
            Dock dock = new Dock(i);
            Port.docks.add(dock);
        }

    	List<Ship> listShip = new LinkedList<Ship>();
    	Ship ship = new Ship(0, 100, 100, 500, false);
        listShip.add(ship);
        
        for (int i = 1; i < 4; i++) {
            boolean toLoad = true;
        	int capacity = 25;
        	int cargo = 0;
        	int waitingTime = 500;
        	
        	ship = new Ship(i, capacity, cargo, waitingTime, toLoad);
            listShip.add(ship);
        }

        Port.docks.forEach(Thread::start);
        listShip.forEach(Thread::start);
        listShip.forEach(Port.shipQueue::add);
		
        try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		
		Assert.assertEquals(0, listShip.get(0).getCargo());
		Assert.assertEquals(25, listShip.get(1).getCargo());
		Assert.assertEquals(25, listShip.get(2).getCargo());
		Assert.assertEquals(25, listShip.get(3).getCargo());
		
		Assert.assertEquals(25, Port.cargo);
	}
}
