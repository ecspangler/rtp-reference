#!/usr/bin/env bash

oc import-image java8 --from=registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift -n openshift --confirm

# --- Deploy the RTP Reference Services
mvn --non-recursive clean install
for dependency in \
    rtp-message-model \
    rtp-debtor-domain-model \
    rtp-debtor-account-repository \
    rtp-debtor-account-repository-jdg \
    rtp-debtor-transaction-repository \
    rtp-debtor-transaction-repository-mysql \
    rtp-creditor-domain-model \
    rtp-creditor-validation-model \
    rtp-creditor-rules \
    rtp-creditor-account-repository \
    rtp-creditor-account-repository-jdg \
    rtp-creditor-transaction-repository \
    rtp-creditor-transaction-repository-mysql
do
    printf "Building $dependency\n"
    cd $dependency
    mvn clean install
    cd ..
done


for service in \
    rtp-creditor-auditing \
    rtp-creditor-complete-payment \
    rtp-creditor-core-banking \
    rtp-creditor-customer-notification \
    rtp-creditor-payment-acknowledgement \
    rtp-creditor-payment-confirmation \
    rtp-creditor-payment-service \
    rtp-creditor-receive-payment \
    rtp-creditor-fraud-detection \
    rtp-debtor-auditing \
    rtp-debtor-complete-payment \
    rtp-debtor-core-banking \
    rtp-debtor-customer-notification \
    rtp-debtor-payment-confirmation \
    rtp-debtor-payment-service \
    rtp-debtor-send-payment \
    rtp-mock
do
    printf "Maven building $service\n"
    cd $service
    mvn clean install
    cd ..
done


cd rtp-debtor-payment-service

oc new-build java8 --name=rtp-debtor-payment-service --binary=true -n rtp-reference
oc start-build rtp-debtor-payment-service --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-debtor-payment-service -n rtp-reference \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e PRODUCER_TOPIC=debtor-payments \
-e SECURITY_PROTOCOL=PLAINTEXT \
-e SERIALIZER_CLASS=rtp.demo.debtor.domain.model.payment.serde.PaymentSerializer \
-e ACKS=1 \
-e DATABASE_URL="jdbc:mysql://mysql-56-rhel7:3306/rtpdb?autoReconnect=true" \
-e DATABASE_USER=dbuser \
-e DATABASE_PASS=dbpass \
-e JDG_HOST=rtp-demo-cache \
-e JDG_PORT=11222 \
-e JDG_USER=jdguser \
-e JDG_PASS=P@ssword1 \
-e JDG_DEBTOR_ACCT_CACHE_NAME=debtorAccountCache

oc expose svc/rtp-debtor-payment-service

cd ../rtp-debtor-send-payment

oc new-build java8 --name=rtp-debtor-send-payment --binary=true -n rtp-reference
oc start-build rtp-debtor-send-payment --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-debtor-send-payment -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e DEBTOR_PAYMENTS_TOPIC=debtor-payments \
-e MOCK_RTP_CREDIT_TRANSFER_TOPIC=mock-rtp-debtor-credit-transfer \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-debtor-send-payment \
-e ACKS=1

cd ../rtp-creditor-auditing

oc new-build java8 --name=rtp-creditor-auditing --binary=true -n rtp-reference
oc start-build rtp-creditor-auditing --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-creditor-auditing -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-creditor-auditing \
-e ACKS=1

cd ../rtp-creditor-complete-payment

oc new-build java8 --name=rtp-creditor-complete-payment --binary=true -n rtp-reference
oc start-build rtp-creditor-complete-payment --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-creditor-complete-payment -n rtp-reference \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
-e CREDITOR_PAYMENTS_TOPIC=creditor-processed-payments \
-e CREDITOR_CONFIRMATION_TOPIC=creditor-payment-confirmation \
-e APPLICATION_ID=creditor-complete-payment \
-e CLIENT_ID=creditor-complete-payment-client \
-e DATABASE_URL="jdbc:mysql://mysql-56-rhel7:3306/rtpdb?autoReconnect=true" \
-e DATABASE_USER=dbuser \
-e DATABASE_PASS=dbpass

cd ../rtp-creditor-core-banking

oc new-build java8 --name=rtp-creditor-core-banking --binary=true -n rtp-reference
oc start-build rtp-creditor-core-banking --from-file=target/ROOT.jar -n rtp-reference
sleep 30
oc new-app rtp-creditor-core-banking -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-creditor-core-banking \
-e ACKS=1

cd ../rtp-creditor-customer-notification

oc new-build java8 --name=rtp-creditor-customer-notification --binary=true -n rtp-reference
oc start-build rtp-creditor-customer-notification --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-creditor-customer-notification -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-creditor-customer-notification \
-e ACKS=1

cd ../rtp-creditor-payment-acknowledgement

oc new-build java8 --name=rtp-creditor-payment-acknowledgement --binary=true -n rtp-reference
oc start-build rtp-creditor-payment-acknowledgement --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-creditor-payment-acknowledgement -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_PAYMENTS_TOPIC=creditor-processed-payments \
-e MOCK_RTP_CREDITOR_ACK_TOPIC=mock-rtp-creditor-acknowledgement \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-creditor-payment-acknowledgement \
-e ACKS=1

cd ../rtp-creditor-payment-confirmation

