package com.app.api.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDTO {
    private String rating;
    private String snippet;
    private String date;

    public ReviewDTO(String rating, String snippet, String data){
        this.rating = rating;
        this.snippet = snippet;
        this.date = date;
    }

    public String getRating(){
        return rating;
    }
    public String getSnippet(){
        return snippet;
    }

    public String getDate(){
        return date;
    }

}
