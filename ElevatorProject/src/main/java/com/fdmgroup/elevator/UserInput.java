package com.fdmgroup.elevator;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * UserInput class is responsible for taking in inputs and validating them.
 * It would also change the corresponding booleans according to the input
 * so that it would run a certain feature such as morning mode, evening mode
 * and normal mode. The class also has functions to exit the program as well
 * as calling the ReadAndWriteConfiguration class to 'save' or 'load' json
 * files with existing elevators.
 * 
 * 
 * @author Elevator Team 1
 *
 */
public class UserInput implements Runnable{
	
	private boolean isCommandA = false;
	private String[] commands = null;
	private String commandForAuto = null;
	Scheduler scheduler;
	CommandGenerator commandGenerator;
	private String [] methodArray = new String[] {"morning","evening","normal","slow","busy"};
	
	
	ArrayList<Elevator> elevators;
	
	Logger logger = LogManager.getLogger("com.fdmgroup.elevator.UserInput");

	
	
	
	/**
	 * Constructor that passes in calculator and commandGenerator
	 * 
	 * @param calculator Calculator
	 * @param commandGenerator CommandGenerator
	 */
	public UserInput(Scheduler scheduler, CommandGenerator commandGenerator, ArrayList<Elevator> elevators) {
		this.scheduler = scheduler;
		this.commandGenerator = commandGenerator;
		this.elevators = elevators;
		logger.info("UserInput object created.");
	}
	
	
	/**
	 * Calls on splitCommands method
	 * 
	 * @param input  input from user
	 * @return Array of input commands
	 */
	public String[] convertInput(String input){
			return splitCommands(input);
	}
	
	
	/**
	 * This method checks if the input is a operation command, and will call the printState method if true
	 * If the input is not an operation command, it will call the validate input method to prompt the user
	 * to input a valid command
	 * 
	 * @param input input from User
	 */
	public void readInput(String input)
	{

		if(isOperationCommand(input)){
			printState(input);
			commandForAuto = input;
		}else if(validateInput(input)) {
			commands = convertInput(input);
		}
	}
	
	
	/**
	 * This method checks if the input is an operation command. It will return true for only "evening",
	 *  "morning", "normal", "A" and "E" inputs
	 *  
	 * @param input 
	 * @return true or false 
	 */
	public boolean isOperationCommand(String input) {
		String uppercaseInput = input.toUpperCase();
		if(uppercaseInput.length()== 1 && uppercaseInput.matches("[AE]")) {
			return true;
		}
		else if(checkMethod(input)) {			
			return true;
	}
		else
			return false;
	}
	
	public boolean checkMethod(String input){
		for(String value: methodArray) {
			if(input.equalsIgnoreCase(value))
				return true;			
		}	
		return false;
	}
	

	/**
	 * Checks inputs and prints statement based on input
	 * 
	 * @param input
	 */
	public void printState(String input) {
		
		 if(input.equalsIgnoreCase("A"))
			{
			 if(!isCommandA) {
				System.out.println("Automate command called");
				logger.info("Automated commands called");
				isCommandA = true;
			 }
			 else {
				 System.out.println("Automate command cancelled");
				 logger.info("Automated commands cancelled");
				 isCommandA = false;
			 }
			}
			else if(input.equalsIgnoreCase("E")) //exits and stops the program
			{
				System.out.println("Exiting");
				logger.info("Program exiting");
				
				ReadAndWriteConfiguration writeConfig = new ReadAndWriteConfiguration(elevators);
				
				commandGenerator.setBoolean();
				
				File file = new File("currentElevatorStatus.json");
				
				writeConfig.writeConfiguration(file, elevators);
				
				try {
					Thread.sleep(1000);
					logger.trace("Configuration written into " + file.getPath());
				} catch (InterruptedException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				
				logger.info("Program exiting.");
				System.exit(0);
			}

			else
				System.out.println("Automating mode change to " + input);
		 		logger.trace("Changing automating mode to " + input);
		 
	}
	
	
	/**
	 * This method validates string input to ensure that commands are entered correctly and that input is in the 
	 * correct format 
	 * 
	 * @param input
	 * @return true or false
	 */
	public boolean validateInput(String input)
	{
		
		if (input.toUpperCase().matches("[AEPS]") && input.length() == 1) {
			return true;
		}
		
		else if (input.matches("\\d{1}:\\d{1}")) {
			return true;
		}
		else if (input.length() > 4) {
			return true;
		}
		
		logger.error("Invalid input format");
		System.out.println("Invalid input format");
		return false;
		
	}
	
	
	/**
	 * This method will split the commands at "," and insert them into a String array
	 * 
	 * @param command
	 * @return Array of commands
	 */
	public String[] splitCommands(String command) {
		String[] commands = command.split(",");
		return commands;
	}
	
	
	/**
	 * Retrieves commands
	 * 
	 * @return String array of commands
	 */
	public String[] getCommands() {
		return commands;
	}


	
	/**
	 * Entry point for thread
	 * 
     * This method will create a new scanner object to receive input from the user. It will remain open while the programming is running.
     * If commands are not null, Scheduler will set the commands and the run method in Scheduler will be called.
     * If commandForAuto is not null , meaning an "A", "morning", "evening", "normal" was passed in, the runCommandGenerator will be called.
     * 
     */
	@Override
	public void run() {
			
		Scanner scanner = new Scanner(System.in);
		
		while(true) { 				
		System.out.println("Please input a command: ");

		String input = scanner.nextLine();
		
		logger.trace("Received Input");

		
		readInput(input);
		if(commands != null) {
			scheduler.setCommands(commands);
			scheduler.run();
		}
		else if(commandForAuto != null){
			runCommandGenerator(commandForAuto);
		}

		commands = null;
		commandForAuto = null;
		}
		//}
		
	}
	
	
	/**
	 * This method checks which automatic command was called. It will change the floor numbers and timing in 
	 * Command generator class to mimic different times of day. 
	 * 
	 */
	public void runCommandGenerator(String commandForAuto){
		if(commandForAuto.equalsIgnoreCase("A")) 
			 commandGenerator.setBoolean();		
		else if(commandForAuto.equalsIgnoreCase("morning"))
			commandGenerator.changeMorning();
		else if(commandForAuto.equalsIgnoreCase("evening"))
		commandGenerator.changeEvening();
		else if(commandForAuto.equalsIgnoreCase("normal"))
			commandGenerator.changeNormal();
		else if(commandForAuto.equalsIgnoreCase("busy"))
			commandGenerator.setGenerateInterval("busy");
		else if(commandForAuto.equalsIgnoreCase("slow"))
			commandGenerator.setGenerateInterval("slow");
	}
	
	
	public String getCommandForAuto() {
		return commandForAuto;
	}

	public boolean getIsCommandA() {
		return isCommandA;
	}	
	public void setIsCommandA(boolean isCommandA) {
		this.isCommandA = isCommandA;
	}


	

}
