#!/usr/bin/env bash

posix_read() {
    # `read -s` is not POSIX compliant, so we use `stty -echo` instead.
    stty -echo
    trap 'stty echo' EXIT
    printf "$1"
    eval "read $2"
    stty echo
    trap - EXIT
    printf "\n"

}

printf "Enter your RHN credentials (these will be used later):\n"
read -p "Username: " username
posix_read "Password: " password
read -p "Email: " email


# --- Setup
minishift start --cpus 4 --disk-size 100GB --memory 12GB
oc login -u system:admin
minishift addon apply admin-user
oc new-project rtp-reference

echo '
apiVersion: rbac.authorization.k8s.io/v1beta1
kind: RoleBinding
metadata:
  name: default-view
  namespace: rtp-reference
roleRef:
  apiGroup: rbac.authorization.k8s.io
  kind: ClusterRole
  name: edit
subjects:
- kind: ServiceAccount
  name: default
  namespace: rtp-reference
' | oc apply -f -


# --- AMQ Streams Cluter and Kafka Topics
oc apply -f kafka/install/cluster-operator/deployment-srtimzi-cluster-operator.yaml -n rtp-reference
oc apply -f kafka/install/cluster/kafka-ephemeral.yaml -n rtp-reference
oc get pods -w -n rtp-reference &
cpid=$!
trap "kill $cpid" EXIT

