# Museum Collection Management

The purpose of this application is to manage the inventory of a small museum's collection. The general principle is that locations, pallets, boxes and artefacts are registered, and then they can be moved to other locations or containers for tracking the whereabouts.

## Development

This is a Spring Boot project built in Maven. You check it out from GitHub and then do `mvn install`. This will produce a mobilereg-jar-with-dependencies.jar, which can be run directly with Java.

### Running locally

The application can be run from Maven. The command below will launch with an in-memory database and a small number of records:

```
mvn spring-boot:run -Dspring-boot.run.profiles=functest
```

You can then connect with your webbrowser to `http://localhost:8080/`. There are three test users:

* **reg** - which has the permissions of a registrant.
* **adm** - who can do user and role administration
* **su** - the superuser has all permissions.

They all have the same password: _testkode_.

## Installation

When installing directly in the operating system it is a prerequisite to have either MySQL or MariaDB running.

```sql
CREATE DATABASE museumdb;
CREATE USER 'regbase'@'%' IDENTIFIED BY  'CHANGEME';
GRANT SELECT ON museumdb.* TO 'regbase'@'%';
GRANT CREATE, ALTER, DROP, INSERT, UPDATE, DELETE, SELECT, REFERENCES, RELOAD on museumdb.* TO 'regbase'@'%' WITH GRANT OPTION;
FLUSH PRIVILEGES;
```
You can then launch the application like this:

```sh
#!/bin/sh
java \
  -Dspring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver \
  -Dspring.datasource.url=jdbc:mysql:///museumdb \
  -Dspring.datasource.username=regbase \
  -Dspring.datasource.password=CHANGEME \
  -Dmobilereg.storage-root-dir=/opt/regbase/pictures \
  -jar mobilereg-jar-with-dependencies.jar
```

### Kubernetes deployment

The application can be deployed in Kubernetes with the Helm chart. It will run with the official image, which was built with:

```
podman build . -t sorenroug/mobilereg
podman push sorenroug/mobilereg
```

You configure the Helm chart the usual way with an overriding prod-values.yaml, which could look like:

```yaml
ingress:
  enabled: true
  className: "traefik"
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-production

  hosts:
    - host: mcm.example.com
      paths:
        - path: /
          pathType: ImplementationSpecific
  tls:
    - secretName: mobilereg-tls
      hosts:
        - mcm.example.com

environment:
  TZ: Europe/Copenhagen
  MOBILEREG_STORAGE-ROOT-DIR: /var/local/pictures
  SPRING_DATASOURCE_DRIVER-CLASS-NAME: com.mysql.cj.jdbc.Driver
  SPRING_DATASOURCE_URL: jdbc:mysql://mobilereg-mysql:3306/museumdb?createDatabaseIfNotExist=true
  SPRING_DATASOURCE_USERNAME: regbase
  SPRING_DATASOURCE_PASSWORD: REPLACEME
  SPRING_JPA_HIBERNATE_DDL-AUTO: none
  SPRING_LIQUIBASE_CONTEXTS: prod

mysql:
  enabled: true
  auth:
    rootPassword: REPLACEROOTPW
    database: museumdb
    username: regbase
    password: REPLACEME
  primary:
    livenessProbe:
      enabled: true
    readinessProbe:
      enabled: false
    resources:
      requests:
        memory: 100Mi
      limits:
        memory: 768Mi
        cpu: 750m
```
