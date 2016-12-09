package com.roguewave.oss.activemq;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import javax.xml.bind.DatatypeConverter;

import javax.jms.JMSException;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerFilter;
import org.apache.activemq.broker.ProducerBrokerExchange;
import org.apache.activemq.command.Message;
import org.apache.activemq.command.MessageDispatch;
import org.apache.activemq.command.ActiveMQTextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActiveMQAESBroker extends BrokerFilter {

	private static Logger LOG = LoggerFactory.getLogger(ActiveMQAESBroker.class);
	private String keyStr = System.getProperty("activemq.aeskey");
    private Key aesKey = null;
    private Cipher cipher = null;
	
	public ActiveMQAESBroker(Broker next) {
		super(next);
	}
		
    private void init() throws Exception {
        if (keyStr == null || keyStr.length() != 16) {
            throw new Exception("Bad aes key configured - ensure that system property 'activemq.aeskey' is set to a 16 character string");
        }
        if (aesKey == null) {
            aesKey = new SecretKeySpec(keyStr.getBytes(), "AES");
            cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        }
    }
	
    public String encrypt(String text) throws Exception {
    	init();
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(new byte[16]));
        return toHexString(cipher.doFinal(text.getBytes()));
    }
    
    public String decrypt(String text) throws Exception {
        init();
        cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(new byte[16]));
        return new String(cipher.doFinal(toByteArray(text)));
    }

    public String toHexString(byte[] array) {
        return DatatypeConverter.printHexBinary(array);
    }

    public byte[] toByteArray(String s) {
        return DatatypeConverter.parseHexBinary(s);
    }    
	
	public Message encryptMessage(Message mesg) {
		
		String mesgBody = "";
		ActiveMQTextMessage tm = initializeTextMessage(mesg);
		try {
			mesgBody = tm.getText();
		}
		catch (JMSException e) {
			LOG.error("Could not get message body contents for encryption \n" + e.getMessage());
		    return mesg;
		}
		
		try { 
		    mesgBody = encrypt(mesgBody);
		} 
		catch (Exception e) {
			LOG.error("Could not encrypt message\n" + e.getMessage());
			return mesg;
		}
		    
		LOG.debug("Encrypted message to: " + mesgBody);
		
		try {
		    tm.setText(mesgBody);
		}
		catch (Exception e) {
			LOG.error("Could not write to message body\n" + e.getMessage());
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
			LOG.error("Could not get message body contents for decryption \n" + e.getMessage());
		    return mesg;
		}
		
		try { 
		    mesgBody = decrypt(mesgBody);
		} 
		catch (Exception e) {
			LOG.error("Could not decrypt message\n" + e.getMessage());
			return mesg;
		}
		
		LOG.debug("Decrypting message to: " + mesgBody);
		
		try {
		    tm.setText(mesgBody);
		}
		catch (Exception e) {
			LOG.error("Could not write to message body\n" + e.getMessage());
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
	
	public void preProcessDispatch(MessageDispatch messageDispatch) {
		ActiveMQTextMessage encryptedMessage = (ActiveMQTextMessage) messageDispatch.getMessage();
		ActiveMQTextMessage decryptedMessage = (ActiveMQTextMessage) decryptMessage(encryptedMessage);
		messageDispatch.setMessage(decryptedMessage);
		next.preProcessDispatch(messageDispatch);		
	}
	
}
