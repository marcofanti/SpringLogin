package com.behaviosec.client;

public class ClientConfiguration {
    private String endPoint;
    private String tenantID;

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }



    public String getTenantId() {
        return this.tenantID;
    }

    public void setTenantId(String tenantId) {
        this.tenantID = tenantId;
    }


    public ClientConfiguration(String endPoint){
        this(endPoint, "THyek3Nd9qx6SbB2");
    }
    public ClientConfiguration(String endPoint, String tenantID){
        this.endPoint = endPoint;
        this.tenantID = tenantID;
    }

    public ClientConfiguration(){ }


}
