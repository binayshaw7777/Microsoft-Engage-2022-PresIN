package com.geekym.face_recognition_engage;

public class Users {
    public String name,email, id, organization, embeddings;

    public Users() { }

    public Users(String Name, String Email, String ID, String Organization, String Embeddings){
        this.name = Name;
        this.email = Email;
        this.id = ID;
        this.organization = Organization;
        this.embeddings = Embeddings;
    }
}
