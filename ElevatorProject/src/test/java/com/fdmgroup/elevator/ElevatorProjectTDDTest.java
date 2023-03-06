package com.fdmgroup.elevator;


import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
//import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


class ElevatorProjectTDDTest {
	
	ArrayList<Integer> queue;
	ArrayList<Elevator> elevators;
	Scheduler scheduler;
	CommandGenerator commandGenerator;
	UserInput userInput;
	ThreadManager threadManager;
	
//	@Rule
//    public final ExpectedSystemExit exit = ExpectedSystemExit.none();
	
	@BeforeEach
	void setUp() throws Exception {
		queue = new ArrayList<Integer>();
		elevators = new ArrayList<Elevator>();
		elevators.add(new Elevator(1));
		elevators.add(new Elevator(2));
		scheduler = new Scheduler(elevators);
		commandGenerator = new CommandGenerator(scheduler,10, 5000, elevators);
		userInput = new UserInput(scheduler,  commandGenerator, elevators);
		threadManager = new ThreadManager();
	}

	@Test
	void test_elevator_constructor() {
		
		
		queue.add(2);
		Elevator elevator = new Elevator(2, 2, queue);
		
		assertEquals(queue, elevator.getQueue());
		assertEquals(2, elevator.getElevatorId());
		assertEquals(2, elevator.getCurrentFloor());
	}
	
	@Test
	void test_elevator_add_queue() {
		Elevator elevator = new Elevator(0);
		
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

		queue.add(1);
		Elevator elevator = new Elevator(1, 2, queue);
		
		elevator.changeState();
		
		assertEquals(State.DOWN, elevator.getState());
	}
	
