package com.fdmgroup.elevator;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Scheduler class is responsible for calculating the next best elevator 
 * to assign inputs to and then assigns them to the designated elevator. It
 * does so in the order of checking whether there are idle elevators, then
 * checks to see if there are any elevators that are moving in the same direction
 * to pick them up on the way and then finally checks which elevator would take
 * the shortest time possible to complete its current tasks and reach the next
 * source floor first.
 * 
 * @author Elevator Team 1
 *
 */
public class Scheduler implements Runnable {
	

	private int time;													// Variable to store time it takes for elevators to complete tasks
	private TimeCalculator timeCalculator = new TimeCalculator();		// timeCalculator object to calculate time for completion
	private ArrayList<Elevator> elevators;								// List of elevators
	private String [] commands;											// Array that would contain new inputs
	
	
	Logger logger = LogManager.getLogger("com.fdmgroup.elevator.Scheduler");


	
	/**
	 * Constructor for calculator
	 * 
	 * @param elevators
	 */
	public Scheduler(ArrayList<Elevator> elevators) {
		this.elevators = elevators;
	}
	
	
	/**
	 * Entry point for thread
	 */
	@Override
	public void run() {
		
		// If there are commands, feed them into the calculator
		if(commands != null) {
			runScheduler(elevators, commands);
		}
	}
	
	
	/**
	 * Gets inputs and calculates the best elevator to assign the input to
	 * 
	 * @param elevators
	 * @param new inputs as an array
	 */
	public boolean runScheduler(ArrayList<Elevator> elevators, String[] commands) {
		int[] integerArray = new int[2];
		for (String command : commands) {
			
		getCommand(command, integerArray);
		
		System.out.println("\nInput command: " + command);
		logger.trace("Input command: " + command);

			
			
		if(isIdleElevator(elevators)) {
			
			for(Elevator elevator: elevators) {
				
				if(elevator.isIdle()) {
					
					assignIdleElevator(elevator, integerArray);
					
					System.out.println("There is IDLE elevator, input is assigned to elevator "+ elevator.getElevatorId());
					logger.trace("There is IDLE elevator, input is assigned to elevator " + elevator.getElevatorId());

					
					elevator.changeState();

					printQueue(elevator);
					return true;
				}
			}
		}
		else {
			
			int indexOfBestElevator = calculateTime(elevators, integerArray);
			Elevator bestElevator = elevators.get(indexOfBestElevator);
			
			assignElevator(bestElevator, integerArray, bestElevator.getQueue() );
			
			logger.trace("There is no IDLE elevator, input assigned to elevator " + bestElevator.getElevatorId());
			System.out.println("There is no IDLE elevator, input assigned to elevator "+ bestElevator.getElevatorId());
			
			printQueue(bestElevator);
			return true;
		}
			
		}
		return false;
	}
	
	
	/**
	 * Prints current queue of elevators
	 * 
	 * @param elevator
	 */
	public boolean printQueue(Elevator elevator) {
		
		logger.trace("Current Queue for elevotor" + elevator.getElevatorId());
		System.out.println("Current Queue for elevotor"+elevator.getElevatorId());
		for(Integer x: elevator.getQueue()) {
			System.out.print(x + " ");
			logger.trace(x + " ");
		}
		return true;
	}
	
	
	/**
	 * Checks if elevator is idle
	 * 
	 * @param elevators
	 * @return whether elevator is idle
	 */
	public boolean isIdleElevator(ArrayList<Elevator> elevators) {
		for(Elevator elevator: elevators) {
			if(elevator.isIdle())
				return true;
		}
				return false;
	}
	
	
	/**
	 * Inserting input of source and destination into Array
	 * 
	 * @param input of source and destination
	 * @param Array of integers
	 */
	public int[] getCommand(String command, int[]integerArray ) {
	
			String[] inputs = command.split(":");
			
			for (int i = 0 ; i < integerArray.length ; i++) {
				integerArray[i] = Integer.parseInt(inputs[i]);
			}
			
			return integerArray;
		
	}	

