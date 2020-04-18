package by.epam.grodno.pronych.port.entity;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import by.epam.grodno.pronych.port.service.Port;

public class Dock extends Thread{
    final static Logger logger = Logger.getLogger(Dock.class);
	
	int id;
	Ship currentShip;
	Lock locker = new ReentrantLock();
	Condition cond = locker.newCondition();
	int waitingTime;
	int numberOfLoads;
	public Dock(int id) {
		this.id = id;
		numberOfLoads = 4;
		waitingTime = new Random().nextInt(5);
	}
	
	public void unload(Ship ship) {
		if (locker.tryLock()) {
			try {
				int canBeUnload = Port.capacity-Port.cargo;
				int toUnload = Math.min(canBeUnload, ship.cargo);
				Port.cargo = Port.cargo + toUnload;
				ship.cargo = ship.cargo - toUnload;
			}
			finally {
				locker.unlock();
			}
		}
	}
	
	public void load(Ship ship) {
		if (locker.tryLock()) {
			try {
				int canBeload = Port.cargo;
				int toload = Math.min(canBeload, ship.getCapacity() - ship.getCargo());
				Port.cargo = Port.cargo - toload;
				ship.setCargo(ship.getCargo() + toload);
		    	if (logger.isInfoEnabled())
		    		logger.info("Ship:" + ship.id + " loaded " + toload);
			}
			finally {
				locker.unlock();
			}
		}
	}
	
	@Override
	public void run() {
		//забрать из очереди или уснуть-если никого
		//обработать выгрузку или загрузку
		//если выгрузка или загрузка неполная тогда в конец очереди
		while(numberOfLoads > 0) {
			numberOfLoads--;
			currentShip = Port.getFormQueue();
			if (currentShip == null) {
				continue;
			}
			
			if (currentShip.toLoad) {
		    	if (logger.isInfoEnabled())
		    		logger.info("Load ship:"+currentShip.id);
				load(currentShip);
			}else {
		    	if (logger.isInfoEnabled())
		    		logger.info("Unload ship:"+currentShip.id);
				unload(currentShip);
			}
			
			try {
				Thread.sleep(waitingTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (currentShip.getCargo() != 0) {
		    	if (logger.isInfoEnabled())
		    		logger.info("Ship:"+currentShip.getId()+" not fully unloaded. Stay:"+currentShip.getCargo());
				if (currentShip.isAlive()) {
					Port.setToQueue(currentShip);
				}
				currentShip = null;
			}
		}
    	if (logger.isInfoEnabled())
    		logger.info("Doc:"+id+" finish work. In Port:"+Port.cargo);
	}

}
