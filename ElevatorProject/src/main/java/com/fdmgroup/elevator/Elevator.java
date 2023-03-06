package com.fdmgroup.elevator;


import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Elevator class creates the elevators specified in the Configurations.txt file
 * and assigns them an elevatorId, currentFloor which would be at 1 by default and
 * also a queue of floors it has to go to. Each elevator is also assigned a state 
 * depending on its direction of travel and if it is idle or not.
 * 
 * @author Elevator Team 1
 *
 */
public class Elevator implements Runnable{
	
	private int elevatorId;											// Elevator ID
	private int currentFloor = 1;									// Current floor of elevator
	private static int time = 1000;									// Time for moving between floors and open/close doors
	private State state = State.IDLE;								// State of elevator

	ArrayList<Integer> queue = new ArrayList<Integer>();			// Queue of floors for elevator
	ArrayList<Integer> tempQueue = new ArrayList<Integer>();		// Temporary queue of floors

	
	Logger logger = LogManager.getLogger("com.fdmgroup.elevator.Elevator");

	
	
	/**
	 * Constructor for Elevator
	 * 
	 * @param elevatorId
	 * @param currentFloor
	 * @param queue
	 */
	@JsonCreator
	public Elevator (@JsonProperty("elevatorId")int elevatorId, @JsonProperty("currentFloor") int currentFloor, @JsonProperty("queue") ArrayList<Integer> queue) {
		this.elevatorId = elevatorId;
		this.currentFloor = currentFloor;
		this.queue = queue;
	}
	
	
	/**
	 * Alternate constructor for Elevator
	 * 
	 * @param elevatorId
	 */
	public Elevator (int elevatorId) {
		this.elevatorId = elevatorId;
	}
		

	/**
	 * Grabs current floor of elevator
	 * 
	 * @return the current floor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	
	/**
	 * Adds a destination/source floor to the queue
	 * 
	 * @param destination/source floor
	 */
	public void addQueue(int number) {
		queue.add(number);
	}
	
	
	/**
	 * Entry point for thread
	 */
	public void run() {
		while(true) {
		while(queue.size() > 0) {	
			changeState();
			
			logger.trace("Elevator " + elevatorId + " state " + state);
			logger.trace("Number of current Queue for elevator " + elevatorId + " is " + getQueue().size());

			
			while(currentFloor != queue.get(0)) {
				try {
					Thread.sleep(time);			
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.error(e.getMessage());

				}
				
				if (state == State.UP)
					currentFloor = currentFloor+1;
				else if (state == State.DOWN)
					currentFloor = currentFloor - 1;
				logger.trace("Elevator " + elevatorId + " current floor " + currentFloor);

			}
			
			openDoor();
			closeDoor();
		
			queue.remove(0);
			
		}
			setIdle();
		}
	}
	
	
	/**
	 * Sleeps for 1 second while opening door
	 */
	public boolean openDoor() {
		try {
			Thread.sleep(time);
			return true;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;

		}
		
	}
	
	
	/**
	 * Sleeps for 1 second while closing door
	 */
	public boolean closeDoor() {
		try {
			Thread.sleep(time);
			logger.info("Elevator " + elevatorId + " door closes.");
			return true;

		} catch (InterruptedException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;

		}
	}
	
	
	/**
	 * Changes state of elevator
	 */
	public boolean changeState() {
		if (queue.size() == 0) {
			state = State.IDLE;
			return true;
		}
		else if(currentFloor > queue.get(0)) {
			state = State.DOWN;
			return true;
		}
		else if(currentFloor < queue.get(0)) {
			state = State.UP;
			return true;
		}
		return false;
	}
	
	
	/**
	 * Changes elevator state to idle
	 */
	public void setIdle() {
		state = State.IDLE;
	}

	
	/**
	 * Retrieves state of elevator
	 * 
	 * @return state of elevator
	 */
	@JsonIgnore
	public State getState() {
		return state;
	}
	
	
	/**
	 * Replaces actual queue once it is sorted
	 * 
	 * @param assigns copyQueue to queue
	 */
	public void setQueue(ArrayList<Integer> copyQueue) {
		queue = copyQueue;
	}
	
	
	/**
	 * Retrieves existing queue
	 * 
	 * @return queue
	 */
	public ArrayList<Integer> getQueue() {
		return queue;
	}
	
	
	/**
	 * Retrieves tempQueue
	 * 
	 * @return tempQueue
	 */
	@JsonIgnore
	public ArrayList<Integer> getTempQueue() {
		return tempQueue;
	}
	

	
	@JsonIgnore
	public String getCommand() {
		// TODO Auto-generated method stub
		return "";
	}

	@JsonIgnore
	public int getPeople() {
		// TODO Auto-generated method stub
		return 0;
	}

	@JsonIgnore
	public String getInfo() {
		// TODO Auto-generated method stub
		return "";
	}
	

	/**
	 * Retrieves elevatorId
	 * 
	 * @return elevatorId
	 */
	public int getElevatorId() {
		return elevatorId;
	}
	
	
	/**
	 * Checks to see if elevator is idle
	 * 
	 * @return status of isIdle
	 */
	@JsonIgnore
	public boolean isIdle() {
		if (state == State.IDLE) {
			return true;
		}
		return false;
	}
	
	
	public void setUp() {
		state = State.UP;
	}
	
	public void setDown() {
		state = State.DOWN;
	}
	
}
