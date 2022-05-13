package com.geekym.face_recognition_engage;

public class Users {
    public String name,email, id, college, embeddings;

    public Users() { }

    public Users(String Name, String Email, String ID, String College, String Embeddings){
        this.name = Name;
        this.email = Email;
        this.id = ID;
        this.college = College;
        this.embeddings = Embeddings;
    }
}
