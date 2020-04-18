package by.epam.grodno.pronych.port.entity;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import by.epam.grodno.pronych.port.service.Port;

public class Ship extends Thread {
    final static Logger logger = Logger.getLogger(Ship.class);
	
	int capacity;
	int cargo;
	int id;
	int waitingTime;
	boolean toLoad;//load or unload ship

	Lock locker = new ReentrantLock();
	Condition cond = locker.newCondition();
	
	public Ship(int id, int capacity, int cargo, int waitingTime, boolean toLoad) {
		this.capacity = capacity;
		this.cargo = cargo;
		this.id = id;
		this.waitingTime = waitingTime;
		this.toLoad = toLoad;
	}

	public int getCargo() {
		return cargo;
	}

	public void setCargo(int cargo) {
		this.cargo = cargo;
	}

	public int unload(int cargoToUnload) {
		int returnCargo = cargoToUnload;
		if (locker.tryLock()) {
			try {
				int canBeUnload = capacity - cargo;
				int toUnload = Math.min(canBeUnload, cargoToUnload);
				cargo = cargo + toUnload;
				returnCargo = cargoToUnload - toUnload;
			}
			finally {
				locker.unlock();
			}
		}
		return returnCargo;
	}
	
	
	@Override
	public void run() {
    	if (logger.isInfoEnabled())
    		logger.info("Прибыл корабль:"+id+" cargo:"+getCargo()+" tL:"+toLoad);
		Port.shipQueue.add(this);
		
		try {
			Thread.sleep(waitingTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		if (toLoad) {
			if (getCargo() == getCapacity()) {
		    	if (logger.isInfoEnabled())
		    		logger.info("Уезжает загруженный корабль:"+id+" cargo:"+getCargo());
			}else {
		    	if (logger.isInfoEnabled())
		    		logger.info("Уезжает незагруженный корабль"+id+" cargo:"+getCargo());
			}
		}else {
			if (getCargo() == 0) {
		    	if (logger.isInfoEnabled())
		    		logger.info("Уезжает разгруженный корабль:"+id+" cargo:"+getCargo());
			}else {
		    	if (logger.isInfoEnabled())
		    		logger.info("Уезжает неразгруженный корабль"+id+" cargo:"+getCargo());
			}
		}
	}

	public int getCapacity() {
		return capacity;
	}


}
