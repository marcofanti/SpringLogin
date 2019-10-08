package com.behaviosec.client;


import com.behaviosec.config.BehavioSecConfigurationException;
import com.behaviosec.config.BehavioSecException;
import com.behaviosec.config.BehavioSecResponsetException;
import com.behaviosec.config.Constants;
import com.behaviosec.entities.Report;
import com.behaviosec.entities.ReportRequest;
import com.behaviosec.entities.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Client implements API {

    private static final String TAG = Client.class.getName();
    private static final Logger LOGGER = LoggerFactory.getLogger(TAG);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ClientConfiguration requestConfig;
    private RESTClientImpl restClient;

    public Client(ClientConfiguration config){
        this.requestConfig = config;
        if(config.getTenantId().length() > 0){

        }
        this.restClient = new RESTClientImpl(this.requestConfig.getEndPoint());
    }

    private Response makeCall(ReportRequest rq, String path) throws BehavioSecException {
        if(requestConfig.getTenantId().length() > 0){
            rq.setTenantId(requestConfig.getTenantId());
        }

        List<NameValuePair> request = rq.getNameValuePaiList();
        try {
            long startTime = System.currentTimeMillis();
            HttpResponse reportResponse = restClient.executeRequest(request, path);
            long elapsedTime = System.currentTimeMillis() - startTime;
            Response response = new Response();
            response.setResponseTime( elapsedTime);
            if (reportResponse.getStatusLine().getStatusCode() == 200) {
                response.setResponseCode(reportResponse.getStatusLine().getStatusCode());
                response.setReponseString(EntityUtils.toString(reportResponse.getEntity()));
                return response;
            } else {
                return handleAPIerror(reportResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BehavioSecConfigurationException("Error connecting to API server: " + e.getMessage());
        }
    }

    @Override
    public Response getReport(ReportRequest request) throws BehavioSecException {
        Response response = makeCall(request, Constants.API_GET_REPORT);
        if(response.getResponseCode() == 200 ) {
            try {
                Report bhsReport = objectMapper.readValue(
                        response.getReponseString(),
                        Report.class
                );
                response.setReport(bhsReport);
                response.setReponseString(response.getReponseString());
                return response;
            } catch (IOException e) {
                e.printStackTrace();
                throw new BehavioSecResponsetException("Error parsing report");
            }
        }
        return response;
    }

    private Response handleAPIerror(HttpResponse reportResponse) throws IOException {
        Response response = new Response();
        int responseCode = reportResponse.getStatusLine().getStatusCode();
        String error = getResponseString(reportResponse);
        response.setResponseCode(responseCode);
        if (responseCode == 400) {
            response.addError(error);
            LOGGER.error(TAG + " response 400  " + error + ".");
        } else if (responseCode == 403) {
            LOGGER.error(TAG + " response 403  " + error + ".");
            response.setErrorID(403);
            response.setMessage("Unauthorized request: " + error + ".");
        } else if (responseCode == 405) {
            LOGGER.error(TAG + " response 403  " + error + ".");
            response.setErrorID(405);
            response.setMessage("Post is not supported for this URL.");
        }else if (responseCode == 500) {
            LOGGER.error(TAG + " response 500  " + error);
            response.setErrorID(500);
            response.setMessage("Internal API server error: " + error + ".");
        } else {
            LOGGER.error(TAG + " response code: " + responseCode + " DATA: " + error + ".");
            response.setErrorID(responseCode);
            response.setMessage("Response code " + responseCode + " message" + error + ".");
        }
        return response;
    }

    @Override
    public Response finalizeSession(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response forceTrain(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response getHealthCheck() throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response getInvestigationReport(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");

    }

    @Override
    public Response getObfuscatedJavaScript(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response getReportAndInvestigate(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");

    }

    @Override
    public Response getSession(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response getUser(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response getVersion(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response removeUser(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response resetProfile(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response setFraudStatus(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response setUser(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    @Override
    public Response whitelistUser(List<NameValuePair> report) throws BehavioSecException {
        throw new BehavioSecConfigurationException("API call not implemented");
    }

    private String getResponseString(HttpResponse resp) throws IOException {
        return EntityUtils.toString(resp.getEntity(), "UTF-8");
    }


}
