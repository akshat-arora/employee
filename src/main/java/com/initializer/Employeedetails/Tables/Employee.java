package com.initializer.Employeedetails.Tables;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.models.auth.In;

import javax.persistence.*;

@Entity
@Table(name="employee")

public class Employee {
    @Id
    //Creating table with different columns and generating their getters and setters
    @JsonProperty("id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @JsonProperty("name")
    private String name;
    //@JsonProperty("designation")
    //private String desi;

    //@JsonProperty("managerId")
    @JsonIgnore
    @Column(name = "manager",nullable = true)
    private int managerId;

    @Transient
    @JsonProperty("jobTitle")
    private String jobTitle;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "designation")
    @JsonIgnore
    private Relation jid;
    public Employee(){}
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public Relation getJid() {
        return jid;
    }

    public void setJid(Relation jid) {
        this.jid = jid;
    }

    public String getJobTitle() {
        return jid.getJobTitle();
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

}

