## Manual Setup

#### OC and Minishift

Follow the Minishift install instructions according to your environment:

https://docs.okd.io/latest/minishift/getting-started/preparing-to-install.html

Install the most recent `oc` binary:

https://github.com/openshift/origin/releases

Ensure that Minishift and `oc` versions are aligned:
```
$ oc version
oc v3.11.0+0cbc58b
kubernetes v1.11.0+d4cacc0
features: Basic-Auth

$ minishift status
Server https://192.168.64.6:8443
kubernetes v1.11.0+d4cacc0
```

#### Start the Minishift VM and Enable Admin User

Start minishift with enough resources:
```
$ minishift start --cpus 4 --disk-size 100GB --memory 12GB
```

NOTE: The above command creates a VM with the above specifications. When no longer using Minishift or the VM, run 'minishift delete' to remove the VM and free up space.

Once the Kubernetes cluster is running, login as admin user:
```
$ oc login -u system:admin
```

Enable the admin user so that you can login to the console as u:admin, p:admin
```
$ minishift addon apply admin-user
```
You should be able to login to the web console with user:admin, pass:admin

Create a new project for the demo:
```
$ oc new-project rtp-reference
```


#### AMQ Streams Cluster and Kafka Topics

Note: Installation of the AMQ Streams Kafka cluster requires an OpenShift user with the cluster-admin role.

Apply the Cluster Operator installation file:
```
$ oc apply -f kafka/install/cluster-operator/deployment-srtimzi-cluster-operator.yaml -n rtp-reference
```

Provision an ephemeral Kafka cluster:
```
$ oc apply -f kafka/install/cluster/kafka-ephemeral.yaml -n rtp-reference
```

Watch the deployment until all Kafka pods are created and running:
```
$ oc get pods -w -n rtp-reference
NAME                                          READY     STATUS    RESTARTS   AGE
my-cluster-entity-operator-5d7cd7774c-x8sg7   3/3       Running   0          33s
my-cluster-kafka-0                            2/2       Running   0          57s
my-cluster-kafka-1                            2/2       Running   0          57s
my-cluster-kafka-2                            2/2       Running   0          57s
my-cluster-zookeeper-0                        2/2       Running   0          1m
my-cluster-zookeeper-1                        2/2       Running   0          1m
my-cluster-zookeeper-2                        2/2       Running   0          1m
strimzi-cluster-operator-56d699b5c5-ch9r2     1/1       Running   0          2m
```

Create the topics for the rtp demo application:
```
$ oc apply -f kafka/install/topics/creditor-completed-payments.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/creditor-payment-confirmation.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/creditor-payments.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/debtor-completed-payments.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/debtor-payment-confirmation.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/debtor-payments.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/mock-rtp-creditor-acknowledgment.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/mock-rtp-creditor-confirmation.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/mock-rtp-creditor-credit-transfer.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/mock-rtp-debtor-confirmation.yaml -n rtp-reference
$ oc apply -f kafka/install/topics/mock-rtp-debtor-credit-transfer.yaml -n rtp-reference
```

Confirm on each Kafka broker that the topics were replicated.
```
$ oc exec -it rtp-demo-cluster-kafka-0 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
creditor-completed-payments
creditor-payment-confirmation
creditor-payments
debtor-completed-payments
debtor-payment-confirmation
debtor-payments
mock-rtp-creditor-acknowledgement
mock-rtp-creditor-confirmation
mock-rtp-creditor-credit-transfer
mock-rtp-debtor-confirmation
mock-rtp-debtor-credit-transfer
```
```
$ oc exec -it rtp-demo-cluster-kafka-1 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
creditor-completed-payments
creditor-payment-confirmation
creditor-payments
debtor-completed-payments
debtor-payment-confirmation
debtor-payments
mock-rtp-creditor-acknowledgement
mock-rtp-creditor-confirmation
mock-rtp-creditor-credit-transfer
mock-rtp-debtor-confirmation
mock-rtp-debtor-credit-transfer
```
```
$ oc exec -it rtp-demo-cluster-kafka-2 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
creditor-completed-payments
creditor-payment-confirmation
creditor-payments
debtor-completed-payments
debtor-payment-confirmation
debtor-payments
mock-rtp-creditor-acknowledgement
mock-rtp-creditor-confirmation
mock-rtp-creditor-credit-transfer
mock-rtp-debtor-confirmation
mock-rtp-debtor-credit-transfer
```


#### JBoss Datagrid Server and Caches

Import the JDG OpenShift image:
```
$ oc import-image -n openshift registry.access.redhat.com/jboss-datagrid-7/datagrid72-openshift --confirm
```

Confirm the image was imported:
```
$ oc get is -n openshift
NAME                   DOCKER REPO                                      TAGS                         UPDATED
datagrid72-openshift   172.30.1.1:5000/openshift/datagrid72-openshift   latest                       41 hours ago
```

