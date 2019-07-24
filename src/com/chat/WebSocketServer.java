package com.chat;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
 
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;
 
 
@ServerEndpoint(value = "/websocket/{username}")  
@Component
public class WebSocketServer {
	
	//��̬������������¼��ǰ������������Ӧ�ð�����Ƴ��̰߳�ȫ�ġ�  
    private static int onlineCount = 0;  
    //concurrent�����̰߳�ȫSet���������ÿ���ͻ��˶�Ӧ��MyWebSocket����  
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    //��ĳ���ͻ��˵����ӻỰ����Ҫͨ���������ͻ��˷�������  
    private Session session;  
    private String username;  
	@OnOpen
	public void onOPen(@PathParam("username") String username, Session session) {
		  this.username = username;  
		  this.session = session;  
	        webSocketSet.add(this);     //����set��  
	        addOnlineCount();           //��������1  
	        try {  
	             sendMessage("{\"msg\":\"success\"}");  
	        } catch (IOException e) {  
	           
	        }  
	}
 
	
	@OnMessage
	public void onMessage(String message, Session session) throws EncodeException, IOException {	
		session.getUserProperties();
			  System.out.println("�û���¼��"+message);
			  System.out.println("��ǰ����������"+ onlineCount);
			  sendInfo("�����յ������Ϣ");
	}
	
	@OnClose
	public void onClose() {
		  webSocketSet.remove(this);  //��set��ɾ��  
	      subOnlineCount();           //��������1  
	      System.out.println("��һ���ӹرգ���ǰ��������Ϊ" + getOnlineCount());  	
	}
	
	@OnError
	public void onErroe(Session session, Throwable error) {
		 System.out.println("��������");  
	     error.printStackTrace();  
	}
	public  void sendMessage(String message) throws IOException {  
	          this.session.getBasicRemote().sendText(message);  
	}  
	  /** 
     * Ⱥ���Զ�����Ϣ 
     * */  
    public  void sendInfo(String message) throws IOException {  
        for (WebSocketServer item : webSocketSet) {  
              item.sendMessage(message); 
            }  
    }
    /** 
     * �����Զ�����Ϣ 
     * */  
    public void sendSomeoneInfo(String message,String someone) throws IOException {  
        for (WebSocketServer item : webSocketSet) {  
            try { 
            	if(item.username.equals(someone)) {
            		item.sendMessage(message);  
            		System.out.println("��ʼ������Ϣ����"+someone);
            	}
            } catch (IOException e) {  
                continue;  
            }  
        }  
    }
	
   public static synchronized int getOnlineCount() {  
        return onlineCount;  
    }  
  
    public static synchronized void addOnlineCount() {  
        WebSocketServer.onlineCount++;  
    }  
  
    public static synchronized void subOnlineCount() {  
        WebSocketServer.onlineCount--;  
    }  
 
}