until [ "$(oc get pods --selector strimzi.io/name=rtp-demo-cluster-kafka -o jsonpath="{.items[:3].status.containerStatuses[?(@.name == \"kafka\")].ready}" 2>/dev/null)" = "true true true" ]; do sleep 1; done
until [ "$(oc get pods --selector strimzi.io/name=rtp-demo-cluster-zookeeper -o jsonpath="{.items[:3].status.containerStatuses[?(@.name == \"zookeeper\")].ready}" 2>/dev/null)" = "true true true" ]; do sleep 1; done
until [ "$(oc get pods --selector strimzi.io/name=rtp-demo-cluster-entity-operator -o jsonpath="{.items[0].status.containerStatuses[:3].ready}" 2>/dev/null)" = "true true true" ]; do sleep 1; done

kill $cpid
trap - EXIT

oc apply -f kafka/install/topics/creditor-completed-payments.yaml -n rtp-reference
oc apply -f kafka/install/topics/creditor-payment-confirmation.yaml -n rtp-reference
oc apply -f kafka/install/topics/creditor-payments.yaml -n rtp-reference
oc apply -f kafka/install/topics/debtor-completed-payments.yaml -n rtp-reference
oc apply -f kafka/install/topics/debtor-payment-confirmation.yaml -n rtp-reference
oc apply -f kafka/install/topics/debtor-payments.yaml -n rtp-reference
oc apply -f kafka/install/topics/mock-rtp-creditor-acknowledgement.yaml -n rtp-reference
oc apply -f kafka/install/topics/mock-rtp-creditor-confirmation.yaml -n rtp-reference
oc apply -f kafka/install/topics/mock-rtp-creditor-credit-transfer.yaml -n rtp-reference
oc apply -f kafka/install/topics/mock-rtp-debtor-confirmation.yaml -n rtp-reference
oc apply -f kafka/install/topics/mock-rtp-debtor-credit-transfer.yaml -n rtp-reference

oc exec -it rtp-demo-cluster-kafka-0 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
oc exec -it rtp-demo-cluster-kafka-1 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
oc exec -it rtp-demo-cluster-kafka-2 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list


# --- JBoss Datagrid Server and Caches
oc import-image -n openshift registry.access.redhat.com/jboss-datagrid-7/datagrid72-openshift --confirm
oc get is -n openshift
oc new-app --name=rtp-demo-cache \
--image-stream=datagrid72-openshift:latest \
-e INFINISPAN_CONNECTORS=hotrod \
-e CACHE_NAMES=debtorAccountCache,creditorAccountCache \
-e HOTROD_SERVICE_NAME=rtp-demo-cache\
-e HOTROD_AUTHENTICATION=true \
-e USERNAME=jdguser \
-e PASSWORD=P@ssword1


# --- Install Fuse on the OpenShift Cluster
oc project openshift

oc create secret docker-registry imagestreamsecret \
  --docker-server=registry.redhat.io \
  --docker-username=$username \
  --docker-password=\'"$password"\' \
  --docker-email=$email

BASEURL=https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001
oc create -n openshift -f ${BASEURL}/fis-image-streams.json

for template in eap-camel-amq-template.json \
    eap-camel-cdi-template.json \
    eap-camel-cxf-jaxrs-template.json \
    eap-camel-cxf-jaxws-template.json \
    eap-camel-jpa-template.json \
    karaf-camel-amq-template.json \
    karaf-camel-log-template.json \
    karaf-camel-rest-sql-template.json \
    karaf-cxf-rest-template.json \
    spring-boot-camel-amq-template.json \
    spring-boot-camel-config-template.json \
    spring-boot-camel-drools-template.json \
    spring-boot-camel-infinispan-template.json \
    spring-boot-camel-rest-sql-template.json \
    spring-boot-camel-teiid-template.json \
    spring-boot-camel-template.json \
    spring-boot-camel-xa-template.json \
    spring-boot-camel-xml-template.json \
    spring-boot-cxf-jaxrs-template.json \
    spring-boot-cxf-jaxws-template.json ;
do
    oc create -n openshift -f ${BASEURL}/quickstarts/${template}
done

oc create -n openshift -f ${BASEURL}/fis-console-cluster-template.json
oc create -n openshift -f ${BASEURL}/fis-console-namespace-template.json
oc create -n openshift -f ${BASEURL}/fuse-apicurito.yml

oc get template -n openshift


# --- MySQL Installation
oc project rtp-reference
oc new-app \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=dbpass \
    -e MYSQL_DATABASE=rtpdb \
    --name=mysql-56-rhel7 \
    registry.access.redhat.com/rhscl/mysql-56-rhel7

# IntelliJ may THINK that this isn't formatted correctly, but don't worry--it is.
until [ "$(oc get pods --selector app=mysql-56-rhel7 -o jsonpath="{.items[0].status.containerStatuses[?(@.name == \"mysql-56-rhel7\")].ready}" 2> /dev/null)" = "true" ]; do sleep 3; printf "Waiting until container is ready...\n"; done

oc port-forward $(oc get pods --selector app=mysql-56-rhel7 -o jsonpath="{.items[0].metadata.name}") 3306 &> /dev/null &
cpid=$!
trap "kill $cpid" EXIT
until mysql --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass --execute exit &> /dev/null; do sleep 3; printf "Waiting until MySQL comes up...\n"; done

printf "Loading create_debtor_credit_payment.sql\n"
mysql -w --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass < ./rtp-debtor-transaction-repository-mysql/src/main/resources/database-scripts/create_debtor_credit_payment.sql
printf "Loading create_debtor_debit_payment.sql\n"
mysql --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass < ./rtp-debtor-transaction-repository-mysql/src/main/resources/database-scripts/create_debtor_debit_payment.sql
kill $cpid
trap - EXIT


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
    rtp-creditor-account-repository \
    rtp-creditor-account-repository-jdg ;
do
    printf "Building $dependency\n"
    cd $dependency
    mvn clean install
    cd ..
done

cd rtp-debtor-payment-service
mvn clean fabric8:deploy -Popenshift
cd ..

cd rtp-debtor-send-payment
mvn clean fabric8:deploy -Popenshift
cd ..

cd rtp-mock
mvn clean fabric8:deploy -Popenshift
cd ..

cd rtp-creditor-receive-payment
mvn clean fabric8:deploy -Popenshift
cd ..

cd rtp-creditor-payment-acknowledgement
oc create configmap rtp-creditor-payment-acknowledgement-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=CREDITOR_PAYMENTS_TOPIC=creditor-payments \
            --from-literal=MOCK_RTP_CREDITOR_ACK_TOPIC=mock-rtp-creditor-acknowledgement \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-payment-acknowledgement \
            --from-literal=ACKS=1
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-creditor-payment-acknowledgement --from configmap/rtp-creditor-payment-acknowledgement-config
cd ..

cd rtp-creditor-payment-confirmation
oc create configmap rtp-creditor-payment-confirmation-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=CREDITOR_CONFIRMATION_TOPIC=creditor-payment-confirmation \
            --from-literal=MOCK_RTP_CREDITOR_CONFIRMATION_TOPIC=mock-rtp-creditor-confirmation \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-payment-confirmation \
            --from-literal=ACKS=1
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-creditor-payment-confirmation --from configmap/rtp-creditor-payment-confirmation-config
cd ..

cd rtp-creditor-complete-payment
oc create configmap rtp-creditor-complete-payment-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
            --from-literal=CREDITOR_PAYMENTS_TOPIC=creditor-payments \
            --from-literal=CREDITOR_CONFIRMATION_TOPIC=creditor-payment-confirmation \
            --from-literal=APPLICATION_ID=creditor-complete-payment \
            --from-literal=CLIENT_ID=creditor-complete-payment-client \
            --from-literal=DATABASE_URL="jdbc:mysql://mysql-56-rhel7:3306/rtpdb" \
            --from-literal=DATABASE_USER=dbuser \
            --from-literal=DATABASE_PASS=dbpass
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-creditor-complete-payment --from configmap/rtp-creditor-complete-payment-config
cd ..

cd rtp-creditor-customer-notification
oc create configmap rtp-creditor-customer-notification-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-customer-notification \
            --from-literal=ACKS=1
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-creditor-customer-notification --from configmap/rtp-creditor-customer-notification-config
cd ..

cd rtp-creditor-core-banking
oc create configmap rtp-creditor-core-banking-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-core-banking \
            --from-literal=ACKS=1
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-creditor-core-banking --from configmap/rtp-creditor-core-banking-config
cd ..

cd rtp-creditor-auditing
oc create configmap rtp-creditor-auditing-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-auditing \
            --from-literal=ACKS=1
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-creditor-auditing --from configmap/rtp-creditor-auditing-config
cd ..

cd rtp-debtor-payment-confirmation
oc create configmap rtp-debtor-payment-confirmation-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=DEBTOR_CONFIRMATION_TOPIC=debtor-payment-confirmation \
            --from-literal=MOCK_RTP_DEBTOR_CONFIRMATION_TOPIC=mock-rtp-debtor-confirmation \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-debtor-payment-confirmation \
            --from-literal=ACKS=1
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-debtor-payment-confirmation --from configmap/rtp-debtor-payment-confirmation-config
cd ..

cd rtp-debtor-complete-payment
oc create configmap rtp-debtor-complete-payment-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=DEBTOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
            --from-literal=DEBTOR_PAYMENTS_TOPIC=debtor-payments \
            --from-literal=DEBTOR_CONFIRMATION_TOPIC=debtor-payment-confirmation \
            --from-literal=APPLICATION_ID=debtor-complete-payment \
            --from-literal=CLIENT_ID=debtor-complete-payment-client \
            --from-literal=DATABASE_URL="jdbc:mysql://mysql-56-rhel7:3306/rtpdb" \
            --from-literal=DATABASE_USER=dbuser \
            --from-literal=DATABASE_PASS=dbpass
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-debtor-complete-payment --from configmap/rtp-debtor-complete-payment-config
cd ..

cd rtp-debtor-customer-notification
oc create configmap rtp-debtor-customer-notification-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=DEBTOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-debtor-customer-notification \
            --from-literal=ACKS=1
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-debtor-customer-notification --from configmap/rtp-debtor-customer-notification-config
cd ..

cd rtp-debtor-core-banking
oc create configmap rtp-debtor-core-banking-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=DEBTOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-debtor-core-banking \
            --from-literal=ACKS=1
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-debtor-core-banking --from configmap/rtp-debtor-core-banking-config
cd ..

cd rtp-debtor-auditing
oc create configmap rtp-debtor-auditing-config \
            --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092" \
            --from-literal=DEBTOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-debtor-auditing \
            --from-literal=ACKS=1
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-debtor-auditing --from configmap/rtp-debtor-auditing-config
cd ..


# --- Deploy the visualizer gateway
cd rtp-flow-viz-service
oc create configmap rtp-flow-viz-service-config --from-literal=BOOTSTRAP_SERVERS="rtp-demo-cluster-kafka-bootstrap:9092"
mvn fabric8:deploy -Popenshift
oc set env dc/rtp-flow-viz-service --from configmap/rtp-flow-viz-service-config
cd ..


# --- Deploy the web apps
