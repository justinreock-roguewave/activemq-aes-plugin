package com.roguewave.oss.activemq;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQPGPBrokerPlugin implements BrokerPlugin {
	
	private static Logger LOG = LoggerFactory.getLogger(ActiveMQPGPBroker.class);
	
	public ActiveMQPGPBrokerPlugin() {}
	
	   public Broker installPlugin(Broker broker) throws Exception {
		   
		   ActiveMQPGPBroker pB = new ActiveMQPGPBroker(broker);
		   LOG.info("Installing PGP encryption plugin");
		   return pB;
		   
	   }

}
