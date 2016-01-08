#!/bin/bash

ostype=${OSTYPE//[0-9.]/}
if [[ "$ostype" == "linux-gnu" ]]; then
    #  deployment env
    export JAVA_HOME=/etc/alternatives/java_sdk_1.8.0
    export PROJ_HOME=/PVZDjava
elif [[ "$ostype" == "darwin" ]]; then
    if [[ `/bin/hostname` == "devl8.local" ]]; then  # r2h2 development env
        export devlhome=~/devl
    elif [[ `/bin/hostname` == "ClapTsuNami.local" ]]; then
        export devlhome=/Volumes/devl
    else
        echo "no environment defined for  host `/bin/hostname`"
        exit 1
    fi
    export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0.jdk/Contents/Home
    export PROJ_HOME=$devlhome/java/rhoerbe/PVZDjava
else
    echo "no environment defined for $ostype"
    exit 1
fi


export CLASSPATH="$PROJ_HOME/bin/test/VerifySigAPI:\
$PROJ_HOME/lib/unittests/junit-4.11.jar:\
$PROJ_HOME/lib/unittests/hamcrest-core-1.3.jar:\
$PROJ_HOME/lib/unittests/hamcrest-library-1.3.jar:\
$PROJ_HOME/lib/unittests/commons-codec-1.10.jar"

$JAVA_HOME/bin/java -cp $CLASSPATH org.junit.runner.JUnitCore \
    at.wien.ma14.pvzd.verifysigapitest.JavaSCIOtest
