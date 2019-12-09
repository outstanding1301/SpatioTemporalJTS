package kpu.bigdata.st.operator;

import kpu.bigdata.st.type.STObject;
import kpu.bigdata.st.type.SpatioTemporal;

public interface AnalsysOperator {
	
	/*
	 * 
	 *  분석 연산자 인터페이스
	 * 
	 * 
	 * */
	
	public STObject union(SpatioTemporal st);
	public STObject difference(SpatioTemporal st);
	public STObject intersection(SpatioTemporal st);
}
