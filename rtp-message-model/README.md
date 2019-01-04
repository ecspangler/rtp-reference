# Notes 

RTP XSDs sourced from:
- https://www.theclearinghouse.org/payment-systems/rtp/-/media/831511fc488b4498ae4df08ff29e868e.ashx
- https://www.iso20022.org/full_catalogue.page
- https://www.iso20022.org/message_archive.page 

For demo purposes, the following RTP-specific administrative message types are omitted:
- admn.005.001.01 -- EchoRequest
- admn.006.001.01 -- EchoResponse
- admn.003.001.01 -- SignOffRequest
- admn.004.001.01 -- SignOffResponse
- admn.001.001.01 -- SignOnRequest
- admn.002.001.01 -- SignOnResponse

These messages are used by a participating FI sign on to and off from the RTP network and heartbeat checks, 
and are out of the scope of this demo.

For more detail see:
- https://www.theclearinghouse.org/payment-systems/rtp/-/media/46e82eec9ad049009aa683fc15535c65.ashx