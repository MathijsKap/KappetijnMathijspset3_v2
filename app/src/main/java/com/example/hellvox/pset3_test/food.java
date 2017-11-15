package com.example.hellvox.pset3_test;

/**
 * Created by HellVox on 15-11-2017.
 */

public class Food {
    private String name;
    private int price;
    private int menuid;

    public Food(String name, int price, int menuid) {
        this.name = name;
        this.price = price;
        this.menuid = menuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getMenuid() {
        return menuid;
    }

    public void setMenuid(int menuid) {
        this.menuid = menuid;
    }
}
