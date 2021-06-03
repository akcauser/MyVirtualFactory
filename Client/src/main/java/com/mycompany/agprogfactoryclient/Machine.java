/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.agprogfactoryclient;

import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static javax.swing.JOptionPane.showMessageDialog;

/**
 *
 * @author ertugrulg
 */
public class Machine implements Runnable {

    public Socket socket;
    public PrintWriter sender;
    public BufferedReader reader;
    public MachineFrame machineFrame;

    @Override
    public void run() {

        // ağ bağlantısını gerçekleştir 
        try {
            socket = new Socket("localhost", 9000);

            // sunucuya veri gönderir
            sender = new PrintWriter(socket.getOutputStream(), true);
            // sunucudan gelen verileri okur
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            this.createMachineForm();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    NewMachineForm nmf;

    private void createMachineForm() {
        nmf = new NewMachineForm(sender);
        nmf.setVisible(true);
        this.readHandle();
    }

    private void readHandle() {
        // machineadded|name=tezgah1&type=cnc&speed=4&status=empty
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
        // createJob|cost=5&type=cnc
        // createMachine|name=tezgah1&type=cnc
        // getmachines
        // getjobs|
        // machineadded
        System.out.println("NewMessage: " + message);
        String[] strArr = message.split("\\|");
        String command = strArr[0];
        String data = strArr[1];

        HashMap obj = new HashMap<>();

        String[] dataArr = data.split("\\&");
        for (int i = 0; i < dataArr.length; i++) {
            String[] field = dataArr[i].split("\\=");
            obj.put(field[0], field[1]);
        }

        switch (command) {
            case "machineCreated":
                this.machineCreated(obj);
                break;
            case "machineClosed":
                this.machineClosed(obj);
                break;
            case "doJob":
                this.doJob(obj);
                break;
            default:
                System.out.println("404");
        }
    }

    private String id;
    private String name;
    private String type;
    private String status;
    private Integer speed;

    public void machineCreated(HashMap obj) {
        id = obj.get("id").toString();
        name = obj.get("name").toString();
        type = obj.get("type").toString();
        status = obj.get("status").toString();
        speed = Integer.parseInt(obj.get("speed").toString());

        showMessageDialog(null, "Machine added!");
        nmf.setVisible(false);
        this.startMachineState();
    }

    private void startMachineState() {
        machineFrame = new MachineFrame(sender);
        machineFrame.setVisible(true);
        machineFrame.idLabel.setText(id);
        machineFrame.nameLabel.setText(name);
        machineFrame.typeLabel.setText(type);
        machineFrame.speedLabel.setText(speed.toString());
        machineFrame.statusLabel.setText(status);
    }

    public void machineClosed(HashMap obj) {
        id = obj.get("id").toString();
        showMessageDialog(null, "Machine closed!");
        machineFrame.dispose();
    }

    public void doJob(HashMap obj) {
        machineFrame.statusLabel.setText("Busy");
        machineFrame.statusLabel.setBackground(Color.red);
        machineFrame.closeMachineButton.setVisible(false);
        Integer time = Integer.parseInt(obj.get("cost").toString()) / Integer.parseInt(this.speed.toString());
        try {
            TimeUnit.SECONDS.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        machineFrame.statusLabel.setText("Empty");
        machineFrame.statusLabel.setBackground(Color.green);
        machineFrame.closeMachineButton.setVisible(true);
        sender.println("jobCompleted|id="+obj.get("id").toString()+"&machineId="+this.id);
    }
}
