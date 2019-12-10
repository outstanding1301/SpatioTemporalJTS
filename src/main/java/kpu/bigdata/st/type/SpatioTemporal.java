package kpu.bigdata.st.type;

import java.util.HashMap;

import com.vividsolutions.jts.geom.Geometry;

import kpu.bigdata.st.operator.AnalsysOperator;
import kpu.bigdata.st.operator.RelationalOperator;
import kpu.bigdata.st.operator.TrajectoryOperators;
import kpu.bigdata.temporal.type.Instant;
import kpu.bigdata.temporal.type.Temporal;


public interface SpatioTemporal extends RelationalOperator, AnalsysOperator, TrajectoryOperators{

	/*
	 * 
	 *  시공간 데이터 타입
	 * 
	 * 
	 * */
	
	// 데이터
	
	public void put(long t, Geometry p);
	public void put(Temporal t, Geometry p);
	public void put(SpatioTemporal obj);
	
	public HashMap<Long, Geometry> getGeomtryMap();
	
	public Geometry at(Instant i);
	public Geometry at(long i);
	
	public String toString();
}
