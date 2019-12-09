package kpu.bigdata.st.operator;

import kpu.bigdata.st.type.SpatioTemporal;

public interface TrajectoryOperators {

	/*
	 * 
	 *  궤적 연산자 인터페이스
	 * 
	 * 
	 * */
	
	public boolean enters(SpatioTemporal st);
	public boolean insides(SpatioTemporal st);
	public boolean leaves(SpatioTemporal st);
	public boolean meets(SpatioTemporal st);
	public boolean passes(SpatioTemporal st);
}
