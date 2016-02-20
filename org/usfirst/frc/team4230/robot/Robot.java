
package org.usfirst.frc.team4230.robot;

import org.usfirst.frc.team4230.robot.commands.ExampleCommand;
import org.usfirst.frc.team4230.robot.subsystems.ExampleSubsystem;

import edu.wpi.first.wpilibj.CANSpeedController.ControlMode;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;
	public final int LEFTFRONT = 1; 
	public final int LEFTBACK = 2; 	
	public final int RIGHTFRONT = 3;
	public final int RIGHTBACK = 4;
	public final int SHOOTERLEFT = 5;
	public final int SHOOTERRIGHT = 6;
	public final int ARM = 7;
	public final int KICKER = 3;
	///////////////////////////////////////////
	public final int XBUTTON = 1;// different button numbers, buttons on
	public final int YBUTTON = 4;// controllers are randomly assigned a
	public final int ABUTTON = 2;// number from 1 to 12, this way you can
	public final int BBUTTON = 3;// just type the button you want in the
	public final int LBUMPER = 5;// code instead of remembering the
	public final int RBUMPER = 6;// button number
	public final int LTRIGGER = 7;
	public final int RTRIGGER = 8;
	public final int BACK = 9;
	public final int START = 10;
	public final int L3BUTTON = 11;
	public final int R3BUTTON = 12;
	///////////////////////////////////////
	public final int ZERO = -125;
	public final int SHOOT = -1200;
	int setpoint;
	int autoloop;

	Joystick joystick; // telling the code that the joystick(s) exist
	Joystick joystick2;// but doesn't declare them yet
	RobotDrive drive; // same as above but for drive train
	Compressor air; // compressor for pneumatics
	Solenoid shifter1; // valve for pneumatics, makes pneumatic actuator
	Solenoid shifter2; // (thingy) go in and out
	PIDController pid;
	int auto;
	CANTalon leftfront;
	CANTalon leftback;
	CANTalon rightfront;
	CANTalon rightback;
	CANTalon shooterleft;
	CANTalon shooterright;
	CANTalon arm;
	Servo kicker;// servo motor
	Command autonomousCommand;// these are included in the code by default,
	SendableChooser chooser;// don't delete them
	int speed;

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	public void robotInit() {
		oi = new OI();
		
		leftfront = new CANTalon(LEFTFRONT);
		leftback = new CANTalon(LEFTBACK);
		rightfront = new CANTalon(RIGHTFRONT);
		rightback = new CANTalon(RIGHTBACK);
		drive = new RobotDrive(leftfront, leftback, rightfront, rightback);
		joystick = new Joystick(0);
		joystick2 = new Joystick(1);
		shooterleft = new CANTalon(SHOOTERLEFT);
		shooterright = new CANTalon(SHOOTERRIGHT);
		kicker = new Servo(KICKER);
		air = new Compressor(0);
		air.start();
		shifter1 = new Solenoid(0);
		shifter2 = new Solenoid(1);
		arm = new CANTalon(ARM);
		arm.clearStickyFaults();
		arm.changeControlMode(TalonControlMode.Position);
		arm.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		arm.setPosition(0);
		arm.setForwardSoftLimit(1.0);
		arm.setReverseSoftLimit(-1.0);
		arm.reverseSensor(false);
		arm.setVoltageRampRate(0);
		arm.setP(2.5);
		arm.setI(0.001);
		arm.setD(0);
		speed = 0;
		setpoint = 0;
		autoloop = 0;
		chooser = new SendableChooser();
		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	public void disabledInit() {

	}

	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	public void autonomousInit() {
		autonomousCommand = (Command) chooser.getSelected();
		auto = 0;
		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		// schedule the autonomous command (example)
		if (autonomousCommand != null)
			autonomousCommand.start();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		if(autoloop == 0){
			drive.setSafetyEnabled(false);
			drive.drive(-0.5, 0); //forward for 2 seconds
			Timer.delay(3);
			autoloop++;
		}else if(autoloop == 1){
			drive.drive(0.5, 1);
			Timer.delay(1);
			autoloop++;
		}else if(autoloop == 2){
			drive.drive(-0.5, 0); //forward for 2 seconds
			Timer.delay(2);
			autoloop++;
		}else if(autoloop == 3){
			arm.set(SHOOT);
			Timer.delay(2);
			kicker.set(-1);
			Timer.delay(1);
			autoloop++;
		}else{
			drive.drive(0, 0);
			arm.set(ZERO);
			kicker.set(1);
		}
	}

	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();

	}

	/**
	 * This function is called periodically during operator control
	 */
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
		System.out.println(arm.getEncPosition() + " " + arm.getPosition() + " " + arm.getClosedLoopError()+ " " + setpoint);
		// drive.tankDrive(joystick.getY()*.75, joystick.getThrottle()*.75);
		
		if(joystick2.getRawButton(BBUTTON)){ 
			  setpoint = ZERO; 
		  }else if(joystick2.getRawButton(YBUTTON)){
			  setpoint = SHOOT;	
		  }else{
		      arm.set(arm.getPosition()); 
		  }
		  
		  arm.set(setpoint);
		 
		drive.arcadeDrive(joystick.getY() * .75, joystick.getX() * .75);
		
		if (joystick2.getRawButton(RTRIGGER)) {
			kicker.set(-1);
		} else {
			kicker.set(1);
		}

		if (joystick2.getRawButton(LTRIGGER)) {
			shooterright.set(1);
			shooterleft.set(-1);
		} else if (joystick2.getRawButton(LBUMPER)) {
			shooterright.set(-.7);
			shooterleft.set(.7);
		} else if (joystick.getRawButton(RBUMPER)) {
			shifter1.set(false);
			shifter2.set(true);
		} else {
			shifter1.set(true);
			shifter2.set(false);
			shooterleft.set(0);
			shooterright.set(0);
		}

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}
}
