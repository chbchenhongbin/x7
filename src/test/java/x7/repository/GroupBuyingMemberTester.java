package x7.repository;

import java.util.Date;


import x7.config.ConfigBuilder;


public class GroupBuyingMemberTester {

	public static void main(String[] args) {
		
		ConfigBuilder.newInstance();
		
		RepositoryBooter.boot();
		
		Date date = new Date();
		
		GroupBuyingMember member = new GroupBuyingMember();
		
		member.setGroupBuyingId(111);
		member.setBuyerId(3423345);
		member.setCreateTime(date);
		member.setRefreshTime(date);
		member.setOrderTime(date);
		
		boolean flag = Repositories.getInstance().refresh(member);
		
		System.out.println("------------ flag = " + flag);
		
	}
}
