package in.tdrhq.lisp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Cons {
	public Object car;
	public Object cdr;

	// convenience call
	public Object getIndex(int i) {
		if (i == 0) {
			return car;
		} else {
			return ((Cons) cdr).getIndex(i - 1);
		}
	}
	
	public static Cons build(Object... objects) {
		return fromList(new ArrayList<Object>(Arrays.asList(objects)));
	}
	
	public String toString() {
		return toList().toString();
	}
	
	public List<Object> toList() {
		List<Object> o = new ArrayList<Object>();
		
		Cons c = this;
		while (c != null) {
			o.add(c.car);
			c = (Cons) c.cdr;
		}
		return o;
	}
	
	public boolean equals(Object other_) {
		if (other_ == null || !(other_ instanceof Cons)) {
			return false;
		}
		
		Cons other = (Cons) other_;
		
		return nullOrEquals(car, other.car) &&
				nullOrEquals(cdr, other.cdr);
	}
	
	public boolean nullOrEquals(Object a, Object b) {
		if (a == null) {
			return b == null;
		}
		return a.equals(b);
	}
	
	public static Cons fromList(List<Object> values) {
		if (values.size() == 0) {
			return null;
		}
		
		Cons end = new Cons();
		end.car = values.remove(0);
		
		Cons start = end;
		for (Object value : values) {
			Cons c = new Cons();
			c.car = value;
			end.cdr = c;
			end = c;
		}
		
		return start;
	}
}
