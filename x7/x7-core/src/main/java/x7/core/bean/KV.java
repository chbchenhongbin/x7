package x7.core.bean;

import java.io.Serializable;

public class KV implements Serializable{

	private static final long serialVersionUID = -3617796537738183236L;
	public String k;
	public Object v;
	public KV(){}
	public KV(String k, Object v){
		this.k = k;
		this.v = v;
	}
	
	public String getK() {
		return k;
	}
	public void setK(String k) {
		this.k = k;
	}
	public Object getV() {
		return v;
	}
	public void setV(Object v) {
		this.v = v;
	}
}
