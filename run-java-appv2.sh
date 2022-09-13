#!/usr/bin/env bash

mvn clean package

java -jar target/kafka-streams-demo-jar-with-dependencies.jar configuration/appv2/dev.properties
