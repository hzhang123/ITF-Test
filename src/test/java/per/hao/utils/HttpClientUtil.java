package per.hao.utils;

import per.hao.listener.HttpSnapShot;
import per.hao.listener.TestFaildListener;
import per.hao.utils.builder.HCB;
import per.hao.utils.common.*;
import per.hao.utils.exception.HttpProcessException;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 使用HttpClient模拟发送（http/https）请求
 *
 * @version 1.0
 */
public class HttpClientUtil{
	
	//默认采用的http协议的HttpClient对象
	private static HttpClient client4HTTP;
	
	//默认采用的https协议的HttpClient对象
	private static HttpClient client4HTTPS;
	
	static{
		try {
			client4HTTP = HCB.custom().build();
			client4HTTPS = HCB.custom().ssl().build();
		} catch (HttpProcessException e) {
			Utils.errorException("创建https协议的HttpClient对象出错：{}", e);
		}
	}
	
	/**
	 * 判定是否开启连接池、及url是http还是https <br>
	 * 		如果已开启连接池，则自动调用build方法，从连接池中获取client对象<br>
	 * 		否则，直接返回相应的默认client对象<br>
	 * 
	 * @param config		请求参数配置
	 * @throws HttpProcessException	http处理异常
	 */
	private static void create(HttpConfig config) throws HttpProcessException  {
		if(config.getClient()==null){//如果为空，设为默认client对象
			if(config.setUrl().toLowerCase().startsWith("https://")){
				config.getClient(client4HTTPS);
			}else{
				config.getClient(client4HTTP);
			}
		}
	}
	
	//-----------华----丽----分----割----线--------------
	//-----------华----丽----分----割----线--------------
	//-----------华----丽----分----割----线--------------
	
	/**
	 * 以Get方式，请求资源或服务
	 * 
	 * @param client				client对象
	 * @param url					资源地址
	 * @param httpHeader			请求头信息
	 * @param context			http上下文，用于cookie操作
	 * @param encoding		编码
	 * @return						返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String get(HttpClient client, String url, HttpHeader httpHeader, HttpContext context, String encoding) throws HttpProcessException {
		return get(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.context(context)
				.encoding(encoding)
		);
	}
	/**
	 * 以Get方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return	返回结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static String get(HttpConfig config) throws HttpProcessException {
		return send(config.method(HttpMethods.GET));
	}

	/**
	 * 以Get方式，请求资源或服务
	 *
	 * @param config		请求参数配置
	 * @return	返回结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static HttpResponse doGet(HttpConfig config) throws HttpProcessException {
		return doSend(config.method(HttpMethods.GET));
	}

	/**
	 * 以Post方式，请求资源或服务
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param httpHeader		请求头信息
	 * @param parasMap		请求参数
	 * @param context		http上下文，用于cookie操作
	 * @param encoding		编码
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String post(HttpClient client, String url, HttpHeader httpHeader, Map<String,Object>parasMap, HttpContext context, String encoding) throws HttpProcessException {
		return post(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.getParams(parasMap)
				.context(context)
				.encoding(encoding));
	}
	/**
	 * 以Post方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static String post(HttpConfig config) throws HttpProcessException {
		return send(config.method(HttpMethods.POST));
	}

	/**
	 * 以Post方式，请求资源或服务
	 *
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 *
	 * */
	public static HttpResponse doPost(HttpConfig config) throws HttpProcessException {
		return doSend(config.method(HttpMethods.POST));
	}
	
