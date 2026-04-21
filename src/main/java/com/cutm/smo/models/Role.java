package com.cutm.smo.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "role",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_role_name", columnNames = "role_name")
        }
)
public class Role {

    @Id
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "role_name", nullable = false, length = 100)
    private String roleName;

    @Column(name = "activities", length = 500)
    private String activity;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
