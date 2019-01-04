# rtp-reference







docker login registry.redhat.io


cat ~/.docker/config.json

oc project myproject

oc create secret generic pull-secret-name   --from-file=.dockerconfigjson=/Users/liz/.docker/config.json   --type=kubernetes.io/dockerconfigjson

oc secrets link default pull-secret-name --for=pull

oc secrets link builder pull-secret-name


for resource in datagrid72-image-stream.json \
  datagrid72-basic.json \
  datagrid72-https.json \
  datagrid72-mysql-persistent.json \
  datagrid72-mysql.json \
  datagrid72-partition.json \
  datagrid72-postgresql.json \
  datagrid72-postgresql-persistent.json
do
  oc create -n openshift -f \
  https://raw.githubusercontent.com/jboss-container-images/jboss-datagrid-7-openshift-image/1.3/templates/${resource}
done

oc get templates -n openshift | grep datagrid72


oc get is -n openshift | grep datagrid

docker pull registry.access.redhat.com/jboss-datagrid-7/datagrid72-openshift

oc -n openshift import-image jboss-datagrid72-openshift:1.3



------------

minishift docker-env

eval $(minishift docker-env)

docker pull registry.access.redhat.com/jboss-datagrid-7/datagrid72-openshift

for resource in datagrid72-image-stream.json \
  datagrid72-basic.json \
  datagrid72-https.json \
  datagrid72-mysql-persistent.json \
  datagrid72-mysql.json \
  datagrid72-partition.json \
  datagrid72-postgresql.json \
  datagrid72-postgresql-persistent.json
do
  oc create -n openshift -f \
  https://raw.githubusercontent.com/jboss-container-images/jboss-datagrid-7-openshift-image/1.3/templates/${resource}
done

oc get templates -n openshift | grep datagrid72

oc describe is jboss-datagrid72-openshift -n openshift



---------------------------------
oc -n openshift import-image registry.access.redhat.com/jboss-datagrid-7/datagrid72-openshift --confirm


oc describe template datagrid72-basic -n openshift

oc new-app --template=datagrid72-basic --name=rhdg \
  -e USERNAME=developer -e PASSWORD=password \
  -e CACHE_NAMES=mycache -e MYCACHE_CACHE_START=EAGER



-------------------------------------

minishift start --cpus 4 --disk-size 100GB --memory 12GB



oc -n openshift import-image registry.access.redhat.com/jboss-datagrid-7/datagrid72-openshift --confirm



oc new-project datagrid

oc new-app --template=datagrid72-basic --name=rhdg \
  -e CACHE_NAMES=mycache -e MYCACHE_CACHE_START=EAGER


  curl -i -H "Accept:application/json" \
  http://datagrid-app-datagrid.192.168.64.5.nip.io/rest/default/a




  curl -X POST -i -H "Content-type:application/json" \
  -d "{\"name\":\"Red Hat Data Grid\"}" \
  http://datagrid-app-datagrid.192.168.64.5.nip.io/rest/mycache/a



----
oc -n openshift import-image registry.access.redhat.com/jboss-eap-7/eap71-openshift:1.2

jboss-eap-7/eap71-openshift:1.2

----

oc -n openshift import-image my-jboss-eap-7/eap71-openshift --from=registry.access.redhat.com/jboss-eap-7/eap71-openshift --confirm

oc new-build --binary=true --image-stream=eap71-openshift:latest --name=eap-app

oc start-build eap-app --from-dir=deployments/ --follow

---













oc create -n openshift secret docker-registry imagestreamsecret \
  --docker-server=registry.redhat.io \
  --docker-username=espangle@redhat.com \
  --docker-password=EniTln23= \
  --docker-email=espangle@redhat.com


  BASEURL=https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-000064-redhat-4

  oc create -n openshift -f ${BASEURL}/fis-image-streams.json

  oc replace -n openshift -f ${BASEURL}/fis-image-streams.json


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
   oc create -n openshift -f \
   https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001/quickstarts/${template}
   done


   oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001/fis-console-cluster-template.json


oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001/fis-console-namespace-template.json


oc create -n openshift -f ${BASEURL}/fuse-apicurito.yml


oc get template -n openshift


mvn fabric8:deploy -Popenshift




**
for resource in datagrid72-image-stream.json \
  datagrid72-basic.json \
  datagrid72-https.json \
  datagrid72-mysql-persistent.json \
  datagrid72-mysql.json \
  datagrid72-partition.json \
  datagrid72-postgresql.json \
  datagrid72-postgresql-persistent.json
do
  oc create -n openshift -f \
  https://raw.githubusercontent.com/jboss-container-images/jboss-datagrid-7-openshift-image/1.3/templates/${resource}
