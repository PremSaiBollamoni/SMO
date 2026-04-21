package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "buttons")
public class Buttons {
    @Id
    @Column(name = "button_id") private Long buttonId;
    @Column(name = "button_name") private String buttonName;
    @Column(name = "button_code") private String buttonCode;
    @Column(name = "status") private String status;

    public Long getButtonId() { return buttonId; }
    public void setButtonId(Long buttonId) { this.buttonId = buttonId; }
    public String getButtonName() { return buttonName; }
    public void setButtonName(String buttonName) { this.buttonName = buttonName; }
    public String getButtonCode() { return buttonCode; }
    public void setButtonCode(String buttonCode) { this.buttonCode = buttonCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}