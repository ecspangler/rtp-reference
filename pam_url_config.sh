#!/usr/bin/env bash

bc_host=`oc get routes --selector app=rhpam72-trial-ephemeral --selector service=myapp-kieserver -o jsonpath="{.items[0].spec.host}"`

oc create configmap rtp-creditor-pam-url-http-config \
            --from-literal=BC_PATH="http://${bc_host}"

oc create configmap rtp-creditor-pam-url-config \
            --from-literal=BC_HOST="host=executionUser:RedHat@${bc_host}"


oc set env dc/rtp-account-validation-payment-glue --from configmap/rtp-creditor-pam-url-http-config

oc set env dc/rtp-creditor-complete-case-glue  --from configmap/rtp-creditor-pam-url-http-config

oc set env dc/rtp-creditor-elastic-glue --from configmap/rtp-creditor-pam-url-config

oc set env dc/rtp-creditor-fraud-validation-glue --from configmap/rtp-creditor-pam-url-http-config

oc set env dc/rtp-creditor-payment-received-glue --from configmap/rtp-creditor-pam-url-config
