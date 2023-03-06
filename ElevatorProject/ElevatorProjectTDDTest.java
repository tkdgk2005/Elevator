package com.fdmgroup.elevator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ElevatorProjectTDDTest {
	
	@Mock
	ReadAndWriteConfiguration readAndWriteConfiguration;

	@BeforeEach
	void setUp() throws Exception {
		
	}

	@Test
	void test_elevator_constructor() {
		
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(2);
		Elevator elevator = new Elevator(2, 2, queue);
		
		assertEquals(queue, elevator.getQueue());
		assertEquals(2, elevator.getElevatorId());
		assertEquals(2, elevator.getCurrentFloor());
	}
	
	@Test
	void test_elevator_add_queue() {
		Elevator elevator = new Elevator(0);
		
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(2);
		
		elevator.addQueue(2);
		
		assertEquals(queue, elevator.getQueue());
	}
	
	@Test
	void test_elevator_change_state_idle() {
		Elevator elevator = new Elevator(1);
		
		elevator.changeState();
		
		assertEquals(State.IDLE, elevator.getState());
	}
	
	@Test
	void test_elevator_change_state_down() {
		
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(1);
		Elevator elevator = new Elevator(1, 2, queue);
		
		elevator.changeState();
		
		assertEquals(State.DOWN, elevator.getState());
	}
	
	@Test
	void test_elevator_change_state_up() {
		
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(3);
		Elevator elevator = new Elevator(1, 2, queue);
		
		elevator.changeState();
		
		assertEquals(State.UP, elevator.getState());
	}
	
	@Test
	void test_elevator_is_idle_true() {
		Elevator elevator = new Elevator(0);
		
		boolean expected = true;
		
		assertEquals(expected, elevator.isIdle());
	}
	
	@Test
	void test_elevator_is_idle_false() {
		Elevator elevator = new Elevator(0);
		elevator.queue.add(2);
		elevator.changeState();
		
		boolean expected = false;
		
		assertEquals(expected, elevator.isIdle());
	}
	
	@Test
	void test_calculator_constructor() {
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		
		assertEquals(elevators, calculator.getElevators());
	}

	@Test
	void test_calculator_is_idle_elevator_true() {
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		elevators.add(new Elevator(1));
		elevators.add(new Elevator(2));
		Calculator calculator = new Calculator(elevators);
		
		assertEquals(true, calculator.isIdleElevator(elevators));
	}
	
	@Test
	void test_calculator_is_idle_elevator_false() {
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		
		assertEquals(false, calculator.isIdleElevator(elevators));
	}
	
	@Test
	void test_calculator_assign_idle_elevator() {
		
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(1);
		queue.add(3);
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		Elevator elevator = new Elevator(1);
		calculator.assignIdleElevator(elevator, new int[] {1, 3});
		
		assertEquals(queue, elevator.getQueue());
	}
	
	@Test
	void test_calculator_check_input_returns_true() {
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		
		assertEquals(true, calculator.checkInput(new int[] {1, 3}));
	}
	
	@Test
	void test_calculator_check_input_returns_false() {
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		
		assertEquals(false, calculator.checkInput(new int[] {3, 2}));
	}
	
	@Test
	void test_calculator_check_floor_returns_true_if_going_up() {
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		Elevator elevator = new Elevator(1);
		
		assertEquals(true, calculator.checkFloor(elevator, new int[] {2, 3}));
	}
	
	@Test
	void test_calculator_check_floor_returns_true_if_going_down() {
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		Elevator elevator = new Elevator(1, 5, new ArrayList<Integer>());
		
		assertEquals(true, calculator.checkFloor(elevator, new int[] {4, 2}));
	}

	@Test
	void test_calculator_check_floor_returns_false_when_idle() {
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		Elevator elevator = new Elevator(1);
		
		assertEquals(false, calculator.checkFloor(elevator, new int[] {1, 3}));
	}
	
	@Test
	void test_calculator_check_same_direction_returns_true_when_going_up() {
		
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(2);
		queue.add(3);
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		Elevator elevator = new Elevator(1, 1, queue);
		elevator.changeState();
		
		assertEquals(true, calculator.checkSameDirection(elevator, new int[] {1, 3}));
	}
	
	@Test
	void test_calculator_check_same_direction_returns_true_when_going_down() {
		
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(2);
		queue.add(3);
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		Elevator elevator = new Elevator(1, 6, queue);
		elevator.changeState();
		
		assertEquals(true, calculator.checkSameDirection(elevator, new int[] {5, 3}));
	}
	
	@Test
	void test_calculator_check_same_direction_returns_false_when_idle() {
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Calculator calculator = new Calculator(elevators);
		Elevator elevator = new Elevator(1);
		
		assertEquals(false, calculator.checkSameDirection(elevator, new int[] {5, 3}));
	}
	
	@Test
	void test_read_and_write_configuration_read_configuration_from_file_source() {
		
		ReadAndWriteConfiguration readAndWriteConfiguration = new ReadAndWriteConfiguration();
		
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(1);
		queue.add(3);
		queue.add(5);
		
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		Elevator elevator = new Elevator(1, 1, queue);
		elevators.add(elevator);
		
		assertEquals(elevators.get(0).getCurrentFloor(), readAndWriteConfiguration.readConfiguration(new File("currentElevatorStatus.json")).get(0).getCurrentFloor());
		assertEquals(elevators.get(0).getElevatorId(), readAndWriteConfiguration.readConfiguration(new File("currentElevatorStatus.json")).get(0).getElevatorId());
		assertEquals(elevators.get(0).getQueue(), readAndWriteConfiguration.readConfiguration(new File("currentElevatorStatus.json")).get(0).getQueue());
	}
	
	@Test
	void test_read_system_configuration_read_configuration_from_text_file() {
		
		ReadSystemConfiguration readSystemConfiguration = new ReadSystemConfiguration();
		
		assertEquals(10, readSystemConfiguration.getMaximumFloor());
		assertEquals(4, readSystemConfiguration.getMaximumElevatorNumber());
	}
	
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	@Test
	void test_read_system_configuration_read_configuration_from_text_file_throws_File_Not_Found_Exception() throws FileNotFoundException {
		
		ReadSystemConfiguration readSystemConfiguration = new ReadSystemConfiguration();
		readSystemConfiguration.setFile(new File(""));
		readSystemConfiguration.readConfiguration();
	
	}
	
	@Test
	void test_read_and_write_configuration_read_configuration_from_text_file_throws_IO_exception() throws IOException {
		
		ThreadManager threadManager = new ThreadManager();
		threadManager.fileName = "";
		threadManager.configuration();
	}
	
	@Test
	void test_read_and_write_configuration_write_configuration_to_text_file(){
		
		ThreadManager threadManager = new ThreadManager();
		threadManager.fileName = "currentElevatorStatus.json";
		threadManager.configuration();
		
		ReadAndWriteConfiguration readAndWriteConfiguration = new ReadAndWriteConfiguration();
		
		ArrayList<Elevator> eles = new ArrayList<Elevator>();
		ArrayList<Integer> queue = new ArrayList<Integer>();
		queue.add(1);
		queue.add(3);
		queue.add(5);
		
		eles.add(new Elevator(1, 1, queue));
		
		assertEquals(true, readAndWriteConfiguration.writeConfiguration(new File(threadManager.fileName), eles));
		
	}
	
	@Test
	void test_read_and_write_configuration_write_configuration_to_text_file_throws_IO_exception() throws IOException{
				
		ReadAndWriteConfiguration readAndWriteConfiguration = new ReadAndWriteConfiguration();
		ArrayList<Elevator> eles = new ArrayList<Elevator>();
		
		readAndWriteConfiguration.writeConfiguration(new File(""), eles);
	}
}
