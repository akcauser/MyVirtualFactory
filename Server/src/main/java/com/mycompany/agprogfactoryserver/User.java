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
public class User {
    public User(String name, String password){
        this.name = name;
        this.password = password;
        System.out.println("User Created");
    }

    private String name;
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
