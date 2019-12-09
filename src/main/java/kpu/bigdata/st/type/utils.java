package kpu.bigdata.st.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

public class utils {
	// DATETIME 형식 문자열을 Date 객체로 변환
	public static Date getDateTime(String datetime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date;
		try {
			date = sdf.parse(datetime);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			return null;
		}
		return date;
	}
	
	// 시간 l에 해당하는 DATETIME 형식 문자열을 반환
	public static String getDateTimeString(long l) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date(l);
		return sdf.format(date);
	}

	// 점 생성
	public static Point createPoint(double x, double y) {
		GeometryFactory gf = new GeometryFactory();
		Coordinate c[] = new Coordinate[1];
		c[0] = new Coordinate(x,y);
		Point p = new Point(new CoordinateArraySequence(c), gf);
		return p;
	}

	// 다각형 생성
	public static Polygon createPolygon(double x[], double y[], int size) {
		GeometryFactory gf = new GeometryFactory();
		Coordinate coordinates[] = new Coordinate[size];
		for(int i=0;i<size;i++) {
			coordinates[i] = new Coordinate(x[i], y[i]);
		}
		LinearRing lr = gf.createLinearRing(coordinates);
		Polygon p = gf.createPolygon(lr, null);
		return p;
	}

	// 라인스트링 생성
	public static LineString createLineString(double x[], double y[], int size) {
		GeometryFactory gf = new GeometryFactory();
		Coordinate coordinates[] = new Coordinate[size];
		for(int i=0;i<size;i++) {
			coordinates[i] = new Coordinate(x[i], y[i]);
		}
		LineString p = gf.createLineString(coordinates);
		return p;
	}
	
	public static STObject getSTObjectFromString(String s){
		
		return null;
	}
}
