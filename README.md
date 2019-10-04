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


It should be possible to trace the message through the logs of each deployed service.

rtp-debtor-payment-service
```
INFO: Sending message: {}Payment [paymentId=KEYFORTHBANK20191004042945059, senderRoutingNumber=null, senderAccountNumber=12000194212199008, senderFirstName=null, senderLastName=null, senderEmail=null, senderCellPhone=null, amount=25.00, receiverFirstName=Edward, receiverLastName=Garcia, receiverEmail=edward.garcia@mail.org, receiverCellPhone=null, receiverRoutingNumber=020010001, receiverAccountNumber=egarcia, status=PENDING, messageStatusReportId=null, getPaymentId()=KEYFORTHBANK20191004042945059, getSenderRoutingNumber()=null, getSenderAccountNumber()=12000194212199008, getSenderFirstName()=null, getSenderLastName()=null, getSenderEmail()=null, getSenderCellPhone()=null, getAmount()=25.00, getReceiverFirstName()=Edward, getReceiverLastName()=Garcia, getReceiverEmail()=edward.garcia@mail.org, getReceiverCellPhone()=null, getReceiverRoutingNumber()=020010001, getReceiverAccountNumber()=egarcia, getStatus()=PENDING, getMessageStatusReportId()=null, hashCode()=1064236873, getClass()=class rtp.demo.debtor.domain.model.payment.Payment, toString()=rtp.demo.debtor.domain.model.payment.Payment@3f6ef749]
```

rtp-debtor-send-payment
```
04:29:45.089 [Camel (MyCamel) thread #1 - KafkaConsumer[debtor-payments]] INFO  FromKafka -
/// Payments to be sent to  RTP  >>> Payment [paymentId=KEYFORTHBANK20191004042945059, senderRoutingNumber=null, senderAccountNumber=12000194212199008, senderFirstName=null, senderLastName=null, senderEmail=null, senderCellPhone=null, amount=25.00, receiverFirstName=Edward, receiverLastName=Garcia, receiverEmail=edward.garcia@mail.org, receiverCellPhone=null, receiverRoutingNumber=020010001, receiverAccountNumber=egarcia, status=PENDING, messageStatusReportId=null, getPaymentId()=KEYFORTHBANK20191004042945059, getSenderRoutingNumber()=null, getSenderAccountNumber()=12000194212199008, getSenderFirstName()=null, getSenderLastName()=null, getSenderEmail()=null, getSenderCellPhone()=null, getAmount()=25.00, getReceiverFirstName()=Edward, getReceiverLastName()=Garcia, getReceiverEmail()=edward.garcia@mail.org, getReceiverCellPhone()=null, getReceiverRoutingNumber()=020010001, getReceiverAccountNumber()=egarcia, getStatus()=PENDING, getMessageStatusReportId()=null, hashCode()=1064236873, getClass()=class rtp.demo.debtor.domain.model.payment.Payment, toString()=rtp.demo.debtor.domain.model.payment.Payment@3f6ef749]
04:29:45.091 [Camel (MyCamel) thread #1 - KafkaConsumer[debtor-payments]] INFO  FromKafka - Transformed to Credit Transfer Message >>> FIToFICustomerCreditTransferV06 [grpHdr=GroupHeader70 [msgId=KEYFORTHBANK20191004042945059, creDtTm=2019-10-04T04:29:45.089Z, btchBookg=null, nbOfTxs=1, ctrlSum=null, ttlIntrBkSttlmAmt=ActiveCurrencyAndAmount [value=25.00, ccy=USD], intrBkSttlmDt=null, sttlmInf=SettlementInstruction4 [sttlmMtd=CLRG, sttlmAcct=null, clrSys=null, instgRmbrsmntAgt=null, instgRmbrsmntAgtAcct=null, instdRmbrsmntAgt=null, instdRmbrsmntAgtAcct=null, thrdRmbrsmntAgt=null, thrdRmbrsmntAgtAcct=null], pmtTpInf=PaymentTypeInformation21 [instrPrty=null, clrChanl=null, svcLvl=ServiceLevel8Choice [cd=null, prtry=null], lclInstrm=LocalInstrument2Choice [cd=null, prtry=null], ctgyPurp=CategoryPurpose1Choice [cd=null, prtry=null]], instgAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=null, mmbId=null], nm=null, pstlAdr=null, othr=GenericFinancialIdentification1 [id=021200201, schmeNm=null, issr=null]], brnchId=null], instdAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=null, mmbId=null], nm=null, pstlAdr=null, othr=GenericFinancialIdentification1 [id=020010001, schmeNm=null, issr=null]], brnchId=null]], cdtTrfTxInf=[CreditTransferTransaction25 [pmtId=PaymentIdentification3 [instrId=20191004042945089, endToEndId=E2E-Ref001, txId=null, clrSysRef=null], pmtTpInf=null, intrBkSttlmAmt=null, intrBkSttlmDt=null, sttlmPrty=null, sttlmTmIndctn=null, sttlmTmReq=null, accptncDtTm=null, poolgAdjstmntDt=null, instdAmt=null, xchgRate=null, chrgBr=null, chrgsInf=null, prvsInstgAgt=null, prvsInstgAgtAcct=null, instgAgt=null, instdAgt=null, intrmyAgt1=null, intrmyAgt1Acct=null, intrmyAgt2=null, intrmyAgt2Acct=null, intrmyAgt3=null, intrmyAgt3Acct=null, ultmtDbtr=null, initgPty=null, dbtr=null, dbtrAcct=CashAccount24 [id=AccountIdentification4Choice [iban=null, othr=GenericAccountIdentification1 [id=12000194212199008, schmeNm=null, issr=null]], tp=null, ccy=null, nm=null], dbtrAgt=null, dbtrAgtAcct=null, cdtrAgt=null, cdtrAgtAcct=null, cdtr=null, cdtrAcct=CashAccount24 [id=AccountIdentification4Choice [iban=null, othr=GenericAccountIdentification1 [id=egarcia, schmeNm=null, issr=null]], tp=null, ccy=null, nm=null], ultmtCdtr=null, instrForCdtrAgt=null, instrForNxtAgt=null, purp=null, rgltryRptg=null, tax=null, rltdRmtInf=null, rmtInf=null, splmtryData=null]], splmtryData=[]]
```