done



 oc new-app --name=carcache-hotrod --image-stream=jboss-datagrid72-openshift:1.3 -e INFINISPAN_CONNECTORS=hotrod -e CACHE_NAMES=carcache -e HOTROD_SERVICE_NAME=carcache-hotrod -e HOTROD_AUTHENTICATION=true -e USERNAME=jdguser -e PASSWORD=P@ssword1


 oc new-app --name=carcache-hotrod \
 --image-stream=jboss-datagrid72-openshift:1.3 \
 -e INFINISPAN_CONNECTORS=hotrod \
 -e CACHE_NAMES=carcache \
 -e HOTROD_SERVICE_NAME=carcache-hotrod \
 -e HOTROD_AUTHENTICATION=true \
 -e USERNAME=jdguser \
 -e PASSWORD=P@ssword1


*****
oc import-image -n openshift registry.access.redhat.com/jboss-eap-7/eap71-openshift --confirm



oc new-build --binary=true \
--image-stream=eap71-openshift:latest \
--name=eap-app


oc start-build eap-app --from-dir=deployments/ --follow

oc new-app eap-app

















==========================================

oc import-image -n openshift registry.access.redhat.com/jboss-eap-7/eap71-openshift --confirm



==========================================
==========================================
==========================================

Create a docker-registry secret using either Red Hat Customer Portal account or Red Hat Developer Program account credentials.
```
oc create secret docker-registry imagestreamsecret \
  --docker-server=registry.redhat.io \
  --docker-username=CUSTOMER_PORTAL_USERNAME \
  --docker-password=CUSTOMER_PORTAL_PASSWORD \
  --docker-email=EMAIL_ADDRESS
```

// fuse image and templates

```
$ BASEURL=https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001
$ oc create -n openshift -f ${BASEURL}/fis-image-streams.json
```

```
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
 ```

  ```
$ oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001/fis-console-cluster-template.json
$ oc create -n openshift -f https://raw.githubusercontent.com/jboss-fuse/application-templates/application-templates-2.1.fuse-720018-redhat-00001/fis-console-namespace-template.json
$ oc create -n openshift -f ${BASEURL}/fuse-apicurito.yml
 ```

 ```
 $ oc get template -n openshift
 ```


// datagrid image
```
$ oc import-image -n openshift registry.access.redhat.com/jboss-datagrid-7/datagrid72-openshift --confirm
$ oc get is -n openshift
```



###







```
$ oc new-app --name=rtp-creditor-cache \
--image-stream=datagrid72-openshift:latest \
-e INFINISPAN_CONNECTORS=hotrod \
-e CACHE_NAMES=accountcache \
-e HOTROD_SERVICE_NAME=rtp-creditor-cache\
-e HOTROD_AUTHENTICATION=true \
-e USERNAME=jdguser \
-e PASSWORD=P@ssword1
```









curl -X POST -i -H "Content-type:application/json" \
-d "{\"name\":\"Red Hat Data Grid\"}" \
http://rhdgroute-datagrid.192.0.2.0.nip.io/rest/mycache/a


curl -X POST -i -H "Content-type:application/json" \
{\"accountNumber\":\"12000194212199001\",\"accountType\":\"Checking\"} \
http://rtp-creditor-cache-rtp-test.192.168.64.7.nip.io/rest/mycache/12000194212199001



{
  "accountNumber":"12000194212199001",
  "accountType":"Checking"
}




oc new-app --name=rtpcache-hotrod --image-stream=jboss-datagrid72-openshift:1.3 -e INFINISPAN_CONNECTORS=hotrod -e CACHE_NAMES=rtpcache -e HOTROD_SERVICE_NAME=rtpcache-hotrod -e HOTROD_AUTHENTICATION=true -e USERNAME=jdguser -e PASSWORD=P@ssword1





### Resources

https://access.redhat.com/documentation/en-us/red_hat_fuse/7.2/html-single/fuse_on_openshift_guide/
https://access.redhat.com/documentation/en-us/red_hat_jboss_data_grid/7.2/html-single/data_grid_for_openshift/




















public static class TestCallbackHandler implements CallbackHandler {
  final private String username;
  final private char[] password;
  final private String realm;

  public TestCallbackHandler(String username, String realm, char[] password) {
    this.username = username;
    this.password = password;
    this.realm = realm;
  }

  @Override
  public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
    for (Callback callback : callbacks) {
      if (callback instanceof NameCallback) {
        NameCallback nameCallback = (NameCallback) callback;
        nameCallback.setName(username);
      } else if (callback instanceof PasswordCallback) {
        PasswordCallback passwordCallback = (PasswordCallback) callback;
        passwordCallback.setPassword(password);
      } else if (callback instanceof AuthorizeCallback) {
        AuthorizeCallback authorizeCallback = (AuthorizeCallback) callback;
        authorizeCallback.setAuthorized(
            authorizeCallback.getAuthenticationID().equals(authorizeCallback.getAuthorizationID()));
      } else if (callback instanceof RealmCallback) {
        RealmCallback realmCallback = (RealmCallback) callback;
        realmCallback.setText(realm);
      } else {
        throw new UnsupportedCallbackException(callback);
      }
    }
  }
}




https://github.com/oscerd/camel-infinispan-kafka-demo/blob/master/src/main/java/com/github/oscerd/camel/infinispan/kafka/demo/Application.java


  .
