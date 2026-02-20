# Deployment

## Legacy deployment

When installing directly in the operating system it is a prerequisite to have either MySQL or MariaDB running.

```sql
CREATE DATABASE museumdb;
CREATE USER 'regbase'@'%' IDENTIFIED BY  'CHANGEME';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, DROP, REFERENCES, ALTER, CREATE VIEW, SHOW VIEW, INDEX ON museumdb.* TO 'regbase'@'%';

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
  -Dserver.servlet.context-path=/ \
  -jar mobilereg-jar-with-dependencies.jar
```

You can change the `server.servlet.context-path` to e.g. `/registration` if you don't want to run the application in the root context. Then visit http://localhost:8080/.

## Kubernetes

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
