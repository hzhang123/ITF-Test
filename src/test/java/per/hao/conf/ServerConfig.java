package per.hao.conf;

import org.aeonbits.owner.Config;

import java.io.PrintStream;
import java.net.URI;


/**
 * 环境服务配置文件，从resources目录下ServerConfig.properties中加载
 *
 * */
@Config.HotReload
@Config.Sources({ "classpath:conf/ServerConfig.properties" })
public interface ServerConfig extends Config {

    @Key("server.port")
    @DefaultValue("80")
    int port();

    @Key("server.main.hostname")
    String mainUrl(String path);

    @Key("server.account.hostname")
    String accountUrl(String path);

    @Key("server.marketing.automation.hostname")
    String marketingAutomationUrl(String path);

    @Key("account.username")
    String userName();

    @Key("account.password")
    String passWord();

    /**
     * 调试获取到的所有的变量，便于debug
     * example：
     *      ServerConfig serverConf = ConfigFactory.create(ServerConfig.class);
     *      serverConf.list(System.out);
     * */
    void list(PrintStream out);

}
