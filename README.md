Amazon Product Advertising API Client extension
==========
**paapi-client** is a library for Amazon's Product Advertising API. 

# Features
* supports all of the SOAP API operations using JAX-WS
* automatic retries on `503 Service Unavailable`
* configurable number of retries and time between requests
* error checking

# Usage
`cc.freiberg.paapi.client.PaapiClientImpl` is the main class to send requests to AWS. The constructor needs all of the following properties:

- Marketplace (e.g. `DE`,`US` etc)
- Access Key Id
- Secret Access Key
- Associate Tag (e.g. `freiberg-123`)

# Examples

## Initialization
```java
final String accessKeyId = "<YOUR_ACCESS_KEY_ID>";
final String endpoint = "www.amazon.de";
final String secretAccessKeyId = "<YOUR_SECRET_ACCESS_KEY>";
final String tag = "<YOUR_TAG>";
final PaapiClient client = new PaapiClientImpl(endpoint, accessKeyId, secretAccessKeyId, tag);
```

## Item Lookup
```java
final ItemLookupRequest request = new ItemLookupRequest();
request.getItemId().add("B00E6TYD6I");
request.getResponseGroup().add("Small");
final List<Items> itemLookup = client.itemLookup(request);
```

# Requirements
- JDK 1.8
- Commons Codec
- slf4j

# Note
The  contents of the **com.ecs.client** package is a product of using `wsimport` against [AWSECommerceService.wsdl](http://ecs.amazonaws.com/AWSECommerceService/2013-08-01/AWSECommerceService.wsdl)

# License 
