# Kontroller

A REST-ful web service for managing KaBoom clusters

## Building

A maven install produces an exutable JAR file that you can you launch to start the server.

## Bundling with Kontroller-UI

This project is purely the REST-ful API for storing and retrieving KaBoom and related configurations.  There's no user interface whatsoever.  

However there is built-in and recommended functionality for serving up static assets (HTML/JavaScript--i.e., the user interface) from the web server embedded within this JAR.

## Running

```
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
    /: /root/kontroller/static/
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

## Accessing

The API is accessible on TCP 8080, and an administrative interface can be found on TCP 8081

## Contributing
To contribute code to this repository you must be [signed up as an official contributor](http://blackberry.github.com/howToContribute.html).

## Disclaimer 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