rtp-mock
```
04:29:45.122 [Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-debtor-credit-transfer]] INFO  route1 -
/// Mock RTP - Sending Credit Transfer Message >>> FIToFICustomerCreditTransferV06 [grpHdr=GroupHeader70 [msgId=KEYFORTHBANK20191004042945059, creDtTm=2019-10-04T04:29:45.089Z, btchBookg=null, nbOfTxs=1, ctrlSum=null, ttlIntrBkSttlmAmt=ActiveCurrencyAndAmount [value=25.00, ccy=USD], intrBkSttlmDt=null, sttlmInf=SettlementInstruction4 [sttlmMtd=CLRG, sttlmAcct=null, clrSys=null, instgRmbrsmntAgt=null, instgRmbrsmntAgtAcct=null, instdRmbrsmntAgt=null, instdRmbrsmntAgtAcct=null, thrdRmbrsmntAgt=null, thrdRmbrsmntAgtAcct=null], pmtTpInf=PaymentTypeInformation21 [instrPrty=null, clrChanl=null, svcLvl=ServiceLevel8Choice [cd=null, prtry=null], lclInstrm=LocalInstrument2Choice [cd=null, prtry=null], ctgyPurp=CategoryPurpose1Choice [cd=null, prtry=null]], instgAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=null, mmbId=null], nm=null, pstlAdr=null, othr=GenericFinancialIdentification1 [id=021200201, schmeNm=null, issr=null]], brnchId=null], instdAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=null, mmbId=null], nm=null, pstlAdr=null, othr=GenericFinancialIdentification1 [id=020010001, schmeNm=null, issr=null]], brnchId=null]], cdtTrfTxInf=[CreditTransferTransaction25 [pmtId=PaymentIdentification3 [instrId=20191004042945089, endToEndId=E2E-Ref001, txId=null, clrSysRef=null], pmtTpInf=null, intrBkSttlmAmt=ActiveCurrencyAndAmount [value=null, ccy=null], intrBkSttlmDt=null, sttlmPrty=null, sttlmTmIndctn=null, sttlmTmReq=null, accptncDtTm=null, poolgAdjstmntDt=null, instdAmt=null, xchgRate=null, chrgBr=null, chrgsInf=[], prvsInstgAgt=null, prvsInstgAgtAcct=null, instgAgt=null, instdAgt=null, intrmyAgt1=null, intrmyAgt1Acct=null, intrmyAgt2=null, intrmyAgt2Acct=null, intrmyAgt3=null, intrmyAgt3Acct=null, ultmtDbtr=null, initgPty=null, dbtr=null, dbtrAcct=CashAccount24 [id=AccountIdentification4Choice [iban=null, othr=GenericAccountIdentification1 [id=12000194212199008, schmeNm=null, issr=null]], tp=null, ccy=null, nm=null], dbtrAgt=null, dbtrAgtAcct=null, cdtrAgt=null, cdtrAgtAcct=null, cdtr=null, cdtrAcct=CashAccount24 [id=AccountIdentification4Choice [iban=null, othr=GenericAccountIdentification1 [id=egarcia, schmeNm=null, issr=null]], tp=null, ccy=null, nm=null], ultmtCdtr=null, instrForCdtrAgt=[], instrForNxtAgt=[], purp=null, rgltryRptg=[], tax=null, rltdRmtInf=[], rmtInf=null, splmtryData=[]]], splmtryData=[]]
```

