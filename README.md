# Museum Collection Management

The purpose of this application is to manage the inventory of a small museum's collection. The general principle is that locations, pallets, boxes and artefacts are registered, and then they can be moved to other locations or containers for tracking the whereabouts.

## Development

This is a Spring Boot project built in Maven. You check it out from GitHub and then do `mvn install`. This will produce a mobilereg-jar-with-dependencies.jar, which can be run directly with Java.

- [Database structure](docs/DATABASE.md)
- [Kubernetes deployment](docs/KUBERNETES.md)

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

## API

There is a HATEOAS compliant web services API available at /api/v1alpha. From there you can explore the methods.
It requires Basic authentication. Here is how it can be done with `curl` and `wget`:
```
curl http://reg:testkode@localhost:8080/api/v1alpha
curl -u reg:testkode http://localhost:8080/api/v1alpha
wget --user=reg --password=testkode  http://localhost:8080/api/v1alpha
```
Best practice is to create a functional account that only has the permissions it needs.

