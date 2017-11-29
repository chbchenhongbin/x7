package x7.core.search;


public class TagView{

	private String tag;

	public void setTag(String tagStr) {
		this.tag = tagStr;
		
	}


	public String getTag() {
		return this.tag;
	}

	
	public void makeTag() {
		
	}

	@Override
	public String toString() {
		return "TagView [tag=" + tag + "]";
	}

}
