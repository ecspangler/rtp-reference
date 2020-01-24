# --- Install Fuse on the OpenShift Cluster
oc project openshift

oc create secret docker-registry imagestreamsecret \
  --docker-server=registry.redhat.io \
  --docker-username=$username \
  --docker-password=\'"$password"\' \
  --docker-email=$email

BASEURL=https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-750056-redhat-00006
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
