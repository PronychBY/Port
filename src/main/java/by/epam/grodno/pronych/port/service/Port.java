package by.epam.grodno.pronych.port.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import by.epam.grodno.pronych.port.entity.*;


public class Port {
    public static int capacity=100;
    public static int cargo=0;
	public static List<Dock> docks = new ArrayList<Dock>();
    
    public static BlockingQueue<Ship> shipQueue = new LinkedBlockingQueue<Ship>();
   
	public static Lock locker = new ReentrantLock();
	public static Condition cond = locker.newCondition();
    
    final static Logger logger = Logger.getLogger(Port.class);
	
	public static Ship getFormQueue() {
    	if (logger.isInfoEnabled())
		logger.info("Begin get from queue"+Thread.currentThread().getId());
		
    	locker.lock(); 
        try{
        	if (logger.isInfoEnabled())
        		logger.info("try to get from queue:"+Thread.currentThread().getId()+" sq:"+shipQueue.size());
            while (shipQueue.size() == 0) {
            	//System.out.println("Doc begin sleep:"+Thread.currentThread().getId()+" sq:"+shipQueue.size());
            	cond.await(500L, TimeUnit.MILLISECONDS);
            }
        	if (logger.isInfoEnabled())
        		logger.info("get from queue"+Thread.currentThread().getId());
            Ship ship = shipQueue.poll();
			if (!ship.isAlive()) {
				return null;
			}
            return ship;
        }
        catch(InterruptedException e){
            Thread.currentThread().interrupt();
       		logger.error("Interrupter exeception in thread:"+Thread.currentThread().getId());
        	//e.printStackTrace();
        }
        finally{
        	locker.unlock();
        }
        return null;
    }

	public static void setToQueue(Ship ship) {
		shipQueue.add(ship);
		try {
			cond.signalAll();
		}
        catch(IllegalMonitorStateException e){
    	
        }
	}
	
	
}
