package com.cutm.smo.models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.CascadeType;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(
        name = "employee",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_employee_phone", columnNames = "phone"),
                @UniqueConstraint(name = "uk_employee_email", columnNames = "email")
        }
)
public class EmployeeInfo {

    @Id
    @Column(name = "emp_id", nullable = false)
    private Long empId;

    @Column(name = "name", nullable = false, length = 150)
    private String empName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @JsonManagedReference
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EmployeeLogin> logins;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "salary", precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "emp_date", nullable = false)
    private LocalDate empDate;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact;

    @Column(name = "aadhar_number", length = 512)
    private String aadharNumber;

    @Column(name = "pan_card_number", length = 512)
    private String panCardNumber;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    public Long getEmpId() {
        return empId;
    }

    public void setEmpId(Long empId) {
        this.empId = empId;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<EmployeeLogin> getLogins() {
        return logins;
    }

    public void setLogins(List<EmployeeLogin> logins) {
        this.logins = logins;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public LocalDate getEmpDate() {
        return empDate;
    }

    public void setEmpDate(LocalDate empDate) {
        this.empDate = empDate;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPanCardNumber() {
        return panCardNumber;
    }

    public void setPanCardNumber(String panCardNumber) {
        this.panCardNumber = panCardNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
