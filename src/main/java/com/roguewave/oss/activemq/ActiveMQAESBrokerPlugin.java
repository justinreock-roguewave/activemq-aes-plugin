package com.roguewave.oss.activemq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQAESBrokerPlugin implements BrokerPlugin {
	
	private static Logger LOG = LoggerFactory.getLogger(ActiveMQAESBroker.class);
	
	public ActiveMQAESBrokerPlugin() {}
	
	   public Broker installPlugin(Broker broker) throws Exception {
		   
		   ActiveMQAESBroker pB = new ActiveMQAESBroker(broker);
		   LOG.info("Installing AES payload encryption plugin");
		   return pB;
		   
	   }

}
