package per.hao.cases.marketing.automation;

import io.qameta.allure.*;
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
import static per.hao.utils.Functions.__UUID;
import static per.hao.utils.Functions.__jsonExtractor;

@Epic("marketing-automation")
@Feature("消息组创建接口")
public class CampaignsTest extends InterfaceTester {
    @BeforeClass
    public void setUp() {
        login();
    }

    @Test(description = "创建消息组")
    @Description("指定一个产品ID，创建资源位")
    @Severity(SeverityLevel.CRITICAL)
    @DataSource(filePath = "growingio.xlsx", name = "CampaignsTest.testCampaignsCreate")
    @Step("创建消息组")
    public void testCampaignsCreate(Map<String, String> row) throws Exception{

        Map<String, Object> params = new HashMap<>(row);
        params.put("uid", productConfig.uid());
        params.put("random", __UUID());

        step("参数准备");
        defaultHttpHeader.contentType(HttpHeader.HttpHeaderValue.APPLICATION_JSON);
        String url = serverConfig.marketingAutomationUrl(jsonReplace(row.get("path"), params));
        String body = jsonReplace(row.get("参数模板"), params);

        HttpConfig config = HttpConfig.custom()
                .getClient(defaultClient)
                .setHttpHeader(defaultHttpHeader)
                .setUrl(url)
                .setBody(body);

        step("发送请求");
        String reponseBody = HttpClientUtil.post(config);

        step("验证结果");
        assertTextContain(reponseBody, __jsonExtractor(body, "$.name"));
        assertTextContain(reponseBody, __jsonExtractor(body, "$.type"));
//        assertTextContain(reponseBody, __jsonExtractor(body, "$.productIds"));
        assertTextContain(reponseBody, __jsonExtractor(body, "$.state"));
    }
}
