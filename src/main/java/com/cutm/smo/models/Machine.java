package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "machine")
public class Machine {
    @Id
    @Column(name = "machine_id") private Long machineId;
    @Column(name = "name") private String name;
    @Column(name = "type") private String type;
    @Column(name = "status") private String status;

    public Long getMachineId() { return machineId; }
    public void setMachineId(Long machineId) { this.machineId = machineId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
