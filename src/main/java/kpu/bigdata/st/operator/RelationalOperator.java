package kpu.bigdata.st.operator;

import kpu.bigdata.st.type.SpatioTemporal;

public interface RelationalOperator {

	/*
	 * 
	 *  관계 연산자 인터페이스
	 * 
	 * 
	 * */
	
	public boolean equals(SpatioTemporal st);
	public boolean crosses(SpatioTemporal st);
	public boolean contains(SpatioTemporal st);
	public boolean disjoint(SpatioTemporal st);
	public boolean touches(SpatioTemporal st);
	public boolean overlaps(SpatioTemporal st);
}
