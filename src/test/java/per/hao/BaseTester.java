package per.hao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import per.hao.annotations.DataSource;
import per.hao.annotations.DataSourceType;
import per.hao.utils.*;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;


public class BaseTester<T> {

    public static final Logger log = LoggerFactory.getLogger(BaseTester.class);

    /**
     * 判断s1是否包含s2
     *
     * @param s1 文本
     * @param s2 文本
     */
    protected void assertTextContain(String s1, String s2) {
        Assert.assertTrue(s1.contains(s2),
                s1 + " [not contain] " + s2);
    }

    /**
     * 判断expected是否等于actual
     *
     * @param expected T expected
     * @param actural  T actural
     */
    public void assertTextEqual(T expected, T actural) {
        Assert.assertEquals(expected, actural);
    }

    /**
     * 判断是否为true
     *
     * @param b 参数b
     */
    public void assertTrue(boolean b) {
        Assert.assertTrue(b);
    }



    /**
     * 数据提供公共接口
     */
    @DataProvider(name = "getData")
    public static Iterator<Object[]> getData(Method method) {

        DataSource dataSource = null;

        /** 数据源注解存在判断 */
        if (method.isAnnotationPresent(DataSource.class)) {
            dataSource = method.getAnnotation(DataSource.class);
        } else {
            log.error("未指定@DataSource注解却初始化了dataProvider");
        }

        /** 根据数据源类型返回对应数据迭代器 */
        if (DataSourceType.CSV
                .equals(dataSource.dataSourceType())) {

            // CSVReader

        } else if (DataSourceType.POSTGRESQL
                .equals(dataSource.dataSourceType())) {

            // PostgresqlReader

        }

        /* 默认读取excel */
        // 根据名称
        if (!"".equals(dataSource.name())) {

            return ExcelReader.getDataByName(
                    dealFilePath(dataSource.filePath()), dataSource.name());
            // 根据锚点
        } else if (!"".equals(dataSource.locate())) {

            return ExcelReader.getDataByLocate(
                    dealFilePath(dataSource.filePath()), dataSource.sheetName(), dataSource.locate());
            // 读取整个sheet页
        } else {

            return ExcelReader.getDataBySheetName(
                    dealFilePath(dataSource.filePath()), dataSource.sheetName());

        }
    }

    /**
     * 如果只存在文件名，则拼接默认读取目录，否则使用指定的路径
     *
     * @param filePath 文件路径
     */
    private static String dealFilePath(String filePath) {
        if (!filePath.matches(".*[/\\\\].*")) {
            filePath = "src/test/resources/data/" + filePath;
        }

        return new File(filePath.replaceAll("[/\\\\]+",
                Matcher.quoteReplacement(File.separator))).getAbsolutePath();
    }
}
