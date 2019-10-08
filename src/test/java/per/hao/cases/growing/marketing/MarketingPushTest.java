package per.hao.cases.growing.marketing;

import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import per.hao.InterfaceTester;
import per.hao.annotations.DataSource;
import per.hao.utils.HttpClientUtil;
import per.hao.utils.common.HttpConfig;
import per.hao.utils.common.HttpHeader;

import java.util.HashMap;
import java.util.Map;

import static io.qameta.allure.Allure.step;
import static per.hao.utils.Functions.*;

/**
 *
 * */
@Epic("growing-marketing")
@Feature("G-Push接口")
public class MarketingPushTest extends InterfaceTester {

    @BeforeClass
    public void setUp() {
        // GrowingIO API认证
        accessCode();
    }

    @Test(description = "G-Push推送接口")
    @Description("指定所有推送类型各推送一次，任务正常下发则认为调用接口成功")
    @Severity(SeverityLevel.CRITICAL)
    @DataSource(filePath = "growingio.xlsx", name = "MarketingPushTest.testMarketingPush")
    @Link("https://shimo.im/docs/rvKhdc3wDkhxRt8Y")
    public void testMarketingPush(Map<String, String> row) throws Exception{

        step("发送数据配置");
        Map<String, Object> params = new HashMap<>(row);

        String cid = __UUID();
        params.put("cid", cid);
        params.put("random", __randomInt(100, 1000));
        params.put("uid", productConfig.uid());

        String url = serverConfig.mainUrl(jsonReplace(row.get("path"), params));
        defaultHttpHeader.contentType(HttpHeader.HttpHeaderValue.APPLICATION_JSON);
        String body = jsonReplace(row.get("参数模板"), params);

        HttpConfig config = HttpConfig.custom()
                .getClient(defaultClient)
                .setHttpHeader(defaultHttpHeader)
                .setUrl(url)
                .setBody(body);

        step(row.get("用例编号") + row.get("用例名称"));
        String responseBody = HttpClientUtil.post(config);

        step("测试结果对比");
        Assert.assertEquals(
                __jsonExtractor(responseBody, "$.cid"),
                cid);
    }
}
