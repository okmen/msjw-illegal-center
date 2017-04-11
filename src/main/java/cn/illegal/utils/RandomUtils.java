package cn.illegal.utils;

public class RandomUtils {
	public static boolean isNeedExpire() {
		int rand = (int) (Math.random() * 10);
		return rand < 1;
	}
}
