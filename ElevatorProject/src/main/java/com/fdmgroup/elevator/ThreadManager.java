package com.fdmgroup.elevator;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * ThreadManager class is responsible for creating and starting threads after
 * the user has selected a designated method to start the program.
 * 
 * 
 * @author Elevator Team 1
 *
 */
public class ThreadManager {
	String fileName = null;	
	Thread threadScheduler;
	Thread threadInput ;
	Thread threadCommands;
	
	Scanner scanner = new Scanner(System.in);
	ArrayList<Elevator> elevators = new ArrayList<>();
	List<Thread> threads;
	File elevatorJSON = new File("currentElevatorStatus.json");
	int maxFloor;
	
	
	Logger logger = LogManager.getLogger("com.fdmgroup.elevator.ThreadManager");

	
	
	/**
	 * Checks to see if user wants to create a new file or load an existing environment stored in the
	 * currentElevatorStatus.json file
	 * 
	 * @return fileName
	 */
	public String checkFileName() {
		
		logger.info("System starts");
		logger.info("**************");

		System.out.println("Please input N or R to start");		
		String input = scanner.nextLine();
		
		logger.info("Received input: " + input);

		
		if(input.equalsIgnoreCase("N")) {
			fileName = "";
			logger.info("Created new system");
		}
		
		else if(input.equalsIgnoreCase("R")) {
			fileName = "currentElevatorStatus.json";
			logger.info("Read from previous system");
		}
		
		else {
			System.out.println("Invalid input");
			logger.error("Invalid input");
		}
		//scanner.close();
		return fileName;
	}
	
	
	/**
	 * Retrieves number of floors
	 * 
	 * @return number of floors
	 */
	public int getMaxFloor() {

			return maxFloor;
	}
	
	
	/**
	 * Reads the file to retrieve the number of elevators
	 * 
	 * @return number of elevators
	 */
	public int initiateSystem() {
		ReadSystemConfiguration configure = new ReadSystemConfiguration();
		
		maxFloor = configure.getMaximumFloor();
		int elevatorNumber = configure.getMaximumElevatorNumber();
		
		ReadAndWriteConfiguration readWriteConfig = new ReadAndWriteConfiguration();
		String fileName = checkFileName();		
		File file = new File(fileName);			
	
		if (fileName.equals("")){
			for(int i =1; i<elevatorNumber+1; i++) {
				elevators.add(new Elevator(i));				
			}
		}
		else  {
			elevators = readWriteConfig.readConfiguration(file);
			
		}
		return elevators.size() ;
	}
	
	
	/**
	 * Creates the threads based on the given parameters for each elevator and starts them
	 */
	public void configuration() {

		Scheduler scheduler = new Scheduler(elevators);
		
		CommandGenerator commandGenerator = new CommandGenerator(scheduler, maxFloor, 3000, elevators);
				
		UserInput userInput = new UserInput(scheduler,commandGenerator, elevators);
		
		List<Thread> threads = new ArrayList<>(); 
		
		int i = 1;
		for (Elevator elevator : elevators) {
			Thread thread = new Thread(elevator);
			thread.setName("thread" + i);
			threads.add(thread);
			i++;				
		}		
		for (Thread thread : threads) {
			thread.start();
		}
		
		setThreads(threads);
		logger.info("Threads implemented.");


		threadScheduler = new Thread(scheduler);
		threadInput = new Thread(userInput);		
		threadCommands = new Thread(commandGenerator);
		
	}
		
	
	/**
	 * Retrieves list of elevators
	 * 
	 * @return list of elevators
	 */
	public ArrayList<Elevator> getElevators() {
		return elevators;
	}

	/**
	 * Sets list of elevators
	 * 
	 * @param list of elevators
	 */
	public void setElevators(ArrayList<Elevator> elevators) {
		this.elevators = elevators;
	}

	/*
	 * Starts userInput, scheduler and commandGenerator thread
	 */
	public void runElevator() {		
		threadInput.start();
		threadScheduler.start();	
		threadCommands.start();
		logger.info("All threads have been started.");

	}
	
	/**
	 * Retrieves list of threads
	 * 
	 * @return list of threads
	 */
	public List<Thread> getThreads() {
		return threads;
	}

	/**
	 * Sets list of threads
	 * 
	 * @param list of threads
	 */
	public void setThreads(List<Thread> threads) {
		this.threads = threads;
	}


	public void setMaxFloor(int maxFloor) {
		this.maxFloor = maxFloor;
	}
	
	


	
}