rtp-creditor-payment-received
```
/// Creditor Intake Route >>> FIToFICustomerCreditTransferV06 [grpHdr=GroupHeader70 [msgId=KEYFORTHBANK20191004042945059, creDtTm=2019-10-04T04:29:45.089Z, btchBookg=null, nbOfTxs=1, ctrlSum=null, ttlIntrBkSttlmAmt=ActiveCurrencyAndAmount [value=25.00, ccy=USD], intrBkSttlmDt=null, sttlmInf=SettlementInstruction4 [sttlmMtd=CLRG, sttlmAcct=null, clrSys=null, instgRmbrsmntAgt=null, instgRmbrsmntAgtAcct=null, instdRmbrsmntAgt=null, instdRmbrsmntAgtAcct=null, thrdRmbrsmntAgt=null, thrdRmbrsmntAgtAcct=null], pmtTpInf=PaymentTypeInformation21 [instrPrty=null, clrChanl=null, svcLvl=ServiceLevel8Choice [cd=null, prtry=null], lclInstrm=LocalInstrument2Choice [cd=null, prtry=null], ctgyPurp=CategoryPurpose1Choice [cd=null, prtry=null]], instgAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=null, mmbId=null], nm=null, pstlAdr=null, othr=GenericFinancialIdentification1 [id=021200201, schmeNm=null, issr=null]], brnchId=null], instdAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=null, mmbId=null], nm=null, pstlAdr=null, othr=GenericFinancialIdentification1 [id=020010001, schmeNm=null, issr=null]], brnchId=null]], cdtTrfTxInf=[CreditTransferTransaction25 [pmtId=PaymentIdentification3 [instrId=20191004042945089, endToEndId=E2E-Ref001, txId=null, clrSysRef=null], pmtTpInf=null, intrBkSttlmAmt=ActiveCurrencyAndAmount [value=null, ccy=null], intrBkSttlmDt=null, sttlmPrty=null, sttlmTmIndctn=null, sttlmTmReq=null, accptncDtTm=null, poolgAdjstmntDt=null, instdAmt=null, xchgRate=null, chrgBr=null, chrgsInf=[], prvsInstgAgt=null, prvsInstgAgtAcct=null, instgAgt=null, instdAgt=null, intrmyAgt1=null, intrmyAgt1Acct=null, intrmyAgt2=null, intrmyAgt2Acct=null, intrmyAgt3=null, intrmyAgt3Acct=null, ultmtDbtr=null, initgPty=null, dbtr=null, dbtrAcct=CashAccount24 [id=AccountIdentification4Choice [iban=null, othr=GenericAccountIdentification1 [id=12000194212199008, schmeNm=null, issr=null]], tp=null, ccy=null, nm=null], dbtrAgt=null, dbtrAgtAcct=null, cdtrAgt=null, cdtrAgtAcct=null, cdtr=null, cdtrAcct=CashAccount24 [id=AccountIdentification4Choice [iban=null, othr=GenericAccountIdentification1 [id=egarcia, schmeNm=null, issr=null]], tp=null, ccy=null, nm=null], ultmtCdtr=null, instrForCdtrAgt=[], instrForNxtAgt=[], purp=null, rgltryRptg=[], tax=null, rltdRmtInf=[], rmtInf=null, splmtryData=[]]], splmtryData=[]]
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO rtp.demo.creditor.intake.routes.CreditorIntakeRouteBuilder - KEYFORTHBANK20191004042945059
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO FromKafka -  >>> CreditTransferMessage [creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia]
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO FromKafka -  Retrieved message >>> CreditTransferMessage [creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia]
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO rtp.demo.creditor.intake.beans.CreditTransferMessageValidationBean - Validation Rules
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO rtp.demo.creditor.intake.beans.CreditTransferMessageValidationBean - CreditTransferMessage [creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia]
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO rtp.demo.creditor.intake.beans.CreditTransferMessageValidationBean - Incoming Payment Validation Request: PaymentValidationRequest [creditTransferMessage=CreditTransferMessage [creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia], errors=[]]
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO rtp.demo.creditor.intake.beans.CreditTransferMessageValidationBean - Outgoing Payment Validation Request: PaymentValidationRequest [creditTransferMessage=CreditTransferMessage [creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia], errors=[]]
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO FromKafka -  Validated payment >>> PaymentValidationRequest [creditTransferMessage=CreditTransferMessage [creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia], errors=[]]
Hibernate: insert into CREDITOR_CREDIT_PAYMENT (CREATE_TIMESTAMP, CREDIT_TRANS_MSG_ID, CREDITOR_ACCT_NUMBER, CREDITOR_ID, DEBTOR_ACCT_NUMBER, DEBTOR_ID, END_TO_END_ID, FRAUD_SCORE, IS_FRAUD_VALIDATED, IS_VALIDATED, MSG_STATUS_RPT_ID, NUMBER_OF_TRANS, PAYMENT_AMOUNT, PAYMENT_CURRENCY, PMT_INSTR_ID, REJECT_REASON_CODE, SETTLEMENT_METHOD, PMT_STATUS) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO FromKafka -  Transformed payment >>> Payment [id=3, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=ACCEPTED, isValidated=true, isFraudValidated=true, fraudScore=null, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=null]  >> key KEYFORTHBANK20191004042945059
[Camel (MyCamel) thread #1 - KafkaConsumer[mock-rtp-creditor-credit-transfer]] INFO FromKafka -  Sending payment >>> Payment [id=3, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=ACCEPTED, isValidated=true, isFraudValidated=true, fraudScore=null, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=null]
```