	@Test
	void test_elevator_change_state_up() {

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
	
//	@Test
//	void test_scheduler_constructor() {
//		
//	
//		Scheduler scheduler = new Scheduler(elevators);
//		
//		assertEquals(elevators, scheduler.getElevators());
//	}

	@Test
	void test_scheduler_is_idle_elevator_true() {

		elevators.add(new Elevator(1));
		elevators.add(new Elevator(2));
		Scheduler scheduler = new Scheduler(elevators);
		
		assertEquals(true, scheduler.isIdleElevator(elevators));
	}
	
	@Test
	void test_scheduler_is_idle_elevator_false() {

		Scheduler scheduler = new Scheduler(elevators);
		
		assertEquals(true, scheduler.isIdleElevator(elevators));
	}
	
	@Test
	void test_scheduler_assign_idle_elevator() {

		queue.add(1);
		queue.add(3);

		Scheduler scheduler = new Scheduler(elevators);
		Elevator elevator = new Elevator(1);
		scheduler.assignIdleElevator(elevator, new int[] {1, 3});
		
		assertEquals(queue, elevator.getQueue());
	}
	
	@Test
	void test_scheduler_check_input_returns_true() {

		Scheduler scheduler = new Scheduler(elevators);
		
		assertEquals(true, scheduler.checkInput(new int[] {1, 3}));
	}
	
	@Test
	void test_scheduler_check_input_returns_false() {

		Scheduler scheduler = new Scheduler(elevators);
		
		assertEquals(false, scheduler.checkInput(new int[] {3, 2}));
	}
	
	@Test
	void test_scheduler_check_floor_returns_true_if_going_up() {

		Scheduler scheduler = new Scheduler(elevators);
		Elevator elevator = new Elevator(1);
		
		assertEquals(true, scheduler.checkFloor(elevator, new int[] {2, 3}));
	}
	
	@Test
	void test_scheduler_check_floor_returns_true_if_going_down() {

		Scheduler scheduler = new Scheduler(elevators);
		Elevator elevator = new Elevator(1, 5, queue);
		
		assertEquals(true, scheduler.checkFloor(elevator, new int[] {4, 2}));
	}

	@Test
	void test_scheduler_check_floor_returns_false_when_idle() {

		Scheduler scheduler = new Scheduler(elevators);
		Elevator elevator = new Elevator(1);
		
		assertEquals(false, scheduler.checkFloor(elevator, new int[] {1, 3}));
	}
	
	@Test
	void test_scheduler_check_same_direction_returns_true_when_going_up() {

		queue.add(2);
		queue.add(3);

		Scheduler scheduler = new Scheduler(elevators);
		Elevator elevator = new Elevator(1, 1, queue);
		elevator.changeState();
		
		assertEquals(true, scheduler.checkSameDirection(elevator, new int[] {1, 3}));
	}
	
	@Test
	void test_scheduler_check_same_direction_returns_true_when_going_down() {

		queue.add(2);
		queue.add(3);
		

		Scheduler scheduler = new Scheduler(elevators);
		Elevator elevator = new Elevator(1, 6, queue);
		elevator.changeState();
		
		assertEquals(true, scheduler.checkSameDirection(elevator, new int[] {5, 3}));
	}
	
	@Test
	void test_scheduler_check_same_direction_returns_false_when_idle() {
		

		Scheduler scheduler = new Scheduler(elevators);
		Elevator elevator = new Elevator(1);
		
		assertEquals(false, scheduler.checkSameDirection(elevator, new int[] {5, 3}));
	}
	
	@Test
	void test_read_and_write_configuration_read_configuration_from_file_source() {
		
		ReadAndWriteConfiguration readAndWriteConfiguration = new ReadAndWriteConfiguration();
	
		queue.add(1);
		queue.add(3);
		queue.add(5);

		Elevator elevator = new Elevator(1, 1, queue);
		elevators.add(elevator);
		
		assertEquals(1, readAndWriteConfiguration.readConfiguration(new File("currentElevatorStatus.json")).get(0).getCurrentFloor());
	}
	
	@Test
	void test_read_system_configuration_read_configuration_from_text_file() {
		
		ReadSystemConfiguration readSystemConfiguration = new ReadSystemConfiguration();
		
		assertEquals(10, readSystemConfiguration.getMaximumFloor());
		assertEquals(6, readSystemConfiguration.getMaximumElevatorNumber());
	}
		
	
	@Test
	void test_calculate_time_for_same_direction() {
		
		Elevator elevator = new Elevator(1, 1, queue);
		TimeCalculator calculator = new TimeCalculator();
		
		int result = calculator.calculateForSameDirection(elevator, 10);
		
		assertEquals(10, result);
		
	}
	
	
	@Test
	void test_calculate_time_not_same_direction() {
		
		queue.add(5);
		Elevator elevator = new Elevator(1, 3, queue);
		TimeCalculator calculator = new TimeCalculator();
		
		int result = calculator.calculateForOtherCase(elevator, 9);
		
		assertEquals(6, result);

	}
	
	
	@Test
	void test_calculate_time_not_same_direction_multiple_floors() {
		
		queue.add(5);
		queue.add(8);
		queue.add(2);
		Elevator elevator = new Elevator(1, 3, queue);
		TimeCalculator calculator = new TimeCalculator();
		
		int result = calculator.calculateForOtherCase(elevator, 9);
		
		assertEquals(22, result);

	}
	
	
	@Test
	void test_calculate_time_not_same_direction_multiple_floors_DOWN() {
		
		queue.add(10);
		queue.add(7);
		queue.add(2);
		Elevator elevator = new Elevator(1, 3, queue);
		TimeCalculator calculator = new TimeCalculator();
		
		int result = calculator.calculateForOtherCase(elevator, 4);
		
		assertEquals(16, result);
	}
	
	
	@Test
	void test_calculate_time_not_same_direction_above_source() {
		
		queue.add(10);
		queue.add(7);
		queue.add(6);
		Elevator elevator = new Elevator(1, 3, queue);
		TimeCalculator calculator = new TimeCalculator();
		
		int result = calculator.calculateForOtherCase(elevator, 4);
		
		assertEquals(12, result);
	}
	
	
	@Test
	void test_get_and_set_threads() {
		
		ThreadManager threadManager = new ThreadManager();
		
		List<Thread> threads = new ArrayList<Thread>();
		
		Thread thread1 = new Thread();
		Thread thread2 = new Thread();
		Thread thread3 = new Thread();
		
		threads.add(thread1);
		threads.add(thread2);
		threads.add(thread3);
		
		threadManager.setThreads(threads);
		
		assertEquals(threads, threadManager.getThreads());
		
	}
	
	
	@Test
	void test_get_and_set_commands() {
		
		Scheduler scheduler = new Scheduler(elevators);
		
//		String command = "1:4";
		String[] commands = {"1:4"};
		
//		int[] intArray = new  int[2];
		
		scheduler.setCommands(commands);
		
		assertEquals(commands, scheduler.getCommands());
	}
	
	
	@Test
	void convert_commands() {
		
		Scheduler scheduler = new Scheduler(elevators);
		
		String command = "1:4";
		
		int[] intArray = new int[2];
		
		int[] result = {1,4};
		
		int[] numbers = scheduler.getCommand(command, intArray);
		
		assertEquals(result[0], numbers[0]);
		assertEquals(result[1], numbers[1]);
	}
	
	
	@Test
	void test_calculate_time() {
		
		Scheduler scheduler = new Scheduler(elevators);
		
		ArrayList<Integer> queue1 = new ArrayList<Integer>();
		ArrayList<Integer> queue2 = new ArrayList<Integer>();
		
		queue1.add(6);
		queue2.add(5);
		
		
		int[] input = {4,6};
		
		Elevator elevator1 = new Elevator(1,2,queue1);
		Elevator elevator2 = new Elevator(1,9,queue2);

		
		elevators.add(elevator1);
		elevators.add(elevator2);
		
		int result = scheduler.calculateTime(elevators, input);
		
		assertEquals(0, result);		
	}
	
	
	@Test
	void test_assign_elevator() {
		
		Scheduler scheduler = new Scheduler(elevators);
		
		ArrayList<Integer> queue1 = new ArrayList<Integer>();
		
		queue.add(6);
		queue.add(4);
		queue.add(3);
		queue.add(8);
		queue.add(10);

		
		queue1.add(3);
		queue1.add(8);
		queue1.add(10);
		
		
		int[] input = {4,6};
		
		Elevator elevator1 = new Elevator(1,1,queue1);

		elevator1.setUp();
		
		scheduler.assignElevator(elevator1, input, queue1);
		
		assertEquals(queue, elevator1.getQueue());
		
	}
	
	
	@Test
	void test_assign_elevator_2() {
		
		Scheduler scheduler = new Scheduler(elevators);
		
		ArrayList<Integer> queue1 = new ArrayList<Integer>();
		
		queue1.add(1);
		queue1.add(6);
		
		Elevator elevator1 = new Elevator(1,1,queue1);
		
		queue.add(5);
		queue.add(3);
		queue.add(1);
		queue.add(6);
		
		elevator1.setUp();;
		
		int[] input = {3,5};
		
		
		scheduler.assignElevator(elevator1, input, queue1);
		
		assertEquals(queue, elevator1.getQueue());
		
	}
	
	
	@Test
	void test_assign_elevator_3() {
		
		Scheduler scheduler = new Scheduler(elevators);
		
		ArrayList<Integer> queue1 = new ArrayList<Integer>();
		
		queue1.add(7);
		queue1.add(2);
		
		Elevator elevator1 = new Elevator(1,9,queue1);
		
		queue.add(1);
		queue.add(5);
		queue.add(7);
		queue.add(2);
		
		elevator1.setDown();;
		
		int[] input = {5,1};
		
		
		scheduler.assignElevator(elevator1, input, queue1);
		
		assertEquals(queue, elevator1.getQueue());
		
	}
	
	
	@Test
	void test_run_scheduler() {
		
		Scheduler scheduler = new Scheduler(elevators);
		
		ArrayList<Integer> queue1 = new ArrayList<Integer>();
		ArrayList<Integer> queue2 = new ArrayList<Integer>();
		ArrayList<Integer> queue3 = new ArrayList<Integer>();
		ArrayList<Integer> queue4 = new ArrayList<Integer>();

		
		queue1.add(1);
		queue1.add(3);
		queue1.add(8);
		
		queue2.add(9);
		queue2.add(7);
		queue2.add(3);
		
		queue3.add(3);
		queue3.add(6);
		queue3.add(2);
		
		queue4.add(9);
				
		Elevator elevator1 = new Elevator(1,2,queue1);
		Elevator elevator2 = new Elevator(1,9,queue2);
		Elevator elevator3 = new Elevator(1,5,queue3);
		Elevator elevator4 = new Elevator(1,1,queue3);

		
		elevators.add(elevator1);
		elevators.add(elevator2);
		elevators.add(elevator3);
		elevators.add(elevator4);
		
		String[] input = {"5:1"};
		
		scheduler.runScheduler(elevators, input);
		
		TimeCalculator calculator = new TimeCalculator();
		
		int[] integerArray = {5,1};
		
		assertEquals(true, scheduler.runScheduler(elevators, input));
		
	}
	
	
	@Test
	void test_run_scheduler_2() {
		
		Scheduler scheduler = new Scheduler(elevators);
		
		ArrayList<Integer> queue1 = new ArrayList<Integer>();
		

		
		queue1.add(1);
		queue1.add(3);
		queue1.add(8);
				
		Elevator elevator1 = new Elevator(1,2,queue1);
		
		elevators.add(elevator1);
		
		elevator1.setUp();
		
		String[] input = {"5:1"};
		
		scheduler.runScheduler(elevators, input);
		
		TimeCalculator calculator = new TimeCalculator();
		
		int[] integerArray = {5,1};
		
		assertEquals(true, scheduler.runScheduler(elevators, input));
		
	}
		
	
	@Test
	void scheduler_run() {
		
		Scheduler scheduler = new Scheduler(elevators);
		
		Thread thread = new Thread(scheduler);
		thread.start();
		
				
		assertEquals(true, thread.isAlive());
		
	}
	
	@Test
	void elevator_run() {
		
		Elevator elevator1 = new Elevator(1,2,queue);
		
		Thread thread = new Thread(elevator1);
		thread.start();
		
				
		assertEquals(true, thread.isAlive());
		
	}
	
	
	@Test
	void set_and_get_queue() {
		
		Elevator elevator1 = new Elevator(1,2,queue);
		
		queue.add(1);
		queue.add(5);
		
		elevator1.setQueue(queue);
		
				
		assertEquals(queue, elevator1.getQueue());
		
	}
	
	
	@Test
	void test_open_door() {
		
		Elevator elevator1 = new Elevator(1,2,queue);
		
		Thread thread = new Thread(elevator1);
		
		thread.start();
		
		queue.add(1);
		queue.add(5);
		
		elevator1.setQueue(queue);
		
				
		assertEquals(true, elevator1.openDoor());
		assertEquals(true, elevator1.closeDoor());
		assertEquals(false, elevator1.changeState());
		
	}
	
	
	@Test
	void test_close_door() {
		
		Elevator elevator1 = new Elevator(1,2,queue);
		
		queue.add(1);
		queue.add(5);
		
		elevator1.setQueue(queue);
		
				
		assertEquals(queue, elevator1.getQueue());
		
	}
	
	
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
		
		assertEquals(true, readAndWriteConfiguration.writeConfiguration((new File(threadManager.fileName)), eles));
		
	}
	
