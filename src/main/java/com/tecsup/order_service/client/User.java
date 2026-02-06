package com.tecsup.order_service.client;


import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class User {

    private Long id;
    @JsonAlias("name")
    @JsonProperty("username")
    private String name;
    private String email;

    public String getFullname() {
        return name;
    }

}
