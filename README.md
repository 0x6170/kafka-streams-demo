kafka streams demo app
----------------------

A simple demo app demonstrating kafka streams ETL (Extract-Transform-Load) pipeline.



Prerequisites
-------------

1. Java 17.0.4 and up
1. Maven 3.8.6 and up
1. Docker Desktop 4.6.1 and up



Boot up docker containers
-------------------------

```bash
# pull all the necessary docker images
docker compose pull

# spin up all the containers
docker compose up

# check if all containers are running
docker compose ps

# if the above fails, start each container one-by-one in it's own terminal window
# start and wait until ready
docker compose up zookeeper
docker compose up broker
docker compose up ksqldb-server ksqldb-cli

# to stop, CTRL+C
```



Do some investigations
----------------------

```bash
# exec into ksqldb-cli to execute sql commands
docker exec -it ksqldb-cli ksql http://ksqldb-server:8088

# create 'rawTempReading' stream
CREATE STREAM rawTempReadings (sensorID VARCHAR KEY, temp INTEGER)
  WITH (kafka_topic='RawTempReadings', key_format='KAFKA', value_format='KAFKA', partitions=1);

# create 'validatedTempReadings' stream
CREATE STREAM validatedTempReadings (sensorID VARCHAR KEY, temp INTEGER)
  WITH (kafka_topic='ValidatedTempReadings', key_format='KAFKA', value_format='KAFKA', partitions=1);

# create streaming query for filtered results
SELECT * FROM validatedTempReadings EMIT CHANGES;

# open another terminal window, build&run the Java app
./run-java-app.sh

# open another terminatal window and exec into ksqldb-cli container
docker exec -it ksqldb-cli ksql http://ksqldb-server:8088

# insert some values into rawTempReadings stream
INSERT INTO rawTempReadings(sensorID, temp) VALUES ('sensor1', 20);
INSERT INTO rawTempReadings(sensorID, temp) VALUES ('sensor1', 2000);
INSERT INTO rawTempReadings(sensorID, temp) VALUES ('sensor1', -10);

# observe the results in the previous terminal window where validatedTempReadings are emitted

```



Results
-------

![results](/results/streams01.png)



Other
-----

```bash
# exec into ksqldb-server in order to inspect ksqldb-server various setting files
docker exec -it --workdir /etc/ksqldb/ ksqldb-server bash
```

---

To see the kafka topics, configure your kafka topics inspection tool with these params:
```
Zookeeper host:     localhost
Zookeeper port:     2281
Bootstrap servers:  localhost:29092
Broker security:    Plaintext
Schema Registry:    http://localhost:8081/
```

---

Clean up stopped docker containers and remove automatically created volumes (start fresh):
```bash
# gracefully shut down all containers and clean up most of the stuff
docker compose down

# remove stopped containers
docker compose rm

# remove dangling volumes
docker volume prune
```



References
----------

* https://ksqldb.io/quickstart.html
* https://www.pluralsight.com/courses/kafka-streams-ksql-fundamentals
* ...
