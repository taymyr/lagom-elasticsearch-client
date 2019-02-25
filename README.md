[![Gitter](https://img.shields.io/badge/chat-gitter-purple.svg)](https://gitter.im/taymyr/taymyr)
[![Gitter_RU](https://img.shields.io/badge/chat-russian%20channel-purple.svg)](https://gitter.im/taymyr/taymyr_ru)
[![Build Status](https://travis-ci.org/taymyr/lagom-elasticsearch-client.svg?branch=master)](https://travis-ci.org/taymyr/lagom-elasticsearch-client)
[![codecov](https://codecov.io/gh/taymyr/lagom-elasticsearch-client/branch/master/graph/badge.svg)](https://codecov.io/gh/taymyr/lagom-elasticsearch-client)
[![Maven Central](https://img.shields.io/maven-central/v/org.taymyr.lagom/lagom-elasticsearch-client_2.12.svg)](https://search.maven.org/search?q=a:lagom-elasticsearch-client-java_2.12%20AND%20g:org.taymyr.lagom)

# Lagom client for [Elasticsearch](https://www.elastic.co/products/elasticsearch)

This is Lagom Service Descriptor for [Elasticsearch](https://www.elastic.co/products/elasticsearch).

Lagom Elasticsearch Client has next services:

* [ElasticSearch](java/src/main/kotlin/org/taymyr/lagom/elasticsearch/search/ElasticSearch.kt) implement 
[Elasticsearch Search APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/search.html) and named `elastic-search`
* [ElasticIndices](java/src/main/kotlin/org/taymyr/lagom/elasticsearch/indices/ElasticIndices.kt) implement 
  [Elasticsearch Indices APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/indices.html) and named `elastic-indices`
* [ElasticDocument](java/src/main/kotlin/org/taymyr/lagom/elasticsearch/document/ElasticDocument.kt) implement 
  [Elasticsearch Document APIs](https://www.elastic.co/guide/en/elasticsearch/reference/current/docs.html) and named `elastic-document`

_Note: We try not to change the API, but before the release of stable version `1.0.0` API may be changed._

## Versions compatibility

| Lagom Elasticsearch Client | Lagom           | Scala          | Elastic                 |
|----------------------------|-----------------|----------------|-------------------------|
| 1.+                        | 1.4.+ <br> 1.5.+| 2.11 <br> 2.12 | 5.+(partial) <br> 6.+   |

## How to use

### Calling the services

Some service methods has class `ByteString` in their signature. 
For example method `getSource` in `ElasticDocument` service return `ServiceCall<NotUsed, ByteString>`.  
For calling this method need to use static helper function `ServiceCall#invoke`.

Use next code:

```java
import static org.taymyr.lagom.elasticsearch.ServiceCall.invoke;
...
invoke(elasticDocument.getSource("test", "sample", "1"), TestDocument.class)
```
instead of

```java
elasticDocument.getSource("test", "sample", "1").invoke()
```

### Adding the dependency

All **released** artifacts are available in the [Maven central repository](https://search.maven.org/search?q=a:lagom-elasticsearch-client-java_2.12%20AND%20g:org.taymyr.lagom).
Just add a `lagom-elasticsearch-client` to your service dependencies:

* **SBT**

```scala
libraryDependencies += "org.taymyr.lagom" %% "lagom-elasticsearch-client-java" % "X.Y.Z"
```

* **Maven**

```xml
<dependency>
  <groupId>org.taymyr.lagom</groupId>
  <artifactId>lagom-elasticsearch-client-java_${scala.binary.version}</artifactId>
  <version>X.Y.Z</version>
</dependency>
```

All **snapshot** artifacts are available in the [Sonatype snapshots repository](https://oss.sonatype.org/content/repositories/snapshots/org/taymyr/lagom).
This repository must be added in your build system. 

* **SBT**

```scala
resolvers in ThisBuild += Resolver.sonatypeRepo("snapshots")
```

* **Maven**
```xml
<repositories>
  <repository>
    <id>snapshots-repo</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    <releases><enabled>false</enabled></releases>
    <snapshots><enabled>true</enabled></snapshots>
  </repository>
</repositories>
``` 

## Contributions

Contributions are very welcome.

## License

Copyright © 2018-2019 Digital Economy League (https://www.digitalleague.ru/).

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this project except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0.

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.

