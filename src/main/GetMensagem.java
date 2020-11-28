package main;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class GetMensagem {

    private Tela            tela        = null;
    private String          usuario     = null;
    private String          broadcast   = null;
    private Context         jndiContext = null;
    private QueueConnection queueConn   = null;
    private TopicConnection topicconn   = null;
    
    public GetMensagem(String usuario, Tela tela) {
        this.tela    = tela;
        this.usuario = usuario;

        QueueConnectionFactory queueFactory  = null;
        TopicConnectionFactory topicFactory  = null;
        
        Queue queue = null;
        Topic topic = null;
        
        try {
            jndiContext = new InitialContext(); 
            queueFactory = (QueueConnectionFactory) jndiContext.lookup(Conf.FACTORY);
            topicFactory = (TopicConnectionFactory) jndiContext.lookup(Conf.FACTORY);
            queue        = (Queue)                  jndiContext.lookup(Conf.QUEUE_CHAT);
            topic        = (Topic)                  jndiContext.lookup(Conf.TOPIC_CHAT);
        } catch (NamingException e) { e.printStackTrace(); }
        
        try {
            queueConn = queueFactory.createQueueConnection();
            topicconn = topicFactory.createTopicConnection();
            
            QueueSession  queueSession = queueConn.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            TopicSession  topicSession = topicconn.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
            
            QueueReceiver   receiver   = queueSession.createReceiver(queue, "KEY='" + this.usuario + "'");
            TopicSubscriber subscriber = topicSession.createSubscriber(topic);
            
            receiver.setMessageListener(new RecebeMensagem());
            subscriber.setMessageListener(new RecebeMensagem());
            
            queueConn.start();
            topicconn.start();
        } catch (JMSException e) { e.printStackTrace(); }
    }
   
    public void Dispose(){
        try{
            queueConn.stop();
            topicconn.stop();
            queueConn.close();
            topicconn.close();
        } catch(JMSException e) { e.printStackTrace(); }
    }
   
    private class RecebeMensagem implements MessageListener {

        public void onMessage(Message message) {
            try {
                usuario   = message.getStringProperty(StrProperty.UNICAST);
                broadcast = message.getStringProperty(StrProperty.BROADCAST);

                if (message == null) tela.tx_area.setText(MsgPadrao.MENSAGEM_INVALIDA);
                else if (message instanceof TextMessage) {
                    
                    TextMessage msg = (TextMessage) message;
                    if (broadcast.equals(StrProperty.ALL)) tela.tx_area.setText(tela.tx_area.getText()+"\n"+msg.getText());
                    else tela.tx_area.setText(tela.tx_area.getText()+"\n"+msg.getText());
                    
                } else tela.tx_area.setText(MsgPadrao.MENSAGEM_INVALIDA);
            } catch (JMSException e) { e.printStackTrace(); }
        }
    }
}
