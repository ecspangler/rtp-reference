#!/usr/bin/env bash

printf "Enter Kie Server Route and Elastic URL:\n"
read -p "Kie Server Route: " kieserver
read -p "Elastic URL: " elastic

for service in \
    rtp-creditor-account-validation-glue \
    rtp-creditor-complete-case-glue \
    rtp-creditor-fraud-validation-glue \
    rtp-creditor-payment-received-glue \
    rtp-creditor-elastic-glue
do
    printf "Maven building $service\n"
    cd $service
    mvn clean install
    cd ..
done


cd rtp-creditor-account-validation-glue

oc new-build java8 --name=rtp-creditor-account-validation-glue --binary=true -n rtp-reference
oc start-build rtp-creditor-account-validation-glue --from-file=target/ROOT.jar -n rtp-reference
sleep 30
oc new-app rtp-creditor-account-validation-glue -n rtp-reference \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_PAYMENTS_TOPIC=creditor-payments \
-e CASE_ID_GLUE=rtp-case-list \
-e APPLICATION_ID=account-validation-glue \
-e CLIENT_ID=account-validation-glue \
-e BC_PATH=http://$kieserver \
-e BC_USER_NAME=executionUser \
-e BC_PASSWORD=RedHat

cd ../rtp-creditor-complete-case-glue

oc new-build java8 --name=rtp-creditor-complete-case-glue --binary=true -n rtp-reference
oc start-build rtp-creditor-complete-case-glue --from-file=target/ROOT.jar -n rtp-reference
sleep 30
oc new-app rtp-creditor-complete-case-glue -n rtp-reference \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_PAYMENTS_TOPIC=creditor-completed-payments \
-e CASE_ID_GLUE=rtp-case-list \
-e APPLICATION_ID=complete-glue \
-e CLIENT_ID=complete-glue \
-e BC_PATH=http://$kieserver \
-e BC_USER_NAME=executionUser \
-e BC_PASSWORD=RedHat

cd ../rtp-creditor-fraud-validation-glue

oc new-build java8 --name=rtp-creditor-fraud-validation-glue --binary=true -n rtp-reference
oc start-build rtp-creditor-fraud-validation-glue --from-file=target/ROOT.jar -n rtp-reference
sleep 30
oc new-app rtp-creditor-fraud-validation-glue -n rtp-reference \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_PAYMENTS_TOPIC=creditor-processed-payments \
-e CASE_ID_GLUE=rtp-case-list \
-e APPLICATION_ID=fraud-validation-glue \
-e CLIENT_ID=fraud-validation-glue \
-e BC_PATH=http://$kieserver \
-e BC_USER_NAME=executionUser \
-e BC_PASSWORD=RedHat

cd ../rtp-creditor-payment-received-glue

oc new-build java8 --name=rtp-creditor-payment-received-glue  --binary=true -n rtp-reference
oc start-build rtp-creditor-payment-received-glue  --from-file=target/ROOT.jar -n rtp-reference
sleep 30
oc new-app rtp-creditor-payment-received-glue -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDIT_TRANS_CREDITOR_TOPIC=mock-rtp-creditor-credit-transfer \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=mock-rtp-creditor-credit-transfer \
-e ACKS=1 \
-e BC_HOST=host=executionUser:RedHat@$kieserver

cd ../rtp-creditor-elastic-glue

oc new-build java8 --name=rtp-creditor-elastic-glue --binary=true -n rtp-reference
oc start-build rtp-creditor-elastic-glue --from-file=target/ROOT.jar -n rtp-reference
sleep 30
oc new-app rtp-creditor-elastic-glue -n rtp-reference \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDIT_TRANS_CREDITOR_TOPIC=rtp-case-file-elastic \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=elstic-glue \
-e ACKS=1 \
-e BC_HOST=host=executionUser:RedHat@$kieserver \
-e ELASTIC_URL=$elastic

cd ..
