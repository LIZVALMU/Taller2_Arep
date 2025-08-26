# Taller 1 Diseño y estructuración de aplicaciones distribuidas en internet

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Maven](https://img.shields.io/badge/Maven-3.x-blue.svg)](https://maven.apache.org/)
![HTTP](https://img.shields.io/badge/HTTP-1.1-blue) |
![JSON](https://img.shields.io/badge/JSON-Supported-green?logo=json&logoColor=white) 

## Introducción

Este es un prototipo basado en Java diseñado para implementar un servidor web, el objetivo es proporcionar una plataforma funcional capaz de gestionar recuros como: 
- Atender multiples solicitudes Http 
- Leer archivos y retornar todo lo solicitado en los que se incluyen paginas html, archivos java script e imagenes 
- Comunicación asincrónica entre el frontend y el backend a través de servicios REST personalizados implementados directamente en sockets Java.

---

## Tecnologías Utilizadas

| Tecnología      | Versión       | Propósito                   |
|-----------------|---------------|-----------------------------|
| Java            | 21            | Desarrollo backend          |
| Sockets         | Java Nativo   | Comunicación de red         |
| HTTP/1.1        | -             | Implementación de protocolo |
| JSON            | Nativo        | Respuestas API              |

---

## Instalación

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/LIZVALMU/arep_httpserver.git
   cd arep_httpserver
   ```

2. Compilar el proyecto (Maven):
   ```bash
   mvn clean package
   ```

3. Asegurarse de tener instalado:
   - **Java 21**
   - **Maven 3.x**

---

## Ejecución

1. Ingresar a la carpeta 
    ```bash
   cd taller2_arep
   ```
2. Instalar las dependencias
    ```bash
   mvn clean compile
   ```
3. Ejecutar el servidor:
   ```bash
      java -cp target/classes escuela.edu.co.HttpServer
   ```

**si desea especificar un puerto diferente, puede hacerlo al iniciar el servidor:**

```bash
   java -cp target/classes escuela.edu.co.HttpServer <puerto>
```

4. Abrir en un navegador:
   ```
   http://localhost:35000/index.html
   ```

   > El servidor por defecto corre en el puerto `35000`.  

5. Para probar los servicios REST (ejemplo):
   - GET: `http://localhost:35000/hello?name=Alison`
   - POST: `http://localhost:35000/hellopost?name=Alison`

---

## Arquitectura del Prototipo

El sistema está compuesto por tres módulos principales:

1. **Servidor HTTP (HttpServer.java)**  
   - Maneja las conexiones entrantes mediante sockets TCP.  
   - Interpreta las solicitudes HTTP y envía respuestas con código de estado, cabeceras y cuerpo.  

2. **Gestor de Recursos Estáticos (www/)**  
   - Contiene las páginas HTML, archivos JavaScript, CSS e imágenes.  
   - El servidor localiza y devuelve estos recursos según la ruta solicitada.  

3. **Módulo REST**  
   - Endpoints personalizados (`/hello`, `/hellopost`, etc.).  
   - Devuelven datos en formato JSON para ser consumidos por el cliente.  
   - Probados con llamadas asíncronas desde `app.js` en el frontend.  

---

## Evaluación (Pruebas Realizadas)

- **Prueba de archivos estáticos**:  
  Verificación de que el servidor devuelve correctamente `index.html`, `style.css`, `app.js` y recursos multimedia.  

- **Prueba de servicios REST (GET y POST)**:  
  - `GET /hello?name=John` → Respuesta con saludo personalizado.  
  - `POST /hellopost?name=John` → Respuesta en formato JSON.  

- **Prueba de navegador**:  
  Acceso al frontend (`index.html`) y comunicación exitosa con el backend.

---

## Project Structure

```bash
arep_httpserver/
├── src/
│   └── main/
│       └── java/
│           └── escuela/
│               └── edu/
│                   └── co/
│                       └── HttpServer.java    # Servidor HTTP principal
└── www/                                        # Archivos estáticos
    ├── index.html                             # Página principal
    ├── app.js                                 # JavaScript frontend
    ├── style.css                              # Estilos CSS
    └── logo.png                               # Recursos multimedia
```
---

## Autor

- [Alison Geraldine Valderrama Munar](https://github.com/alisongvalderrama)

## License
Este proyecto está licenciado bajo la MIT License
