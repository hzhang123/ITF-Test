package per.hao.utils;

import com.jayway.jsonpath.JsonPath;
import org.aeonbits.owner.ConfigFactory;
import org.hashids.Hashids;
import per.hao.conf.ProjectConfig;

import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Functions {
    // ProjectConfig.properties映射对象
    private static ProjectConfig projectConfig = ConfigFactory.create(ProjectConfig.class);

    private static Hashids hashids = new Hashids(projectConfig.salt(), projectConfig.saltMinLength());

    private static Random ramdom = new Random();


    /**
     * 获取UUID
     *
     * @return String
     * */
    public static String __UUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * 获取区间[min, max)内的随机整数
     *
     * @param min 随机数最小值
     * @param max 随机数最大值
     *
     * @return int
     * */
    public static int __randomInt(int min, int max) {
        return ramdom.nextInt(max - min) + min;
    }

    /**
     * 从无符号（长整数）创建可解码的哈希值
     *
     * @param number 无符号（长整数）
     *
     * @return 无符号（长整数）的哈希值
     * */
    public static String __encodeHashId(long number) {
        return hashids.encode(number);
    }

    /**
     * 从哈希值字符中解析出代表的无符号长整数
     *
     * @param hash 哈希字符串
     *
     * @return 哈希值代表的无符号（长整数）
     * */
    public static long __decodeHashId(String hash) {
        long[] numbers = hashids.decode(hash);
        if (numbers.length > 0) {
            return numbers[0];
        } else {
            return -1;
        }
    }

    /**
     * 返回指定正则匹配输入字符串的数据
     *
     * @param input 输入字符串
     * @param regex 正则表达式
     * @param group 返回第几匹配组
     *
     * @return 返回匹配到的字符串，如果未匹配到默认返回null
     * */
    public static String __regularExtractor(String input, String regex, int group) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(group);
        } else {
            return null;
        }
    }

    /**
     * 指定json path获取json字符串中的指定值
     *
     * @param jsonStr json字符串
     * @param jsonPathExpressions json path
     *
     * @return String
     * */
    public static <T> T __jsonExtractor(String jsonStr, String jsonPathExpressions) {
        return JsonPath.read(jsonStr, jsonPathExpressions);
    }

}
