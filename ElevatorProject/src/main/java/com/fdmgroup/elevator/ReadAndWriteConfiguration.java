package com.fdmgroup.elevator;

import java.io.File;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * ReadAndWriteConfiguration is responsible for writing the current status of each 
 * elevator to a json file when the program exits. This is to give the user an option to resume 
 * elevator tasks when the program is running again where it reads each elevator's 
 * status stored in the json file.
 * 
 * @author Elevator Team 1
 *
 */
public class ReadAndWriteConfiguration {
	
	private ArrayList<Elevator> eles;				// List of elevators
	
	
	Logger logger = LogManager.getLogger("com.fdmgroup.elevator.ReadAndWriteConfiguration");
	
	
	/**
	 * Constructor for ReadAndWriteConfiguration
	 * 
	 * @param elevators
	 */
	public ReadAndWriteConfiguration(ArrayList<Elevator> eles) {
		this.eles = eles;
	}
	
	
	/**
	 * Alternate constructor for ReadAndWriteConfiguration
	 */
	public ReadAndWriteConfiguration() {
		
	}
	
	
	/**
	 * Writes current status of all elevators to JSON file
	 * 
	 * @param destination file
	 * @param array list of elevators
	 */
	public boolean writeConfiguration(File destination, ArrayList<Elevator> eles) {
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValue(destination, eles);
			logger.trace("Successfully written into " + destination.getPath());
			return true;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}

	
	/**
	 * Reads information of all elevators from a JSON file and imports it
	 * 
	 * @param source file
	 * @return array list of elevators
	 */
	public ArrayList<Elevator> readConfiguration(File source) {
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			elevators = mapper.readValue(source, new TypeReference<ArrayList<Elevator>>() {});
			System.out.println("Successfully loaded.");
			logger.trace("Read successfully from " + source.getPath());
		} catch (IOException e) {
			System.out.println(e.getMessage());
			logger.error(e.getMessage());

		}
		
		return elevators;
	}
	

}