	@Test
	void test_read_and_write_configuration_write_configuration_to_text_file_throws_IO_exception() throws IOException{
				
		ReadAndWriteConfiguration readAndWriteConfiguration = new ReadAndWriteConfiguration();
		ArrayList<Elevator> eles = new ArrayList<Elevator>();
		
		readAndWriteConfiguration.writeConfiguration(new File(""), eles);
	}
	
	@Test
	void test_userInput_getCommand_returns_command() {
		String input = "1:3";
		String[] result = userInput.convertInput(input);
		assertEquals("1:3", result[0]);
	}
	@Test
	void test_userInput_readInput_gets_valid_input() {
		String input = "1:3";
		userInput.readInput(input);		
		assertEquals(1,userInput.getCommands().length);
	}
	@Test
	void test_userInput_readInput_does_not_get_when_input_is_invalid() {
		String input = "13";
		userInput.readInput(input);		
		assertEquals(null,userInput.getCommands());
	}
	@Test
	void test_userInput_readInput_gets_operation_input() {
		String input = "a";
		userInput.readInput(input);		
		assertEquals(input,userInput.getCommandForAuto());
	}	
	
	@Test
	void test_userInput_isOperationCommand_returns_ture_A() {
		String input = "a";
		boolean result = userInput.isOperationCommand(input);		
		assertEquals(true,result);
	}
	@Test
	void test_userInput_isOperationCommand_returns_ture_E() {
		String input = "e";
		boolean result = userInput.isOperationCommand(input);		
		assertEquals(true,result);
	}
	@Test
	void test_userInput_isOperationCommand_returns_ture_morning() {
		String input = "morning";
		boolean result = userInput.isOperationCommand(input);		
		assertEquals(true,result);
	}
	@Test
	void test_userInput_isOperationCommand_returns_ture_evening() {
		String input = "evening";
		boolean result = userInput.isOperationCommand(input);		
		assertEquals(true,result);
	}
	@Test
	void test_userInput_isOperationCommand_returns_ture_normal() {
		String input = "normal";
		boolean result = userInput.isOperationCommand(input);		
		assertEquals(true,result);
	}
	@Test
	void test_userInput_isOperationCommand_returns_ture_busy() {
		String input = "busy";
		boolean result = userInput.isOperationCommand(input);		
		assertEquals(true,result);
	}
	@Test
	void test_userInput_isOperationCommand_returns_ture_slow() {
		String input = "slow";
		boolean result = userInput.isOperationCommand(input);		
		assertEquals(true,result);
	}
	@Test
	void test_userInput_isOperationCommand_returns_false_invalid_input() {
		String input = "hello";
		boolean result = userInput.isOperationCommand(input);		
		assertEquals(false,result);
	}
	@Test
	void test_userInput_printState_gets_input_A() {
		String input = "a";
		userInput.setIsCommandA(false);
		userInput.printState(input);		
		assertEquals(true,userInput.getIsCommandA());
	}
		
