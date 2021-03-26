# AIC21-Server

# Get started
To run locally follow these instructions

- Get last server.jar file
- Or clone the repository and use `mvn package`, then your jar is in this location:
  - `target/AIC21-Server-1.0-SNAPSHOT-jar-with-dependencies.jar`
- Add a game config file `map.config` besides jar file
  - Its content should be similar to `map.config` above.
- Also add map details file ( `map.json` ) besides `server.jar`
  - don't rename
  - You can pass `map.json` file address using `--read-map` arg to server

# Run server 
`java -jar server.jar --first-team=/path/to/first/team/client --second-team=/path/to/second/team/client`
## main arguments
  - `--first-team`: full path to client binary or jar file for first team
  - `--second-team`: full path to client binary or jar file for second team
## optional arguments
  - `--run-manually` : run clients manually (server asks you to run a new instance when needed!)
  - `--read-map`: if `map.json` is not at the same dir as `server.jar`, pass the address
  - `--max-agents`: maximum agents (client instances) to run 
  - `--show-log`: show more output logs for server debug

# Graphics
When server finished successfully (Winner is anounced in console), use generated `log.json` for graphics. 
