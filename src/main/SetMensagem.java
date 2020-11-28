package main;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class SetMensagem {
    public static void enviaUnicast(String mensagem, String usuario, String dest) {
        ConnectionFactory connectionFactory = null;
        
        Destination destination = null;
        Connection  connection  = null;
        Context     jndiContext = null;
        
        try { 
            jndiContext       = new InitialContext(); 
            connectionFactory = (ConnectionFactory) jndiContext.lookup(Conf.FACTORY);
            destination       = (Destination)       jndiContext.lookup(Conf.QUEUE_CHAT);
        } catch (NamingException e) { e.printStackTrace(); }

        try {
            connection               = connectionFactory.createConnection();
            Session session          = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(destination);
            TextMessage message      = session.createTextMessage();

            message.setStringProperty("KEY", dest);
            message.setStringProperty(StrProperty.UNICAST, usuario);
            message.setStringProperty(StrProperty.BROADCAST, StrProperty.ALL);
            message.setText(mensagem);
            producer.send(message);
        } catch (JMSException e) { e.printStackTrace(); }
        finally {
            if (connection != null) {
                try { connection.close(); }
                catch (JMSException e) { e.printStackTrace(); }
            }
        }
    }

    public static void enviaBroadCast(String mensagem, String usuario, String dest) {
        TopicConnectionFactory connectionFactory = null;
        
        TopicConnection  topicConn  = null;
        Topic            topic      = null;

        try {
            Context jndiContext = new InitialContext();
            connectionFactory   = (TopicConnectionFactory) jndiContext.lookup(Conf.FACTORY);
            topic               = (Topic)                  jndiContext.lookup(Conf.TOPIC_CHAT);
        } catch (NamingException e) { e.printStackTrace(); }
        
        try {
            topicConn                 = connectionFactory.createTopicConnection();
            TopicSession   session    = topicConn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicPublisher publisher  = session.createPublisher(topic);
            TextMessage    message    = session.createTextMessage();
            
            message.setText(mensagem);
            message.setStringProperty(StrProperty.UNICAST, usuario);
            message.setStringProperty(StrProperty.BROADCAST, StrProperty.ALL);
            
            publisher.publish(message);
            publisher.close();
            session.close();
        } catch (JMSException e) { e.printStackTrace(); }
        finally {
            if (topicConn != null) {
                try { topicConn.close(); }
                catch (JMSException e) { e.printStackTrace(); }
            }
        }
    }
}