rtp-creditor-fraud-detection
```
04:29:45.176 [Camel (MyCamel) thread #1 - KafkaConsumer[creditor-payments]] INFO  route1 -
/// Creditor Fraud Detection - Sending Payment Message >>> Payment [id=3, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=ACCEPTED, isValidated=true, isFraudValidated=true, fraudScore=null, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=null]
04:29:45.177 [Camel (MyCamel) thread #1 - KafkaConsumer[creditor-payments]] INFO  route1 -  Fraud detection >>> Payment [id=3, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=ACCEPTED, isValidated=true, isFraudValidated=true, fraudScore=null, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=null]
04:29:45.177 [Camel (MyCamel) thread #1 - KafkaConsumer[creditor-payments]] INFO  r.d.c.f.b.CreditorPaymentFraudDetectionBean - Payment Pre-Fraud Check: Payment [id=3, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=ACCEPTED, isValidated=true, isFraudValidated=true, fraudScore=null, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=null]
04:29:45.177 [Camel (MyCamel) thread #1 - KafkaConsumer[creditor-payments]] INFO  r.d.c.f.b.CreditorPaymentFraudDetectionBean - Payment Post-Fraud Check: Payment [id=3, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=ACCEPTED, isValidated=true, isFraudValidated=true, fraudScore=92.345, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=null]
04:29:45.177 [Camel (MyCamel) thread #1 - KafkaConsumer[creditor-payments]] INFO  route1 -  Fraud detected payment >>> Payment [id=3, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=ACCEPTED, isValidated=true, isFraudValidated=true, fraudScore=92.345, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=null]
```

