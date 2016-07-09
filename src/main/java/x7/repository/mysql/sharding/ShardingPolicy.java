package x7.repository.mysql.sharding;

import java.util.Arrays;

import x7.core.util.VerifyUtil;

public enum ShardingPolicy {

	
	MD5 {
		@Override
		public String[] getKeyArr(int key) {
			String md5Str = VerifyUtil.toMD5(String.valueOf(key));
			String[] arr = new String[2];
			arr[0] = md5Str.substring(0, 1);
			arr[1] = md5Str.substring(1, 2);
			System.out.println("key = " + key + ", keyArr = " + Arrays.toString(arr));
			return arr;
		}

		@Override
		public String[] getKeyArr(String key) {
			String md5Str = VerifyUtil.toMD5(key);
			String[] arr = new String[2];
			arr[0] = md5Str.substring(0, 1);
			arr[1] = md5Str.substring(1, 2);
			System.out.println("key = " + key + ", keyArr = " + Arrays.toString(arr));
			return arr;
		}
	},
	
	HASH {
		@Override
		public String[] getKeyArr(int key) {
			int hash = key;
			int keySize = ShardingConfig.getInstance().getKeySize();
			int keyTableSize = ShardingConfig.getInstance().getKeyTableSize();

			String[] arr = new String[2];
			arr[0] = String.valueOf(hash % keySize);
			arr[1] = String.valueOf(hash % keyTableSize);
			System.out.println("key = " + key + ", keyArr = " + Arrays.toString(arr));
			return arr;
		}

		@Override
		public String[] getKeyArr(String key) {
			int hash = key.hashCode();
			int keySize = ShardingConfig.getInstance().getKeySize();
			int keyTableSize = ShardingConfig.getInstance().getKeyTableSize();

			String[] arr = new String[2];
			arr[0] = String.valueOf(hash % keySize);
			arr[1] = String.valueOf(hash % keyTableSize);
			System.out.println("key = " + key + ", keyArr = " + Arrays.toString(arr));
			return arr;
		}
	},
	
	;
	

	public abstract String[] getKeyArr(int key);
	public abstract String[] getKeyArr(String key);
	
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
