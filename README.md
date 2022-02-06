# PowerDC Project

This software can manage setpoint and read current value info from Programmable DC Power Supply *HANMATEK HM310T*.

You'll need InfluxDB 2 if you want save the values read from Power Supply. 
The project has also a MQTT client to receive command to change current setpoint. 

See application.properties to set InfluxDB and MQTT parameters.

A simple demonstration: [![Watch the video](https://img.youtube.com/vi/910y8kMq1Lc/default.jpg)](https://youtu.be/910y8kMq1Lc)

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
mvn compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
mvn package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
mvn package -Dquarkus.package.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.