	@Test
	void test_userInput_validateInput_returns_true_operation_input() {
		String input = "a";
		boolean result = userInput.validateInput(input);		
		assertEquals(true,result);
	}
	@Test
	void test_userInput_validateInput_returns_true_valid_input() {
		String input = "4:5";
		boolean result = userInput.validateInput(input);		
		assertEquals(true,result);
	}
	@Test
	void test_userInput_validateInput_returns_false_invalid_input() {
		String input = "15";
		boolean result = userInput.validateInput(input);		
		assertEquals(false,result);
	}
	@Test
	void test_userInput_splitCommands_split_the_input() {
		String input = "1:5";
		String [] result = userInput.splitCommands(input);		
		assertEquals("1:5",result[0]);

	}
	@Test
	void test_userInput_runCommandGenerator_change_boolean_A() {
		commandGenerator.setIsInputA(false);
		String input = "a";	
		userInput.runCommandGenerator(input);		
		assertEquals(true,commandGenerator.getIsInputA());

	}
	
	@Test
	void test_userInput_runCommandGenerator_change_boolean_morning() {
		commandGenerator.setEvening();
		String input = "morning";	
		userInput.runCommandGenerator(input);		
		assertEquals(true,commandGenerator.getIsMorning());

	}
	@Test
	void test_userInput_runCommandGenerator_change_boolean_evening() {
		commandGenerator.setNormal();
		String input = "evening";	
		userInput.runCommandGenerator(input);		
		assertEquals(true,commandGenerator.getIsEvening());

	}
	@Test
	void test_userInput_runCommandGenerator_change_boolean_normal() {
		commandGenerator.setEvening();
		String input = "normal";	
		userInput.runCommandGenerator(input);		
		assertEquals(false,commandGenerator.getIsEvening());
		assertEquals(false,commandGenerator.getIsMorning());

	}
	
