package com.fdmgroup.elevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * ReadSystemConfiguration is responsible for reading a .txt file to set the
 * environment based on the information stored in that .txt file. It would
 * set the number of floors and number of elevators. This can be changed by
 * changing the configurations in the .txt file.
 * 
 * @author Elevator Team 1
 *
 */
public class ReadSystemConfiguration {
	
	private int maximumFloor;					// Number of floors
	private int maximumElevatorNumber;			// Number of elevators
	
	private File file = new File("Configurations (1).txt");
	
	Logger logger = LogManager.getLogger("com.fdmgroup.elevator.ReadSystemConfiguration");

	
	
	/**
	 * Constructor would call the readConfiguration method by default to read file
	 */
	public ReadSystemConfiguration() {
		readConfiguration();
	}
	
	
	/**
	 * Reads environment configuration file
	 */
	public void readConfiguration(){
		try {
			Scanner reader = new Scanner(file);
			logger.trace("Read from " + file.getPath());
			while (reader.hasNextLine()) {
				String[] separatedCommands = reader.nextLine().replaceAll(" ", "").split(",");
				
				setMaximumFloor(Integer.parseInt(separatedCommands[0].split(":")[1]));
				setMaximumElevatorNumber(Integer.parseInt(separatedCommands[1].split(":")[1]));
				logger.trace("Maximum Floor is : " + Integer.parseInt(separatedCommands[0].split(":")[1]));
				logger.trace("Maximum Elevator Number is : " + Integer.parseInt(separatedCommands[1].split(":")[1]));

			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error(e.getMessage());

		}
	}

	
	/**
	 * Retrieves number of floors
	 * 
	 * @return number of floors
	 */
	public int getMaximumFloor() {
		return maximumFloor;
	}

	
	/**
	 * Sets number of floors
	 * 
	 * @param number of floors
	 */
	public void setMaximumFloor(int maximumFloor) {
		this.maximumFloor = maximumFloor;
	}

	
	/**
	 * Retrieves number of elevators
	 * 
	 * @return number of elevators
	 */
	public int getMaximumElevatorNumber() {
		return maximumElevatorNumber;
	}

	
	/**
	 * Sets number of elevators
	 * 
	 * @param number of elevators
	 */
	public void setMaximumElevatorNumber(int maximumElevatorNumber) {
		this.maximumElevatorNumber = maximumElevatorNumber;
	}


	public void setFile(File file) {
		this.file = file;
		
	}

}
