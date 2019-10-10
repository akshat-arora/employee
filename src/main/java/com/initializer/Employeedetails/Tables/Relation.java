package com.initializer.Employeedetails.Tables;

//import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Relation")
public class Relation implements Serializable {
    @Id
    //Creating table with different columns and generating their getters and setters
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @JoinColumn
    @JsonIgnore
    private Integer jid;
    private String desi;
    @JsonIgnore
    private float lid;
    public Integer getJid() { return jid; }
    public void setJid(Integer j_id) { this.jid = jid; }
    public String getDesi() {
        return desi;
    }
    public void setDesi(String desi) {
        this.desi = desi;
    }
    public float getLid() { return lid;}
    public void setLid(float lid) { this.lid = lid;}
}
