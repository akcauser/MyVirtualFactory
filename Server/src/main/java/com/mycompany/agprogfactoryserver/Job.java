/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.agprogfactoryserver;

import static com.mycompany.agprogfactoryserver.Server.mydb;

/**
 *
 * @author ertugrulg
 */
public class Job {

    Job(String type, Integer cost) {
        this.id = this.generateId();
        this.type = type;
        this.cost = cost;
        this.machine = "-";
        this.status = "Todo";
    }

    private Integer id;
    private String type;
    private Integer cost;
    private Boolean completed = false;
    private String status;
    private String machine;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer generateId() {
        Integer id = 1;
        for (int i = 0; i < mydb.jobs.size(); i++) {
            if (id == mydb.jobs.get(i).getId()) {
                id += 1;
                i = 0;
            }
        }
        return id;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }
}
