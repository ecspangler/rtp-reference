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


# --- AMQ Streams Cluster and Kafka Topics
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

shopt -s nullglob
for topic in kafka/install/topics/*.y{a,}ml; do
    oc apply -f $topic
done
shopt -u nullglob

#oc exec -it rtp-demo-cluster-kafka-0 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
#oc exec -it rtp-demo-cluster-kafka-1 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
#oc exec -it rtp-demo-cluster-kafka-2 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list

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


# --- JBoss Datagrid Server and Caches
oc project rtp-reference
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


# --- Create an instance of the Confluent Kafka REST Proxy for the Kafka Topics UI
oc project rtp-reference
oc new-app \
    -e KAFKA_REST_BOOTSTRAP_SERVERS=rtp-demo-cluster-kafka-bootstrap:9092 \
    -e KAFKA_REST_HOST_NAME=cp-kafka-rest \
    -e KAFKA_REST_LISTENERS="http://0.0.0.0:8082" \
    --name=cp-kafka-rest \
    confluentinc/cp-kafka-rest
oc expose svc/cp-kafka-rest

# --- Create an instance of the Kafka Topics UI
oc project rtp-reference
oc new-app \
    -e KAFKA_REST_PROXY_URL=http://cp-kafka-rest:8082 \
    -e PROXY=true \
    --name=kafka-ui \
    landoop/kafka-topics-ui
oc expose svc/kafka-ui

# --- MySQL Installation
oc project rtp-reference
oc new-app \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=dbpass \
    -e MYSQL_DATABASE=rtpdb \
    --name=mysql-56-rhel7 \
    registry.access.redhat.com/rhscl/mysql-56-rhel7

until [ "$(oc get pods --selector app=mysql-56-rhel7 -o jsonpath="{.items[0].status.containerStatuses[?(@.name == \"mysql-56-rhel7\")].ready}" 2> /dev/null)" = "true" ]; do sleep 3; printf "Waiting until container is ready...\n"; done

oc port-forward $(oc get pods --selector app=mysql-56-rhel7 -o jsonpath="{.items[0].metadata.name}") 3306 &> /dev/null &
cpid=$!
trap "kill $cpid" EXIT
until mysql --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass --execute exit &> /dev/null; do sleep 3; printf "Waiting until MySQL comes up...\n"; done

printf "Loading create_debtor_credit_payment.sql\n"
mysql -w --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass < ./rtp-debtor-transaction-repository-mysql/src/main/resources/database-scripts/create_debtor_credit_payment.sql
printf "Loading create_debtor_debit_payment.sql\n"
mysql --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass < ./rtp-debtor-transaction-repository-mysql/src/main/resources/database-scripts/create_debtor_debit_payment.sql
printf "Loading create_creditor_debit_payment.sql\n"
mysql --host localhost -P 3306 --protocol tcp -u dbuser -D rtpdb -pdbpass < ./rtp-creditor-transaction-repository-mysql/src/main/resources/database-scripts/create_creditor_credit_payment.sql
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
    rtp-creditor-account-repository-jdg \
    rtp-creditor-transaction-repository \
    rtp-creditor-transaction-repository-mysql;
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
    rtp-debtor-auditing \
    rtp-debtor-complete-payment \
    rtp-debtor-core-banking \
    rtp-debtor-customer-notification \
    rtp-debtor-payment-confirmation \
    rtp-debtor-payment-service \
    rtp-debtor-send-payment \
    rtp-flow-viz-service \
    rtp-mock
do
    printf "Deploying $service\n"
    cd $service
    mvn clean fabric8:deploy
    cd ..
done
