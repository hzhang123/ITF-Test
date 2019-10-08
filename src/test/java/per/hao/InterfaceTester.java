package per.hao;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import per.hao.conf.ProductConfig;
import per.hao.conf.ProjectConfig;
import per.hao.conf.ServerConfig;
import per.hao.utils.HttpClientUtil;
import per.hao.utils.builder.HCB;
import per.hao.utils.common.HttpConfig;
import per.hao.utils.common.HttpHeader;
import per.hao.utils.common.Utils;
import per.hao.utils.exception.HttpProcessException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static per.hao.utils.Functions.__jsonExtractor;
import static per.hao.utils.Functions.__regularExtractor;

public class InterfaceTester extends BaseTester {
    // log
    protected static final Logger log = LoggerFactory.getLogger(InterfaceTester.class);
    // HttpClient
    public static HttpClient defaultClient;
    // CookieStore
    public static CookieStore cookieStore;
//    protected static Header[] defaultHeaders;
    // Header
    protected static HttpHeader defaultHttpHeader;

    // 配置文件映射对象
    protected ServerConfig serverConfig = ConfigFactory.create(ServerConfig.class);
    protected ProductConfig productConfig = ConfigFactory.create(ProductConfig.class);
    private static ProjectConfig projectConfig = ConfigFactory.create(ProjectConfig.class);

    static {

        // 自定义默认cookieStore
        cookieStore = new BasicCookieStore();

        // HttpClient配置
        HCB hcb = HCB.custom();
        hcb.setDefaultCookieStore(cookieStore);
        if (projectConfig.isOpenProxy()) {
            hcb.setProxy(
                    new HttpHost(projectConfig.proxyHost(), projectConfig.proxyPort()));
        }
        defaultClient = hcb.build();

        // HttpHeader 配置
        defaultHttpHeader = HttpHeader.custom();

//        defaultHeaders = HttpHeader.custom()
//                .contentType("application/json")
//                .build();

    }


    /**
     * GrowingIO API认证
     * 接口自动读取
     * */
    protected void accessCode() {
        Long epoch = System.currentTimeMillis();
        String authCode = authToken(productConfig.privateKey(), productConfig.uid(), productConfig.id(), epoch);
        HttpConfig config;
        StringBuilder body;
        String apiToken = null;

        defaultHttpHeader.put("X-Client-Id", productConfig.publicKey());
        defaultHttpHeader.contentType(HttpHeader.HttpHeaderValue.TEXT_PLAIN);

        body = new StringBuilder();
        body.append("project=");
        body.append(productConfig.uid());

        body.append("&ai=");
        body.append(productConfig.id());

        body.append("&tm=");
        body.append(epoch);

        body.append("&auth=");
        body.append(authCode);

        config = HttpConfig.custom()
                .getClient(defaultClient)
                .setHttpHeader(defaultHttpHeader)
                .setUrl(serverConfig.mainUrl("/auth/token"))
                .setBody(body.toString());

        try {
            apiToken = __jsonExtractor(HttpClientUtil.post(config), "$.code");
        } catch (HttpProcessException e) {
            log.error("accessCode failed", e);
        }

        defaultHttpHeader.put("Authorization", apiToken);

    }

    /**
     * 通过认证算法计算出签名
     *
     * @param secret 这里是 GrowingIO 给项目分配的私钥
     * @param project 项目UID
     * @param ai 项目ID
     * @param epoch 当前请求时间戳（unix 毫秒时间戳)
     *
     * @return String auth代码
     * */
    private String authToken(String secret, String project, String ai, Long epoch){
        String message = "POST\n/auth/token\nproject="+project+"&ai="+ai+"&tm="+epoch;
        byte[] signature = null;
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256"));
            signature = hmac.doFinal(message.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            log.error("authToken Error1", e);
        } catch (InvalidKeyException e) {
            log.error("authToken Error2", e);
        } catch (UnsupportedEncodingException e) {
            log.error("authToken Error3", e);
        }
        return Hex.encodeHexString(signature);
    }

    /**
     * 指定模板文件自动填充数据
     *
     * @param jsonTemplate 模板字符串
     * @param data Map参数，key为模板文件中的待替换变量，value为替换后变量
     *
     * @return String
     * */
    protected String jsonReplace(String jsonTemplate, Map<String, Object> data) {
        Template template;
        StringWriter result = new StringWriter();
        try {
            template = new Template(
                    "jsonReplace",
                    new StringReader(jsonTemplate),
                    new Configuration(new Version("2.3.28")));

            result = new StringWriter();
            template.process(data, result);

        } catch (TemplateException e) {
            log.error("模板错误", e);
        } catch (IOException e) {
            log.error("读取或者输入错误", e);
        }
        return result.toString();
    }

