package com.test.appv1;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

@Slf4j
public class App {
    public static void main(String... args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Weather filter"); // this equals to "group.id" in an ordinary kafka consumer https://kafka.apache.org/32/javadoc/org/apache/kafka/streams/StreamsConfig.html
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Integer().getClass());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, Integer> rawReadings = builder.stream("RawTempReadings");
        // the actual filtering of input stream values to output stream happens here
        KStream<String, Integer> validatedReadings = rawReadings
                .filter((key, value) -> value > -50 && value < 130);
        validatedReadings.to("ValidatedTempReadings");

        Topology topo = builder.build();
        log.info(topo.describe().toString());

        KafkaStreams streams = new KafkaStreams(topo, props);
        streams.cleanUp();
        streams.start();

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        log.info("Simple Streams ETL started");
    }
}
