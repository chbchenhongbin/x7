package x7.core.search;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TagParsed implements Serializable{

	private static final long serialVersionUID = 7631076184961243538L;

	private Class type;
	private String tagKey;
	private Field field;
	
	
	public Class getType() {
		return type;
	}
	public void setType(Class type) {
		this.type = type;
	}


	public String getTagKey() {
		return tagKey;
	}
	public void setTagKey(String tagKey) {
		this.tagKey = tagKey;
	}
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	@Override
	public String toString() {
		return "TagParsed [type=" + type + ", tagKey=" + tagKey 
				+ "]";
	}
}
