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
