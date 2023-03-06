package com.fdmgroup.elevator;



public class ElevatorContorller {
//	FrameView frameView ;		
//	Thread threadGUI;


	/**
	 * Initializes program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		ThreadManager threadManager = new ThreadManager();
		
		int elevatorNumber = threadManager.initiateSystem();

		threadManager.configuration();
		threadManager.runElevator();

		FrameView frameView = new FrameView(1, threadManager.getMaxFloor(), elevatorNumber, threadManager.getElevators());		
		Thread threadGUI = new Thread(frameView);
		threadGUI.start();

	}
}
