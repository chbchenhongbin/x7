package x7.config;

import x7.core.template.ITemplateable;

public class DogTemplate implements ITemplateable{

	private long templateId;
	private String name;
	@Override
	public Object getTemplateId() {
		// TODO Auto-generated method stub
		return templateId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
	@Override
	public void setTemplateId(Object id) {
		// TODO Auto-generated method stub
		this.templateId = (long)id;
	}

}
