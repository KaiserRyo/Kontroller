# Kontroller

A REST-ful web service for managing KaBoom clusters

## Building

A maven install produces an exutable JAR file that you can you launch to start the server.

## Bundling with Kontroller-UI

This project is purely the REST-ful API for storing and retrieving KaBoom and related configurations.  There's no user interface whatsoever.  

However there is built-in and recommended functionality for serving up static assets (HTML/JavaScript--i.e., the user interface) from the web server embedded within this JAR.

## Running

java -jar `[-Dlog4j.configuration=file:/path/to/log4j.properties]` `[-Ddw.logging.level=INFO]` `/path/to/kontroller-0.0.1.jar` server `/path/to/kontroller.yaml`

The log4j config is purely optional, as is the log level.  The bare minimum to start the server is: 

```
java -jar kontroller.0.0.1.jar server kontroller.yaml
```

The above expects the JAR and the YAML configuration to be in the current directory.

## Configuring

Here's the sample configuration file.

```
assets:
  overrides:
    /: /path/to/kontroller-UI/
server:
  rootPath: /api/*
  gzip:
    bufferSize: 8KiB
kaboomZkConnString: r3k1.kafka.company.com:2181,r3k2.kafka.company.com:2181,r3k3.kafka.company.com:2181/KaBoomDev
kaboomZkTopicPath: /kaboom/topics #remember, this is relative to the root at the end of kaboomZkConnString:
kaboomZkClientPath: /kaboom/clients #remember, this is relative to the root at the end of kaboomZkConnString:
kaboomZkAssignmentPath: /kaboom/assignments #remember, this is relative to the root at the end of kaboomZkConnString:
kaboomZkConfigPath: /kaboom/config #remember, this is relative to the root at the end of kaboomZkConnString:
kafkaZkConnString: r3k1.kafka.company.com:2181,r3k2.kafka.company.com:2181,r3k3.kafka.company.com:2181
kafkaSeedBrokers: r3k1.kafka.company.com:9092,r3k2.kafka.company.com:9092,r3k3.kafka.company.com:9092
kafkaZkBrokerPath: /brokers/ids #again--remember, although BDP doesn't usually prefix kafka namespace
adminGroupDn: CN=CommonName,OU=Hadoop,DC=ad0,DC=bblabs
ldapConfiguration:
  ldapServers: ldap://ldap.company.com
  username: readonlyuser
  password: secret
  userFilter: OU=users,DC=com,DC=company
  groupBaseDN: OU=team,DC=com,DC=company
  trustAnySecuredHost: true
  cachePolicy: maximumSize=10000, expireAfterWrite=10m
  #connectTimeout: 500ms
  #readTimeout: 500ms
```

## Bundling with Kontroller-UI

Kontroller-UI is a feature rich web interface written against Kontroller's API.  It supports all the methods that Kontroller supports and while it's not strictly required, it is intended to be how Kontroller is accessed.  

Simple fetch/download/checkout Kontroller-UI from Git and configure it's path where `/path/to/kontroller-UI/` occurs in the sample configuration.

The entire API suite is then configured to be served from the `rootPath: /api/*` and the static configuraiton (i.e. Kontroller) is served from `/`.

You'll need to then configure the appropriate ZK connection strings, and Kafka seed brokers accordingly (these should match your KaBoom configuration).

## Securing via LDAP Auth.

Here's a rundown of the LDAP configuration.

## Configuration

```
ldapConfiguration:
  ldapServers: ldap://ldap.company.com
  username: accessuser
  password: secret
  userFilter: OU=department,DC=com,DC=company
  groupBaseDN: OU=groups,DC=com,DC=company
  trustAnySecuredHost: true
  cachePolicy: maximumSize=10000, expireAfterWrite=10m
  userIdentifierObjectName: sAMAccountName
  merbershipIdentifierObjectName: member
```
Where:

* ldapServers: A single or comma seperated list of your LDAP server (i.e. ldap[s]://host1:port1...[ldap[s]://hostN:portN]).  Port is optional and will be infered from the URI schema if ommited (ldap=389, ldaps=636).
* username: The account that binds to the LDAP connection and is used for searching
* password: The 'username's password
* userFilter: The tree where the users are searched for by 'username' when looking for the principals DN
* groupBaseDN: The tree where memberships are searched
* trustAnySecuredHost: Accept non-valid SSL certificates for ldaps:// servers (self signed, expired)
* cachePolicy: How many credentials to cache and for how long?
* userIdentifierObjectName: Object name that identifies the username of a principal
* merbershipIdentifierObjectName: Object name that identifies the object value being a member


## Accessing

The API is accessible on TCP 8080, and an administrative interface can be found on TCP 8081

## Contributing
To contribute code to this repository you must be [signed up as an official contributor](http://blackberry.github.com/howToContribute.html).

## Disclaimer 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
