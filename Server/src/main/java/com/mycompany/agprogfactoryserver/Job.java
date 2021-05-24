/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.agprogfactoryserver;

/**
 *
 * @author ertugrulg
 */
public class Job {

    Job(String type, Integer cost) {
        this.type = type;
        this.cost = cost;
    }

    private String type;
    private Integer cost;
    private Boolean completed = false;

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
}
