package kpu.bigdata.sample;

import java.util.Date;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import kpu.bigdata.st.type.STObjectViz;
import kpu.bigdata.st.type.STObject;
import kpu.bigdata.st.type.STObjectMultiViz;
import kpu.bigdata.st.type.utils;
import kpu.bigdata.temporal.type.Instant;
import kpu.bigdata.temporal.type.Period;

public class Sample {
	public static void main(String[] args) {
		/*
		 * 
		 * 테스트 코드
		 * 
		 * */
		
		// 배율, MUL이 30이면 (1,1) 점은 (30,30)에 사상됨
		
		if(true){
			STObject stobj = STObject.getFromString("ST_OBJECT((PERIOD(1970-01-01 09:00:00~1970-01-01 09:00:04), POLYGON ((90 90, 240 90, 240 240, 90 240, 90 90))) (PERIOD(1970-01-01 09:00:05~1970-01-01 09:00:10), POLYGON ((90 120, 90 240, 240 240, 240 90, 120 90, 120 30, 30 30, 30 120, 90 120))))");
			System.out.println(stobj.toString());
			return;
		}
		
		final int MUL = 30;
		
		Date t1 = new Date(0000);
		Date t2 = new Date(10000);
		Date t3 = new Date(5000);

		Period p1 = new Period(t1, t2); // 0 ~ 10
		Period p2 = new Period(t3, t2); // 5 ~ 10
		Instant i = new Instant("2018-12-28 14:15:07"); // 7

		// 시공간 점 객체 생성
		STObject stPoint1 = new STObject(i, utils.createPoint(5*MUL, 5*MUL)); // POINT(7,5 5)
		STObject stPoint2 = new STObject(i, utils.createPoint(5*MUL, 6*MUL)); // POINT(7,5 6)
		
		// stPoint1 시각화
		new STObjectViz(stPoint1);
		new STObjectViz(stPoint2);
		
		// 시공간 라인스트링 생성을 위한 x, y 좌표들
		double stLine1_x[] = {
			1*MUL,3*MUL,5*MUL,7*MUL,9*MUL
		};
		double stLine1_y[] = {
			1*MUL,3*MUL,5*MUL,7*MUL,9*MUL
		};

		// 시공간 라인스트링 생성
		STObject stLine1 = new STObject(p1, utils.createLineString(stLine1_x, stLine1_y, stLine1_x.length));
		STObject stLine2 = new STObject(p2, utils.createLineString(stLine1_x, stLine1_y, stLine1_x.length));

		// stLine1,2 시각화
		new STObjectViz(stLine1);
		new STObjectViz(stLine2);
		
		// 시공간 다각형 생성을 위한 x, y 좌표들
		double stPolygon1_x[] = {
			3*MUL,8*MUL,8*MUL,3*MUL,3*MUL
		};
		double stPolygon1_y[] = {
			3*MUL,3*MUL,8*MUL,8*MUL,3*MUL
		};

		double stPolygon2_x[] = {
			1*MUL,4*MUL,4*MUL,1*MUL,1*MUL
		};
		double stPolygon2_y[] = {
			1*MUL,1*MUL,4*MUL,4*MUL,1*MUL
		};
		
		// 시공간 다각형 생성
		STObject stPolygon1 = new STObject(p1, utils.createPolygon(stPolygon1_x, stPolygon1_y, stPolygon1_x.length));
		STObject stPolygon2 = new STObject(p2, utils.createPolygon(stPolygon2_x, stPolygon2_y, stPolygon2_x.length));
		
		// stPolygon1 시각화
		new STObjectViz(stPolygon1);
		new STObjectViz(stPolygon2);
		
		// --------- 관계 연산자 테스트 -------------
		System.out.println("stPOINT 1 :\n"+stPoint1.toString());
		System.out.println();
		System.out.println("stPOINT 2 :\n"+stPoint2.toString());
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();

		System.out.println("stPOINT 1 [EQUALS] stPOINT 2 => "+stPoint1.equals(stPoint2));
		System.out.println("stPOINT 1 [DISJOINT] stPOINT 2 => "+stPoint1.disjoint(stPoint2));
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();
		System.out.println("stLINE 1 :\n"+stLine1.toString());
		System.out.println();
		System.out.println("stLINE 2 :\n"+stLine2.toString());
		System.out.println();
		System.out.println("stPOLYGON 1 :\n"+stPolygon1.toString());
		System.out.println();
		System.out.println("stPOLYGON 2 :\n"+stPolygon2.toString());
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();

		System.out.println("stPOLYGON 1 [EQUALS] stPOLYGON 2 => "+stPolygon1.equals(stPolygon2));
		System.out.println("stLINE 1 [CROSSES] stPOLYGON 1 => "+stLine1.crosses(stPolygon1));
		System.out.println("stPOLYGON 1 [CONTAINS] stPOINT 2 => "+stPolygon1.contains(stPoint2));
		System.out.println("stPOLYGON 1 [TOUCHES] stPOLYGON 2 => "+stPolygon1.touches(stPolygon2));
		System.out.println("stPOLYGON 1 [OVERLAPS] stPOLYGON 2 => "+stPolygon1.overlaps(stPolygon2));
		System.out.println("stLINE 1 [DISJOINT] stPOINT 1 => "+stLine1.disjoint(stPoint1));
		System.out.println("stLINE 1 [DISJOINT] stPOINT 2 => "+stLine1.disjoint(stPoint2));
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();
		
		
		//--------------- 분석 연산자 테스트 -----------------
		
		
		System.out.println("stPOLYGON 1 :\n"+stPolygon1.toString());
		System.out.println();
		System.out.println("stPOLYGON 2 :\n"+stPolygon2.toString());
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();
		System.out.println("stPOLYGON 1 [UNION] stPOLYGON 2 => ");
		STObject union = stPolygon1.union(stPolygon2);
		new STObjectViz("UNION",union);
		System.out.println(union.toString());
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();
		System.out.println("stPOLYGON 1 [DIFFERENCE] stPOLYGON 2 => ");
		STObject dif = stPolygon1.difference(stPolygon2);
		System.out.println(dif.toString());
		new STObjectViz("DIFFERENCE",dif);
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();
		System.out.println("stPOLYGON 1 [INTERSECTION] stPOLYGON 2 => ");
		STObject inte = stPolygon1.intersection(stPolygon2);
		System.out.println(inte.toString());
		new STObjectViz("INTERSECTION",inte);
		System.out.println();
		System.out.println("------------------------------------------");
		System.out.println();

		//--------------- 궤적 연산자 테스트 -----------------
		
		double stPolygon_x[] = {
			2*MUL,4*MUL,4*MUL,2*MUL,2*MUL
		};
		double stPolygon_y[] = {
			2*MUL,2*MUL,4*MUL,4*MUL,2*MUL
		};
		
		// 궤적연산을 위한 다각형 생성
		STObject someSTPolygon = new STObject(new Period(new Date(0000), new Date(5000)), utils.createPolygon(stPolygon_x, stPolygon_y, stPolygon_x.length));

		
		System.out.println("SOME STPOLYGON :\n"+someSTPolygon.toString());
		System.out.println();
		
		// 택시 1
		STObject taxi1 = new STObject(new Instant(new Date(0000)), utils.createPoint(0, 0));
		taxi1.put(new Instant(new Date(1000)), utils.createPoint(1*MUL, 1*MUL));
		taxi1.put(new Instant(new Date(2000)), utils.createPoint(2*MUL, 2*MUL));
		taxi1.put(new Instant(new Date(3000)), utils.createPoint(3*MUL, 3*MUL));
		taxi1.put(new Instant(new Date(4000)), utils.createPoint(3*MUL, 3*MUL));
		taxi1.put(new Instant(new Date(5000)), utils.createPoint(3*MUL, 3*MUL));
		
		// 택시 2
		STObject taxi2 = new STObject(new Instant(new Date(0000)), utils.createPoint(3*MUL, 3*MUL));
		taxi2.put(new Instant(new Date(1000)), utils.createPoint(3*MUL, 3*MUL));
		taxi2.put(new Instant(new Date(2000)), utils.createPoint(2*MUL, 2*MUL));
		taxi2.put(new Instant(new Date(3000)), utils.createPoint(1*MUL, 1*MUL));
		taxi2.put(new Instant(new Date(4000)), utils.createPoint(0, 0));
		taxi2.put(new Instant(new Date(5000)), utils.createPoint(0, 0));
		
		// 택시 3
		STObject taxi3 = new STObject(new Instant(new Date(0000)), utils.createPoint(0, 0));
		taxi3.put(new Instant(new Date(1000)), utils.createPoint(1*MUL, 1*MUL));
		taxi3.put(new Instant(new Date(2000)), utils.createPoint(2*MUL, 2*MUL));
		taxi3.put(new Instant(new Date(3000)), utils.createPoint(3*MUL, 3*MUL));
		taxi3.put(new Instant(new Date(4000)), utils.createPoint(4*MUL, 4*MUL));
		taxi3.put(new Instant(new Date(5000)), utils.createPoint(5*MUL, 5*MUL));
	
		System.out.println("TAXI1 :\n"+taxi1.toString());
		System.out.println();
		
		System.out.println("TAXI1 ENTERS SOME STPOLYGON ? : "+taxi1.enters(someSTPolygon));
		System.out.println("TAXI1 LEAVES SOME STPOLYGON ? : "+taxi1.leaves(someSTPolygon));
		System.out.println("TAXI1 PASSES SOME STPOLYGON ? : "+taxi1.passes(someSTPolygon));

		new STObjectMultiViz("TAXI 1", someSTPolygon, taxi1);
		
		System.out.println("TAXI2 :\n"+taxi2.toString());
		System.out.println();
		
		System.out.println("TAXI2 ENTERS SOME STPOLYGON ? : "+taxi2.enters(someSTPolygon));
		System.out.println("TAXI2 LEAVES SOME STPOLYGON ? : "+taxi2.leaves(someSTPolygon));
		System.out.println("TAXI2 PASSES SOME STPOLYGON ? : "+taxi2.passes(someSTPolygon));
		
		new STObjectMultiViz("TAXI 2", someSTPolygon, taxi2);
		
		System.out.println("TAXI3 :\n"+taxi3.toString());
		System.out.println();
		
		System.out.println("TAXI3 ENTERS SOME STPOLYGON ? : "+taxi3.enters(someSTPolygon));
		System.out.println("TAXI3 LEAVES SOME STPOLYGON ? : "+taxi3.leaves(someSTPolygon));
		System.out.println("TAXI3 PASSES SOME STPOLYGON ? : "+taxi3.passes(someSTPolygon));
		
		new STObjectMultiViz("TAXI 3", someSTPolygon, taxi3);
		System.out.println(taxi3.toString());
	}
}
