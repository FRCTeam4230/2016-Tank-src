package org.usfirst.frc.team4230.robot;

import edu.wpi.first.wpilibj.SpeedController;

public class MultiController implements SpeedController {
	SpeedController[] speedcontrollers;
	double speed;
	
	public MultiController(SpeedController... speedControllers){
		this.speedcontrollers = speedcontrollers;
		this.set(0.0);
	}
	@Override
	public void pidWrite(double output) {
		// TODO Auto-generated method stub
		this.set(output);
	}

	@Override
	public double get() {
		// TODO Auto-generated method stub
		return this.speed;
	}

	@Override
	public void set(double speed, byte syncGroup) {
		// TODO Auto-generated method stub
		this.set(speed);
		
	}

	@Override
	public void set(double speed) {
		// TODO Auto-generated method stub
		this.speed = speed;
		for(SpeedController speedcontroller : this.speedcontrollers){
			speedcontroller.set(speed);
		}
		
	}

	@Override
	public void setInverted(boolean isInverted) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getInverted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void disable() {
		// TODO Auto-generated method stub
		for(SpeedController speedcontroller : this.speedcontrollers){
			speedcontroller.disable();
		}
	}

}
