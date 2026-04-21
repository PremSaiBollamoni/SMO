package com.cutm.smo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "login")
public class EmployeeLogin {

    @Id
    @Column(name = "emp_id", nullable = false)
    private Long empId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "emp_id", referencedColumnName = "emp_id", insertable = false, updatable = false)
    private EmployeeInfo employee;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public EmployeeInfo getEmployee() {
        return employee;
    }

    public void setEmployee(EmployeeInfo employee) {
        this.employee = employee;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
