# Laboratorio #4 – REST API Blueprints

Implementación de una REST API con Java 21 y Spring Boot 3.3.x para el manejo de blueprints (planos), aplicando buenas prácticas de diseño de APIs, persistencia en PostgreSQL y documentación con OpenAPI/Swagger.

## Getting Started

Estas instrucciones te permitirán obtener una copia del proyecto y ejecutarlo en tu máquina local para desarrollo y pruebas.

### Prerequisites

- Java 21
- Maven 3.9+
- Docker y Docker Compose (para la base de datos PostgreSQL)

### Installing

**1. Clonar el repositorio**
```bash
git clone https://github.com/SantiagoSu15/Lab-4-ARSW.git
```

**2. Levantar la base de datos e imagen con Docker Compose**
```bash
docker-compose up -d
```

El archivo `docker-compose.yml` levanta un contenedor de PostgreSQL y el contenedor de la aplicación Spring Boot:
```yaml
version: '3.9'

services:
  java_app:
    container_name: java_app
    image: lab4-arsw-java-app
    build: .
    ports:
      - 8080:8080
    environment:
      - DATABASE_URL=jdbc:postgresql://java_DB:5432/blueprints
      - DATABASE_USER=postgres
      - DATABASE_PASSWORD=postgres
    depends_on:
      - java_DB


  java_DB:
    container_name: java_DB
    image: postgres:13.3
    ports:
      - 5432:5432
    environment:
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_USER=postgres
      - POSTGRES_DB=blueprints

```

**3. Compilar y ejecutar el proyecto**
```bash
mvn clean install
mvn spring-boot:run
```

**4. Probar los endpoints con `curl`**
```bash
curl -s http://localhost:8080/api/v1/blueprints | jq
curl -s http://localhost:8080/api/v1/blueprints/john | jq
curl -s http://localhost:8080/api/v1/blueprints/john/house | jq
curl -i -X POST http://localhost:8080/api/v1/blueprints \
  -H 'Content-Type: application/json' \
  -d '{ "author":"john","name":"kitchen","points":[{"x":1,"y":1},{"x":2,"y":2}] }'
curl -i -X PUT http://localhost:8080/api/v1/blueprints/john/kitchen/points \
  -H 'Content-Type: application/json' \
  -d '{ "x":3,"y":3 }'
```

Abrir en navegador:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## Running the Tests
```bash
mvn test
```

### End to End Tests

```bash
# Ejemplo de prueba e2e
```


## Deployment

Para desplegar con imagen de contenedor:
```bash
mvn spring-boot:build-image
docker run -p 8080:8080 <nombre-imagen>
```

O usando Docker Compose directamente (ver sección Installing).

## Estructura del Proyecto
```
src/main/java/edu/eci/arsw/blueprints
  ├── model/         # Entidades de dominio: Blueprint, Point
  ├── persistence/   # Interfaz + repositorios (InMemory, Postgres)
  │    └── Postgres/     # Implementaciones BD PostgreSQL
  ├── services/      # Lógica de negocio y orquestación
  ├── filters/       # Filtros de procesamiento (Identity, Redundancy, Undersampling)
  ├── controllers/   # REST Controllers (BlueprintsAPIController)
  ├── Util/          # ErrorHanlder Hlobal
  └── config/        # Configuración (Swagger/OpenAPI, etc.)
```

## Actividades del Laboratorio

### 1. Familiarización con el código base


### 2. Migración a persistencia en PostgreSQL

Se añadieron dependencias en el pom.xml para poder usar JPA y PostgreSQL.
```
    <dependency>
          <groupId>org.postgresql</groupId>
          <artifactId>postgresql</artifactId>
          <scope>runtime</scope>
    </dependency>
    
    <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
```
Se decide usar JPA poder trabajar facilmente con la BD. 

Para la creacion de se creo el archivo `docker-compose.yml` que levanta la DB y la app
Y se añanen en `application.properties`

```
    spring.datasource.url=${DATABASE_URL}
    spring.datasource.username=${DATABASE_USER}
    spring.datasource.password=${DATABASE_PASSWORD}
    spring.jpa.hibernate.ddl-auto=update
```

Para la migración de la BD se crean 2 clases:
```
-  PostgresBlueprintPersistence  # Clase concreta que implementa BlueprintPersistence e inyecta la interfaz PostgresBlueprintPersistenceRepo para operaciones con la BD
-  PostgresBlueprintPersistenceRepo #Interfaz que implementa JpaRepository para operaciones CRUD entre otras
```
Se hizo uso de la etiqueta `@Profile` para decidir que tipo de DB usar, donde se asigno: 
- `postgresBD` para la base de datos PostgreSQL.
- `enMemoriaBD` para la base de datos en memoria.

En `application.properties` se añade `spring.profiles.active=postgresBD` para la eleccion de DB.


Esto para que sea flexible el cambio de BD dado que ambas implementan la interfaz BlueprintPersistence Spring inyecta el perfil correcto sin tener que editar codigo.

Prueba Base con docker:
![Actua](/docs/img_1.png)


### 3. Buenas prácticas de API REST

## ErrorHandle 
Se añade un error handler para tener mayor claridad a las expeciones que se lanzan.

![error](/docs/img_2.png)



### 4. OpenAPI / Swagger


### 5. Filtros de Blueprints

Para el uso de filtros se debe editar el archivo `application.properties` en `spring.profiles.active=postgresBD`, dependiendo el tipo de perfil se cargan los filtros correspondientes.

Para que se use con la BD se debe separar por ",": `spring.profiles.active=postgresBD,redundancy`.
Por defecto se carga el filtro `Identity` al tener la marquilla `@Profile("default")`


Adicionalmente se agregaron Logs en cada constructor del filtro para verificar cual fue el que se inyecto
```
    private static final Logger log = LoggerFactory.getLogger(IdentityFilter.class);
    
    public IdentityFilter() {
        log.info("IdentityFilter bean creado");
    }
```

![Logs filtros](/docs/logs.png)


### 6. Actuator 

Se añade la dependencia

```
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-actuator</artifactId>
   </dependency>
```

y en `application.properties`

```
    management.endpoints.web.exposure.include=health,info,metrics
    management.endpoint.health.show-details=always
```

### 7. Imagen de Docker con spring-boot:build-image




prueba de endpoint:
![Actua](/docs/img.png)


## Built With

* [Spring Boot 3.3.x](https://spring.io/projects/spring-boot) - Framework principal
* [Maven](https://maven.apache.org/) - Gestión de dependencias
* [PostgreSQL](https://www.postgresql.org/) - Base de datos relacional
* [springdoc-openapi](https://springdoc.org/) - Documentación OpenAPI/Swagger
* [Docker](https://www.docker.com/) - Contenedores

## Authors

* **Juan Felipe Range Y Santiago Suarez** 

