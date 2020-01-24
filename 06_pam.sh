#!/usr/bin/env bash

cd ..
git clone https://github.com/jboss-container-images/rhpam-7-openshift-image.git

cd rhpam-7-openshift-image
git checkout 7.2.x
git pull

oc project openshift

oc create -f rhpam72-image-streams.yaml

oc get imagestreamtag -n openshift | grep rhpam72-businesscentral-openshift
oc get imagestreamtag -n openshift | grep rhpam72-kieserver-openshift

oc project rtp-reference

oc new-app -f templates/rhpam72-trial-ephemeral.yaml

cd rtp-reference
