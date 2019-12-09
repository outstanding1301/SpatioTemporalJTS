package kpu.bigdata.st.type;

import java.util.HashMap;

import kpu.bigdata.st.operator.AnalsysOperator;
import kpu.bigdata.st.operator.RelationalOperator;
import kpu.bigdata.st.operator.TrajectoryOperators;


public abstract class SpatioTemporal implements RelationalOperator, AnalsysOperator, TrajectoryOperators{

	/*
	 * 
	 *  시공간 데이터 타입
	 * 
	 * 
	 * */
	
	// 데이터
	private Object userData = null;
	
	public SpatioTemporal() {
		
	}
	
	public SpatioTemporal(Object userData) {
		this.userData = userData;
	}
	
	public Object getUserData() {
		return userData;
	}
	
	public String getName(){
		if(userData instanceof HashMap){
			HashMap<String, String> ud = (HashMap<String, String>)userData;
			return ud.getOrDefault("name", null);
		}
		return "undefined";
	}
	
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	// STGeometry 타입으로 변환
	// 본 라이브러리에서 제공하는 시각화 도구는 STGeometry 타입을 인자로 가짐
	public STObject toSTObject() {
		STObject stg = null;
		if(this instanceof STObject) {
			return (STObject)this;
		}
		return stg;
	}
}
