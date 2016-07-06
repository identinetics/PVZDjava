#!/usr/bin/env bash

# wrapper for XSDValidatorCLI
# Usage: $0 <file-to-be-validated> [schema-dir]

ostype=${OSTYPE//[0-9.]/}
if [[ "$ostype" == "linux-gnu" ]]; then
    #  deployment env
    export JAVA_HOME=/etc/alternatives/java_sdk_1.8.0
    export PROJ_HOME=/opt/PVZDjava
elif [[ "$ostype" == "darwin" ]]; then
    if [[ `/bin/hostname` == "devl8.local" ]]; then  # r2h2 development env
        export devlhome=~/devl
        echo "I am @ devl8: ${devlhome}"
    elif [[ `/bin/hostname` == "ClapTsuNami.local" ]]; then
        export devlhome=/Volumes/devl
        echo "I am ClapTsuNami"
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
export CLASSPATH=$PROJ_HOME/bin/test/ValidateXSD:$PROJ_HOME/bin/artifacts/pvzdValidateXsd/pvzdValidateXsd.jar:$PROJ_HOME/lib/unittests/jhades-1.0.4.jar

$JAVA_HOME/bin/java at.wien.ma14.pvzd.validatexsd.cli.XSDValidatorCLI $@
