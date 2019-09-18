#!/usr/bin/env bash

# --- Deploy the RTP Reference Services
mvn --non-recursive clean install
for dependency in \
    rtp-message-model \
    rtp-debtor-domain-model \
    rtp-debtor-account-repository \
    rtp-debtor-account-repository-jdg \
    rtp-debtor-transaction-repository \
    rtp-debtor-transaction-repository-mysql \
    rtp-creditor-domain-model \
    rtp-creditor-validation-model \
    rtp-creditor-rules \
    rtp-creditor-account-repository \
    rtp-creditor-account-repository-jdg \
    rtp-creditor-transaction-repository \
    rtp-creditor-transaction-repository-mysql;
do
    printf "Building $dependency\n"
    cd $dependency
    mvn clean install
    cd ..
done



for service in \
    rtp-creditor-auditing \
    rtp-creditor-complete-payment \
    rtp-creditor-core-banking \
    rtp-creditor-customer-notification \
    rtp-creditor-payment-acknowledgement \
    rtp-creditor-payment-confirmation \
    rtp-creditor-payment-service \
    rtp-creditor-receive-payment \
    rtp-creditor-fraud-detection \
    rtp-debtor-complete-payment \
    rtp-creditor-payment-received-glue \
    rtp-creditor-account-validation-glue \
    rtp-creditor-fraud-validation-glue \
    rtp-creditor-complete-case-glue \
    rtp-creditor-elastic-glue
do
    printf "Deploying $service\n"
    cd $service
    mvn clean fabric8:deploy
    cd ..
done



