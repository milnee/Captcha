package me.millen.captcha.handler;
/*
 *  created by turben on 22/05/2020
 */

public class Stopwatch{

	private long time;
	private boolean pause;
	private long pauseTime;

	public Stopwatch(){
		this.time = System.currentTimeMillis();
	}

	public boolean reached(double seconds){
		return elapsedTime() >= seconds;
	}

	public double elapsedTime(){
		return (System.currentTimeMillis() - time) / 1000.0D;
	}

	public double elapsedTime(int precision){
		int scale = (int) Math.pow(10, precision);
		return (double) Math.round((System.currentTimeMillis() - time) * scale) / scale;
	}

	public void reset(){
		time = System.currentTimeMillis();
	}

	public boolean pause(){
		if(!pause){
			this.pauseTime = System.currentTimeMillis();
		}else{
			this.time += pauseTime;
		}

		pause = !pause;
		return pause;
	}

	public void fastForward(double seconds){
		time -= (seconds * 1000.0);
	}
}