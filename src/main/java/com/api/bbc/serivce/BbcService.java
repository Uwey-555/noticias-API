package com.api.bbc.serivce;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import com.api.bbc.model.Noticia;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

@Service
public class BbcService {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BbcService.class);

    public List<Noticia> buscarNoticias(String q) {
        log.info("Buscando nnoticias en la bbc con la queyr {}", q);
        List<Noticia> noticias = new ArrayList<>(); // creamos una lista de noticias

        try (Playwright Playwrigth = Playwright.create()) {

            // creamos un navegador y lo lanzamos en modo headless para que no se abra una 
            //ventana de navegador en el sistema operativo del servidor 
            Browser browser = Playwrigth.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true));

            // creamos una nueva pagina en el navegador 
            Page page = browser.newPage(); // abre una nueva pagina

            // creamos la url de la pagina de la bbc con el termino de busqueda que le pasamos por parametro 

            String Url = "https://www.bbc.com/search?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8);

            page.navigate(Url); // navega a la pagina de la url que le pasamos por parametro en este caso la url
                                // de la bbc con el termino de busqueda

            page.waitForSelector("[data-testid='new-jersey-grid']",
                    new Page.WaitForSelectorOptions().setTimeout(10000)); // espera a que cargue la pagina

                    // busca todos los elementos que tengan el atributo data-testid con el valor newport-card 
            List<ElementHandle> cards = page.querySelectorAll("[data-testid='newport-card']");

            // recorremos todos los elementos que encontramos con el atributo data-testid con el valor newport-card
            for (ElementHandle card : cards) {
                // creamos un objeto de la clase Noticia
                Noticia noticia = new Noticia();

                // buscamos los elementos que tengan el atributo data-testid con el valor card-headline 
                ElementHandle title = card.querySelector("[data-testid='card-headline']");
                //ElementHandle fecha = card.querySelector("time");
               // ElementHandle resumen = card.querySelector("");

                //1. fecha: fecha/hora de la noticia en formato ISO-8601
               ElementHandle fecha = card.querySelector("[data-testid='card-metadata-lastupdated']");
                
                // Fecha de la noticia en formato ISO-8601
                String fechaOriginal = fecha.innerText().trim();
                // Formateamos la fecha en formato ISO-8601 a un objeto LocalDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
                // Obtenemos la fecha en formato ISO-8601 y la convertimos a un objeto LocalDateTime con el formato dd MMM yyyy 
                LocalDateTime fechaFormateada = LocalDate.parse(fechaOriginal, formatter).atStartOfDay();
                
                
                
                
                ElementHandle resumen = card.querySelector("div.sc-4ea10043-3.eVeOye");

                ElementHandle enlace = card.querySelector("a[data-testid='internal-link']");
                // obtenemos el valor del atributo href del elemento que encontramos con el atributo data-testid 
                //con el valor internal-link 
                String url = enlace.getAttribute("href");
                String fullUrl = "https://www.bbc.com" + url;

                ElementHandle imagen = card.querySelector("img");


                noticia.setTitulo(title.innerText().trim());
                noticia.setFecha(fechaFormateada);
                noticia.setResumen(resumen.innerText().trim());
                //noticia.setFecha(fecha.innerText().trim());
                noticia.setEnlace(fullUrl);
                noticia.setEnlace_foto(imagen.getAttribute("src"));

                noticias.add(noticia);

            }
            browser.close(); // cierra el navegador

        } catch (Exception e) {
        }

        return noticias;
    }

}
