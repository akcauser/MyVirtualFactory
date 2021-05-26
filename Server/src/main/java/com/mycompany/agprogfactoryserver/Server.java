/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.agprogfactoryserver;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ertugrulg
 */
public class Server {

    public static Database mydb;

    public static void main(String[] args) {
        mydb = new Database();
        /*
         Kullanıcıların 
         Açık Olan Makinelerin tutulması gerekiyor. 
         Açık olan Planner'ların tutulması gerekiyor. 
         Planner'dan yeni iş ekleme emiri gelir. 
         Gelen emir Makinelere gönderilir. 
         */

 /*
        İlk olarak sadece makine kısmı yapılacak. Serverda manuel olarak iş oluşturulacak
        Ardından Planner kısmı yapılacak ve iş oluşturma bir kullanıcı tarafından yapılacak 
         */
        User user1 = new User("ali", "123");
        User user2 = new User("veli", "321");
        mydb.users.add(user1);
        mydb.users.add(user2);

        ServerSocket server = null;
        try {
            server = new ServerSocket(9000);
            server.setReuseAddress(true);

            // Server daima istekleri dinlemektedir.
            while (true) {

                // Yeni bir client kabul etme
                Socket client = server.accept();
                ClientHandler clientSock = new ClientHandler(client);
                new Thread(clientSock).start();

                System.out.println("New Client Connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket clientSocket;
        PrintWriter sender = null;
        BufferedReader reader = null;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {

            try {

                sender = new PrintWriter(clientSocket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Client tarafından gelen istek
                    this.messageParser(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (sender != null) {
                        sender.close();
                    }
                    if (reader != null) {
                        reader.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // USING AGPROG PROTOCOL
        public void messageParser(String message) {
            System.out.println("NewMessage: " + message);
            String[] strArr = message.split("\\|");
            String command = strArr[0];

            String data = null;
            if (strArr.length > 1) {
                data = strArr[1];
            }

            String type = null;
            if (strArr.length > 2) {
                type = strArr[2];
            }

            HashMap obj = new HashMap<>();
            if (data != null && !data.equalsIgnoreCase("")) {
                String[] dataArr = data.split("\\&");
                for (int i = 0; i < dataArr.length; i++) {
                    String[] field = dataArr[i].split("\\=");
                    obj.put(field[0], field[1]);
                }
            }

            switch (command) {
                case "createMachine":
                    this.createMachine(obj);
                    break;
                case "createJob":
                    this.createJob(obj);
                    break;
                case "getMachines":
                    mydb.listMachines();
                    break;
                case "closeMachine":
                    this.closeMachine(obj);
                    break;
                case "login":
                    this.login(obj);
                    break;
                case "jobCompleted":
                    this.jobCompleted(obj);
                    break;
                default:
                    System.out.println("404");
            }
        }

        public void createMachine(HashMap obj) {
            try {
                String name = obj.get("name").toString();
                String type = obj.get("type").toString();
                String speed = obj.get("speed").toString();
                Integer speedInt = Integer.parseInt(speed);
                Machine machine = new Machine(name, type, speedInt, this.generateMachineId(), sender);
                mydb.machines.add(machine);
                sender.println("machineCreated|"
                        + "name=" + machine.getName()
                        + "&type=" + machine.getType()
                        + "&speed=" + machine.getSpeed()
                        + "&status=" + machine.getStatus()
                        + "&id=" + machine.getId()
                );
                sender.flush();
                mydb.listMachines();
                this.doJob();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private Integer generateMachineId() {
            Integer id = 1;
            for (int i = 0; i < mydb.machines.size(); i++) {
                if (id == mydb.machines.get(i).getId()) {
                    id += 1;
                    i = 0;
                }
            }

            return id;
        }

        private void jobCompleted(HashMap obj) {
            try {
                if (obj.get("machineId") != null) {
                    Integer id = Integer.parseInt(obj.get("machineId").toString());
                    for (int i = 0; i < mydb.machines.size(); i++) {
                        if (mydb.machines.get(i).getId() == id) {
                            mydb.machines.get(i).setStatus("Empty");
                        }
                    }
                }
                if (obj.get("id") != null) {
                    Integer id = Integer.parseInt(obj.get("id").toString());
                    for (int i = 0; i < mydb.jobs.size(); i++) {
                        if (mydb.jobs.get(i).getId() == id) {
                            mydb.jobs.get(i).setStatus("Done");
                        }
                    }
                }
                mydb.listJobs();
                mydb.listMachines();
                this.doJob();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void closeMachine(HashMap obj) {
            try {
                if (obj.get("id") != null) {
                    Integer id = Integer.parseInt(obj.get("id").toString());
                    for (int i = 0; i < mydb.machines.size(); i++) {
                        if (mydb.machines.get(i).getId() == id) {
                            mydb.machines.remove(i);
                            sender.println("machineClosed|id=" + id);
                            sender.flush();
                            mydb.listMachines();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void createJob(HashMap obj) {
            try {
                String type = obj.get("type").toString();
                String costStr = obj.get("cost").toString();
                Integer cost = Integer.parseInt(costStr);
                Job job = new Job(type, cost);
                mydb.jobs.add(job);
                // Bütün planlayıcıların ekranına yeni ise create edildiği bilgisi gitmelidir.
                sender.println("jobCreated|id="+ job.getId() +"&type=" + type + "&cost=" + cost);
                   
                mydb.listJobs();
                // iş yaptırma emri
                doJob();
            } catch (Exception e) {
                sender.println("createJobFailed");
            }
            sender.flush();
        }

        public void doJob() {
            System.out.println("Do Job");
            // boş olup speed en yüksek olan makine gelir, makine yoksa bir şey yapma
            // makine create edildiğinde de bu fonksiyon çağırılır. 
            // amaç makineler çalışmadan önce eklenen işlerin yapılmasını sağlamaktadır.
            // Makine boş ise en hızlı olana işi atama yapar. 
            // Makinenin durumunu değiştirir. 
            for (int i = 0; i < mydb.jobs.size(); i++) {
                if (mydb.jobs.get(i).getCompleted() == false) {
                    String jobType = mydb.jobs.get(i).getType();
                    if (mydb.getEmptyMachineFastest(jobType) != null) {
                        System.out.println("Null Değil");
                        Integer index = mydb.machines.indexOf(mydb.getEmptyMachineFastest(jobType));
                        mydb.machines.get(index).doJob(mydb.jobs.get(i));
                        
                        // makineye gönderilen iş silinir.
                        mydb.jobs.get(i).setMachine(mydb.machines.get(index).getName());
                        mydb.jobs.get(i).setCompleted(Boolean.TRUE);
                        mydb.jobs.get(i).setStatus("Doing");
                    }
                }
            }
            mydb.listJobs();
        }

        public void login(HashMap obj) {
            String username = obj.get("username").toString();
            String password = obj.get("password").toString();

            Boolean userLogged = false;
            for (int i = 0; i < mydb.users.size(); i++) {
                if (mydb.users.get(i).getName().equalsIgnoreCase(username)) {
                    if (mydb.users.get(i).getPassword().equalsIgnoreCase(password)) {
                        // user bilgileri doğru
                        userLogged = true;
                    }
                }
            }
            if (userLogged) {
                // user logged sender
                mydb.planners.add(sender);
                sender.println("logged|username=" + username);
                sender.flush();
            } else {
                // user not logged sender
                sender.println("loginFailed|errorMessage=credentials undefined");
                sender.flush();
            }
        }
    }
}
