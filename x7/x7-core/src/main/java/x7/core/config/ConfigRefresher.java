package x7.core.config;

import java.util.List;
import java.util.Map;

import x7.core.bean.Config;

public class ConfigRefresher {

	public static void refresh(List<Config> list){
		
		Map<String, Object> map = Configs.getMap(null);
		if (map == null)
			return;
//		System.out.println("config list = " + list);
		for (Config config : list){
			if (config == null || config.getKeyX() == null)
				continue;
			map.put(config.getKeyX(), config.getValue());
		}
	}
}