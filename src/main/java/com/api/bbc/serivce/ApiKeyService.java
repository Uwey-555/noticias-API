package com.api.bbc.serivce;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.api.bbc.exception.ApiKeyInvalidaException;
import com.api.bbc.model.ApiKey;
import com.api.bbc.repo.ApiKeyRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApiKeyService {

    @Autowired
    private ApiKeyRepository apiKeyRepository; // Inyección de dependencias de la interfaz ApiKeyRepository 

    public boolean validarApiKey(String clave, String firma) { // Método para validar la API Key y la firma recibida en la petición HTTP 

        // Se busca la API Key en la base de datos por la clave recibida en la petición
        ApiKey apiKey = apiKeyRepository.findByClave(clave);

        log.info("API Key recibida: {}", apiKey.getClave());
        log.info("Secretkey asociada a la API Key: {}", apiKey.getSecret_key());
        log.info("Firma recibida: {}",  firma);

        if (apiKey == null || !apiKey.isEnabled()) { // Si la API Key no existe o no está habilitada, se lanza una excepción
            throw new ApiKeyInvalidaException("No autorizado");
            }

        // Se calcula la firma esperada a partir de la clave secreta de la API Key encontrada en la base de datos     
        String firmaEsperada = calcularFirma(apiKey.getSecret_key()); 
        log.info("Firma esperada: {}", firmaEsperada);
        return firmaEsperada.equals(firma); // Se compara la firma esperada con la firma recibida en la petición



    }

        // Método para calcular la firma a partir de la clave secreta de la API Key y 
        //un mensaje vacío(se usa mensaje vacion en este caso de test) 
    private String calcularFirma(String secretKey) { 
        String mensaje = ""; // Mensaje vacío
        try {
            // Se crea una instancia de la clase SecretKeySpec con la clave secreta y el algoritmo de cifrado HmacSHA256
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            // Se crea una instancia de la clase Mac con el algoritmo de cifrado HmacSHA256
            Mac mac = Mac.getInstance("HmacSHA256");
            // Se inicializa el objeto Mac con la clave secreta 
            mac.init(secretKeySpec);
            // Se calcula la firma a partir del mensaje y se codifica en Base64
            byte[] firma = mac.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));
            // Se retorna la firma codificada en Base64
            return Base64.getEncoder().encodeToString(firma);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) { // Captura de excepciones 
            throw new RuntimeException("Error al calcular la firma", e);
        }
    }

}
