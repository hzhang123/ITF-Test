package per.hao.listener;

import io.qameta.allure.Attachment;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import per.hao.utils.common.Utils;

import java.io.IOException;
import java.util.Arrays;

public class TestFaildListener extends TestListenerAdapter {
    public static HttpSnapShot httpSnapShot = null;

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        snapShot();
    }

    /**
     * 截取相应体作为附件
     *
     * @return String 附件
     * */
    @Attachment(value = "Response snapShot", type = "text/plain")
    public String snapShot() {
        if (httpSnapShot != null) {
            return httpSnapShot.toString();
        }
        return "未捕获到请求快照，请根据日志排查";
    }

}
