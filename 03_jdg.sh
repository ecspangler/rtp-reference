# --- JBoss Datagrid Server and Caches
oc project rtp-reference
oc import-image -n openshift registry.access.redhat.com/jboss-datagrid-7/datagrid73-openshift --confirm
oc new-app --name=rtp-demo-cache \
--image-stream=datagrid73-openshift:latest \
-e INFINISPAN_CONNECTORS=hotrod \
-e CACHE_NAMES=debtorAccountCache,creditorAccountCache \
-e HOTROD_SERVICE_NAME=rtp-demo-cache \
-e HOTROD_AUTHENTICATION=true \
-e USERNAME=jdguser \
-e PASSWORD=P@ssword1
