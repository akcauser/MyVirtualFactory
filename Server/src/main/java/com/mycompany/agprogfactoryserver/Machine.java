/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.agprogfactoryserver;

import static com.mycompany.agprogfactoryserver.Server.mydb;
import java.io.PrintWriter;

/**
 *
 * @author ertugrulg
 */
public class Machine {

    public Machine(String name, String type, Integer speed, Integer id, PrintWriter sender) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.speed = speed;
        this.status = "Empty";
        this.sender = sender;
        this.done = 0;

        // Create a machine frame
        // Machine is a client object;
        // Machine can read message from Server 
        // Machine send message to Server
        System.out.println("Machine Created");
    }
    private Integer id;
    private String name;
    private String type;
    private Integer speed;
    private String status;
    private PrintWriter sender;
    private Integer done;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        Server.mydb.listMachines();
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PrintWriter getSender() {
        return sender;
    }

    public void setSender(PrintWriter sender) {
        this.sender = sender;
    }

    public Integer getDone() {
        return done;
    }

    public void setDone(Integer done) {
        this.done = done;
    }
    
    public void doJob(Job job) {
        this.sender.println("doJob|id=" + job.getId() + "&type=" + job.getType() + "&cost=" + job.getCost());
        this.setStatus("Busy");
    }
}
