package per.hao.cases;

import org.aeonbits.owner.ConfigFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import per.hao.InterfaceTester;
import per.hao.conf.ProductConfig;
import per.hao.conf.ServerConfig;

import java.util.Random;

public class TestClass extends InterfaceTester {

    @BeforeClass
    public void setUp(){
        login();
    }

    @Test
    public void GetDoClass() throws Exception{

        System.out.println(new Random().nextInt(2));
    }
}
