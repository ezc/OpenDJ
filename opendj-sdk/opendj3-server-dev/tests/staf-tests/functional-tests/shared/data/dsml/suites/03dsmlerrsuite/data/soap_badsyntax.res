HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Content-Type: text/xml
Date: Thu, 29 Nov 2007 09:09:13 GMT
Connection: close

<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
<SOAP-ENV:Body>
<dsml:batchResponse xmlns:dsml="urn:oasis:names:tc:DSML:2:0:core">
<dsml:errorResponse type="malformedRequest">
<dsml:message>
javax.xml.transform.TransformerException: org.xml.sax.SAXParseException: The markup in the document following the root element must be well-formed.</dsml:message>
</dsml:errorResponse>
</dsml:batchResponse>
</SOAP-ENV:Body>
</SOAP-ENV:Envelope>
