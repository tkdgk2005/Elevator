package com.fdmgroup.elevator;

import java.util.ArrayList;

/**
 * TimeCalculator class is responsible for calculating the time it takes for each
 * elevator to finish its queue to determine which elevator should take in the new
 * inputs to maintain efficiency.
 * 
 * 
 * @author Elevator Team 1
 *
 */
public class TimeCalculator {
	int floorDifference = 0;
	
	
	/**
	 * This method  calculates the total time needed for an elevator to reach the source if it is not going in
	 * the same direction. It takes into account the amount of time to complete the commands in it's queue, and 
	 * factors in the time for opening and closing doors. 
	 * 
	 * @param elevator Elevator object
	 * @param source source floor from command
	 * @return time needed to reach the source floor
	 */
	public int calculateForOtherCase(Elevator elevator, int source) {
	ArrayList<Integer> floors = elevator.getQueue();
	
	
	int floorDifference = 0;
	int timeToSource = 0;
	
	for (int i = 0 ; i < floors.size()-1 ; i++) {
		if (floors.get(i+1) > floors.get(i)) {
			floorDifference = (floors.get(i+1) - floors.get(i)) + floorDifference;
		} else if (floors.get(i) > floors.get(i+1)) {
				floorDifference = (floors.get(i) - floors.get(i+1)) + floorDifference;
			}
	}
	
	if (floors.size() != 0) {
	
	if (floors.get(floors.size() - 1) > source) {
		timeToSource = floors.get(floors.size() - 1) - source;
	}
	else if (floors.get(floors.size() - 1) < source) {
		timeToSource = source - floors.get(floors.size() - 1);
	}
	
	}
	
	int timeForDoors = floors.size() * 2;
	int timeNeeded = timeForDoors + floorDifference + timeToSource;
	return timeNeeded;
	}
	
	
	/**
	 * If the elevator is going in the same direction as the command input it will return
	 * the floor difference of the elevator in relation to the source input.
	 * 
	 * @param elevator
	 * @param source
	 * @return floor difference
	 */
	public int calculateForSameDirection(Elevator elevator, int source) {
		for(int i=0; i < source; i++) {		
			floorDifference = floorDifference + 1;
		}
		
		return floorDifference;
	}
	
}
