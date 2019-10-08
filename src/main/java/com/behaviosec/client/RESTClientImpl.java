package com.behaviosec.client;


import com.behaviosec.config.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.SSLContext;


/**
 * REST client implementation for connectivity with BehavioSec endpoint
 */
public class RESTClientImpl {

    private static final String TAG = RESTClientImpl.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(TAG);
    private String endPoint;
    private HttpClient httpClient;


    public static HttpClient verifiedClient() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {  
    	TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, 
          NoopHostnameVerifier.INSTANCE);
         
     
        BasicHttpClientConnectionManager connectionManager = 
          new BasicHttpClientConnectionManager(RegistryBuilder.<ConnectionSocketFactory> create()
                  .register("https", sslsf)
                  .register("http", new PlainConnectionSocketFactory())
                  .build());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
          .setConnectionManager(connectionManager).build();
        
		return httpClient;
     
    }  
    
    public RESTClientImpl(String endPoint) {
        this.endPoint =endPoint;
        LOGGER.info(TAG + " BehavioSecRESTClient: " + this.endPoint);
        try {
			httpClient = verifiedClient();
		} catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// HttpClientBuilder.create().build();
    }


    /**
     * Submit behavior data and get evaluation
     * @param report List<NameValuePair>
     * @return server response
     * @throws IOException
     */
    public HttpResponse executeRequest(List<NameValuePair> report, String path) throws IOException {
        String uri = endPoint + path;
        LOGGER.info(TAG + " makePost " + uri);
        HttpPost postRequest = new HttpPost(uri);
        postRequest.setHeader("Accept", Constants.ACCEPT_HEADER);
        postRequest.setHeader("Content-type", Constants.CONTENT_TYPE);
        postRequest.setEntity(new UrlEncodedFormEntity(report));
        return this.httpClient.execute(postRequest);
    }
}