	/**
	 * 以Put方式，请求资源或服务
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param parasMap		请求参数
	 * @param httpHeader		请求头信息
	 * @param context		http上下文，用于cookie操作
	 * @param encoding		编码
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String put(HttpClient client, String url, Map<String,Object>parasMap,
							 HttpHeader httpHeader, HttpContext context, String encoding) throws HttpProcessException {
		return put(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.getParams(parasMap)
				.context(context)
				.encoding(encoding)
		);
	}
	/**
	 * 以Put方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static String put(HttpConfig config) throws HttpProcessException {
		return send(config.method(HttpMethods.PUT));
	}
	
	/**
	 * 以Delete方式，请求资源或服务
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param httpHeader		请求头信息
	 * @param context		http上下文，用于cookie操作
	 * @param encoding		编码
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String delete(HttpClient client, String url, HttpHeader httpHeader,
								HttpContext context, String encoding) throws HttpProcessException {
		return delete(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.context(context)
				.encoding(encoding));
	}
	/**
	 * 以Delete方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static String delete(HttpConfig config) throws HttpProcessException {
		return send(config.method(HttpMethods.DELETE));
	}
	
	/**
	 * 以Patch方式，请求资源或服务
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param parasMap		请求参数
	 * @param httpHeader		请求头信息
	 * @param context		http上下文，用于cookie操作
	 * @param encoding		编码
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String patch(HttpClient client, String url, Map<String,Object>parasMap,
							   HttpHeader httpHeader, HttpContext context, String encoding) throws HttpProcessException {
		return patch(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.getParams(parasMap)
				.context(context)
				.encoding(encoding));
	}
	/**
	 * 以Patch方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static String patch(HttpConfig config) throws HttpProcessException {
		return send(config.method(HttpMethods.PATCH));
	}
	
	/**
	 * 以Head方式，请求资源或服务
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param httpHeader		请求头信息
	 * @param context		http上下文，用于cookie操作
	 * @param encoding		编码
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String head(HttpClient client, String url, HttpHeader httpHeader, HttpContext context, String encoding) throws HttpProcessException {
		return head(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.context(context)
				.encoding(encoding)
		);
	}
	/**
	 * 以Head方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static String head(HttpConfig config) throws HttpProcessException {
		return send(config.method(HttpMethods.HEAD));
	}
	
	/**
	 * 以Options方式，请求资源或服务
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param httpHeader		请求头信息
	 * @param context		http上下文，用于cookie操作
	 * @param encoding		编码
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String options(HttpClient client, String url, HttpHeader httpHeader, HttpContext context, String encoding) throws HttpProcessException {
		return options(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.context(context)
				.encoding(encoding)
		);
	}
	/**
	 * 以Options方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static String options(HttpConfig config) throws HttpProcessException {
		return send(config.method(HttpMethods.OPTIONS));
	}
	
	/**
	 * 以Trace方式，请求资源或服务
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param httpHeader		请求头信息
	 * @param context		http上下文，用于cookie操作
	 * @param encoding		编码
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String trace(HttpClient client, String url, HttpHeader httpHeader, HttpContext context, String encoding) throws HttpProcessException {
		return trace(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.context(context)
				.encoding(encoding)
		);
	}
	/**
	 * 以Trace方式，请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static String trace(HttpConfig config) throws HttpProcessException {
		return send(config.method(HttpMethods.TRACE));
	}
	
	/**
	 * 下载文件
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param httpHeader		请求头信息
	 * @param context		http上下文，用于cookie操作
	 * @param out			输出流
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static OutputStream down(HttpClient client, String url, HttpHeader httpHeader, HttpContext context, OutputStream out) throws HttpProcessException {
		return down(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.context(context)
				.out(out)
		);
	}
	
	/**
	 * 下载文件
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static OutputStream down(HttpConfig config) throws HttpProcessException {
		if(config.method() == null) {
			config.method(HttpMethods.GET);
		}
		return fmt2Stream(execute(config), config.out());
	}
	
	/**
	 * 上传文件
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param httpHeader		请求头信息
	 * @param context		http上下文，用于cookie操作
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String upload(HttpClient client, String url, HttpHeader httpHeader, HttpContext context) throws HttpProcessException {
		return upload(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.context(context)
		);
	}
	
	/**
	 * 上传文件
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static String upload(HttpConfig config) throws HttpProcessException {
		if(config.method() != HttpMethods.POST  && config.method() != HttpMethods.PUT){
			config.method(HttpMethods.POST);
		}
		return send(config);
	}
	
	/**
	 * 查看资源链接情况，返回状态码
	 * 
	 * @param client		client对象
	 * @param url			资源地址
	 * @param httpHeader		请求头信息
	 * @param context		http上下文，用于cookie操作
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static int status(HttpClient client, String url, HttpHeader httpHeader, HttpContext context, HttpMethods method) throws HttpProcessException {
		return status(HttpConfig.custom()
				.getClient(client)
				.setUrl(url)
				.setHttpHeader(httpHeader)
				.context(context)
				.method(method)
		);
	}
	
	/**
	 * 查看资源链接情况，返回状态码
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	public static int status(HttpConfig config) throws HttpProcessException {
		return fmt2Int(execute(config));
	}

	//-----------华----丽----分----割----线--------------
	//-----------华----丽----分----割----线--------------
	//-----------华----丽----分----割----线--------------
	
	/**
	 * 请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static String send(HttpConfig config) throws HttpProcessException {
		return fmt2String(execute(config), config.outenc());
	}

	/**
	 * 请求资源或服务
	 *
	 * @param config		请求参数配置
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static HttpResponse doSend(HttpConfig config) throws HttpProcessException {
		return execute(config);
	}
	
	/**
	 * 请求资源或服务，返回HttpResult对象
	 * 
	 * @param config		请求参数配置
	 * @return				返回HttpResult处理结果
	 * @throws HttpProcessException	http处理异常
	 */
	public static HttpResult sendAndGetResp(HttpConfig config) throws HttpProcessException {
		Header[] reqHeaders = config.setHttpHeader();
		//执行结果
		HttpResponse resp =  execute(config);
		
		HttpResult result = new HttpResult(resp);
		result.setResult(fmt2String(resp, config.outenc()));
		result.setReqHeaders(reqHeaders);
		
		return result;
	}
	
