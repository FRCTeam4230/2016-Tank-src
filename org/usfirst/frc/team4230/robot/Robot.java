
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
	public final int LDRIVE1 = 0; // pwm different motors are in, allows
	// public final int LDRIVE2 = 1; // you to just change pwm port in one place
	// if
	// public final int LDRIVE3 = 2;// electrical moves stuff on you
	// public final int RDRIVE1 = 3;
	// public final int RDRIVE2 = 4;
	// public final int RDRIVE3 = 5;
	public final int THROWL = 7;
	public final int THROWR = 8;
	public final int PICKUP = 6;
	public final int KICKER = 3;
	public final int ARM = 1;
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
	public final int DEADBAND = 10;
	public final double OFF = -0.12;
	public final double LOW = 0.1;
	public final double HIGH = 0.4;
	int setpoint;

	Joystick joystick; // telling the code that the joystick(s) exist
	Joystick joystick2;// but doesn't declare them yet
	RobotDrive drive; // same as above but for drive train
	Compressor air; // compressor for pneumatics
	Solenoid shifter1; // valve for pneumatics, makes pneumatic actuator
						// (thingy)
	// go in and out
	Solenoid shifter2;
	PIDController pid;
	// SpeedController leftdrive; // extra motors (drive motors don't usually
	// need
	// to
	// SpeedController rightdrive;// be declared up here, they are declared with
	// robot drive)

	// SpeedController arm;
	int auto;
	CANTalon leftfront;
	CANTalon leftback;
	CANTalon rightfront;
	CANTalon rightback;
	CANTalon shooterleft;
	CANTalon shooterright;
	CANTalon arm;
	Encoder driverenc;// declaring all of the encoders for the robot
	Encoder drivelenc;
	Encoder throwrenc;
	Encoder encoder;
	Encoder armposenc;
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
		// leftdrive = new MultiController(new Talon(LDRIVE1), new
		// Talon(LDRIVE2), new Talon(LDRIVE3));
		// rightdrive = new MultiController(new Talon(RDRIVE1), new
		// Talon(RDRIVE2), new Talon(RDRIVE3));
		leftfront = new CANTalon(1);
		leftback = new CANTalon(2);
		rightfront = new CANTalon(3);
		rightback = new CANTalon(4);
		drive = new RobotDrive(leftfront, leftback, rightfront, rightback);
		joystick = new Joystick(0);
		joystick2 = new Joystick(1);
		shooterleft = new CANTalon(5);
		shooterright = new CANTalon(6);
		// arm = new Talon(ARM);
		// thing = new CANTalon(2);
		// thing.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		kicker = new Servo(KICKER);
		encoder = new Encoder(0, 1, false);
		encoder.reset();
		// throwrenc = new Encoder(2, 3);
		// armposenc = new Encoder(4, 5);
		// driverenc = new Encoder(6, 7);
		// drivelenc = new Encoder(8, 9);
		air = new Compressor(0);
		air.start();
		shifter1 = new Solenoid(0);
		shifter2 = new Solenoid(1);
		arm = new CANTalon(7);
		arm.clearStickyFaults();
		arm.changeControlMode(TalonControlMode.Position);
		arm.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		// arm.configEncoderCodesPerRev(360);
		arm.setPosition(0);
		arm.setForwardSoftLimit(1.0);
		arm.setReverseSoftLimit(-1.0);
		arm.reverseSensor(false);
		// arm.reverseOutput(false);
		// arm.reverseOutput(true);
		// arm.setEncPosition(0);
		arm.setVoltageRampRate(0);
		arm.setP(1.0);
		// arm.setAllowableClosedLoopErr(100);

		// arm.set(0);
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// arm.setPosition(0);
		speed = 0;
		// armr2 = new Talon(6);
		// arml1 = new Talon(8);
		// arml2 = new Talon(9);
		// pid = new PIDController(1000, 0, 0, encoder, arm);
		setpoint = 200;
		// arm.changeControlMode(TalonControlMode.Position);
		// arm.setSetpoint(setpoint);
		// arm.setPID(1, 0, 0);
		// arm.setFeedbackDevice(FeedbackDevice.QuadEncoder);
		// pid.setContinuous();
		chooser = new SendableChooser();
		chooser.addDefault("Default Auto", new ExampleCommand());
		// chooser.addObject("My Auto", new MyAutoCommand());
		SmartDashboard.putData("Auto mode", chooser);
		// drive.setInvertedMotor(MotorType.kFrontLeft, true);
		// drive.setInvertedMotor(MotorType.kRearLeft, true);

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
		// System.out.println(arm.getEncPosition());
		// arm.set(joystick.getThrottle());
		System.out.println(arm.getEncPosition() + " " + arm.getPosition() + " " + arm.getClosedLoopError());
		// System.out.println(arm.getPosition());
		// System.out.println(joystick.getZ());
		// drive.tankDrive(joystick.getY()*.75, joystick.getThrottle()*.75);
		arm.set(arm.getPosition());

		/*
		 * if(joystick.getRawButton(ABUTTON) ||
		 * joystick2.getRawButton(ABUTTON)){ arm.set(2); }else
		 * if(joystick.getRawButton(YBUTTON) ||
		 * joystick2.getRawButton(YBUTTON)){ arm.set(-2.0); }else{
		 * arm.set(arm.getPosition()); }
		 */ // drive.arcadeDrive(joystick.getY() * .75, joystick.getX() * .75);
			// if(joystick.getRawButton(LTRIGGER)){
			// speed++;
			// }else if(joystick.getRawButton(RTRIGGER)){
			// speed--;
			// }
		// arm.set(0.1 + (speed *.01));
		// System.out.println(arm.get());

		// leftfront.set(joystick.getY());
		// leftback.set(joystick.getY());
		// rightfront.set(joystick.getThrottle());
		// rightback.set(joystick.getThrottle());
		// double axis = joystick.getY();
		// thing.set(axis);
		// thing.changeControlMode(TalonControlMode.Position);
		// thing.setPosition(0);
		// int count = thingy.get();
		// boolean cont = true;
		// if(count == 0){
		// cont = false;
		// }
		// while(cont){
		// int counta = thingy.get();
		// Timer.delay(1);
		// int countb = thingy.get();
		// int rate = countb - counta;
		// System.out.println(rate);
		// if(rate == 255){
		// cont = false;
		// }
		// }
		// System.out.println(count);
		// System.out.println(armposenc.getRate());
		// pickup.set(joystick2.getY());
		if (joystick2.getRawButton(RTRIGGER)) {
			kicker.set(-1);
		} else {
			kicker.set(1);
		}

		if (joystick2.getRawButton(LTRIGGER)) {
			shooterright.set(1);
			shooterleft.set(-1);
			// thing.set(1);
			// kicker.set(0);

		} else if (joystick2.getRawButton(LBUMPER)) {
			// arm2.set(true);
			shooterright.set(-.4);
			shooterleft.set(.4);
			// thing.set(-1);
			// kicker.set(1);
		} else if (joystick.getRawButton(RBUMPER)) {
			shifter1.set(false);
			shifter2.set(true);
		} else {
			shifter1.set(true);
			shifter2.set(false);
			shooterleft.set(0);
			shooterright.set(0);
			// thing.set(0);
			// kicker.set(.5);
		}
		/*
		 * if(joystick.getRawButton(ABUTTON) ||
		 * joystick2.getRawButton(ABUTTON)){ //setpoint+= 10; //
		 * pid.setSetpoint(setpoint); arm.set(1);
		 * System.out.println(encoder.get()); }else
		 * if(joystick.getRawButton(BBUTTON) ||
		 * joystick2.getRawButton(BBUTTON)){ //setpoint-= 10;
		 * //pid.setSetpoint(setpoint); arm.set(-1);
		 * System.out.println(encoder.get());
		 * 
		 * }else{ //pid.setSetpoint(setpoint); arm.set(0);
		 * System.out.println(encoder.get());
		 * 
		 * }
		 */

		/*
		 * if(joystick.getRawButton(ABUTTON) ||
		 * joystick2.getRawButton(ABUTTON)){ int setpoint = 800; int
		 * currentcount = encoder.get(); if(currentcount < 10){ arm.set(OFF);
		 * System.out.println(currentcount); }else if((currentcount) >= 10 &&
		 * (currentcount) < (setpoint - 100)){ arm.set(HIGH);
		 * System.out.println(currentcount); }else if ((currentcount) >=
		 * (setpoint - 100) && (currentcount) < setpoint){ arm.set(LOW);
		 * System.out.println(currentcount); }else{ arm.set(OFF);
		 * System.out.println(currentcount); } }else
		 * if(joystick.getRawButton(LBUMPER) ||
		 * joystick2.getRawButton(LBUMPER)){ int setpoint = -200; int
		 * currentcount = .get(); if(currentcount > -10){ arm.set(OFF);
		 * System.out.println(currentcount); }else if((currentcount) <= -10 &&
		 * (currentcount) > (setpoint + 100)){ arm.set(-1*HIGH);
		 * System.out.println(currentcount); }else if ((currentcount) >=
		 * (setpoint + 100) && (currentcount) > setpoint){ arm.set(-1*LOW);
		 * System.out.println(currentcount); }else{ arm.set(OFF);
		 * System.out.println(currentcount); } }else{ if(joystick.getThrottle()
		 * > 0.1 || joystick.getThrottle() < -0.1){
		 * arm.set(joystick.getThrottle()); System.out.println(.get()); }else
		 * if(joystick2.getThrottle() > 0.1 || joystick2.getThrottle() < -0.1){
		 * arm.set(joystick2.getThrottle()); System.out.println(encoder.get());
		 * }else{ arm.set(OFF); } }
		 */

	}

	/**
	 * This function is called periodically during test mode
	 */
	public void testPeriodic() {
		LiveWindow.run();
	}
}