Create the JDG server and caches:
```
$ oc new-app --name=rtp-demo-cache \
--image-stream=datagrid72-openshift:latest \
-e INFINISPAN_CONNECTORS=hotrod \
-e CACHE_NAMES=debtorAccountCache,creditorAccountCache \
-e HOTROD_SERVICE_NAME=rtp-demo-cache\
-e HOTROD_AUTHENTICATION=true \
-e USERNAME=jdguser \
-e PASSWORD=P@ssword1
```


#### Install Fuse on the OpenShift Cluster

As an admin user, change to the openshift project:
```
$ oc project openshift
```

Create docker-registry using Red Hat credentials:
```
$ oc create secret docker-registry imagestreamsecret \
  --docker-server=registry.redhat.io \
  --docker-username=CUSTOMER_PORTAL_USERNAME \
  --docker-password=CUSTOMER_PORTAL_PASSWORD \
  --docker-email=EMAIL_ADDRESS
```

Install Fuse on OpenShift image streams and templates:
```
$ BASEURL=https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001

$ oc create -n openshift -f ${BASEURL}/fis-image-streams.json

$ for template in eap-camel-amq-template.json \
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
 oc create -n openshift -f \
 https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001/quickstarts/${template}
 done

$ oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001/fis-console-cluster-template.json

$ oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001/fis-console-namespace-template.json

$ oc create -n openshift -f ${BASEURL}/fuse-apicurito.yml
```

Confirm Fuse images and templates were installed:
```
$ oc get template -n openshift
```

#### Install MySQL Database

Create a new MySQL database from the image:
```
oc new-app \
    -e MYSQL_USER=dbuser \
    -e MYSQL_PASSWORD=dbpass \
    -e MYSQL_DATABASE=rtpdb \
    registry.access.redhat.com/rhscl/mysql-56-rhel7
```

Download and install MySQL workbench:
https://dev.mysql.com/downloads/workbench/

Port forward from OpenShift project to local in order to use MySQL workbench from desktop.
```
$ oc project mysql-demo
$ oc get pods
$ oc port-forward <mysql pod name> 3306:3306
```

In MySQL workbench, create a new connection:
```
hostname: 127.0.0.1
port: 3306
username: dbuser
password: dbpass
default schema: rtpdb
```

Open a connection to the database and run the DDL scripts located in:
```
rtp-debtor-transaction-repository-mysql/src/main/resources/database-scripts
```


#### Deploy the RTP Reference Services

Capture the bootstrap IP for the Kafka cluster and add port 9092:
```
$ bootstrap=`oc get service rtp-demo-cluster-kafka-bootstrap -o=jsonpath='{.spec.clusterIP}{"\n"}'`
$ bootstrap="${bootstrap}:9092"
```

Capture the MySQL database IP and format URL:
```
$ database_url=`oc get service mysql-56-rhel7 -o=jsonpath='{.spec.clusterIP}{"\n"}'`
$ database_url="jdbc:mysql://${database_url}:3306/rtpdb"
```

Build the dependency projects:
```
$ cd rtp-message-model
$ mvn clean install
$ cd ..
$ cd rtp-debtor-domain-model
$ mvn clean install
$ cd ..
$ cd rtp-debtor-account-repository
$ mvn clean install
$ cd ..
$ cd rtp-debtor-account-repository-jdg
$ mvn clean install
$ cd ..
$ cd rtp-debtor-transaction-repository
$ mvn clean install
$ cd ..
$ cd rtp-debtor-transaction-repository-mysql
$ mvn clean install
$ cd ..
$ cd rtp-creditor-domain-model
$ mvn clean install
$ cd ..
$ cd rtp-creditor-validation-model
$ mvn clean install
$ cd ..
$ cd rtp-creditor-account-repository
$ mvn clean install
$ cd ..
$ cd rtp-creditor-account-repository-jdg
$ mvn clean install
$ cd ..
```

Build, configure and deploy the Debtor Payment Service:
```
$ cd rtp-debtor-payment-service
$ oc create configmap rtp-debtor-payment-service-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=PRODUCER_TOPIC=debtor-payments \
            --from-literal=SECURITY_PROTOCOL=PLAINTEXT \
            --from-literal=SERIALIZER_CLASS=rtp.demo.debtor.domain.model.payment.serde.PaymentSerializer \
            --from-literal=ACKS=1 \
            --from-literal=DATABASE_URL="${database_url}" \
            --from-literal=DATABASE_USER=dbuser \
            --from-literal=DATABASE_PASS=dbpass
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-debtor-payment-service --from configmap/rtp-debtor-payment-service-config
$ cd ..
```

