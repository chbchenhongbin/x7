package x7.repository.sharding;


import x7.core.config.Configs;
import x7.core.util.VerifyUtil;

public enum ShardingPolicy {

	
	MD5 {

		@Override
		public String getKey(long key) {
			int num = Configs.getIntValue("x7.db.sharding.num");
			int to = 1;
			if (num == 256)
				to = 2;
			String md5Str = VerifyUtil.toMD5(String.valueOf(key));
			return md5Str.substring(0, to);
		}

		@Override
		public String getKey(String key) {
			int num = Configs.getIntValue("x7.db.sharding.num");
			int to = 1;
			if (num == 256)
				to = 2;
			String md5Str = VerifyUtil.toMD5(key);
			return md5Str.substring(0, to);
		}

		@Override
		public String[] getSuffixArr() {
			String[] arr16 = {"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
			int num = Configs.getIntValue("x7.db.sharding.num");
			if (num == 256){
				int n = 0;
				String[] arr256 = new String[256];
				for (int i = 0; i < 16; i ++){
					for (int j = 0; j < 16; j++){
						arr256[n++] = arr16[i] + arr16[j];
					}
				}
				return arr256;
			}
			return arr16;
		}
	},
	
	HASH {

		@Override
		public String getKey(long key) {
			int num = Configs.getIntValue("x7.db.sharding.num");
			return String.valueOf(key % num);
		}

		@Override
		public String getKey(String key) {
			int hash = key.hashCode();
			
			return getKey(hash);
		}

		@Override
		public String[] getSuffixArr() {
			int num = Configs.getIntValue("x7.db.sharding.num");
			String[] arr = new String[num];
 			for (int i =0; i < num; i++){
				arr[i] = String.valueOf(i);
			}
			return arr;
		}
	},
	
	;

	
	public abstract String getKey(long key);
	public abstract String getKey(String key);
	public abstract String[] getSuffixArr();
	
	public static ShardingPolicy get(String key){
		if (key == null || key.equals(""))
			return MD5;
		for (ShardingPolicy value : values()){
			if (value.toString().equals(key)){
				return value;
			}
		}
		throw new RuntimeException("CONFIG EXCEPTION, SHARDING NO POLICY");
	}
}