rtp-creditor-payment-acknowledgement
```
04:29:45.193 [Camel (camelContext-5cc46c7d-f82b-481d-acf2-003fc8bf1ad1) thread #1 - KafkaConsumer[creditor-processed-payments]] INFO  FromKafka -
/// Creditor Acknowledegement Route >>> Payment [id=3, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=ACCEPTED, isValidated=true, isFraudValidated=true, fraudScore=92.345, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=null]
04:29:45.194 [Camel (camelContext-5cc46c7d-f82b-481d-acf2-003fc8bf1ad1) thread #1 - KafkaConsumer[creditor-processed-payments]] INFO  FromKafka - Sending payment status report >>> FIToFIPaymentStatusReportV07 [grpHdr=GroupHeader53 [msgId=PAYMENT_STATUS20191004042945194, creDtTm=null, instgAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=ClearingSystemIdentification2Choice [cd=null, prtry=null], mmbId=null], nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@c428cff, othr=GenericFinancialIdentification1 [id=null, schmeNm=FinancialIdentificationSchemeName1Choice [cd=null, prtry=null], issr=null]], brnchId=BranchData2 [id=null, nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@2a0e9d04]], instdAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=ClearingSystemIdentification2Choice [cd=null, prtry=null], mmbId=null], nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@71032efb, othr=GenericFinancialIdentification1 [id=null, schmeNm=FinancialIdentificationSchemeName1Choice [cd=null, prtry=null], issr=null]], brnchId=BranchData2 [id=null, nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@189fe42f]]], orgnlGrpInfAndSts=[OriginalGroupHeader1 [orgnlMsgId=KEYFORTHBANK20191004042945059, orgnlMsgNmId=null, orgnlCreDtTm=null, orgnlNbOfTxs=null, orgnlCtrlSum=null, grpSts=null, stsRsnInf=[], nbOfTxsPerSts=[]]], txInfAndSts=[PaymentTransaction63 [stsId=null, orgnlGrpInf=null, orgnlInstrId=KEYFORTHBANK20191004042945059, orgnlEndToEndId=null, orgnlTxId=null, txSts=ACCP, stsRsnInf=null, chrgsInf=null, accptncDtTm=null, acctSvcrRef=null, clrSysRef=null, instgAgt=null, instdAgt=null, orgnlTxRef=null, splmtryData=null]], splmtryData=[]]
```

rtp-mock
```
04:29:45.224 [Camel (MyCamel) thread #2 - KafkaConsumer[mock-rtp-creditor-acknowledgement]] INFO  FromKafka -
/// Mock RTP - Receiving Acknowledgements >>> FIToFIPaymentStatusReportV07 [grpHdr=GroupHeader53 [msgId=PAYMENT_STATUS20191004042945194, creDtTm=null, instgAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=ClearingSystemIdentification2Choice [cd=null, prtry=null], mmbId=null], nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@507e71ab, othr=GenericFinancialIdentification1 [id=null, schmeNm=FinancialIdentificationSchemeName1Choice [cd=null, prtry=null], issr=null]], brnchId=BranchData2 [id=null, nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@5f2f9bb6]], instdAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=ClearingSystemIdentification2Choice [cd=null, prtry=null], mmbId=null], nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@695d17ba, othr=GenericFinancialIdentification1 [id=null, schmeNm=FinancialIdentificationSchemeName1Choice [cd=null, prtry=null], issr=null]], brnchId=BranchData2 [id=null, nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@53458b4e]]], orgnlGrpInfAndSts=[OriginalGroupHeader1 [orgnlMsgId=KEYFORTHBANK20191004042945059, orgnlMsgNmId=null, orgnlCreDtTm=null, orgnlNbOfTxs=null, orgnlCtrlSum=null, grpSts=null, stsRsnInf=[], nbOfTxsPerSts=[]]], txInfAndSts=[PaymentTransaction63 [stsId=null, orgnlGrpInf=null, orgnlInstrId=KEYFORTHBANK20191004042945059, orgnlEndToEndId=null, orgnlTxId=null, txSts=ACCP, stsRsnInf=[], chrgsInf=[], accptncDtTm=null, acctSvcrRef=null, clrSysRef=null, instgAgt=null, instdAgt=null, orgnlTxRef=null, splmtryData=[]]], splmtryData=[]]
```

