package x7.repository;

import x7.core.util.MathExt;
import x7.core.util.VerifyUtil;

public class PigTester {

	public static void main(String[] args) {
		
		int j = 0;
		
		for (int i=0; i<500; i++){
			int hit = MathExt.random(133444, 53334444);
			
			String str = VerifyUtil.toMD5(String.valueOf(hit));
			
			str = str.substring(0, 5);
			
			try{
				Integer.valueOf(str);
				
			}catch(Exception e){
				System.out.println(str);
				
				j++;
				
				if (j==160)
					break;
			}
			
		}
		
	}
}