    /**
     * 计算encrypted
     *
     * @param responseBody /login请求返回的相应体
     * @return String
     */
    private String getEncrypted(String responseBody) {
        // 获取tk ts
        String tk = __regularExtractor(responseBody, "name=\"giotk\".*?content=\"(.*?)\"", 1);
        int ts = Integer.parseInt(__regularExtractor(responseBody, "name=\"giots\".*?content=\"(.*?)\"", 1));
        // 计算encrypted
        StringBuffer encrypted = new StringBuffer();
        char[] tkChars = tk.toCharArray();
        for (int i = 0; i < tkChars.length; i++) {
            encrypted.append((char) ((((int) tkChars[i] - 32 ^ 31 & i) + 95 - ts) % 95 + 32));
        }

        return encrypted.toString();
    }

    /**
     * 根据ServerConfig.properties中指定的用户名密码登录系统
     * 获取cookie信息保存到defaultClient
     */
    protected void login() {
        try {
            Utils.info("Login To:" + serverConfig.mainUrl(""));
            Utils.info("登录用户名：" + serverConfig.userName());
            Utils.info("登录密码：" + serverConfig.passWord());
            login(serverConfig.userName(), serverConfig.passWord());
        } catch (Exception e) {
            log.error("登录失败", e);
        }
    }

    /**
     * 根据用户名密码登录系统
     *
     * @param userName 用户名
     * @param passWord 密码
     */
    protected void login(String userName, String passWord) throws Exception {
        Map<String, Object> params;
        HttpConfig config;
        HttpResponse response;
        String responseBody;

        /* post: /login获取 token encrypted */
        params = new HashMap<>();
        params.put("accountName", userName);
        params.put("password", passWord);
        defaultHttpHeader.contentType(HttpHeader.HttpHeaderValue.TEXT_PLAIN);
        config = HttpConfig.custom()
                .getClient(defaultClient)
                .setHttpHeader(defaultHttpHeader)
                .setUrl(serverConfig.accountUrl("/login"))
                .getParams(params);
        responseBody = HttpClientUtil.post(config);

        String tk = __regularExtractor(responseBody, "name=\"giotk\".*?content=\"(.*?)\"", 1);
        if (tk == null) {
            Assert.assertEquals(tk, null, "登录失败");
        }
        String encrypted = getEncrypted(responseBody);

        /* post: /login 获取 oauthUrl and Cookie gioat */
        params.put("token", tk);
        params.put("encrypted", encrypted);
        defaultHttpHeader.contentType(HttpHeader.HttpHeaderValue.TEXT_PLAIN);
        config = HttpConfig.custom()
                .getClient(defaultClient)
                .setHttpHeader(defaultHttpHeader)
                .setUrl(serverConfig.accountUrl("/login"))
                .isRedirect(false)
                .getParams(params);
        response = HttpClientUtil.doPost(config);

        Assert.assertEquals(303, response.getStatusLine().getStatusCode(), "登录失败，用户名或密码不正确");

        String oauthUrl = serverConfig.accountUrl("") + response.getFirstHeader("Location").getValue();
        close(response);

        /* get: oauthUrl 获取 callbackUrl */
        config = HttpConfig.custom()
                .getClient(defaultClient)
                .setUrl(oauthUrl)
                .isRedirect(false);
        response = HttpClientUtil.doGet(config);

        String callbackUrl = serverConfig.mainUrl("") +
                __regularExtractor(
                        response.getFirstHeader("Location").getValue(),
                        ".*growingio\\.com(.*)", 1);
        close(response);

        /* get: callbackUrl 获取 Cookie user_id user_session */
        config = HttpConfig.custom()
                .getClient(defaultClient)
                .setUrl(callbackUrl)
                .isRedirect(false);
        response = HttpClientUtil.doGet(config);


        Assert.assertEquals(303, response.getStatusLine().getStatusCode(), "登录失败");
        close(response);

        Utils.info("Get Login Cookies:");
        cookieStore.getCookies().stream().forEach(cookie -> {
            Utils.info(cookie.toString());
        });

    }


    /**
     * 尝试关闭response
     *
     * @param response	HttpResponse对象
     */
    protected static void close(HttpResponse response) {
        try {
            if(response == null) return;
            //如果CloseableHttpResponse 是resp的父类，则支持关闭
            if(CloseableHttpResponse.class.isAssignableFrom(response.getClass())){
                ((CloseableHttpResponse)response).close();
            }
        } catch (IOException e) {
            Utils.exception(e);
        }
    }

    @AfterSuite
    protected void tearDown() {
        if (defaultClient != null) {
            try {
                ((CloseableHttpClient) defaultClient).close();
                log.info("HttpClient关闭...");
            } catch (IOException e) {
                log.error("关闭HttpClient失败", e);
            }
        }

    }

    @AfterClass
    public void afterClass() {
        defaultHttpHeader = HttpHeader.custom();
    }
}
