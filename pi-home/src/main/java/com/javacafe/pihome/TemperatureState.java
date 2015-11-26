package com.javacafe.pihome;

import java.util.Date;

public class TemperatureState {
	private long dttm = new Date().getTime();
	private float temp = 0.0f;
	
	public long getDttm() {
		return dttm;
	}
	
	public void setDttm(long dttm) {
		this.dttm = dttm;
	}
	
	public float getTemp() {
		return temp;
	}
	
	public void setTemp(float temp) {
		this.temp = temp;
	}

	@Override
	public String toString() {
		return "TemperatureState [dttm=" + dttm + ", temp=" + temp + "]";
	}
	
}
