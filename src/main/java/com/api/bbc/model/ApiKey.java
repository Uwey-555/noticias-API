package com.api.bbc.model;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ApiKey {
    
    private String client_id;         // Client ID 
    private String clave;      //  API Key
    private String secret_key;      // Secret Key
    private boolean enabled;     //  Active o Inactivo

}
