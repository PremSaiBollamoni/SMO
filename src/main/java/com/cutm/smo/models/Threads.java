package com.cutm.smo.models;

import jakarta.persistence.*;

@Entity
@Table(name = "threads")
public class Threads {
    @Id
    @Column(name = "thread_id") private Long threadId;
    @Column(name = "thread_name") private String threadName;
    @Column(name = "thread_code") private String threadCode;
    @Column(name = "color_code") private String colorCode;
    @Column(name = "status") private String status;

    public Long getThreadId() { return threadId; }
    public void setThreadId(Long threadId) { this.threadId = threadId; }
    public String getThreadName() { return threadName; }
    public void setThreadName(String threadName) { this.threadName = threadName; }
    public String getThreadCode() { return threadCode; }
    public void setThreadCode(String threadCode) { this.threadCode = threadCode; }
    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}