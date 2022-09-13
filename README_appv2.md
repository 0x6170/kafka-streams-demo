kafka streams demo app v2
-------------------------

The example app adapted from 
[here](https://developer.confluent.io/tutorials/join-a-stream-to-a-table/kstreams.html).

**Question:**<br/>
If you have events in a Kafka topic and a table of reference data (also known as a lookup table), 
how can you join each event in the stream to a piece of data in the table based on a common key?

**Example use case:**<br/>
Suppose you have a set of movies that have been released and a stream of ratings from moviegoers about how 
entertaining they are. In this tutorial, we'll write a program that joins each rating with content about the movie. 
Related pattern: [Event Joiner](https://developer.confluent.io/patterns/stream-processing/event-joiner)



Prerequisites
-------------

1. Complete the tutorial from [README.md](/README.md).
2. The Java code is located in this class: `src/main/java/com/test/appv2/App.java`
3. Edit the `pom.xml`, change this line:
    ```XML
    <mainClass>com.test.App</mainClass>
    ```
    to this:
    ```XML
    <mainClass>com.test.appv2.App</mainClass>
    ```



Boot up docker containers
-------------------------

*Go through the procedure in [README.md](/README.md)*.

```bash
# start only the necessarry containers
docker compose up zookeeper broker schema-registry

# to stop, CTRL+C
```



Do some investigations
----------------------

```bash
# in a new terminal start the java app
./run-java-appv2.sh

# in a new terminal exec into schema-registry container
docker compose exec schema-registry bash

# start kafka console consumer for topic 'movie'
kafka-avro-console-consumer \
  --topic movies \
  --bootstrap-server broker:9092 \
  --property schema.registry.url=http://localhost:8081

# in a new terminal exec into schema-registry container
docker compose exec schema-registry bash

# start kafka-avro producer for topic 'movie'
kafka-avro-console-producer \
  --topic movies \
  --bootstrap-server broker:9092 \
  --property schema.registry.url=http://localhost:8081 \
  --property value.schema="$(< /host_files/src/main/avro/movie.avsc)"

# produce the following records to the 'movie' topic, copy/paste one-line-at-a-time
{"id": 294, "title": "Die Hard", "release_year": 1988}
{"id": 354, "title": "Tree of Life", "release_year": 2011}
{"id": 782, "title": "A Walk in the Clouds", "release_year": 1995}
{"id": 128, "title": "The Big Lebowski", "release_year": 1998}
{"id": 780, "title": "Super Mario Bros.", "release_year": 1993}

# in a new terminal exec into schema-registry container
docker compose exec schema-registry bash

# set up observation of the 'rated-movies' topic
kafka-avro-console-consumer \ 
  --topic rated-movies \
  --bootstrap-server broker:9092 \
  --from-beginning

# in a new terminal exec into schema-registry container
docker compose exec schema-registry bash

# start kafka-avro producer for topic 'ratings'
kafka-avro-console-producer \
  --topic ratings \
  --bootstrap-server broker:9092 \
  --property schema.registry.url=http://localhost:8081 \
  --property value.schema="$(< /host_files/src/main/avro/rating.avsc)"
  
# produce the following records to the 'ratings' topic, copy/paste one-line-at-a-time
{"id": 294, "rating": 8.2}
{"id": 294, "rating": 8.5}
{"id": 354, "rating": 9.9}
{"id": 354, "rating": 9.7}
{"id": 782, "rating": 7.8}
{"id": 782, "rating": 7.7}
{"id": 128, "rating": 8.7}
{"id": 128, "rating": 8.4}
{"id": 780, "rating": 2.1}

# watch for results in the 'rated-movies' topic

```



Results
-------

![results](/results/streams02.png)



Other
-----

Get registered subjects in schema-registry:
http://localhost:8081/subjects/


Get the schemas from schema-registry for various topics:
* movies: http://localhost:8081/subjects/movies-value/versions/1/schema
* ratings: http://localhost:8081/subjects/ratings-value/versions/1/schema
* rated-movies: http://localhost:8081/subjects/rated-movies-value/versions/1/schema



References
----------

* https://developer.confluent.io/tutorials/join-a-stream-to-a-table/kstreams.html
* https://developer.confluent.io/patterns/stream-processing/event-joiner
* https://docs.confluent.io/platform/current/schema-registry/develop/api.html