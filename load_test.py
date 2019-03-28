import requests, random, time
url = "http://rtp-debtor-payment-service-rtp-reference.192.168.42.20.nip.io/payments-service/payments"

for x in range(100):
    data = {"payments":[{"senderAccountNumber":"bob","amount":random.randint(1,101),"receiverFirstName":"bob","receiverLastName":"smith","receiverEmail":"a@a.aom","receiverCellPhone":"null"}]}
    print(requests.post(url, json=data).text)
    time.sleep(.5)
