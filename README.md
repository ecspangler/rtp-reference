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

The Red Hat subscription credentials requested by the script are used to retrieve Red Hat images.


## Install Instructions

Clone this repository to a location where oc and mvn can run.

Change into the cloned directory and run the script:
```
./bootstrap.sh
```

## Validating Installation

Find the route for the Debtor Payment Service:

```
oc get routes | grep rtp-debtor-payment-service
rtp-debtor-payment-service             rtp-debtor-payment-service-rtp-reference.apps.nyc-3f5a.open.redhat.com                       rtp-debtor-payment-service             8080                     None

```

Open a browser and navigate to the URL. In the above example, the URL would be http://rtp-debtor-payment-service-rtp-reference.apps.nyc-3f5a.open.redhat.com

Use any username to login and password 'redhat'

Send a payment to a test user:
First Name: Edward
Last Name: Garcia
Amount: <amount>
Email or Phone Number: edward.garcia@mail.org


The send payments REST api can also be accessed at http://<host>/payments-service/payments

Request:
POST http://<host>/payments-service/payments
```
{
  "payments":[
    {
  		"senderAccountNumber":"12000194212199008",
  		"amount":"25.00",
  		"receiverFirstName":"Edward",
  		"receiverLastName":"Garcia",
      	"receiverEmail":"edward.garcia@mail.org"
	  }
  ]
}
```

Response:
```
{
    "payments": [
        {
            "id": null,
            "paymentId": "KEYFORTHBANK20191004042945059",
            "senderRoutingNumber": null,
            "senderAccountNumber": "12000194212199008",
            "senderFirstName": null,
            "senderLastName": null,
            "senderEmail": null,
            "senderCellPhone": null,
            "amount": 25,
            "receiverFirstName": "Edward",
            "receiverLastName": "Garcia",
            "receiverEmail": "edward.garcia@mail.org",
            "receiverCellPhone": null,
            "receiverRoutingNumber": "020010001",
            "receiverAccountNumber": "egarcia",
            "status": "PENDING",
            "messageStatusReportId": null
        }
    ]
}
```