	@Test
	void test_userInput_runCommandGenerator_change_boolean_busy() {
		commandGenerator.setGenerateInterval("slow");
		String input = "busy";	
		userInput.runCommandGenerator(input);		
		assertEquals(1500,commandGenerator.getGenerateInterval());

	}
	@Test
	void test_userInput_runCommandGenerator_change_boolean_slow() {
		commandGenerator.setGenerateInterval("busy");
		String input = "slow";	
		userInput.runCommandGenerator(input);		
		assertEquals(5000,commandGenerator.getGenerateInterval());
	}
	
	@Test
	void test_commandGenerator_constructor_setMaxFloor_10() {
		

		CommandGenerator command = new CommandGenerator();
		
		assertEquals(10, command.getMaxFloor());
	}
	
	@Test
	void test_commandGenerator_constructor_multipleParameters()
	{
		Scheduler scheduler = new Scheduler(elevators);
		int maxFloor = 10;
		int generateInterval = 2;
		CommandGenerator generate = new CommandGenerator(scheduler, maxFloor, generateInterval, elevators);
		
		assertEquals(10, generate.getMaxFloor());
		assertEquals(2, generate.getGenerateInterval());
		
	}
	
	@Test
	void test_setGenerateInterval_sets_to_2()
	{
		int interval = 2;
		commandGenerator.setGenerateInterval(2);
		
		assertEquals(interval, commandGenerator.getGenerateInterval());
	}
	
