package per.hao.listener;

import org.apache.http.Header;
import per.hao.InterfaceTester;

import java.util.Arrays;

public class HttpSnapShot {
    private Header[] requestHeaders;
    private String requestUrl;
    private String requestBody;
    private String cookies;

//    private String responseHeaders;
//    private String responseBody;

    @Override
    public String toString() {
        return  "\n--------Request URL--------:\n" +
                requestUrl + "\n" +
                "\n--------Request Header--------:\n" +
                Arrays.toString(requestHeaders) + "\n" +
                "\n--------Request Body--------:\n" +
                requestBody + "\n" +
                "\n--------Cookies--------:\n" +
                InterfaceTester.cookieStore.getCookies().toString();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public Header[] getRequestHeaders() {
        return requestHeaders;
    }

    public void setRequestHeaders(Header[] requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

//    public String getResponseHeaders() {
//        return responseHeaders;
//    }
//
//    public void setResponseHeaders(String responseHeaders) {
//        this.responseHeaders = responseHeaders;
//    }
//
//    public String getResponseBody() {
//        return responseBody;
//    }
//
//    public void setResponseBody(String responseBody) {
//        this.responseBody = responseBody;
//    }

}
