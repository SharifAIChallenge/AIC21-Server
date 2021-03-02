# AIC21-Server

To run locally follow these instructions

- Get last server.jar file
- Or clone the repository and use `mvn package`, then your jar is in this location:
  - `target/AIC21-Server-1.0-SNAPSHOT-jar-with-dependencies.jar`
- Add a game config file `server.config` besides jar file
  - Its content should be similar to `server.config` file above.
- create a `client/` directory besides jar file
- put java client's (soon other cilents!) jar files in these locations: `client/soldier.jar` and `client/worker.jar`
- run server: `java -jar server.jar` (or `java -jar AIC21-Server-1.0-SNAPSHOT-jar-with-dependencies.jar`)
