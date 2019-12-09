package kpu.bigdata.temporal.type;

import java.util.Date;

import kpu.bigdata.st.type.utils;

public class Instant extends Temporal {
	/*
	 * 
	 * 순간을 나타내는 시간 타입
	 * 
	 * */
	
	// 시간
	private Date t;
	
	public Instant(Date time) {
		t = time;
	}
	
	public Instant(Date time, Object userData) {
		t = time;
		setUserData(userData);
	}
	
	public Instant(String t) {
		this.t = utils.getDateTime(t);
	}
	
	public Instant(String t, Object userData) {
		this.t = utils.getDateTime(t);
		setUserData(userData);
	}

	public Date getT() {
		return t;
	}

	public void setT(Date t) {
		this.t = t;
	}
	
}
