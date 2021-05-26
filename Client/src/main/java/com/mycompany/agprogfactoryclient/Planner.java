/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.agprogfactoryclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import static javax.swing.JOptionPane.showMessageDialog;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author ertugrulg
 */
public class Planner implements Runnable {

    Socket socket;
    PrintWriter sender;
    BufferedReader reader;
    PlannerFrame plannerFrame;

    @Override
    public void run() {
        // ağ bağlantısını gerçekleştir 
        try {
            socket = new Socket("localhost", 9000);

            // sunucuya veri gönderir
            sender = new PrintWriter(socket.getOutputStream(), true);

            // sunucudan gelen verileri okur
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.loginFrame();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private LoginFrame loginFrame;

    private void loginFrame() {
        loginFrame = new LoginFrame(sender);
        loginFrame.setVisible(true);
        this.readHandle();
    }

    private void startPlannerState() {
        plannerFrame = new PlannerFrame(sender);
        plannerFrame.setVisible(true);

        sender.println("getMachines||");
    }

    private void readHandle() {
        // machinecreated|id=1&name=tezgah1&type=cnc&status=Empty
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                // sunucudan veri gelme durumu
                // İstek parse edilmeli
                this.messageParser(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // USING AGPROG PROTOCOL
    public void messageParser(String message) {
        // login|username=ali&password=123
        // createjob|cost=5&type=cnc
        // createMachine|name=tezgah1&type=cnc
        // getMachines
        // getJobs|
        // machineadded
        // loginFailed|errormessage=Asd
        // logged|username=username
        System.out.println("NewMessage: " + message);
        String[] strArr = message.split("\\|");
        String command = strArr[0];
        String data = strArr[1];
        String type = null;
        if (strArr.length > 2) {
            type = strArr[2];
        }

        HashMap obj = new HashMap<>();
        if (!data.equalsIgnoreCase("") && data != null) {
            String[] dataArr = data.split("\\&");
            for (int i = 0; i < dataArr.length; i++) {
                String[] field = dataArr[i].split("\\=");
                obj.put(field[0], field[1]);
            }
        }

        switch (command) {
            case "logged":
                this.logged(obj);
                break;
            case "loginFailed":
                this.loginFailed(obj);
                break;
            case "jobCreated":
                this.jobCreated(obj);
                break;
            case "machinesListed":
                this.machinesListed(obj);
                break;
            case "jobsListed":
                this.jobsListed(obj);
                break;
            default:
                System.out.println("404");
        }
    }

    public void logged(HashMap obj) {
        showMessageDialog(null, "Welcome " + obj.get("username").toString());
        loginFrame.dispose();
        this.startPlannerState();
    }

    public void loginFailed(HashMap obj) {
        String errorMessage = obj.get("errorMessage").toString();
        showMessageDialog(null, "Login Failed: " + errorMessage);
    }

    public void jobCreated(HashMap obj) {
        if (plannerFrame.newJobFrame != null) {
            showMessageDialog(null, "Job created");
            plannerFrame.newJobFrame.dispose();
        }
    }

    public void machinesListed(HashMap obj) {

        Object[][] array = new Object[obj.size()][];
        for (int i = 0; i < obj.size(); i++) {
            HashMap listItem = new HashMap<>();
            String[] fieldsArr = obj.get(obj.keySet().toArray()[i]).toString().split("\\,");
            for (int j = 0; j < fieldsArr.length; j++) {
                String[] field = fieldsArr[j].split("\\:");
                listItem.put(field[0], field[1]);
            }
            array[i] = new Object[4];
            array[i][0] = listItem.get("name");
            array[i][1] = listItem.get("type");
            array[i][2] = listItem.get("speed");
            array[i][3] = listItem.get("status");
        }

        /*
        Object[][] data = {
            {},{},{}
        };
         */
        String[] tableModel = new String[]{
            "Name", "Type", "Speed", "Status"
        };

        DefaultTableModel defaultTableModel = new DefaultTableModel(array, tableModel);

        plannerFrame.machineTable.setModel(defaultTableModel);
    }

    public void jobsListed(HashMap obj) {

        Object[][] array = new Object[obj.size()][];
        for (int i = 0; i < obj.size(); i++) {
            HashMap listItem = new HashMap<>();
            String[] fieldsArr = obj.get(obj.keySet().toArray()[i]).toString().split("\\,");
            for (int j = 0; j < fieldsArr.length; j++) {
                String[] field = fieldsArr[j].split("\\:");
                listItem.put(field[0], field[1]);
            }
            array[i] = new Object[4];
            array[i][0] = listItem.get("type");
            array[i][1] = listItem.get("cost");
            array[i][2] = listItem.get("status");
            array[i][3] = listItem.get("machine");
        }

        /*
        Object[][] data = {
            {},{},{}
        };
         */
        String[] tableModel = new String[]{
            "Type", "Cost", "Status", "Machine"
        };

        DefaultTableModel defaultTableModel = new DefaultTableModel(array, tableModel);

        plannerFrame.jobsTable.setModel(defaultTableModel);
    }
}
