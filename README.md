# 📰 API de Consulta de Noticias 

API REST para buscar noticias en múltiples formatos (JSON, XML, texto, HTML) con autenticación HMAC.

## 🛠️ Requisitos
- **Java 17+**
- **Maven 3.8+**
- **Postman** (para pruebas)

# 1. Clonar el repositorio
git clone https://github.com/Uwey-555/noticias-API.git
cd noticias-API

# 2. Compila el proyecto
./mvnw clean install

# 3. Ejecuta la aplicación (puerto 8080 por defecto)

java -jar target/noticias-api-{version}.jar

##  🌐 Uso de la API
 
## Endpoint

`GET /consulta?q={query}`

###Headers requeridos:
|  Key |  Value |
| ------------ | ------------ |
|  X-Api-Key |  API Key |
|  X-Firma |  Firma HMAC-SHA256 |
| Accept  | Formato de respuesta |

### Formatos soportados
##### - application/json (por defecto)
##### - application/xml
##### - text/plain
##### - text/html

# 📚 Documentación (Swagger)

## http://localhost:8080/swagger-ui.html

