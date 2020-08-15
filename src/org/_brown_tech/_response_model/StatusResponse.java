package org._brown_tech._response_model;

/**
 * @author Mandela aka puumInc
 * @version 1.0.0
 */
public enum StatusResponse {

    SUCCESS ("Success"), ERROR ("Error"), WARNING("Warning");

    public String status;

    StatusResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
