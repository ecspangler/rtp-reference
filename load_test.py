#!/usr/bin/env python3
import requests, random, time, subprocess

url = "http://rtp-debtor-payment-service-rtp-reference." + subprocess.run(['minishift', 'ip'], stdout=subprocess.PIPE).stdout.decode("UTF-8").strip() + ".nip.io/payments-service/payments"
print("Connecting to URL: " + url)

for x in range(100):
    data = {"payments":[{"senderAccountNumber":"bob","amount":random.randint(1,101),"receiverFirstName":"bob","receiverLastName":"smith","receiverEmail":"a@a.aom","receiverCellPhone":"null"}]}
    print(requests.post(url, json=data).text)
    time.sleep(.5)
