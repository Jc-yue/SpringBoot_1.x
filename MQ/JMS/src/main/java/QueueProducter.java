import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;

/**
 * @author YJC
 * @version V1.0
 * @description: TODO
 * @date 2019/8/2 20:52
 */
public class QueueProducter {

    //queue方式的JMS连接
    QueueConnection queueConnection;
    //queue会话
    QueueSession queueSession;
    //queue消息发送者
    QueueSender queueSender;
    //消息队列
    Queue queue;

    public QueueProducter(String factoryJNDI, String queueJNDI) throws NamingException, JMSException {

//        //连接JMS Provider的环境参数
//        Hashtable<String, String> props = new Hashtable<String, String>();
//        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
//        //JMS provider的主机和端口
//        props.put(Context.PROVIDER_URL, "localhost:1099");
//        props.put("java.naming.rmi.security.manager", "yes");
//        props.put(Context.URL_PKG_PREFIXES, "org.jboss.naming");

        //lookup到连接工厂
        Context context = new InitialContext();
        QueueConnectionFactory queueFactory = (QueueConnectionFactory) context.lookup(factoryJNDI);
        //创建连接
        queueConnection = queueFactory.createQueueConnection();
        //创建session
        queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        //lookup到特定的消息队列
        queue = (Queue)context.lookup(queueJNDI);
        //创建发生者
        queueSender = queueSession.createSender(queue);
    }

    public void send(String msg) throws JMSException {
        TextMessage message = queueSession.createTextMessage();
        message.setText(msg);
        queueSender.send(queue, message);
    }

    public void close() throws JMSException {
        queueSession.close();
        queueConnection.close();
    }

    public static void main(String[] args) {
        try {
            QueueProducter queue = new QueueProducter("ConnectionFactory", "queue/testQueue");

            for (int i = 11; i < 21; i++) {
                String msg = "Hello World no. " + i;
                System.out.println("Hello Queue Publishing message: " + msg);
                queue.send(msg);
            }
            queue.close();
        } catch (Exception ex) {
            System.err.println("An exception occurred " + "while testing HelloPublisher25: " + ex);
            ex.printStackTrace();
        }
    }
}
