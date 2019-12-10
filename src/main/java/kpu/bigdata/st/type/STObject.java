package kpu.bigdata.st.type;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.w3c.dom.UserDataHandler;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import kpu.bigdata.temporal.type.Instant;
import kpu.bigdata.temporal.type.Period;
import kpu.bigdata.temporal.type.Temporal;

public class STObject implements SpatioTemporal, Cloneable {
	/*
	 * 
	 *  STGeometry 타입
	 *  
	 *  시간 + Geometry 객체
	 * 
	 * */
	
	// 시간에 따른 공간객체 정보를 가지는 해쉬맵
	private HashMap<Long, Geometry> stGeometry;
	private HashMap<String, String> userData = null;

	public STObject() {
		stGeometry = new HashMap<>();
	}
	public STObject(Temporal t, Geometry p) {
		stGeometry = new HashMap<>();
		put(t, p);
	}

	// 시간 t에 공간객체 p의 상태를 추가함
	public void put(long t, Geometry p) {
		stGeometry.put(t, p);
	}
	
	public void put(Temporal t, Geometry p) {
		if(t instanceof Instant) {
			Instant ins = (Instant)t;
			long at = ins.getT().getTime()/1000;
			stGeometry.put(at, p);
		}
		else if(t instanceof Period) {
			Period per = (Period)t;
			long from = per.getT1().getTime()/1000;
			long to = per.getT2().getTime()/1000;
			for(long time = from; time<=to; time++) {
				stGeometry.put(time, p);
			}
		}
	}

	
	@Override
	public void put(SpatioTemporal obj) {
		LinkedList<Long> keys = new LinkedList<>(obj.getGeomtryMap().keySet());
		for(long l : keys){
			stGeometry.put(l, obj.getGeomtryMap().get(l));
		}
	}

	// 시간에 따른 공간객체 정보를 가지는 해쉬맵을 반환
	public HashMap<Long, Geometry> getGeomtryMap(){
		return stGeometry;
	}

	// 시간 i에 해당하는 공간객체의 상태를 나타냄
	public Geometry at(Instant i) {
		return stGeometry.getOrDefault(i.getT().getTime()/1000, null);
	}
	
	// 시간 i에 해당하는 공간객체의 상태를 나타냄
	public Geometry at(long i) {
		return stGeometry.getOrDefault(i, null);
	}
	