rtp-debtor-ppayment-confirmation
```
04:29:45.258 [Camel (camelContext-5cc46c7d-f82b-481d-acf2-003fc8bf1ad1) thread #1 - KafkaConsumer[mock-rtp-debtor-confirmation]] INFO  FromKafka -
/// Creditor Confirmation Route >>> FIToFIPaymentStatusReportV07 [grpHdr=GroupHeader53 [msgId=PAYMENT_STATUS20191004042945194, creDtTm=null, instgAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=ClearingSystemIdentification2Choice [cd=null, prtry=null], mmbId=null], nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@7d098b7c, othr=GenericFinancialIdentification1 [id=null, schmeNm=FinancialIdentificationSchemeName1Choice [cd=null, prtry=null], issr=null]], brnchId=BranchData2 [id=null, nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@4959ae1e]], instdAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=ClearingSystemIdentification2Choice [cd=null, prtry=null], mmbId=null], nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@3c4467e6, othr=GenericFinancialIdentification1 [id=null, schmeNm=FinancialIdentificationSchemeName1Choice [cd=null, prtry=null], issr=null]], brnchId=BranchData2 [id=null, nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@2ccb4e1c]]], orgnlGrpInfAndSts=[OriginalGroupHeader1 [orgnlMsgId=KEYFORTHBANK20191004042945059, orgnlMsgNmId=null, orgnlCreDtTm=null, orgnlNbOfTxs=null, orgnlCtrlSum=null, grpSts=null, stsRsnInf=[], nbOfTxsPerSts=[]]], txInfAndSts=[PaymentTransaction63 [stsId=null, orgnlGrpInf=null, orgnlInstrId=KEYFORTHBANK20191004042945059, orgnlEndToEndId=null, orgnlTxId=null, txSts=ACCP, stsRsnInf=[], chrgsInf=[], accptncDtTm=null, acctSvcrRef=null, clrSysRef=null, instgAgt=null, instdAgt=null, orgnlTxRef=null, splmtryData=[]]], splmtryData=[]]
04:29:45.314 [Camel (camelContext-5cc46c7d-f82b-481d-acf2-003fc8bf1ad1) thread #1 - KafkaConsumer[mock-rtp-debtor-confirmation]] INFO  FromKafka - Sending payment message confirmation >>> MessageStatusReport [messageStatusReportId=PAYMENT_STATUS20191004042945194, creationDateTime=null, paymentAmount=null, paymentCurrency=null, originalMessageId=KEYFORTHBANK20191004042945059, originalPaymentInstructionId=null, originalMessageType=null, originalMessageCreationDateTime=null, originalNumberOfTransactions=null, transactionStatus=ACCP, rejectReasonCode=null, acceptanceDateTime=null, debtorId=null, creditorId=null] >> key KEYFORTHBANK20191004042945059
```

