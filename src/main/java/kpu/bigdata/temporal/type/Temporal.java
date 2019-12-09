package kpu.bigdata.temporal.type;

public abstract class Temporal {
	/*
	 * 
	 * 시간 타입
	 * 
	 * */
	
	// 객체 정보
	private Object userData = null;
	
	public Temporal() {
		
	}

	public Temporal(Object ud) {
		this.userData = ud;
	}
	
	public Object getUserData() {
		return userData;
	}
	
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof Temporal)) return false;
		if(this instanceof Instant) {
			if(!(o instanceof Instant)) {
				return false;
			}
			Instant i1 = (Instant)this;
			Instant i2 = (Instant)o;
			if(i1.getT().equals(i2.getT())) {
				return true;
			}
		}
		else if(this instanceof Period) {
			if(!(o instanceof Period)) {
				return false;
			}
			Period i1 = (Period)this;
			Period i2 = (Period)o;
			if(i1.getT1().equals(i2.getT1()) && i1.getT2().equals(i2.getT2())) {
				return true;
			}
		}
		return false;
	}
	
	public boolean contains(Temporal o) {
		if(this instanceof Instant) {
			if(!(o instanceof Instant)) {
				return false;
			}
			Instant i1 = (Instant)this;
			Instant i2 = (Instant)o;
			if(i1.getT().equals(i2.getT())) {
				return true;
			}
		}
		if(this instanceof Period) {
			Period p1 = (Period)this;
			if(o instanceof Period) {
				Period p2 = (Period)o;
				if(p2.getT1().after(p1.getT1()) && p2.getT2().before(p1.getT2())) {
					return true;
				}
			}else {
				Instant i2 = (Instant)o;
				if(i2.getT().after(p1.getT1()) && i2.getT().before(p1.getT2())) {
					return true;
				}
			}
		}
		return false;
	}
}