	/**
	 * 请求资源或服务
	 * 
	 * @param config		请求参数配置
	 * @return				返回HttpResponse对象
	 * @throws HttpProcessException	http处理异常 
	 */
	private static HttpResponse execute(HttpConfig config) throws HttpProcessException {
        //获取链接
	    create(config);
		HttpResponse resp = null;
        // 监听信息
        HttpSnapShot httpSnapShot = new HttpSnapShot();
        TestFaildListener.httpSnapShot = httpSnapShot;

		try {
			//创建请求对象
			HttpRequestBase request = getRequest(config.setUrl(), config.method());
			//设置超时
			request.setConfig(config.requestConfig());
			//设置header信息
			request.setHeaders(config.setHttpHeader());
			// 请求失败头监听
            httpSnapShot.setRequestHeaders(config.setHttpHeader());

			//判断是否支持设置entity(仅HttpPost、HttpPut、HttpPatch支持)
			if(HttpEntityEnclosingRequestBase.class.isAssignableFrom(request.getClass())){
				List<NameValuePair> nvps = new ArrayList<>();
				
				if(request.getClass()== HttpGet.class) {
					//检测url中是否存在参数
					//注：只有get请求，才自动截取url中的参数，post等其他方式，不再截取
//					config.setUrl(Utils.checkHasParas(config.setUrl(), nvps, config.inenc()));
					config.setUrl(Utils.checkHasParas(config.setUrl(), nvps, config));
				}
				
				//装填参数
//				HttpEntity entity = Utils.map2HttpEntity(nvps, config.getParams(), config.inenc());
				HttpEntity entity = Utils.map2HttpEntity(nvps, config);

				//设置参数到请求对象中
				((HttpEntityEnclosingRequestBase)request).setEntity(entity);
				
				Utils.info("请求地址："+config.setUrl());
				httpSnapShot.setRequestUrl(config.setUrl());

				if(nvps.size()>0){
					Utils.info("请求参数："+nvps.toString());
                    httpSnapShot.setRequestBody(nvps.toString());
				}
				if(config.setBody()!=null){
					Utils.info("请求参数："+ config.setBody());
                    httpSnapShot.setRequestBody(config.setBody());
				}
			}else{
				int idx = config.setUrl().indexOf("?");

				String url = config.setUrl().substring(0, (idx>0 ? idx : config.setUrl().length()));
				Utils.info("请求地址：" + url);
                httpSnapShot.setRequestUrl(url);

				if(idx>0){
					Utils.info("请求参数："+config.setUrl().substring(idx+1));
                    httpSnapShot.setRequestBody(config.setUrl().substring(idx+1));
				}
			}
			//执行请求操作，并拿到结果（同步阻塞）
			resp = (config.context() == null) ? config.getClient().execute(request) : config.getClient().execute(request, config.context()) ;

			if(config.isReturnRespHeaders()){
				//获取所有response的header信息
				config.setHttpHeader(resp.getAllHeaders());
			}

            //获取结果实体
			return resp;
			
		} catch (IOException e) {
			throw new HttpProcessException(e);
		}
	}
	