rtp-debtor-complete-payment
```
[KSTREAM-SOURCE-0000000000]: KEYFORTHBANK20191004042945059, Payment [paymentId=KEYFORTHBANK20191004042945059, senderRoutingNumber=null, senderAccountNumber=12000194212199008, senderFirstName=null, senderLastName=null, senderEmail=null, senderCellPhone=null, amount=25.00, receiverFirstName=Edward, receiverLastName=Garcia, receiverEmail=edward.garcia@mail.org, receiverCellPhone=null, receiverRoutingNumber=020010001, receiverAccountNumber=egarcia, status=PENDING, messageStatusReportId=null, getPaymentId()=KEYFORTHBANK20191004042945059, getSenderRoutingNumber()=null, getSenderAccountNumber()=12000194212199008, getSenderFirstName()=null, getSenderLastName()=null, getSenderEmail()=null, getSenderCellPhone()=null, getAmount()=25.00, getReceiverFirstName()=Edward, getReceiverLastName()=Garcia, getReceiverEmail()=edward.garcia@mail.org, getReceiverCellPhone()=null, getReceiverRoutingNumber()=020010001, getReceiverAccountNumber()=egarcia, getStatus()=PENDING, getMessageStatusReportId()=null, hashCode()=1064236873, getClass()=class rtp.demo.debtor.domain.model.payment.Payment, toString()=rtp.demo.debtor.domain.model.payment.Payment@3f6ef749]
[KSTREAM-MERGE-0000000006]: KEYFORTHBANK20191004042945059, Payment [paymentId=KEYFORTHBANK20191004042945059, senderRoutingNumber=null, senderAccountNumber=12000194212199008, senderFirstName=null, senderLastName=null, senderEmail=null, senderCellPhone=null, amount=25.00, receiverFirstName=Edward, receiverLastName=Garcia, receiverEmail=null, receiverCellPhone=null, receiverRoutingNumber=null, receiverAccountNumber=egarcia, status=COMPLETED, messageStatusReportId=PAYMENT_STATUS20191004042945194, getPaymentId()=KEYFORTHBANK20191004042945059, getSenderRoutingNumber()=null, getSenderAccountNumber()=12000194212199008, getSenderFirstName()=null, getSenderLastName()=null, getSenderEmail()=null, getSenderCellPhone()=null, getAmount()=25.00, getReceiverFirstName()=Edward, getReceiverLastName()=Garcia, getReceiverEmail()=null, getReceiverCellPhone()=null, getReceiverRoutingNumber()=null, getReceiverAccountNumber()=egarcia, getStatus()=COMPLETED, getMessageStatusReportId()=PAYMENT_STATUS20191004042945194, hashCode()=1746099170, getClass()=class rtp.demo.debtor.domain.model.payment.Payment, toString()=rtp.demo.debtor.domain.model.payment.Payment@68135be2]
Oct 04, 2019 4:29:45 AM rtp.demo.debtor.complete.payment.stream.CompletePaymentStream lambda$new$1
INFO: Updating Payment Key: KEYFORTHBANK20191004042945059
Oct 04, 2019 4:29:45 AM rtp.demo.debtor.complete.payment.stream.CompletePaymentStream lambda$new$1
[KSTREAM-SOURCE-0000000001]: KEYFORTHBANK20191004042945059, MessageStatusReport [messageStatusReportId=PAYMENT_STATUS20191004042945194, creationDateTime=null, paymentAmount=null, paymentCurrency=null, originalMessageId=KEYFORTHBANK20191004042945059, originalPaymentInstructionId=null, originalMessageType=null, originalMessageCreationDateTime=null, originalNumberOfTransactions=null, transactionStatus=ACCP, rejectReasonCode=null, acceptanceDateTime=null, debtorId=null, creditorId=null]
```

