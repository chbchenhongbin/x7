package x7.core.config;

/**
 * 游戏里各种常量KEY<br>
 * 请手动添加属性<br>
 *
 */
public class Keys {

	public static int getId(String key) {
		int id = 0;
		try {
			id = Keys.class.getDeclaredField(key).getAnnotation(Key.class).id();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return id;
	}

	public static String getDesc(String key) {
		String name = "";
		try {
			name = Keys.class.getDeclaredField(key).getAnnotation(Key.class)
					.desc();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		return name;
	}
	


	@Key(id = 1, desc = "经验")
	public final static String exp = "exp";

	@Key(id = 2, desc = "等级")
	public final static String level = "level";
	// ///////////////战斗//////////////////////
	@Key(id = 3, desc = "血量")
	public final static String hp = "hp";
	
	@Key(id = 4, desc = "HP比例")
	public final static String hpRate = "hpRate";
	
	@Key(id = 5, desc = "当前HP")
	public final static String currentHp = "currentHp";

	@Key(id = 6, desc = "伤害")
	public final static String damage = "damage";
	
	@Key(id = 7, desc = "吸收伤害")
	public final static String suckDamage = "suckDamage";
	
	@Key(id = 8, desc = "免伤比例")
	public final static String reduceDamageRate = "reduceDamageRate";

	@Key(id = 9, desc = "攻击")
	public final static String attack = "attack";

	@Key(id = 10, desc = "防御, 减伤的作用，例如：(攻击转换的伤害) * (1 - (1/(1 + defense/(2000 + level * 200) )")
	public final static String defense = "defense";

	@Key(id = 11, desc = "护甲")
	public final static String armor = "armor";
	
	@Key(id = 12, desc = "体能")
	public final static String physical = "physical";
	
	@Key(id = 13, desc = "能量")
	public final static String energy = "energy";	

	@Key(id = 14, desc = "闪避")
	public final static String dodgePlty = "dodgePlty";

	@Key(id = 15, desc = "命中")
	public final static String hitPlty = "hitPlty";

	@Key(id = 16, desc = "暴击概率")
	public final static String critPlty = "critPlty";

	@Key(id = 17, desc = "免暴概率，例如：150")
	public final static String resistCritPlty = "resistCritPlty";

	@Key(id = 18, desc = "暴击伤害比例")
	public final static String critRate = "critRate";

	@Key(id = 19, desc = "攻击吸血")
	public final static String hpSuckRate = "hpSuckRate";
	
	@Key(id = 21, desc = "攻击速度比例")
	public final static String aSpRate = "aSpRate";
	
	@Key(id = 22, desc = "移动速度比例")
	public final static String mSpRate = "mSpRate";
	
	@Key(id = 23, desc = "自动回血")
	public final static String autoRecoveryHp = "autoRecoveryHp";
	
	@Key(id = 24, desc = "忽视防御比例")
	public final static String ignoreDefenseRate = "ignoreDefenseRate";
	
	@Key(id = 25, desc = "技能时间比,负数表示减少")
	public final static String skillCDRate = "skillCDRate";
	
	@Key(id = 26, desc = "技能等级加点")
	public final static String skillUp = "skillUp";

	@Key(id = 27, desc = "移动速度")
	public final static String moveSpeed = "moveSpeed";	
	
	@Key(id = 28, desc = "士兵攻击‰")
	public final static String attackRate = "attackRate";

	@Key(id = 29, desc = "士兵防御‰")
	public final static String defenseRate = "defenseRate";	
	
	@Key(id = 36, desc = "装备基础属性加成")
	public final static String equipmentBaseRate = "equipmentBaseRate";
	
	
///////////////////FIGHTER PHYX////////////////////////	
	@Key(id = 40, desc = "格宽")
	public final static String tileWidth = "tileWidth";
	@Key(id = 41, desc = "格高")
	public final static String tileHeight = "tileHeight";
	@Key(id = 42, desc = "普攻CD")
	public final static String pAtkCDFrame = "pAtkCDFrame";
	@Key(id = 43, desc = "普攻距离")
	public final static String pAtkRange = "pAtkRange";
	@Key(id = 44, desc = "普攻命中帧数")
	public final static String pAtkHitFrame = "pAtkHitFrame";
	@Key(id = 45, desc = "受击框")
	public final static String actorH = "actorH";
	@Key(id = 46, desc = "偏移量")
	public final static String offsetH = "offsetH";
	@Key(id = 47, desc = "子弹")
	public final static String bullet = "bullet";
	
	

	// ///////////////资源//////////////////////

	@Key(id = 200, desc = "游戏币")
	public final static String goldCoin = "goldCoin";
	
	@Key(id = 201, desc = "充值换取的货币")
	public final static String diamond = "diamond";

	@Key(id = 202, desc = "木材")
	public final static String wood = "wood";
	
	@Key(id = 203, desc = "矿石")
	public final static String ore = "ore";
	
	@Key(id = 204, desc = "食物")
	public final static String food = "food";
	
	@Key(id = 205, desc = "战点,荣誉")
	public final static String fightPoint = "fightPoint";
	
	@Key(id = 206, desc = "贡献,声望")
	public final static String  contributionPoint = "contributionPoint";
	
	@Key(id = 207, desc = "特殊货币")
	public final static String  specialCurrency = "specialCurrency";
	

	@Key(id = 221, desc = "体力(可玩点数),LIMIT")
	public final static String playPointLimit = "playPointLimit";
	
	@Key(id = 222, desc = "可战斗次数限制,LIMIT")
	public final static String playTimesLimit = "playTimesLimit";	
	
	@Key(id = 223, desc = "扫荡次数,LIMIT")
	public final static String sweepTimesLimit = "sweepTimesLimit";	
	
	
	@Key(id = 224, desc = "技能升级次数,LIMIT")
	public final static String skillUpLimit = "skillUpLimit";	

	
	@Key(id = 500, desc = "战士碎片")
	public final static String fighterFragment = "fighterFragment";
	@Key(id = 501, desc = "装备碎片")
	public final static String equipFragment = "equipFragment";


	
	/////////////////模块//////////////////////
	
	@Key(id = 1000, desc = "战斗力")
	public final static String fightAbility = "fightAbility";	
	
	@Key(id = 1001, desc = "评分因子")
	public final static String scoreFactor = "scoreFactor";	



}
