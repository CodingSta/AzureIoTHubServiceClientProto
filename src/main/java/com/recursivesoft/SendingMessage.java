package com.recursivesoft;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;

import com.microsoft.azure.sdk.iot.service.DeliveryAcknowledgement;
import com.microsoft.azure.sdk.iot.service.FeedbackBatch;
import com.microsoft.azure.sdk.iot.service.FeedbackReceiver;
import com.microsoft.azure.sdk.iot.service.IotHubServiceClientProtocol;
import com.microsoft.azure.sdk.iot.service.Message;
import com.microsoft.azure.sdk.iot.service.ServiceClient;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

public class SendingMessage implements ActionListener {
	
	private static final String connectionString = "HostName=.....";
    private static final IotHubServiceClientProtocol protocol = IotHubServiceClientProtocol.AMQPS;
    private static final String deviceId = "device01";
    ServiceClient serviceClient;
    boolean onOff = false;    
    // 생성자
	public SendingMessage() {
		try {
            this.serviceClient = ServiceClient.createFromConnectionString(connectionString, protocol);
            
           this.serviceClient.open();
           
        } catch (IOException e) {
            e.printStackTrace();
        }
		readyUI();
	}
	
	public void readyUI() {
		JFrame frameA = new JFrame();
		frameA.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frameA.setTitle("Cloud to Device");
		frameA.setSize(300, 300);
		
		JButton buttonA = new JButton("PushA");
		buttonA.addActionListener(this);
		frameA.getContentPane().add("Center",buttonA);
		frameA.setVisible(true);
	}

	public static void main(String[] args) {
		new SendingMessage();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		onOff = !onOff;
		try {
			FeedbackReceiver feedbackReceiver = serviceClient.getFeedbackReceiver();
			feedbackReceiver.open();
          
            Message msg = new Message(String.valueOf("value: " + onOff));
            msg.setDeliveryAcknowledgement(DeliveryAcknowledgement.Full);
            
            serviceClient.send(deviceId, msg);
            System.out.println("Message sent to device");
            
            FeedbackBatch feedbackBatch = feedbackReceiver.receive(10000);
            if (feedbackBatch != null) {
                System.out.println("Message feedback received, feedback time: "
                        + feedbackBatch.getEnqueuedTimeUtc().toString());
            }
            if (feedbackReceiver != null) feedbackReceiver.close();

        } catch (IotHubException ee) {
            ee.printStackTrace();
        } catch (IOException eee) {
            eee.printStackTrace();
        } catch (InterruptedException eeee) {
            eeee.printStackTrace();
        }
		
	}

}
