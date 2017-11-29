package x7.repository.util;

import java.util.ArrayList;
import java.util.List;

public class IdFixedShardingUtil {

	public static List<long[]> listBeginEnd(long minId, long maxId, long count, long rowsOfSharding){
		
		List<long[]> list = new ArrayList<long[]>();
		
		long testCount = maxId - minId + 2;
		
		System.out.println("__testCount = " + testCount);
		
		long size = count / rowsOfSharding + (count%rowsOfSharding == 0 ? 0 : 1);
		
		System.out.println("__size = " + size);
		
		long shardingNum = size == 0 ? 1 : testCount / size;
		
		System.out.println("__ShardingNum = " + shardingNum);
		
		for (int i=0; i<size; i++){
			long begin = minId + i * shardingNum;
			long end = minId + (i + 1) * shardingNum - 1;
			
			end = maxId < end ? maxId : end;
			
			long[] be = new long[2];
			be[0] = begin;
			be[1] = end;
			
			if (i == size-1){
				be[1] += end;
			}
			
			list.add(be);
			System.out.println("_________begin: " + be[0] + ", end: " + be[1]);
		}
		
		return list;
	}


}
