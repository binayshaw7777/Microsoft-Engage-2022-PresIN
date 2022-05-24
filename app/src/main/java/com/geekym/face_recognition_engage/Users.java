package com.geekym.face_recognition_engage;

public class Users {
    public String name, email, id, college, year, phone, embeddings, admin;

    public Users() {
    }

    public Users(String Name, String Email, String ID, String College, String Year, String Phone, String Embeddings, String Admin) {
        this.name = Name;
        this.email = Email;
        this.id = ID;
        this.college = College;
        this.embeddings = Embeddings;
        this.year = Year;
        this.phone = Phone;
        this.admin = Admin;
    }
}
