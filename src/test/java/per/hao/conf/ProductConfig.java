package per.hao.conf;

import org.aeonbits.owner.Config;

import java.io.PrintStream;
import java.lang.reflect.Proxy;


/**
 * 产品配置文件，从resources目录下ProductConfig.properties中加载
 *
 * */
@Config.HotReload
@Config.Sources({ "classpath:conf/ProductConfig.properties" })
public interface ProductConfig extends Config {

    @Key("env")
    String env();

    @Key("product.${env}.uid")
    String uid();

    @Key("product.${env}.id")
    String id();

    @Key("product.${env}.public.key")
    String publicKey();

    @Key("product.${env}.private.key")
    String privateKey();


    /**
     * 调试获取到的所有的变量，便于debug
     * example：
     *      ProductConfig productConfig = ConfigFactory.create(ProductConfig.class);
     *      productConfig.list(System.out);
     * */
    void list(PrintStream out);
}
