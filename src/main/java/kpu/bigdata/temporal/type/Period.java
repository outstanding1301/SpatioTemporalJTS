package kpu.bigdata.temporal.type;

import java.util.Date;

import kpu.bigdata.st.type.utils;

public class Period extends Temporal {
	/*
	 * 
	 * 기간을 나타내는 시간 타입
	 * 
	 * */
	
	// 시작점
	private Date t1;
	// 끝점
	private Date t2;

	public Period(Date t1, Date t2) {
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public Period(Date t1, Date t2, Object userData) {
		this.t1 = t1;
		this.t2 = t2;
		setUserData(userData);
	}

	public Period(String t1, String t2) {
		this.t1 = utils.getDateTime(t1);
		this.t2 = utils.getDateTime(t2);
	}
	
	public Period(String t1, String t2, Object userData) {
		this.t1 = utils.getDateTime(t1);
		this.t2 = utils.getDateTime(t2);
		setUserData(userData);
	}

	public Date getT1() {
		return t1;
	}

	public void setT1(Date t1) {
		this.t1 = t1;
	}

	public Date getT2() {
		return t2;
	}

	public void setT2(Date t2) {
		this.t2 = t2;
	}
}