	@Test
	void test_ifMorning_is_True()
	{
		commandGenerator.setMorning(true);

		assertEquals(true, commandGenerator.getIsMorning());
	
	}
	
	@Test
	void test_ifEvening_is_True()
	{
		commandGenerator.setEvening(true);
		assertEquals(true, commandGenerator.getIsEvening());
	}
	
	@Test
	void test_setGenerateInterval_equals_1500_when_busy()
	{
		commandGenerator.setGenerateInterval("busy");
		
		assertEquals(1500, commandGenerator.getGenerateInterval());
	}
	
	@Test
	void test_setGenerateInterval_equals_5000_when_slow()
	{
		commandGenerator.setGenerateInterval("slow");
		
		assertEquals(5000, commandGenerator.getGenerateInterval());
	}
	
	@Test
	void test_setDay_floorNumber_equals_1()
	{
		commandGenerator.setDay();
		
		assertEquals(1, commandGenerator.getFloor1());
	}
	
	@Test
	void test_setEvening_floorNumber2_equals_1()
	{
		commandGenerator.setEvening();
		assertEquals(1, commandGenerator.getFloor2());
	}
	
	@Test
	void test_isInputA_equalsTrue()
	{
		commandGenerator.setInputA(true);
		assertEquals(true, commandGenerator.getIsInputA());
	}
	
	@Test
	void test_changeMorning_setToTrue()
	{
		commandGenerator.changeMorning();
		
		assertEquals(true, commandGenerator.getIsMorning());
	}
	
	@Test
	void test_changeEvening_setToTrue()
	{
		commandGenerator.changeEvening();
		
		assertEquals(true, commandGenerator.getIsEvening());
	}
	
	@Test
	void test_changeNormal_setEvening_and_Morning_toFalse()
	{
		commandGenerator.changeNormal();
		
		assertEquals(false, commandGenerator.getIsEvening());
		assertEquals(false, commandGenerator.getIsMorning());
		
	}
	
