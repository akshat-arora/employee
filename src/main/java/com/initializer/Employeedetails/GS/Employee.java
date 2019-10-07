package com.initializer.Employeedetails.GS;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name="employee")

public class Employee {
    @Id
    @Column(name = "E_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    //@Column(name="Name")
    private String name;
    //@Column(name="Designation")
    private String desi;
    //@Column(name="J_id")
    //private int j_id;
    //@Column(name="P_id")
    //@Nullable
    @JsonIgnore
    private int pID;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "jid")
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

    public String getDesi() {
        return desi;
    }

    public void setDesi(String desi) {
        this.desi = desi;
    }

    public int getpID() {
        return pID;
    }

    public void setpID(int pID) {
        this.pID = pID;
    }

    public Relation getJid() {
        return jid;
    }

    public void setJid(Relation jid) {
        this.jid = jid;
    }
}
