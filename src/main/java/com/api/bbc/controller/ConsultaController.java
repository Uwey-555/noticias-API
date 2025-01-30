package com.api.bbc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.bbc.exception.InternarlServerError;
import com.api.bbc.exception.NoticeNullException;
import com.api.bbc.exception.QueryInvalidException;
import com.api.bbc.model.Noticia;
import com.api.bbc.serivce.ApiKeyService;
import com.api.bbc.serivce.BbcService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ConsultaController {

    private final BbcService bbcService;

    @Autowired
    private ApiKeyService apiKeyService;

    @GetMapping(value = "/consulta", produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE,
            MediaType.TEXT_PLAIN_VALUE,
            MediaType.TEXT_HTML_VALUE
    })
    public ResponseEntity<?> buscarNoticias(
            @RequestParam("q") String q,
            @RequestHeader(HttpHeaders.ACCEPT) String acceptHeader,
            @RequestHeader("X-Api-Key") String apiKeyHeader,
            @RequestHeader("X-Firma") String firma) {
                log.info("ConsultaController buscarNoticias");
                log.info("q: " + q);
                log.info("X-Api-Key: {}", apiKeyHeader);
                log.info("X-Firma: {}", firma);

        if(q == null || q.isEmpty()) {
            throw new QueryInvalidException("Parámetros inválidos");
        }
        // Se valida que las cabeceras X-Api-Key y X-Firma no sean nulas
        if (apiKeyHeader == null || firma == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("codigo", "g104");
            errorResponse.put("error", "Faltan las cabeceras X-Api-Key o X-Firma");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
        //se corrije validacion !
        if (!apiKeyService.validarApiKey(apiKeyHeader, firma)) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("codigo", "g103");
            errorResponse.put("error", "No autorizado");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(errorResponse);
        }

        try {
            // Se valida que el header Accept no sea nulo
            if (acceptHeader == null || acceptHeader.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body("El header Accept es vacio.");
            }
            // Se obtiene el tipo de media del header Accept y se busca las noticias en base a la query q
            MediaType mediaType = MediaType.parseMediaType(acceptHeader);
            // Se obtienen las noticias de la BBC 
            List<Noticia> noticias = bbcService.buscarNoticias(q);

            if(noticias.isEmpty()) {
                throw new NoticeNullException("No se encuentran noticias para el texto: {}" + q);
            }

            // Se retorna la respuesta en base al tipo de media del header Accept 
            return switch (mediaType.getType()) {
                case "application" -> handleApplicationTypes(noticias, mediaType);
                case "text" -> handleTextTypes(noticias, mediaType);
                default -> ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                        .body("Unsupported media type: " + acceptHeader);
            };

        } catch (Exception e) { // Se captura cualquier excepción y se lanza un error interno del servidor 
            throw new InternarlServerError("Error interno del servidor");
        }
    }

    /*
     * return ResponseEntity.ok()
     * .header("Content-Type", "application/json; charset=UTF-8")
     * .header("Content-Type", "application/xml; charset=UTF-8")
     * .body(noticias);
     * 
     * // return ResponseEntity.ok(bbcService.buscarNoticias(q));
     */
    // Se manejan los tipos de media application y text 
    private ResponseEntity<?> handleApplicationTypes(List<Noticia> noticias, MediaType mediaType) {
        if (mediaType.includes(MediaType.APPLICATION_XML)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_XML)
                    .body(noticias);
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(noticias);
    }
     // Se manejan los tipos de media text y text /html 
    private ResponseEntity<?> handleTextTypes(List<Noticia> noticias, MediaType mediaType) {
        if (mediaType.includes(MediaType.TEXT_HTML)) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateHtmlResponse(noticias));
        }
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(generatePlainTextResponse(noticias));
    }
    // Se genera la respuesta en texto plano 
    private String generatePlainTextResponse(List<Noticia> noticias) {
        StringBuilder sb = new StringBuilder();
        noticias.forEach(noticia -> {
            sb.append("Título: ").append(noticia.getTitulo()).append("\n");
            sb.append("URL: ").append(noticia.getEnlace()).append("\n");
            sb.append("Resumen: ").append(noticia.getResumen()).append("\n\n");
        });
        return sb.toString();
    }
    // Se genera la respuesta en HTML, tambien se puede usar un template engine como Thymeleaf
    // pero para este test basico se usa un StringBuilder 
    private String generateHtmlResponse(List<Noticia> noticias) {
        StringBuilder html = new StringBuilder("<html><body><ul>");
        noticias.forEach(noticia -> {
            html.append("<li>")
                    .append("<h3>").append(noticia.getTitulo()).append("</h3>")
                    .append("<p><a href='").append(noticia.getEnlace()).append("'>Enlace</a></p>")
                    .append("<p>").append(noticia.getResumen()).append("</p>")
                    .append("</li>");
        });
        html.append("</ul></body></html>");
        return html.toString();
    }

}
