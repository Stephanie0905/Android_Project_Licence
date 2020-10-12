package com.example.kelys.Models;

public class ModelOrders {

   private String pid,id,Nom_produit,price,date_resto,name_user,phone_user,mail_user,date_debut,date_fin,categorie,statut,mail_user_statut;

    public ModelOrders() {
    }

    public ModelOrders(String pid, String id, String Nom_produit, String price, String date_resto, String name_user, String phone_user, String mail_user, String date_debut, String date_fin, String categorie, String statut, String mail_user_statut) {
        this.pid = pid;
        this.id = id;
        this.Nom_produit = Nom_produit;
        this.price = price;
        this.date_resto = date_resto;
        this.name_user = name_user;
        this.phone_user = phone_user;
        this.mail_user = mail_user;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.categorie = categorie;
        this.statut = statut;
        this.mail_user_statut = this.mail_user_statut;
    }

    public String getMail_user_statut() {
        return mail_user_statut;
    }

    public void setMail_user_statut(String mail_user_statut) {
        this.mail_user_statut = mail_user_statut;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }


    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom_produit() {
        return Nom_produit;
    }

    public void setNom_produit(String nom_produit) {
        Nom_produit = nom_produit;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate_resto() {
        return date_resto;
    }

    public void setDate_resto(String date_resto) {
        this.date_resto = date_resto;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }

    public String getPhone_user() {
        return phone_user;
    }

    public void setPhone_user(String phone_user) {
        this.phone_user = phone_user;
    }

    public String getMail_user() {
        return mail_user;
    }

    public void setMail_user(String mail_user) {
        this.mail_user = mail_user;
    }

    public String getDate_debut() {
        return date_debut;
    }

    public void setDate_debut(String date_debut) {
        this.date_debut = date_debut;
    }

    public String getDate_fin() {
        return date_fin;
    }

    public void setDate_fin(String date_fin) {
        this.date_fin = date_fin;
    }
}
