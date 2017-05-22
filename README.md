# activemq-aes-plugin
Plugin to AES Encrypt Message Payloads in ActiveMQ

This plugin will allow encryption at rest while storing messages in a persistence store like KahaDB.

INSTALL
Compile with Maven and drop the resulting .jar in ActiveMQ's classpath

CONFIGURE

Add a system property called activemq.aeskey to ActiveMQ's JVM options, with a 16-digit AES key, such as:
-Dactivemq.aeskey=oneactivemqkey4u

Update activemq.xml to load the plugin, by adding a block similar to the following:
        <plugins>
             <bean xmlns="http://www.springframework.org/schema/beans" id="aesPlugin" class="com.roguewave.oss.activemq.ActiveMQAESBrokerPlugin"/>
        </plugins>

Upon restarting the broker, the plugin will log a message that it has loaded.

You can validate that the messages are encrypting by browsing pending messages inside of a queue.  You will see a base64 encoded string as opposed to your message payload.


