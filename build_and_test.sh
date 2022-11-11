#!/bin/sh
mvn clean install | tee mvnbuild.log
mvn failsafe:integration-test -Pit | tee mvnintegrationtest.log
