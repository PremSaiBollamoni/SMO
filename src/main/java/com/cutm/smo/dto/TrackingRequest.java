package com.cutm.smo.dto;

public class TrackingRequest {
    private String machineQr;
    private String employeeQr;
    private String trayQr;
    private String status;

    // Getters and Setters
    public String getMachineQr() {
        return machineQr;
    }

    public void setMachineQr(String machineQr) {
        this.machineQr = machineQr;
    }

    public String getEmployeeQr() {
        return employeeQr;
    }

    public void setEmployeeQr(String employeeQr) {
        this.employeeQr = employeeQr;
    }

    public String getTrayQr() {
        return trayQr;
    }

    public void setTrayQr(String trayQr) {
        this.trayQr = trayQr;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
