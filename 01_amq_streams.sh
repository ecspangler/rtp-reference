# --- AMQ Streams Cluster and Kafka Topics
oc apply -f kafka/install/cluster-operator -n rtp-reference
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

oc exec -it rtp-demo-cluster-kafka-0 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
oc exec -it rtp-demo-cluster-kafka-1 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list
oc exec -it rtp-demo-cluster-kafka-2 -c kafka -- bin/kafka-topics.sh --zookeeper localhost:2181 --list


