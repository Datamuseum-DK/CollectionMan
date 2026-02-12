# Kubernetes deployment

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
