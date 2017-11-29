package x7.core.keeper;

import java.util.List;

import x7.core.type.DataEventType;

public interface IKeeper {
	
	String CONFIG_ROOT = "configRoot";
	String SYSTEM_ROOT = "systemRoot";
	
	void onChanged(DataEventType type, List<String> keyList, Object obj);
}