	@Test
	void test_setBoolean_setInputA_true()
	{
		commandGenerator.setInputA(true);
		commandGenerator.setBoolean();
		
		assertEquals(false, commandGenerator.getIsInputA());
	}
	
	@Test
	void test_getFloor1_equals1()
	{
		commandGenerator.setFloor1(1);
		assertEquals(1, commandGenerator.getFloor1());
	}
	
	@Test
	void test_getFloor2_equals1()
	{
		commandGenerator.setFloor2(1);
		assertEquals(1, commandGenerator.getFloor2());
	}


	@Test
	void test_setBoolean_setInputA_false()
	{
		commandGenerator.setInputA(false);
		commandGenerator.setBoolean();
		
		assertEquals(true, commandGenerator.getIsInputA());
	}
	
	@Test
	void test_runAutoCommands()
	{
		//mocking commands
		elevators.add(new Elevator(1));
		Scheduler schedule = new Scheduler(elevators);
		String[] commands = {"1:2"};
		verify(schedule, times(1)).setCommands(commands);	
	}
	
	@Test
	void test_runAutoCommands_runSchedulerMethodCalled_once()
	{
		ArrayList<Elevator> elevators = new ArrayList<Elevator>();
		elevators.add(new Elevator(1));
		String[] commands = {"1:2"};
		Scheduler schedule = new Scheduler(elevators);
		commandGenerator.runAutoCommand();
		assertNotNull(schedule);
		verify(schedule, times(1)).runScheduler(elevators, commands);
		assertEquals(2, schedule.getCommands().length);
	}
	
	@Test
	void test_getElevatorList()
	{
		ArrayList<Elevator> elevatorList = new ArrayList<>();
		ArrayList<Integer> queue = new ArrayList<Integer>();
		elevatorList.add(new Elevator(1,2,queue));
		elevatorList.add(new Elevator(1,3,queue));
		
		threadManager.setElevators(elevatorList);
		assertEquals(elevatorList.size() ,threadManager.getElevators().size());
	}
	
	@Test
	void test_getMaxFlorr()
	{
		int maxFloor = 10;
		threadManager.setMaxFloor(maxFloor);
		assertEquals(10,threadManager.getMaxFloor());
	}
	
	@Test
	void test_threads_are_Alive()
	{
		threadManager.runElevator();
		Thread threadScheduler = new Thread(scheduler);
		Thread threadInput = new Thread(userInput);		
		Thread threadCommands = new Thread(commandGenerator);
		assertEquals(true, threadScheduler.isAlive());
		assertEquals(true, threadInput.isAlive());
		assertEquals(true, threadCommands.isAlive());
	}
	
	@Test 
	void test_checkFileName()
	{
		threadManager.checkFileName();
		
	}
	
	@Test
	void test_elevatorRunning()
	{
		threadManager.runElevator();
		Thread threadScheduler = new Thread(scheduler);
		Thread threadInput = new Thread(userInput);		
		Thread threadCommands = new Thread(commandGenerator);
		threadScheduler.start();
		threadInput.start();
		threadCommands.start();
		assertEquals(true, threadScheduler.isAlive());
		assertEquals(true, threadInput.isAlive());
		assertEquals(true, threadCommands.isAlive());
	}
	
	@Test
	void test_initiateSystem2()
	{
        ReadSystemConfiguration configure = new ReadSystemConfiguration();
		
		ReadAndWriteConfiguration readWriteConfig = new ReadAndWriteConfiguration();
		File file = new File("");
		configure.setFile(file);
		ArrayList<Elevator> elevatorList = new ArrayList<>();
		elevatorList.add(new Elevator(1));
		elevatorList.add(new Elevator(2));
		elevatorList.add(new Elevator(3));
		elevatorList.add(new Elevator(4));
		elevatorList.add(new Elevator(5));
		elevatorList.add(new Elevator(6));
		
		readWriteConfig.readConfiguration(file);
		
		assertEquals(6,threadManager.initiateSystem());
	}
	
	

	
	
	
	
	
	
	
	
	
	
	




	
	
	
	
}