	public STObject periodFilter(Period period){
		STObject res = this;
		try {
			res = (STObject) this.clone();
			long from = period.getT1().getTime()/1000;
			long to = period.getT2().getTime()/1000;
			LinkedList<Long> keys = (LinkedList<Long>) new LinkedList<>(res.getGeomtryMap().keySet()).clone();
			for(long l : keys){
				if(from > l || to < l){
					res.getGeomtryMap().remove(l);
				}
			}
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return res;
	}

	// 문자열로 반환
	@Override
	public String toString() {
		String s = "ST_OBJECT (";
		if(userData != null){
			s = "ST_OBJECT<";
			for(String key:userData.keySet()){
				s+=key+":"+userData.get(key)+";";
			}
			s+="> (";
		}
		LinkedList<Long> keys = new LinkedList<>(getGeomtryMap().keySet());
		Collections.sort(keys);
		Geometry geom = getGeomtryMap().get(keys.getFirst());
		long first = keys.getFirst();
		long to = first;
		if(keys.size() == 1) {
			return s+"(INSTANT ("+utils.getDateTimeString(first*1000)+"), "+geom.toString()+")";
		}
		for(long key : keys){
			Geometry g = at(key);
			if(!g.equals(geom)) {
				if(first == key-1)
					s+="(INSTANT ("+utils.getDateTimeString(first*1000)+"), "+geom.toString()+") ";
				else
					s+="(PERIOD ("+utils.getDateTimeString(first*1000)+" ~ "+(utils.getDateTimeString(key*1000-1000))+"), "+geom.toString()+") ";
				first = key;
				geom = g;
			}else {
				to = key;
			}
		}
		if(to != first) s+="(PERIOD ("+utils.getDateTimeString(first*1000)+" ~ "+utils.getDateTimeString(to*1000)+"), "+geom.toString()+")";
		return s+")";
	}
	
	public static STObject getFromString(String s){
		STObject obj = null;
		
		if(s.startsWith("ST_OBJECT")){
			LinkedList<String> slist = new LinkedList<>();
			
			obj = new STObject();
			if(s.startsWith("ST_OBJECT<")){
				String udstr = s.split("<")[1].split(">")[0];
				String uds[] = udstr.split(";");
				HashMap<String, String> userData = new HashMap<>();
				for(String ud:uds){
					if(ud.length()==0)continue;
					String kv[] = ud.split(":");
					String key = kv[0];
					String val = kv[1];
					
					userData.put(key, val);
				}
				
				obj.setUserData(userData);
				s = s.replace(s.split("\\(")[0], "ST_OBJECT ");
			}
			
			s = s.replace("ST_OBJECT(", "");
			s = s.replace("ST_OBJECT (", "");
			String tmp[] = s.split("\\)");
			String temp = "";
			for(String t : tmp){
				if(t.length()==0)continue;
				if(temp.length() > 0){
					temp += t;
					temp += ")";
					if(temp.contains("POLYGON")) temp += ")";
					temp = temp.trim();
					temp = temp.substring(1);
					slist.add(temp);
					temp = "";
				}else {
					temp = t;
					temp += ")";
				}
			}
			
			for(String t : slist){
				Temporal tem = null;
				Geometry geo = null;
				//System.out.println(t);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String ss = t.split(",")[0];
				String geopart = t.replace(ss+",", "");
				if(ss.startsWith("INSTANT")){
					String ts = ss.split("\\(")[1].split("\\)")[0];
					try {
						Date _t = sdf.parse(ts);
						
						tem = new Instant(_t);
						
						
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				else if(ss.startsWith("PERIOD")){
					String ts = ss.split("\\(")[1].split("\\)")[0];
					String t1 = ts.split("~")[0];
					String t2 = ts.split("~")[1];
					try {
						Date _t1 = sdf.parse(t1);
						Date _t2 = sdf.parse(t2);
						
						tem = new Period(_t1, _t2);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				String geom = geopart.trim();
				WKTReader reader = new WKTReader();
				try {
					geo = reader.read(geom);
				} catch (com.vividsolutions.jts.io.ParseException e) {
					e.printStackTrace();
				}
				
				if(tem != null && geo != null){
					obj.put(tem, geo);
				}
			}
		}
		return obj;
	}
	
	@Override
	public boolean equals(SpatioTemporal st) {
		if(!(st instanceof STObject)) return false;
		STObject p = (STObject)this;
		STObject t = (STObject)st;
		if(p.getGeomtryMap().keySet().size() != t.getGeomtryMap().keySet().size()) return false;
		
		LinkedList<Long> pl = new LinkedList<>(p.getGeomtryMap().keySet());
		Collections.sort(pl);
		LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
		Collections.sort(tl);
			
		if(!pl.getFirst().equals(tl.getFirst()) || !(pl.getLast().equals(tl.getLast()))) return false;
		for(long l = pl.getFirst(); l<=pl.getLast(); l++) {
			if(p.at(l) == null) {
				if(t.at(l) != null) {
					return false;
				}
				continue;
			}
			if(!p.at(l).equals(t.at(l))) return false;
		}
		return true;
	}
	
	@Override
	public boolean contains(SpatioTemporal b) {
		STObject p = (STObject)this;
		if(b instanceof STObject) {
			STObject t = (STObject)b;
			
			if(p.getGeomtryMap().keySet().containsAll(t.getGeomtryMap().keySet())) {
				for(Long l : t.getGeomtryMap().keySet()) {
					if(!p.at(l).contains(t.at(l))) return false;
				}
				return true;
			}else return false;
		}
		return false;
	}
	
	@Override
	public boolean crosses(SpatioTemporal st) {
		STObject p = this;
		if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(p.getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);

			long f1 = Math.min(pl.getFirst(), tl.getFirst());
			long f2 = Math.max(pl.getLast(), tl.getLast());
			
			for(long l : pl){
				Geometry g1 = p.at(l);
				Geometry g2 = t.at(l);
				try {
				if(g1.crosses(g2)) {
					return true;
				}
				}catch(NullPointerException e) {
					continue;
				}
			}
		}
		return false;
	}
	@Override
	public boolean disjoint(SpatioTemporal st) {
		STObject p = this;
		if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(p.getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);

			long f1 = Math.min(pl.getFirst(), tl.getFirst());
			long f2 = Math.max(pl.getLast(), tl.getLast());
			
			for(long l : pl){
				Geometry g1 = p.at(l);
				Geometry g2 = t.at(l);
				try {
					if(!g1.disjoint(g2)) {
					return false;
				}
				}catch(NullPointerException e) {
					continue;
				}
			}
		}
		return true;
	}
	@Override
	public boolean touches(SpatioTemporal st) {
		STObject p = this;
		if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(p.getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);

			long f1 = Math.min(pl.getFirst(), tl.getFirst());
			long f2 = Math.max(pl.getLast(), tl.getLast());
			
			for(long l : pl){
				Geometry g1 = p.at(l);
				Geometry g2 = t.at(l);
				try {
				if(g1.touches(g2)) {
					return true;
				}
				}catch(NullPointerException e) {
					continue;
				}
			}
		}
		return false;
	}
	@Override
	public boolean overlaps(SpatioTemporal st) {
		STObject p = this;
		if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(p.getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);

			long f1 = Math.min(pl.getFirst(), tl.getFirst());
			long f2 = Math.max(pl.getLast(), tl.getLast());
			
			for(long l : pl){
				Geometry g1 = p.at(l);
				Geometry g2 = t.at(l);
				try {
				if(g1.overlaps(g2)) {
					return true;
				}
				}catch(NullPointerException e) {
					continue;
				}
			}
		}
		return false;
	}
	public STObject union(SpatioTemporal b) {
		STObject stg = null;
		STObject p = (STObject)this;
		if(b instanceof STObject) {
			STObject t = (STObject)b;
			stg = new STObject();
			LinkedList<Long> pl = new LinkedList<>(p.getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);
			long f1 = Math.min(pl.getFirst(), tl.getFirst());
			long f2 = Math.max(pl.getLast(), tl.getLast());
			for(long l = f1; l<=f2; l++){
				Geometry g1 = p.at(l);
				Geometry g2 = t.at(l);
				if(g1 == null && g2 == null){
					continue;
				}else if(g1 == null){
					stg.put(l, g2);
				}else if(g2 == null){
					stg.put(l, g1);
				}else{
					Geometry g = g1.union(g2);
					stg.put(l, g);
				}
			}
		}
		return stg;
	}
	
	public STObject difference(SpatioTemporal b) {
		STObject stg = null;
		STObject p = (STObject)this;
		if(b instanceof STObject) {
			STObject t = (STObject)b;
			stg = new STObject();
			LinkedList<Long> pl = new LinkedList<>(p.getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);
			long f1 = Math.min(pl.getFirst(), tl.getFirst());
			long f2 = Math.max(pl.getLast(), tl.getLast());
			for(long l = f1; l<=f2; l++){
				Geometry g1 = p.at(l);
				Geometry g2 = t.at(l);
				if(g1 == null && g2 == null){
					continue;
				}else if(g1 == null){
					continue;
				}else if(g2 == null){
					stg.put(l, g1);
				}else{
					Geometry g = g1.difference(g2);
					stg.put(l, g);
				}
			}
		}
		return stg;
	}
	
	public STObject intersection(SpatioTemporal b) {
		STObject stg = null;
		STObject p = (STObject)this;
		if(b instanceof STObject) {
			STObject t = (STObject)b;
			stg = new STObject();
			LinkedList<Long> pl = new LinkedList<>(p.getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);
			long f1 = Math.min(pl.getFirst(), tl.getFirst());
			long f2 = Math.max(pl.getLast(), tl.getLast());
			for(long l = f1; l<=f2; l++){
				Geometry g1 = p.at(l);
				Geometry g2 = t.at(l);
				if(g1 == null || g2 == null){
					continue;
				}else{
					Geometry g = g1.intersection(g2);
					stg.put(l, g);
				}
			}
		}
		return stg;
	}
	@Override
	public boolean enters(SpatioTemporal st) {
		/*if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);
			int step = 0;
			for(long l : pl){
				Geometry g1 = at(l);
				Geometry g2 = t.at(l);
				
				if(g1 == null || g2 == null) {
					continue;
				}else {
					if(step == 0) {
						if(!g1.contains(g2)) {
							step = 1;
						}
					}else if(step == 1) {
						if(g1.contains(g2)) {
							step = 2;
						}
					}
				}
			}
			if(step == 2)
				return true;
		}*/
		if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);
			int step = 0;
			for(long l : pl){
				Geometry g1 = at(l);
				Geometry g2 = t.at(l);
				
				if(g1 == null || g2 == null) {
					return false;
				}else {
					if(step == 0) {
						if(!g2.contains(g1)) {
							step = 1;
						}else return false;
					}else if(step == 1) {
						if(g2.contains(g1)) {
							step = 2;
						}
					}else if(step == 2) {
						if(!g2.contains(g1)) {
							step = 3;
						}
					}
				}
			}
			if(step == 2)
				return true;
		}
		return false;
	}
	@Override
	public boolean insides(SpatioTemporal st) {
		if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);
			for(long l : pl){
				Geometry g1 = at(l);
				Geometry g2 = t.at(l);
				
				if(g1 == null || g2 == null) {
					return false;
				}else {
					if(!g2.contains(g1)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	@Override
	public boolean leaves(SpatioTemporal st) {
		if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);
			int step = 0;
			for(long l : pl){
				Geometry g1 = at(l);
				Geometry g2 = t.at(l);
				
				if(g1 == null || g2 == null) {
					return false;
				}else {
					if(step == 0) {
						if(g2.contains(g1)) {
							step = 1;
						}else return false;
					}else if(step == 1) {
						if(!g2.contains(g1)) {
							step = 2;
						}
					}else if(step == 2) {
						if(g2.contains(g1)) {
							step = 3;
						}
					}
				}
			}
			if(step == 2)
				return true;
		}
		return false;
	}
	@Override
	public boolean meets(SpatioTemporal st) {
		if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);
			for(long l : pl){
				Geometry g1 = at(l);
				Geometry g2 = t.at(l);
				
				if(g1 == null || g2 == null) {
					return false;
				}else {
					if(!g2.touches(g1)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	@Override
	public boolean passes(SpatioTemporal st) {
		if(st instanceof STObject) {
			STObject t = (STObject)st;
			LinkedList<Long> pl = new LinkedList<>(getGeomtryMap().keySet());
			Collections.sort(pl);
			LinkedList<Long> tl = new LinkedList<>(t.getGeomtryMap().keySet());
			Collections.sort(tl);
			int step = 0;
			for(long l : pl){
				Geometry g1 = at(l);
				Geometry g2 = t.at(l);
				
				if(g1 == null || g2 == null) {
					return false;
				}else {
					if(step == 0) {
						if(!g2.contains(g1)) {
							step = 1;
						}else return false;
					}else if(step == 1) {
						if(g2.contains(g1)) {
							step = 2;
						}
					}else if(step == 2) {
						if(!g2.contains(g1)) {
							step = 3;
						}
					}
				}
			}
			if(step == 3)
				return true;
		}
		return false;
	}

	public String getName(){
		if(userData instanceof HashMap){
			HashMap<String, String> ud = (HashMap<String, String>)userData;
			return ud.getOrDefault("name", null);
		}
		return "undefined";
	}
	
	public void setUserData(HashMap<String, String> userData) {
		this.userData = userData;
	}
	
	public HashMap<String, String> getUserData() {
		return userData;
	}
}
