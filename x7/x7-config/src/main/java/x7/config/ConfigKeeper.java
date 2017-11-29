package x7.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import x7.core.config.Configs;
import x7.core.keeper.IKeeper;
import x7.core.type.DataEventType;


public class ConfigKeeper implements IKeeper{

	private  Map<String, Object> map = Configs.referMap(null);
	
	private static ConfigKeeper instance;
	public static ConfigKeeper getInstance(){
		if (instance == null){
			instance = new ConfigKeeper();
		}
		return instance;
	}
	
	public Map<String, Object> getMap(){
		return map;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onChanged(DataEventType type, List<String> keyList, Object obj){
		if (! CONFIG_ROOT.equals(keyList.get(0)))
			return;
		keyList.remove(0);
		
		String configSpace = Configs.getString("x7.config.space");
		if (! configSpace.equals(keyList.get(0)))
			return;
		keyList.remove(0);
		
		int size = keyList.size();
		if (size == 0)
			return;
		Map<String, Object> mapObject = map;
		int length = size - 1;
		for (int i = 0; i < length; i++) {
			String key = keyList.get(i);
			
			Object o = mapObject.get(key);
			if (o == null){
				o = new ConcurrentHashMap<String,Object>();
				mapObject.put(key, o);
			}
			mapObject = (Map<String, Object>) o;
		}
		
		switch (type){
		case CREATE:
			mapObject.put(keyList.get(length), obj);
			break;
		case REFRESH:
			mapObject.put(keyList.get(length), obj);
			break;
		case REMOVE:
			mapObject.remove(keyList.get(length));
			break;
		}
	}
	
	
}
