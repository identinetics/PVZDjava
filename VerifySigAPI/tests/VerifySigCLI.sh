#!/usr/bin/env bash

# wrapper for VerifySigCLI
# Usage: $0 <file-to-be-verified>

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

export CLASSPATH="$JAVA_HOME/lib/jconsole.jar:\
$JAVA_HOME/lib/sa-jdi.jar:\
$JAVA_HOME/lib/tools.jar:\
$JAVA_HOME/jre/lib/charsets.jar:\
$JAVA_HOME/jre/lib/deploy.jar:\
$JAVA_HOME/jre/lib/javaws.jar:\
$JAVA_HOME/jre/lib/jce.jar:\
$JAVA_HOME/jre/lib/jfr.jar:\
$JAVA_HOME/jre/lib/jsse.jar:\
$JAVA_HOME/jre/lib/management-agent.jar:\
$JAVA_HOME/jre/lib/plugin.jar:\
$JAVA_HOME/jre/lib/resources.jar:\
$JAVA_HOME/jre/lib/rt.jar:\
$JAVA_HOME/jre/lib/ext/cldrdata.jar:\
$JAVA_HOME/jre/lib/ext/dnsns.jar:\
$JAVA_HOME/jre/lib/ext/localedata.jar:\
$JAVA_HOME/jre/lib/ext/sunec.jar:\
$JAVA_HOME/jre/lib/ext/sunjce_provider.jar:\
$JAVA_HOME/jre/lib/ext/sunpkcs11.jar:\
$JAVA_HOME/jre/lib/ext/zipfs.jar:\
$PROJ_HOME/lib/moa-spss-lib/moa-spss.jar:\
$PROJ_HOME/lib/moa-spss-lib/moa-common.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/mail-1.4.7.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/jaxen-1.1.6.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/iaik_cms-5.0.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/iaik_ssl-4.4.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/iaik_tsl-1.1.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/log4j-1.2.17.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/w3c_http-1.0.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/axis-saaj-1.4.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/iaik_jsse-4.4.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/iaik_moa-1.51.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/joda-time-2.4.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/commons-io-2.4.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/iaik_util-0.23.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/axis-jaxrpc-1.4.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/jaxb-api-2.2.11.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/saxpath-1.0-FCS.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/slf4j-api-1.7.7.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/xml-apis-1.4.01.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/activation-1.1.1.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/jaxb-core-2.2.11.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/jaxb-impl-2.2.11.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/serializer-2.7.2.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/xml-resolver-1.2.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/axis-1.0_IAIK_1.2.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/axis-wsdl4j-1.5.1.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/sqlite-jdbc-3.7.2.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/iaik_ixsil-1.2.2.5.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/jul-to-slf4j-1.7.7.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/commons-logging-1.2.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/slf4j-log4j12-1.7.7.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/jcl-over-slf4j-1.7.7.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/commons-discovery-0.5.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/iaik_javax_crypto-1.0.jar:\
$PROJ_HOME/lib/moa-spss-lib/lib/iaik_xsect_eval-1.1709142.jar:\
$PROJ_HOME/lib/moa-spss-lib/endorsed/xalan.jar:\
$PROJ_HOME/lib/moa-spss-lib/endorsed/xml-apis.jar:\
$PROJ_HOME/lib/moa-spss-lib/endorsed/serializer.jar:\
$PROJ_HOME/lib/moa-spss-lib/endorsed/xercesImpl.jar:\
$PROJ_HOME/lib/moa-spss-lib/ext/iaik_ecc.jar:\
$PROJ_HOME/lib/moa-spss-lib/ext/iaik_jce_full.jar:\
$PROJ_HOME/lib/moa-spss-lib/ext/iaik_Pkcs11Wrapper.jar:\
$PROJ_HOME/lib/moa-spss-lib/ext/iaik_Pkcs11Provider.jar:\
$PROJ_HOME/bin/test/VerifySigAPI:\
$PROJ_HOME/bin/artifacts/pvzdVerifySig/pvzdVerifySig.jar:\
$PROJ_HOME/lib/junit-4.xx.jar:\
$PROJ_HOME/lib/unittests/jhades-1.0.4.jar"

$JAVA_HOME/bin/java at.wien.ma14.pvzd.verifysigapi.cli.VerifySigCLI $@
