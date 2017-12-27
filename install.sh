#!/bin/bash

cd $(dirname "$0")

mvn install:install-file -DgroupId=com.nemustech.common -DartifactId=common-web -Dversion=1.0 -Dpackaging=jar -DgeneratePom=true -Dfile=lib/common-web-1.0.jar -Dsources=lib/common-web-1.0-sources.jar