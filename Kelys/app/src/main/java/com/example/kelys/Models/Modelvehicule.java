package com.example.kelys.Models;

import java.util.Map;

public class Modelvehicule {

    private String pname, description, price, image, category, pid, date, time,type_car;

    // liste des options ajoutes par joe
    //private  ModelOption options;
    private Map<String, Object> options;

    public Modelvehicule() {

    }

    public Modelvehicule(String pname, String description, String price, String image, String category, String pid, String date, String time,Map<String, Object> options, String type_car) {
        this.pname = pname;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.pid = pid;
        this.date = date;
        this.time = time;
        this.type_car = type_car;

        // liste des options ajoutes par joe
        //this.options.setOptions(options.getOptions());
        this.options = options;
    }

    /*
    public ModelOption getOptions() {
        return this.options;
    }

    public  void setOptions(ModelOption newOptions) {
        this.options = newOptions;
    }
    */

    public Map<String, Object> getOptions() {
        return options;
    }

    public void setOptions(Map<String, Object> options) {
        this.options = options;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType_car() {
        return type_car;
    }

    public void setType_car(String type_car) {
        this.type_car = type_car;
    }

    @Override
    public String toString() {
        return "Modelvehicule{" +
                "pname='" + pname + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", image='" + image + '\'' +
                ", category='" + category + '\'' +
                ", pid='" + pid + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", type_car='" + type_car + '\'' +
                ", options=" + options.toString() +
                '}';
    }
}
