package com.api.bbc.repo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.api.bbc.model.ApiKey;

@Repository
public class ApiKeyRepository {
     
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // metodo para buscar una api key por su clave 
    public ApiKey findByClave(String clave){
        String sql = "SELECT * FROM api_keys WHERE clave = ?";

        try {
            // ejecutamos la consulta y mapeamos el resultado a un objeto de la clase ApiKey 
             return jdbcTemplate.queryForObject(sql, new Object[]{clave}, (rs, rowNum) ->
                    ApiKey.builder()
                            .client_id(rs.getString("client_id"))
                            .clave(rs.getString("clave"))
                            .secret_key(rs.getString("secret_key"))
                            .enabled(rs.getBoolean("enabled"))
                            .build()); 
        } catch (DataAccessException e) {

            return null;
        }
    }

}
