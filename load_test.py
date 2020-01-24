#!/usr/bin/env python3
import requests, random, time, subprocess

url = "http://rtp-debtor-payment-service-rtp-reference.apps.nyc-f63a.open.redhat.com/payments-service/payments"
print("Connecting to URL: " + url)

for x in range(20):
    data = {"payments":[{"senderAccountNumber":"12000194212199004","amount":random.randint(1,1000),"receiverFirstName":"Edward","receiverLastName":"Garcia","receiverEmail":"edward.garcia@mail.org","receiverCellPhone":"null"}]}
    requests.post(url, json=data)
    time.sleep(.001)
