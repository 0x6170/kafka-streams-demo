kafka streams demo app
----------------------

A simple demo app demonstrating kafka streams ETL (Extract-Transform-Load) pipeline.

Prerequisites
-------------

1. Java 17 and up
1. Maven 3.8.5 and up
1. Docker Desktop 4.6.1 and up


Boot up docker containers
-------------------------

```bash
docker compose pull

docker compose up

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


References
----------

* https://ksqldb.io/quickstart.html
* https://www.pluralsight.com/courses/kafka-streams-ksql-fundamentals
* ...
