package x7.core.util;

import java.util.Random;

public class MathExt {

	private final static Random random = new Random();

	/**
	 * 
	 * @param i
	 * @param offset
	 * @return i << offset same as i * (2^offset)
	 */
	public static int moveLeft(int i, int offset) {
		return i << offset;
	}

	/**
	 * 
	 * @param i
	 *            0< result < i
	 * @return 0<result < i
	 */
	public static int random(int i) {
		return random.nextInt(i);
	}

	/**
	 * 
	 * @param min
	 *            <= result
	 * @param result
	 *            < i
	 * @return min<= result < i
	 */
	public static int random(int min, int i) {
		return min + random.nextInt(i - min);
	}

	/**
	 * 根据权重获得随机索引
	 * 
	 * @param weights
	 * @return weights [] index, -1表示没有(权重总和为0则表示没有)
	 */
	public static int randomByWeights(int[] weights) {
		int length = weights.length;
		int totalWeight = 0;
		for (int i = 0; i < length; i++) {
			totalWeight += weights[i];
		}
		
		if (totalWeight == 0)
			return -1;
		
		int hit = random(totalWeight);

		totalWeight = 0;
		for (int i = 0; i < length; i++) {
			totalWeight += weights[i];
			if (totalWeight >= hit)
				return i;
		}

		return 0;
	}

	/**
	 * 
	 * @param rate
	 *            like, 0.2×100 -> 20
	 * @return
	 */
	public static boolean isHitByRate(int rate) {
		if (rate < 1)
			return false;
		if (rate >= 100)
			return true;
		return rate >= Math.random() * 100 + 1;
	}

	/**
	 * 
	 * @param rate
	 *            like,20% 0.2
	 * @return
	 */
	public static boolean isHitByRate(double rate) {
		if (rate <= 0)
			return false;
		if (rate > 1)
			return true;
		return rate >= Math.random();
	}

	/**
	 * 
	 * @param low
	 * @param high
	 * @param i
	 * @return 不小于L,不大于H
	 */
	public static int LH(int low, int high, int i) {
		if (i < low)
			return low;
		if (i > high)
			return high;
		return i;
	}

	/**
	 * 
	 * @param low
	 * @param high
	 * @param i
	 * @return 不小于L,不大于H
	 */
	public static double LH(double low, double high, double i) {
		if (i < low)
			return low;
		if (i > high)
			return high;
		return i;
	}

	/**
	 * 
	 * @param oldValue
	 *            原始值
	 * @param newValue
	 *            新值
	 * @param replacedTimes
	 *            替换次数
	 * @return
	 */
	public static int sumReplace(int oldValue, int newValue, int replacedTimes) {
		return (newValue - oldValue) * replacedTimes;
	}

	/**
	 * 等级区间定位<br>
	 * 不在区间内，返回-1
	 * 
	 * @param level
	 * @param levelSection
	 * @return
	 */
	public static int fixLevel(int level, int[] levelSections) {
		if (levelSections == null)
			return -1;
		int length = levelSections.length;
		for (int i = length - 1; i >= 0; i--) {
			if (level >= levelSections[i])
				return levelSections[i];
		}
		return -1;
	}
	
	/**
	 * n / dv , 如果小数部分 >= factor，则 + 1
	 * @param n
	 * @param dv
	 * @param factor
	 * @return
	 */
	public static int dividePlusOneOnFactor(double n, double dv, double factor){
		double t = n / dv;
		int r = (int) t;
		if (t - r >= factor)
			return r + 1;
		return r;
	}

}
