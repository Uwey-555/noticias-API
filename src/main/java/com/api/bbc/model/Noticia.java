package com.api.bbc.model;

import java.time.LocalDateTime;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;

@Data
@XmlRootElement
public class Noticia {
    private LocalDateTime fecha;
    //private String fecha;
    private String enlace;
    private String enlace_foto;
    private String titulo;
    private String resumen;


}
