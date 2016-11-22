package com.roguewave.oss.activemq;

import javax.jms.JMSException;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.region.MessageReference;

public class ActiveMQPGPBroker extends BrokerFilter {

	public ActiveMQPGPBroker(Broker next) {
		super(next);
		System.out.println(this.getBrokerSequenceId());
	}

	public Message encryptMessage(Message mesg) {
		
		String mesgBody = "";
		ActiveMQTextMessage tm = initializeTextMessage(mesg);
		try {
			mesgBody = tm.getText();
		}
		catch (JMSException e) {
			System.out.println("Error:  Could not get message body contents");
		    return mesg;
		}
		
		mesgBody = "ENCRYPTED: " + mesgBody; 
		
		try {
		    tm.setText(mesgBody);
		}
		catch (Exception e) {
			System.out.println("Error: Could not write to message body");
			System.out.println(e.getMessage());
			return mesg;
		}
		
		return tm;
		
	}
	
	public Message decryptMessage(Message mesg) {
		
		String mesgBody = "";
		ActiveMQTextMessage tm = initializeTextMessage(mesg);
		try {
			mesgBody = tm.getText();
		}
		catch (JMSException e) {
			System.out.println("Error:  Could not get message body contents");
		    return mesg;
		}
		
		mesgBody = mesgBody.replace("ENCRYPTED: ", "DECRYPTED: ");
		mesgBody = "D: " + mesgBody;
		
		try {
		    tm.setText(mesgBody);
		}
		catch (Exception e) {
			System.out.println("Error: Could not write to message body");
			System.out.println(e.getMessage());
			return mesg;
		}
		
		return tm;
				
	}
	
	public ActiveMQTextMessage initializeTextMessage(Message mesg) {
		
		ActiveMQTextMessage tm = (ActiveMQTextMessage) mesg.getMessage();
		tm.setReadOnlyBody(false);
		return tm;
		
	}

	public void send(ProducerBrokerExchange producerExchange, Message messageSend) throws Exception {
		ActiveMQTextMessage encryptedMessage = (ActiveMQTextMessage) encryptMessage(messageSend.getMessage());
		next.send(producerExchange, encryptedMessage);
	}
	
	public void messageConsumed(ConnectionContext context, MessageReference messageReference) {
		ActiveMQTextMessage encryptedMessage = (ActiveMQTextMessage) messageReference.getMessage();
		ActiveMQTextMessage decryptedMessage = (ActiveMQTextMessage) decryptMessage(encryptedMessage);
		next.messageConsumed(context, decryptedMessage);
		
	}
	
}
