# AIC21-Server

To run locally follow these instructions

- Get last server.jar file
- Or clone the repository and use `mvn package`, then your jar is in this location:
  - `target/AIC21-Server-1.0-SNAPSHOT-jar-with-dependencies.jar`
- Add a game config file `map.config` besides jar file
  - Its content should be similar to `map.config` above.
- run server: `java -jar server.jar` (or `java -jar AIC21-Server-1.0-SNAPSHOT-jar-with-dependencies.jar --first-team=/path/to/client --second-team=/path/to/client`)
- output for graphics: `log.json`
