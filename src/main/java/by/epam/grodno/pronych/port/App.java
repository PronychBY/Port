package by.epam.grodno.pronych.port;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;

import by.epam.grodno.pronych.port.entity.*;
import by.epam.grodno.pronych.port.service.Port;

/**
 *Порт. Корабли заходят в порт для разгрузки или 
 *загрузки контейнеров и швартуются к причалам. 
 *У каждого причала может стоять только один корабль. 
 *Контейнеры перегружаются с корабля на корабль или на склад порта. 
 *Число контейнеров не может превышать емкость склада или корабля. *
 */
public class App 
{
    final static Logger logger = Logger.getLogger(App.class);
    
	public static void main( String[] args )
    {
		logger.info("Begin working");
        
        for (int i = 0; i < 2; i++) {
            Dock dock = new Dock(i);
            Port.docks.add(dock);

        }

        List<Ship> listShip = new LinkedList<Ship>();
        for (int i = 0; i < 3; i++) {
            boolean toLoad = new Random().nextBoolean();
        	int capacity = 50 + new Random().nextInt(50);
        	int cargo;
        	if(toLoad) {
            	cargo = 0;
            }else{
            	cargo = new Random().nextInt(capacity);
            };
        	int waitingTime = 500 + new Random().nextInt(300);
        	
        	Ship ship = new Ship(i, capacity, cargo, waitingTime, toLoad);
            listShip.add(ship);
        }

        Port.docks.forEach(Thread::start);
        listShip.forEach(Thread::start);
        listShip.forEach(Port.shipQueue::add);
        
        try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
    }
}
