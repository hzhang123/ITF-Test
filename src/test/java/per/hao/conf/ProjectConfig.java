package per.hao.conf;

import org.aeonbits.owner.Config;

import java.io.PrintStream;


/**
 * 产品配置文件，从resources目录下ProjectConfig.properties中加载
 *
 * */
@Config.HotReload
@Config.Sources({ "classpath:conf/ProjectConfig.properties" })
public interface ProjectConfig extends Config {

    /*
    * hashId配置
    * */
    @Key("project.salt")
    @DefaultValue("+WX2bv6ctTAh0G")
    String salt();

    @Key("project.salt.min.length")
    @DefaultValue("8")
    int saltMinLength();

    /*
    * 代理配置
    * */
    @Key("project.proxy.server")
    @DefaultValue("false")
    boolean isOpenProxy();

    @Key("project.proxy.server.host")
    @DefaultValue("127.0.0.1")
    String proxyHost();

    @Key("project.proxy.server.port")
    @DefaultValue("8888")
    int proxyPort();

    @Key("project.proxy.server.username")
    @DefaultValue("")
    String proxyUserName();

    @Key("project.proxy.server.password")
    @DefaultValue("")
    String proxyPassWord();


    /**
     * 调试获取到的所有的变量，便于debug
     * example：
     *      ProjectConfig projectConfig = ConfigFactory.create(ProjectConfig.class);
     *      projectConfig.list(System.out);
     * */
    void list(PrintStream out);
}
