/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.agprogfactoryserver;

import static com.mycompany.agprogfactoryserver.Server.mydb;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 *
 * @author ertugrulg
 */
public class Database {

    public ArrayList<Machine> machines;
    public ArrayList<User> users;
    public ArrayList<Job> jobs;
    public ArrayList<PrintWriter> planners;

    Database() {
        this.machines = new ArrayList<Machine>();
        this.users = new ArrayList<User>();
        this.jobs = new ArrayList<Job>();
        this.planners = new ArrayList<>();
    }

    public Machine getEmptyMachineFastest(String type) {
        for (int i = 0; i < machines.size(); i++) {
            if (machines.get(i).getStatus().equalsIgnoreCase("Empty")
                    && machines.get(i).getType().equalsIgnoreCase(type)) {
                return machines.get(i);
            }
        }
        return null;
    }

    public void listMachines() {
        // Makinelerin listesi alınır ve protokole uyarak mesaj oluşturulur.
        String message = "machinesListed|";
        for (int i = 0; i < machines.size(); i++) {
            message += i + "=";
            message += "name:" + machines.get(i).getName();
            message += ",type:" + machines.get(i).getType();
            message += ",speed:" + machines.get(i).getSpeed();
            message += ",status:" + machines.get(i).getStatus();
            message += ",done:" + machines.get(i).getDone();
            if (i != machines.size() - 1) {
                message += "&";
            }
        }

        message += "|list";
        // Bütün planlayıcılara bu mesaj gidecek
        for (int i = 0; i < mydb.planners.size(); i++) {
            planners.get(i).println(message);
        }
    }

    public ArrayList<Job> getPendingJobs() {
        ArrayList<Job> pendingJobs = new ArrayList<>();
        for (int i = 0; i < this.jobs.size(); i++) {
            pendingJobs.add(this.jobs.get(i));
        }

        return pendingJobs;
    }

    public void listJobs() {
        // İşlerin listesi alınır ve protokole uyarak mesaj oluşturulur.
        String message = "jobsListed|";
        for (int i = 0; i < jobs.size(); i++) {
            message += i + "=";
            message += "type:" + jobs.get(i).getType();
            message += ",cost:" + jobs.get(i).getCost();
            message += ",status:" + jobs.get(i).getStatus();
            message += ",machine:" + jobs.get(i).getMachine();
            if (i != jobs.size() - 1) {
                message += "&";
            }
        }

        message += "|list";
        // Bütün planlayıcılara bu mesaj gidecek
        for (int i = 0; i < mydb.planners.size(); i++) {
            planners.get(i).println(message);
        }
    }
}
