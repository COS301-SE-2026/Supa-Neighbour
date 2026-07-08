package com.app.api.dtos;

import java.util.List;

public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private List<String> skills;

    public UpdateProfileRequest(){

    }

    public String getFirstName(){
        return firstName;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public String getLastName(){
        return lastName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public List<String> getSkills(){
        return skills;
    }

    public void setSkills(List<String> skills){
        this.skills = skills;
    }

    public boolean isEmpty() {
        return firstName == null && lastName == null && skills == null;
    }
}