oc new-build java8 --name=rtp-creditor-payment-confirmation --binary=true -n rtp-reference
oc start-build rtp-creditor-payment-confirmation --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-creditor-payment-confirmation -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_CONFIRMATION_TOPIC=creditor-payment-confirmation \
-e MOCK_RTP_CREDITOR_CONFIRMATION_TOPIC=mock-rtp-creditor-confirmation \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-creditor-payment-confirmation \
-e ACKS=1

cd ../rtp-creditor-payment-service

oc new-build java8 --name=rtp-creditor-payment-service --binary=true -n rtp-reference
oc start-build rtp-creditor-payment-service --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-creditor-payment-service -n rtp-reference \
-e DATABASE_URL="jdbc:mysql://mysql-56-rhel7:3306/rtpdb?autoReconnect=true" \
-e DATABASE_USER=dbuser \
-e DATABASE_PASS=dbpass

oc expose svc/rtp-creditor-payment-service

cd ../rtp-creditor-receive-payment

oc new-build java8 --name=rtp-creditor-receive-payment --binary=true -n rtp-reference
oc start-build rtp-creditor-receive-payment --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-creditor-receive-payment -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDIT_TRANS_CREDITOR_TOPIC=mock-rtp-creditor-credit-transfer \
-e CREDITOR_PAYMENTS_TOPIC=creditor-payments \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-creditor-receive-payment \
-e ACKS=1 \
-e DATABASE_URL="jdbc:mysql://mysql-56-rhel7:3306/rtpdb?autoReconnect=true" \
-e DATABASE_USER=dbuser \
-e DATABASE_PASS=dbpass

cd ../rtp-creditor-fraud-detection

oc new-build java8 --name=rtp-creditor-fraud-detection --binary=true -n rtp-reference
oc start-build rtp-creditor-fraud-detection --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-creditor-fraud-detection -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDITOR_PAYMENTS_TOPIC=creditor-payments \
-e CREDITOR_PROCESSED_PAYMENTS_TOPIC=creditor-processed-payments \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-creditor-fraud-detection \
-e ACKS=1

cd ../rtp-debtor-auditing

oc new-build java8 --name=rtp-debtor-auditing --binary=true -n rtp-reference
oc start-build rtp-debtor-auditing --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-debtor-auditing -n rtp-reference  \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e DEBTOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-debtor-auditing \
-e ACKS=1

cd ../rtp-debtor-complete-payment

oc new-build java8 --name=rtp-debtor-complete-payment --binary=true -n rtp-reference
oc start-build rtp-debtor-complete-payment --from-file=target/ROOT.jar -n rtp-reference
sleep 30
oc new-app rtp-debtor-complete-payment -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e DEBTOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
-e DEBTOR_PAYMENTS_TOPIC=debtor-payments \
-e DEBTOR_CONFIRMATION_TOPIC=debtor-payment-confirmation \
-e APPLICATION_ID=debtor-complete-payment \
-e CLIENT_ID=debtor-complete-payment-client \
-e DATABASE_URL="jdbc:mysql://mysql-56-rhel7:3306/rtpdb?autoReconnect=true" \
-e DATABASE_USER=dbuser \
-e DATABASE_PASS=dbpass

cd ../rtp-debtor-core-banking

oc new-build java8 --name=rtp-debtor-core-banking --binary=true -n rtp-reference
oc start-build rtp-debtor-core-banking --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-debtor-core-banking -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e DEBTOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-debtor-core-banking \
-e ACKS=1

cd ../rtp-debtor-customer-notification

oc new-build java8 --name=rtp-debtor-customer-notification --binary=true -n rtp-reference
oc start-build rtp-debtor-customer-notification --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-debtor-customer-notification -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e DEBTOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-debtor-customer-notification \
-e ACKS=1

cd ../rtp-debtor-payment-confirmation

oc new-build java8 --name=rtp-debtor-payment-confirmation --binary=true -n rtp-reference
oc start-build rtp-debtor-payment-confirmation --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-debtor-payment-confirmation -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e DEBTOR_CONFIRMATION_TOPIC=debtor-payment-confirmation \
-e MOCK_RTP_DEBTOR_CONFIRMATION_TOPIC=mock-rtp-debtor-confirmation \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-debtor-payment-confirmation \
-e ACKS=1

cd ../rtp-mock

oc new-build java8 --name=rtp-mock --binary=true -n rtp-reference
oc start-build rtp-mock --from-file=target/ROOT.jar -n rtp-reference
sleep 40
oc new-app rtp-mock -n rtp-reference \
-e SPRING_APPLICATION_JSON='{"server":{"undertow":{"io-threads":1, "worker-threads":2 }}}' \
-e BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
-e CREDIT_TRANS_DEBTOR_TOPIC=mock-rtp-debtor-credit-transfer \
-e CREDIT_TRANS_CREDITOR_TOPIC=mock-rtp-creditor-credit-transfer \
-e CREDITOR_ACK_TOPIC=mock-rtp-creditor-acknowledgement \
-e DEBTOR_CONFIRMATION_TOPIC=mock-rtp-debtor-confirmation \
-e CREDITOR_CONFIRMATION_TOPIC=mock-rtp-creditor-confirmation \
-e CONSUMER_MAX_POLL_RECORDS=500 \
-e CONSUMER_COUNT=1 \
-e CONSUMER_SEEK_TO=end \
-e CONSUMER_GROUP=rtp-mock \
-e ACKS=1

cd ..