	//-----------华----丽----分----割----线--------------
	//-----------华----丽----分----割----线--------------
	//-----------华----丽----分----割----线--------------

    /**
     * 转化为字符串
     *
     * @param resp			响应对象
     * @param encoding		编码
     * @return				返回处理结果
     * @throws HttpProcessException	http处理异常
     */
    private static String fmt2StringNotClose(HttpResponse resp, String encoding) throws HttpProcessException {
        String body = "";
        try {
            if (resp.getEntity() != null) {
                // 按指定编码转换结果实体为String类型
                body = EntityUtils.toString(resp.getEntity(), encoding);
                Utils.info(body);
            }else{//有可能是head请求
                body =resp.getStatusLine().toString();
            }
            EntityUtils.consume(resp.getEntity());
        } catch (IOException e) {
            throw new HttpProcessException(e);
        }
        return body;
    }

	/**
	 * 转化为字符串
	 * 
	 * @param resp			响应对象
	 * @param encoding		编码
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	private static String fmt2String(HttpResponse resp, String encoding) throws HttpProcessException {
		String body = "";
		try {
			if (resp.getEntity() != null) {
				// 按指定编码转换结果实体为String类型
				body = EntityUtils.toString(resp.getEntity(), encoding);
				Utils.info(body);
			}else{//有可能是head请求
				body =resp.getStatusLine().toString();
			}
			EntityUtils.consume(resp.getEntity());
		} catch (IOException e) {
			throw new HttpProcessException(e);
		}finally{			
			close(resp);
		}
		return body;
	}
	
	/**
	 * 转化为数字
	 * 
	 * @param resp			响应对象
	 * @return				返回处理结果
	 * @throws HttpProcessException	http处理异常 
	 */
	private static int fmt2Int(HttpResponse resp) throws HttpProcessException {
		int statusCode;
		try {
			statusCode = resp.getStatusLine().getStatusCode();
			EntityUtils.consume(resp.getEntity());
		} catch (IOException e) {
			throw new HttpProcessException(e);
		}finally{			
			close(resp);
		}
		return statusCode;
	}
	
	/**
	 * 转化为流
	 * 
	 * @param resp			响应对象
	 * @param out			输出流
	 * @return				返回输出流
	 * @throws HttpProcessException	http处理异常 
	 */
	public static OutputStream fmt2Stream(HttpResponse resp, OutputStream out) throws HttpProcessException {
		try {
			resp.getEntity().writeTo(out);
			EntityUtils.consume(resp.getEntity());
		} catch (IOException e) {
			throw new HttpProcessException(e);
		}finally{
			close(resp);
		}
		return out;
	}
	
	/**
	 * 根据请求方法名，获取request对象
	 * 
	 * @param url			资源地址
	 * @param method		请求方式
	 * @return				返回Http处理request基类
	 */
	private static HttpRequestBase getRequest(String url, HttpMethods method) {
		HttpRequestBase request = null;
		switch (method.getCode()) {
			case 0:// HttpGet
				request = new HttpGet(url);
				break;
			case 1:// HttpPost
				request = new HttpPost(url);
				break;
			case 2:// HttpHead
				request = new HttpHead(url);
				break;
			case 3:// HttpPut
				request = new HttpPut(url);
				break;
			case 4:// HttpDelete
				request = new HttpDelete(url);
				break;
			case 5:// HttpTrace
				request = new HttpTrace(url);
				break;
			case 6:// HttpPatch
				request = new HttpPatch(url);
				break;
			case 7:// HttpOptions
				request = new HttpOptions(url);
				break;
			default:
				request = new HttpPost(url);
				break;
		}
		return request;
	}
	
	/**
	 * 尝试关闭response
	 * 
	 * @param resp				HttpResponse对象
	 */
	private static void close(HttpResponse resp) {
		try {
			if(resp == null) return;
			//如果CloseableHttpResponse 是resp的父类，则支持关闭
			if(CloseableHttpResponse.class.isAssignableFrom(resp.getClass())){
				((CloseableHttpResponse)resp).close();
			}
		} catch (IOException e) {
			Utils.exception(e);
		}
	}
}