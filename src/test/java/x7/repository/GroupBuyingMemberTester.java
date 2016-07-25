package x7.repository;

import java.util.Date;

import com.igoxin.bean.entity.groupbuying.GroupBuyingMember;

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
		
		Repositories.getInstance().create(member);
		
	}
}
