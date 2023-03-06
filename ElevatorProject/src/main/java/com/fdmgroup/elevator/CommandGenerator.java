package com.fdmgroup.elevator;

import java.util.ArrayList;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * CommandGenerator class is responsible for creating automatically generated
 * commands and feeds it into the input. It's able to run on different modes
 * such as morning, evening and normal where morning inputs would start only 
 * from the ground floor and evening inputs would end only at the ground floor.
 * The interval at which commands are generated can also be modified to busy
 * or slow.
 * 
 * @author Elevator Team 1
 *
 */
public class CommandGenerator implements Runnable{
	
	private final int maxFloor;						// Number of floors
	private int generateInterval;					// Interval at which commands are generated
	private int floor1;								// Source floor
	private int floor2;								// Destination floor
	private boolean isMorning = true;				// Boolean for morning hours
	private boolean isEvening = false;				// Boolean for evening hours
	private boolean isInputA = false;				// Boolean for receiving input 'A'
	
	private String[] commands;						// Array of inputs
	private Scheduler scheduler;					// scheduler object to run scheduler
	private ArrayList<Elevator> elevators;			// List of elevators
	
	Random random = new Random();
	
	Logger logger = LogManager.getLogger("com.fdmgroup.elevator.CommandGenerator");

	
	
	/**
	 * Constructor for CommandGenerator
	 * 
	 * @param scheduler
	 * @param maxFloor
	 * @param interval of command generation
	 * @param array list of elevators
	 */
	public CommandGenerator(Scheduler scehduler, int maxFloor, int generateInterval, ArrayList<Elevator> elevators) {
		this.scheduler = scehduler;
		this.maxFloor = maxFloor;
		this.generateInterval = generateInterval;
		this.elevators = elevators;
		logger.info("Command Generator object created");

	}
	
	
	/**
	 * Alternate constructor for CommandGenerator
	 */
	public CommandGenerator() {
		this.maxFloor = 10;
	}
	
	
	/**
	 * Changes the interval of command generation
	 * 
	 * @param interval
	 */
	public void setGenerateInterval(String interval) {
		if (interval.equals("busy")) {
			generateInterval = 1500;
		}
		
		else if (interval.equals("slow")) {
			generateInterval = 5000;
		}
	}
	
	
	/**
	 * Generation method to mimic daytime hours
	 */
	public void setDay() {
		floor1 = 1;
		floor2 = random.nextInt(maxFloor) + 1;
		
		while (floor1 == floor2) {
			floor2 = random.nextInt(maxFloor) + 1;
		}
	}
	
	
	/**
	 * Generation method to mimic normal hours
	 */
	public void setNormal() {
		
		floor1 = random.nextInt(maxFloor) + 1;
		floor2 = random.nextInt(maxFloor) + 1;
		
		while (floor1 == floor2) {
			floor2 = random.nextInt(maxFloor) + 1;
		}
	}
	
	
	/**
	 * Generation method to mimic evening hours
	 */
	public void setEvening() {
		floor1 = random.nextInt(maxFloor) + 1;
		floor2 = 1;
		
		while (floor1 == floor2) {
			floor2 = random.nextInt(maxFloor) + 1;
		}
	}
	
	
	/**
	 * Changes generation method to daytime
	 */
	public void changeMorning() {
		isMorning = true;
		isEvening = false;
	}
	
	
	/**
	 * Changes generation method to evening hours
	 */
	public void changeEvening() {
		isMorning = false;
		isEvening = true;
	}
	
	
	/**
	 * Changes generation method to normal hours
	 */
	public void changeNormal() {
		isMorning = false;
		isEvening = false;
	}
	
	
	/**
	 * Switches boolean state to receive inputs
	 */
	public void setBoolean() {
		if(isInputA)
			isInputA = false;
		else
			isInputA = true;
	}
	
	/**
	 * Runs the automatically generated commands
	 */
	public void runAutoCommand() {

		scheduler.setCommands(commands);
		scheduler.runScheduler(elevators, commands);
	}

	
	/**
	 * Entry point for thread
	 */
	@Override
	public void run() {

		// While program is running, generate commands at specified interval and check to see if booleans have been changed
		while(true) {
		try {
				Thread.sleep(generateInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				break;
			}
		
			if (isMorning) {
				setDay();
			}			
			else if (isEvening) {
				setEvening();
			}
			
			else if (!isMorning && !isEvening) {
				setNormal();
			}
						
			String sourceFloor = Integer.toString(floor1);
			String destinationFloor = Integer.toString(floor2);
			
			String command = sourceFloor + ":" + destinationFloor;
			commands = new String[1];
			commands[0] = command;
			
			if(isInputA) {
				scheduler.setCommands(commands);
				scheduler.runScheduler(elevators, commands);
			}
								
		}
			
	}
	
	public boolean getIsMorning() {
		return isMorning;
	}
	public boolean getIsEvening() {
		return isEvening;
	}
	
	public int getGenerateInterval() {
		return generateInterval;
	}


	public boolean getIsInputA() {
		return isInputA;
	}


	public void setIsInputA(boolean isInputA) {
		this.isInputA = isInputA;
	}


	public int getFloor1() {
		return floor1;
	}


	public void setFloor1(int floor1) {
		this.floor1 = floor1;
	}


	public int getFloor2() {
		return floor2;
	}


	public void setFloor2(int floor2) {
		this.floor2 = floor2;
	}


	public String[] getCommands() {
		return commands;
	}


	public void setCommands(String[] commands) {
		this.commands = commands;
	}


	public ArrayList<Elevator> getElevators() {
		return elevators;
	}


	public void setElevators(ArrayList<Elevator> elevators) {
		this.elevators = elevators;
	}


	public int getMaxFloor() {
		return maxFloor;
	}


	public void setGenerateInterval(int generateInterval) {
		this.generateInterval = generateInterval;
	}


	public void setMorning(boolean isMorning) {
		this.isMorning = isMorning;
	}


	public void setEvening(boolean isEvening) {
		this.isEvening = isEvening;
	}


	public void setInputA(boolean isInputA) {
		this.isInputA = isInputA;
	}
	
	

	
	
	

}
