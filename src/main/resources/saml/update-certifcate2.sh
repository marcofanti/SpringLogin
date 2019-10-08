#!/bin/bash


# openssl pkcs12 -export -in /Users/marcofanti/Documents/BehavioSec/certs2/server.crt -inkey /Users/marcofanti/Documents/BehavioSec/certs2/server.key  -name "ping-example-com" -out ./my.p12

CERTIFICATE_FILE=/Users/marcofanti/Documents/BehavioSec/certs2/server.crt
CERTIFICATE_FILE2=/Users/marcofanti/Documents/BehavioSec/certs2/server.crt2
CERTIFICATE_FILE3=/Users/marcofanti/Documents/Development/fr2/SpringLogin/src/main/resources/saml/my.p12

KEYSTORE_FILE=./samlKeystore.jks
KEYSTORE_PASSWORD=nalle123


keytool -delete -alias ping-example-com -keystore $KEYSTORE_FILE -storepass $KEYSTORE_PASSWORD
keytool -importkeystore -srcstorepass $KEYSTORE_PASSWORD -deststorepass $KEYSTORE_PASSWORD -destkeystore $KEYSTORE_FILE -srckeystore $CERTIFICATE_FILE3 -srcstoretype PKCS12

keytool -delete -alias sp -keystore $KEYSTORE_FILE -storepass $KEYSTORE_PASSWORD
keytool -import -alias sp -file $CERTIFICATE_FILE -keystore $KEYSTORE_FILE -storepass $KEYSTORE_PASSWORD -noprompt

keytool -delete -alias sp2 -keystore $KEYSTORE_FILE -storepass $KEYSTORE_PASSWORD
keytool -import -alias sp2 -file $CERTIFICATE_FILE2 -keystore $KEYSTORE_FILE -storepass $KEYSTORE_PASSWORD -noprompt

keytool -list -keystore $KEYSTORE_FILE -storepass $KEYSTORE_PASSWORD
