# Scripted Installation

## Install Prerequisites

The following should be in place prior to installation:
- An OpenShift 3.11 cluster
- oc client logged into the cluster as user with cluster-admin privileges
- JDK 8 installed on host location where install script will run
- Maven 3.6.x installed on host location where install script will run

Note: Host location also assumed to have ability to run bash script

The install script requires Maven in order to run fabric8 builds to deploy to the target cluster.

As the script will create a namespace for the project, install a Strimzi cluster via operator, and import images into the 'openshift' project, the oc user will need to have the correct permissions to perform these actions.



## Install Instructions

Clone this repository to a location from which oc and mvn can be run.


Change into the cloned directory and run the script:
```
./bootstrap.sh
```