Build, configure and deploy the Debtor Send Payment Service
```
$ cd rtp-debtor-send-payment
$ oc create configmap rtp-debtor-send-payment-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=DEBTOR_PAYMENTS_TOPIC=debtor-payments \
            --from-literal=MOCK_RTP_CREDIT_TRANSFER_TOPIC=mock-rtp-debtor-credit-transfer \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-debtor-send-payment \
            --from-literal=DESERIALIZER_CLASS=rtp.demo.debtor.domain.model.payment.serde.PaymentDeserializer \
            --from-literal=SERIALIZER_CLASS=rtp.message.model.serde.FIToFICustomerCreditTransferV06Serializer \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-debtor-send-payment --from configmap/rtp-debtor-send-payment-config
$ cd ..
```

Build, configure and deploy the Mock RTP Service

```
$ cd rtp-mock
$ oc create configmap rtp-mock-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDIT_TRANS_DEBTOR_TOPIC=mock-rtp-debtor-credit-transfer \
            --from-literal=CREDIT_TRANS_CREDITOR_TOPIC=mock-rtp-creditor-credit-transfer \
            --from-literal=CREDITOR_ACK_TOPIC=mock-rtp-creditor-acknowledgement \
            --from-literal=DEBTOR_CONFIRMATION_TOPIC=mock-rtp-debtor-confirmation \
            --from-literal=CREDITOR_CONFIRMATION_TOPIC=mock-rtp-creditor-confirmation \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-mock \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-demo-mock --from configmap/rtp-mock-config
$ cd ..
```

Build, configure and deploy the Creditor Receive Payment Service

```
$ cd rtp-creditor-receive-payment
$ oc create configmap rtp-creditor-receive-payment-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDIT_TRANS_CREDITOR_TOPIC=mock-rtp-creditor-credit-transfer \
            --from-literal=CREDITOR_PAYMENTS_TOPIC=creditor-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-receive-payment \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-creditor-receive-payment --from configmap/rtp-creditor-receive-payment-config
$ cd ..
```

Note: The rtp-creditor-receive-payment service may deploy with the below warning, but this does not impact the application:

```
09:18:00.332 [XNIO-2 task-1] WARN  i.f.s.c.kubernetes.StandardPodUtils - Failed to get pod with name:[rtp-creditor-receive-payment-30-kc8m6]. You should look into this if things aren't working as you expect. Are you missing serviceaccount permissions?
io.fabric8.kubernetes.client.KubernetesClientException: Failure executing: GET at: https://kubernetes.default.svc/api/v1/namespaces/rtp-reference/pods/rtp-creditor-receive-payment-30-kc8m6 . Message: Forbidden!Configured service account doesn't have access. Service account may have been revoked. pods "rtp-creditor-receive-payment-30-kc8m6" is forbidden: User "system:serviceaccount:rtp-reference:default" cannot get pods in the namespace "rtp-reference": no RBAC policy matched.
```

Build, configure and deploy the Creditor Payment Acknowledgment Service

```
$ cd rtp-creditor-payment-acknowledgement
$ oc create configmap rtp-creditor-payment-acknowledgement-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDITOR_PAYMENTS_TOPIC=creditor-payments \
            --from-literal=MOCK_RTP_CREDITOR_ACK_TOPIC=mock-rtp-creditor-acknowledgement \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-payment-acknowledgement \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-creditor-payment-acknowledgement --from configmap/rtp-creditor-payment-acknowledgement-config
$ cd ..
```

Build, configure and deploy the Creditor Payment Confirmation Service

```
$ cd rtp-creditor-payment-confirmation
$ oc create configmap rtp-creditor-payment-confirmation-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDITOR_CONFIRMATION_TOPIC=creditor-payment-confirmation \
            --from-literal=MOCK_RTP_CREDITOR_CONFIRMATION_TOPIC=mock-rtp-creditor-confirmation \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-payment-confirmation \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-creditor-payment-confirmation --from configmap/rtp-creditor-payment-confirmation-config
$ cd ..
```

Build, configure and deploy the Creditor Complete Payment Service

```
$ cd rtp-creditor-complete-payment
$ oc create configmap rtp-creditor-complete-payment-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
            --from-literal=CREDITOR_PAYMENTS_TOPIC=creditor-payments \
            --from-literal=CREDITOR_CONFIRMATION_TOPIC=creditor-payment-confirmation \
            --from-literal=APPLICATION_ID=creditor-complete-payment \
            --from-literal=CLIENT_ID=creditor-complete-payment-client \
            --from-literal=DATABASE_URL="${database_url}" \
            --from-literal=DATABASE_USER=dbuser \
            --from-literal=DATABASE_PASS=dbpass
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-creditor-complete-payment --from configmap/rtp-creditor-complete-payment-config
$ cd ..
```

Build, configure and deploy the Creditor Customer Notification Service

