package com.att;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.att.https.SSLSocketFactoryImp;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class HttpApp
{

	/*
     * 判断wifi/lan连接状态
     *
     * @param
     * @return
     */
	public String postSendAndReceive(String url, List<NameValuePair> params)
	{
		// HttpPost连接对象
		HttpPost httpRequest=new HttpPost(url);

		try
		{
			//设置字符集
			HttpEntity httpentity=new UrlEncodedFormEntity(params,"gb2312");

			//请求httpRequest
			httpRequest.setEntity(httpentity);

			HttpParams httpParameters = new BasicHttpParams();
			//设置请求超时
			HttpConnectionParams.setConnectionTimeout(httpParameters,30000);
			//设置响应超时
			HttpConnectionParams.setSoTimeout(httpParameters, 300000);
			//缓冲
			//HttpConnectionParams.setSocketBufferSize(httpParameters, 10*1024);
			//取得HttpClient对象
			HttpClient httpclient=new DefaultHttpClient(httpParameters);

			//请求HttpCLient，取得HttpResponse
			HttpResponse httpResponse=httpclient.execute(httpRequest);

			//请求成功
			if ( httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK )
			{
				//取得返回的字符串
				String strResult=EntityUtils.toString(httpResponse.getEntity());
				return strResult;
			}
			else
			{
				Log.v("TPATT", "POST:请求错误！");
				return null;
			}
		}
		catch(UnsupportedEncodingException e)
		{
			Log.v("TPATT", "POST:UnsupportedEncodingException");
			SwingCardAttActivity.isnet=false;
			return null;
		}
		catch(IOException e)
		{
			Log.v("TPATT", "POST:IOException: " + e.toString());
			SwingCardAttActivity.isnet=false;
			return null;
		}
		catch(NetworkOnMainThreadException e)
		{
			Log.v("TPATT", "POST:NetworkOnMainThreadException");
			SwingCardAttActivity.isnet=false;
			return null;
		}
		catch(Exception e)
		{
			Log.v("TPATT", "POST:Error: " + e.toString());
			SwingCardAttActivity.isnet=false;
			return null;
		}
	}

	/*
    * HTTP GET
    *
    * @param
    * @return
    */
	public String getSendAndReceive(String url)
	{
		try
		{
			//第一步，创建HttpGet对象
			HttpGet httpGet = new HttpGet(url);

			HttpParams httpParameters = new BasicHttpParams();
			//设置请求超时
			HttpConnectionParams.setConnectionTimeout(httpParameters,10000);
			//设置响应超时
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);

			//第二步，使用execute方法发送HTTP GET请求，并返回HttpResponse对象
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);

			if (httpResponse.getStatusLine().getStatusCode() == 200)
			{
				//第三步，使用getEntity方法活得返回结果
				String result;

				result = EntityUtils.toString(httpResponse.getEntity());

				return result;
			}
			else
			{
				Log.v("TPATT", "GET:请求错误！");
				return null;
			}
		}
		catch(UnsupportedEncodingException e)
		{
			Log.v("TPATT", "GET:UnsupportedEncodingException");
			return null;
		}
		catch(IOException e)
		{
			Log.v("TPATT", "GET:IOException");
			return null;
		}
		catch(NetworkOnMainThreadException e)
		{
			Log.v("TPATT", "GET:NetworkOnMainThreadException");
			return null;
		}
		catch(Exception e)
		{
			Log.v("TPATT", "GET:Error: " + e.toString());
			return null;
		}
	}

	/*
	  * 获取网络图片
	  *
	  * @param urlString
	  *            如：http://f.hiphotos.baidu.com/image/w%3D2048/sign=3
	  *            b06d28fc91349547e1eef6462769358
	  *            /d000baa1cd11728b22c9e62ccafcc3cec2fd2cd3.jpg
	  * @return
	  * @date 2014.05.10
	  */
	public Bitmap getNetWorkBitmap(String urlString)
	{
		URL imgUrl = null;
		Bitmap bitmap = null;

		try
		{
			imgUrl = new URL(urlString);

			// 使用HttpURLConnection打开连接
			HttpURLConnection urlConn = (HttpURLConnection) imgUrl.openConnection();
			urlConn.setConnectTimeout(20000);
			urlConn.setReadTimeout(20000);
			urlConn.setDoInput(true);
			urlConn.connect();

			// 将得到的数据转化成InputStream
			InputStream is = urlConn.getInputStream();

			// 将InputStream转换成Bitmap
			bitmap = BitmapFactory.decodeStream(is);
			is.close();
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			Log.v("TPATT","下载图片出错 MalformedURLException " + e.toString());
			bitmap = null;
		}
		catch (IOException e)
		{
			Log.v("TPATT","下载图片出错 IOException " + e.toString());
			bitmap = null;
		}

		return bitmap;
	}


	/**
	 * HttpUrlConnection 方式，支持指定load-der.crt证书验证，此种方式Android官方建议
	 *
	 * @throws CertificateException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void initSSL(Context context1) throws CertificateException, IOException, KeyStoreException,
			NoSuchAlgorithmException, KeyManagementException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream in = context1.getAssets().open("load-der.crt");
		java.security.cert.Certificate ca = cf.generateCertificate(in);

		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(null, null);
		keystore.setCertificateEntry("ca", ca);

		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keystore);

		// Create an SSLContext that uses our TrustManager
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, tmf.getTrustManagers(), null);
		URL url = new URL("https://certs.cac.washington.edu/CAtest/");
		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		urlConnection.setSSLSocketFactory(context.getSocketFactory());
		InputStream input = urlConnection.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		Log.e("TTTT", result.toString());
	}


//	    /**
//	     * HttpClient方式实现，支持验证指定证书
//	     *
//	     * @throws ClientProtocolException
//	     * @throws IOException
//	     */
//	    public void initSSLCertainWithHttpClient() throws ClientProtocolException, IOException {
//	        int timeOut = 30 * 1000;
//	        HttpParams param = new BasicHttpParams();
//	        HttpConnectionParams.setConnectionTimeout(param, timeOut);
//	        HttpConnectionParams.setSoTimeout(param, timeOut);
//	        HttpConnectionParams.setTcpNoDelay(param, true);
//
//	        SchemeRegistry registry = new SchemeRegistry();
//	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//	        registry.register(new Scheme("https", TrustCertainHostNameFactory.getDefault(this), 443));
//	        ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);
//	        DefaultHttpClient client = new DefaultHttpClient(manager, param);
//
//	        // HttpGet request = new
//	        // HttpGet("https://certs.cac.washington.edu/CAtest/");
//	        HttpGet request = new HttpGet("https://www.alipay.com/");
//	        HttpResponse response = client.execute(request);
//	        HttpEntity entity = response.getEntity();
//	        BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
//	        StringBuilder result = new StringBuilder();
//	        String line = "";
//	        while ((line = reader.readLine()) != null) {
//	            result.append(line);
//	        }
//	        Log.e("HTTPS TEST", result.toString());
//	    }


	public String gethttps(String url){
		Log.i("sCode", "sCode = " );
		String result="";
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);
			HttpClient hc = getHttpClient(httpParameters);
			HttpGet get = new HttpGet(url);
			get.setParams(httpParameters);
			HttpResponse response = null;
			try {
				response = hc.execute(get);
			} catch (UnknownHostException e) {
				throw new Exception("Unable to access "
						+ e.getLocalizedMessage());
			} catch (SocketException e) {
				throw new Exception(e.getLocalizedMessage());
			}
			int sCode = response.getStatusLine().getStatusCode();
			Log.i("sCode", "sCode = " + sCode);
			if (sCode == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(),
						HTTP.UTF_8);
				// handler.sendMessage(Message.obtain(handler, mWhat, result)); // 请求成功
				Log.i("info", "result = " + result);
			} else {
				result = EntityUtils.toString(response.getEntity(),
						HTTP.UTF_8);
				Log.i("info", "result = " + result);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.i("info", "=============异常退出==============");
			// handler.sendMessage(Message.obtain(handler, 404, "异常退出"));
		}



		return result;
	}


	/**
	 * 获取HttpClient
	 *
	 * @param params
	 * @return
	 */
	public static HttpClient getHttpClient(HttpParams params) {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryImp(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
			HttpProtocolParams.setUseExpectContinue(params, true);

			// 设置http https支持
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));// SSL/TSL的认证过程，端口为443
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);
			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient(params);
		}
	}


	public String posthttpps(String url, List<NameValuePair> params){

		String result = null;
		try {
			HttpParams httpParameters = new BasicHttpParams();
			// 设置连接管理器的超时
			ConnManagerParams.setTimeout(httpParameters, 10000);
			// 设置连接超时
			HttpConnectionParams.setConnectionTimeout(httpParameters, 10000);
			// 设置socket超时
			HttpConnectionParams.setSoTimeout(httpParameters, 10000);
			HttpClient hc = getHttpClient(httpParameters);
			HttpPost post = new HttpPost(url);
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			post.setParams(httpParameters);
			HttpResponse response = null;
			try {
				response = hc.execute(post);
			} catch (UnknownHostException e) {
				throw new Exception("Unable to access "
						+ e.getLocalizedMessage());
			} catch (SocketException e) {
				throw new Exception(e.getLocalizedMessage());
			}
			int sCode = response.getStatusLine().getStatusCode();
			if (sCode == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
				Log.i("info", "result = " + result);
			} else {
				result = "请求失败" + sCode; // 请求失败
				// 404 - 未找到

			}
		} catch (Exception e) {
			e.printStackTrace();

			result = "请求失败,异常退出";

		}
		return result;
	}


}
