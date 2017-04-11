package cn.illegal.config;

public class IConfig {
	public static final String[] Nickname_Head_List = {"漂亮的","温柔的","可爱的","美丽的","逗比的","腹黑的",
        "傲娇的","吝啬的","乖巧的","高大的","苗条的","开心的","英俊的","潇洒的","玉树临风的","奇帅无比的",
        "倾国倾城的","羞答答的","舔瓶盖的","沉鱼落雁的","闭月羞花的","臭美的","风流倜傥的","帅到爆的",
        "流鼻血的","激情的","光芒万丈的","天天数钱的","爱看春晚的","上过春晚的","脑洞最大的",
        "身材最好的","十块腹肌的","一块腹肌的","忧郁的"};
	public static final String[] Nickname_End_List = {"汉子","妹子","女汉子","女神","大白","皮卡丘","奥特曼",
		"咸蛋超人","钢铁侠","阿童木","直升机","软妹子","苹果7","挪鸡鸭","火腿肠","唐老鸭","米老鼠","加菲猫",
		"时光鸡","流氓兔","卡卡西","维尼","海尔兄弟","葫芦娃","隔壁老王","大脸猫","蓝皮鼠","黑猫警长","史努比",
		"匹诺曹","流川枫","令狐冲","岳不群","蝙蝠侠","根号2","007","外星人","喵星人"};
	
	public static final String USER_ACCOUNT_ACCESS_TOKEN_REDIS = "USER_ACCOUNT_ACCESS_TOKEN_REDIS";
    //refreshToken默认前缀
    public static final String USER_ACCOUNT_REFRESH_TOKEN_REDIS = "USER_ACCOUNT_REFRESH_TOKEN_REDIS";
    
    public static final String ENCYPT_ACCESS_TOKEN_REDIS_KEY = "USER_ACCOUNT" + "E_A_T_%s";
    
    //accessTokenKey
    public static final String ACCESS_TOKEN_KEY = "choumeiAccessTokenAT357";
    //refreshTokenKey
    public static final String REFRESH_TOKEN_KEY = "choumeiRefreshTokenRT246";
    
    //refresh失效
    public static final String ERR_REFRESH_TOKEN_INVALIDATE = "10614";
}