```
$ cd rtp-creditor-customer-notification
$ oc create configmap rtp-creditor-customer-notification-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-customer-notification \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-creditor-customer-notification --from configmap/rtp-creditor-customer-notification-config
$ cd ..
```

Build, configure and deploy the Creditor Customer Core Banking Service

```
$ cd rtp-creditor-core-banking
$ oc create configmap rtp-creditor-core-banking-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-core-banking \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-creditor-core-banking --from configmap/rtp-creditor-core-banking-config
$ cd ..
```

Build, configure and deploy the Creditor Payment Auditing Service

```
$ cd rtp-creditor-auditing
$ oc create configmap rtp-creditor-auditing-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=creditor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-creditor-auditing \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-creditor-auditing --from configmap/rtp-creditor-auditing-config
$ cd ..
```

Build, configure and deploy the Debtor Payment Confirmation Service

```
$ cd rtp-debtor-payment-confirmation
$ oc create configmap rtp-debtor-payment-confirmation-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=DEBTOR_CONFIRMATION_TOPIC=debtor-payment-confirmation \
            --from-literal=MOCK_RTP_DEBTOR_CONFIRMATION_TOPIC=mock-rtp-debtor-confirmation \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-debtor-payment-confirmation \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-debtor-payment-confirmation --from configmap/rtp-debtor-payment-confirmation-config
$ cd ..
```

Build, configure and deploy the Debtor Complete Payment Service

```
$ cd rtp-debtor-complete-payment
$ oc create configmap rtp-debtor-complete-payment-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=DEBTOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
            --from-literal=DEBTOR_PAYMENTS_TOPIC=debtor-payments \
            --from-literal=DEBTOR_CONFIRMATION_TOPIC=debtor-payment-confirmation \
            --from-literal=APPLICATION_ID=debtor-complete-payment \
            --from-literal=CLIENT_ID=debtor-complete-payment-client \
            --from-literal=DATABASE_URL="${database_url}" \
            --from-literal=DATABASE_USER=dbuser \
            --from-literal=DATABASE_PASS=dbpass
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-debtor-complete-payment --from configmap/rtp-debtor-complete-payment-config
$ cd ..
```

Build, configure and deploy the Debtor Customer Notification Service

```
$ cd rtp-debtor-customer-notification
$ oc create configmap rtp-debtor-customer-notification-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-debtor-customer-notification \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-debtor-customer-notification --from configmap/rtp-debtor-customer-notification-config
$ cd ..
```

Build, configure and deploy the Debtor Customer Core Banking Service

```
$ cd rtp-debtor-core-banking
$ oc create configmap rtp-debtor-core-banking-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-debtor-core-banking \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-debtor-core-banking --from configmap/rtp-debtor-core-banking-config
$ cd ..
```

Build, configure and deploy the Debtor Payment Auditing Service

```
$ cd rtp-debtor-auditing
$ oc create configmap rtp-debtor-auditing-config \
            --from-literal=BOOTSTRAP_SERVERS="${bootstrap}" \
            --from-literal=CREDITOR_COMPLETED_PAYMENTS_TOPIC=debtor-completed-payments \
            --from-literal=CONSUMER_MAX_POLL_RECORDS=500 \
            --from-literal=CONSUMER_COUNT=1 \
            --from-literal=CONSUMER_SEEK_TO=end \
            --from-literal=CONSUMER_GROUP=rtp-debtor-auditing \
            --from-literal=ACKS=1
$ mvn fabric8:deploy -Popenshift
$ oc set env dc/rtp-debtor-auditing --from configmap/rtp-debtor-auditing-config
$ cd ..
```



## Running the Reference Application

Find the URL for the exposed route to the Debtor Payment Service:

```
$ oc get routes | grep rtp-debtor-payment-service
rtp-debtor-payment-service     rtp-debtor-payment-service-rtp-reference.192.168.64.8.nip.io               rtp-debtor-payment-service     8080                      None
```

Using a rest client, POST the following request body to the Debtor Payment Service URL:
```
{
  "payments":[
    {
  		"senderAccountNumber":"12000194212199000",
  		"amount":"100.25",
  		"receiverFirstName":"Amy",
  		"receiverLastName":"Lopez",
      "receiverEmail":"alopez@company.com",
      "receiverCellPhone":null
	  }
  ]
}
```






## Resources

https://access.redhat.com/documentation/en-us/red_hat_amq/7.2/html-single/using_amq_streams_on_openshift_container_platform/
https://access.redhat.com/documentation/en-us/red_hat_fuse/7.2/html-single/fuse_on_openshift_guide/
https://access.redhat.com/documentation/en-us/red_hat_jboss_data_grid/7.2/html-single/data_grid_for_openshift/
https://access.redhat.com/documentation/en-us/reference_architectures/2018/html-single/vert.x_microservices_on_red_hat_openshift_container_platform_3/