	/**
	 * Assigning inputs to an idle elevator
	 * 
	 * @param elevator
	 * @param inputs of source and destination floor
	 */
	public void assignIdleElevator(Elevator elevator, int[] input) {
			if(elevator.getState() == State.IDLE) {
				elevator.queue.add(input[0]);
				elevator.queue.add(input[1]);
				logger.trace("This command " + input[0] + ":" + input[1] + " has been assigned to IDLE Elevator " + elevator.getElevatorId());

		}
	}
	
	
	/**
	 * Assigning inputs to moving elevator
	 * 
	 * @param elevator
	 * @param inputs of source and destination floor
	 * @param existing queue of elevator
	 */
	public void assignElevator(Elevator elevator, int[] input, ArrayList<Integer> queue) {		
	
			if(checkSameDirection(elevator, input) && checkFloor(elevator, input)) {				
				if(elevator.getState() == State.UP) {
					
					// Inserts the source and destination floor in between floors
					for (int i = 0 ; i < elevator.queue.size() -1 ; i++) {						
						if (queue.get(i) < input[0] && queue.get(i+1) > input[0]) {
							queue.add(i, input[0]);
							logger.trace("Input " + input[0] + " has been added to queue position " + i);

						}
						
						if (queue.get(i) < input[1] && queue.get(i+1) > input[1]) {
							queue.add(i, input[1]);
							logger.trace("Input " + input[1] + " has been added to queue position " + i);

							break;
						}						
						else if (queue.get(i) > queue.get(i+1)) {
							queue.add(i, input[1]);
							logger.trace("Input " + input[1] + " has been added to queue position " + i);

							break;
						}						
						else if ((i+1) == queue.size() -1) {
							queue.add(i, input[1]);
							logger.trace("Input " + input[1] + " has been added to queue position " + i);

							break;
						}
					}					

				}
				else if(elevator.getState() == State.DOWN) {
					
					for (int i = 0 ; i < queue.size() -1 ; i++) {
						
						if (queue.get(i) > input[0] && queue.get(i+1) < input[0]) {
							queue.add(i, input[0]);
							logger.trace("Input " + input[0] + " has been added to queue position " + i);

						}
						
						if (queue.get(i) > input[1] && queue.get(i+1) < input[1]) {
							queue.add(i, input[1]);
							logger.trace("Input " + input[1] + " has been added to queue position " + i);

							break;
						}
						
						else if (queue.get(i) < queue.get(i+1)) {
							queue.add(i, input[1]);
							logger.trace("Input " + input[1] + " has been added to queue position " + i);

							break;
						}
						
						else if ((i+1) == queue.size() -1) {
							queue.add(i, input[1]);
							logger.trace("Input " + input[1] + " has been added to queue position " + i);

							break;
						}
					}									
				}
			
			}
			else {
				queue.add(input[0]);
				queue.add(input[1]);
				logger.trace("This command " + input[0] + ":" + input[1] + " has been assigned to MOVING Elevator " + elevator.getElevatorId());

			}		
		}
	
	
	/**
	 * Calculates time it takes for each lift to complete existing queue and reach next source floor
	 * 
	 * @param elevators
	 * @param inputs of source and destination floor
	 * @return time required for each elevator to complete its queue
	 */
	public int calculateTime(ArrayList<Elevator> elevators,int[] input ) {
		ArrayList<Integer> times = new ArrayList<Integer>();
		
		for(Elevator elevator: elevators) {
		 assignElevator(elevator, input, elevator.getTempQueue());
		 if(checkSameDirection(elevator, input) && checkFloor(elevator, input)){
			 time = timeCalculator.calculateForSameDirection(elevator, input[0]);
		 }else
			time = timeCalculator.calculateForOtherCase(elevator, input[0]); 
		 	times.add(time);
		}
		
		int smallestTime = Collections.min(times);
		return times.indexOf(smallestTime);
	}
	
	
	/**
	 * Checks if input floor is 'on the way' from current elevator task
	 * 
	 * @param elevator
	 * @param inputs of source and destination floor
	 * @return whether source floor will be 'on the way'
	 */
	public boolean checkFloor(Elevator elevator, int[] input) {
		if(checkInput(input) && elevator.getCurrentFloor() < input[0])
			return true;
		else if (!checkInput(input) && elevator.getCurrentFloor() > input[0])
			return true;
		else
			return false;
	}
	
	
	/**
	 * Checks if elevator is moving in same direction as the input
	 * 
	 * @param elevator
	 * @param inputs of source and destination floor
	 * @return whether they are moving in same direction
	 */
	public boolean checkSameDirection(Elevator elevator, int[] input) {
		if(elevator.getState()== State.UP && checkInput(input))
			return true;
		else if (elevator.getState()== State.DOWN && !checkInput(input))
			return true;
		else
			return false;
	}
	
	
	/**
	 * Checks if input task is moving up or down
	 * 
	 * @param inputs of source and destination floor
	 * @return whether it is moving up or down
	 */
	public boolean checkInput(int[] input) {
		if(input[0] < input[1])
			return true;
		else
			return false;
		
	}
	
	
	/**
	 * Sets input to a new set of commands
	 * 
	 * @param new array of inputs
	 */
	public void setCommands(String [] commands) {
		this.commands = commands;
	}
	
	/**
	 * Returns commands
	 * 
	 * @return commands
	 */
	public String[] getCommands() {
		return commands;
	}


}