rtp-creditor-payment-confirmation
```
04:29:45.319 [Camel (camelContext-5cc46c7d-f82b-481d-acf2-003fc8bf1ad1) thread #1 - KafkaConsumer[mock-rtp-creditor-confirmation]] INFO  FromKafka -
/// Creditor Confirmation Route >>> FIToFIPaymentStatusReportV07 [grpHdr=GroupHeader53 [msgId=PAYMENT_STATUS20191004042945194, creDtTm=null, instgAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=ClearingSystemIdentification2Choice [cd=null, prtry=null], mmbId=null], nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@5f2d65e6, othr=GenericFinancialIdentification1 [id=null, schmeNm=FinancialIdentificationSchemeName1Choice [cd=null, prtry=null], issr=null]], brnchId=BranchData2 [id=null, nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@5e24ee42]], instdAgt=BranchAndFinancialInstitutionIdentification5 [finInstnId=FinancialInstitutionIdentification8 [bicfi=null, clrSysMmbId=ClearingSystemMemberIdentification2 [clrSysId=ClearingSystemIdentification2Choice [cd=null, prtry=null], mmbId=null], nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@4f686ad3, othr=GenericFinancialIdentification1 [id=null, schmeNm=FinancialIdentificationSchemeName1Choice [cd=null, prtry=null], issr=null]], brnchId=BranchData2 [id=null, nm=null, pstlAdr=iso.std.iso._20022.tech.xsd.pacs_002_001.PostalAddress6@338e3f44]]], orgnlGrpInfAndSts=[OriginalGroupHeader1 [orgnlMsgId=KEYFORTHBANK20191004042945059, orgnlMsgNmId=null, orgnlCreDtTm=null, orgnlNbOfTxs=null, orgnlCtrlSum=null, grpSts=null, stsRsnInf=[], nbOfTxsPerSts=[]]], txInfAndSts=[PaymentTransaction63 [stsId=null, orgnlGrpInf=null, orgnlInstrId=KEYFORTHBANK20191004042945059, orgnlEndToEndId=null, orgnlTxId=null, txSts=ACCP, stsRsnInf=[], chrgsInf=[], accptncDtTm=null, acctSvcrRef=null, clrSysRef=null, instgAgt=null, instdAgt=null, orgnlTxRef=null, splmtryData=[]]], splmtryData=[]]
04:29:45.322 [Camel (camelContext-5cc46c7d-f82b-481d-acf2-003fc8bf1ad1) thread #1 - KafkaConsumer[mock-rtp-creditor-confirmation]] INFO  FromKafka - Sending payment message confirmation >>> MessageStatusReport [messageStatusReportId=PAYMENT_STATUS20191004042945194, creationDateTime=null, paymentAmount=null, paymentCurrency=null, originalMessageId=KEYFORTHBANK20191004042945059, originalPaymentInstructionId=null, originalMessageType=null, originalMessageCreationDateTime=null, originalNumberOfTransactions=null, transactionStatus=ACCP, rejectReasonCode=null, acceptanceDateTime=null, debtorId=null, creditorId=null] >> key KEYFORTHBANK20191004042945059
```

rtp-creditor-complete-payment
```
[KSTREAM-SOURCE-0000000000]: KEYFORTHBANK20191004042945059, Payment [id=3, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=ACCEPTED, isValidated=true, isFraudValidated=true, fraudScore=92.345, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=null]
[KSTREAM-MERGE-0000000006]: KEYFORTHBANK20191004042945059, Payment [id=null, creditTransferMessageId=KEYFORTHBANK20191004042945059, paymentInstructionId=20191004042945089, endToEndId=E2E-Ref001, creationDateTime=null, numberOfTransactions=1, paymentAmount=25.00, paymentCurrency=USD, settlementMethod=CLRG, debtorId=021200201, debtorAccountNumber=12000194212199008, creditorId=020010001, creditorAccountNumber=egarcia, status=COMPLETED, isValidated=true, isFraudValidated=true, fraudScore=92.345, rejectReasonCode=null, errors=[], validationStatus=VALID, messageStatusReportId=PAYMENT_STATUS20191004042945194]
Oct 04, 2019 4:29:45 AM rtp.demo.creditor.complete.payment.stream.CompletePaymentStream lambda$new$1
[KSTREAM-SOURCE-0000000001]: KEYFORTHBANK20191004042945059, MessageStatusReport [messageStatusReportId=PAYMENT_STATUS20191004042945194, creationDateTime=null, paymentAmount=null, paymentCurrency=null, originalMessageId=KEYFORTHBANK20191004042945059, originalPaymentInstructionId=null, originalMessageType=null, originalMessageCreationDateTime=null, originalNumberOfTransactions=null, transactionStatus=ACCP, rejectReasonCode=null, acceptanceDateTime=null, debtorId=null, creditorId=null]
```
