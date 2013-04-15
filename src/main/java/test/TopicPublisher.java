package test;/*
*  Copyright (c) 2005-2012, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/

import org.eclipse.paho.client.mqttv3.*;

public class TopicPublisher {

    private static String SERVER_URL = "tcp://localhost:1883";
    private static String CLIENT_ID = "pradeepClientPublisher";
    private static String PAYLOAD =  "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "   <soapenv:Header/>\n" +
            "<soapenv:Body>\n" +
            "       <p:echoInt xmlns:p=\"http://echo.services.core.carbon.wso2.org\">\n" +
            "      <in>" + 10 + "</in>\n" +
            "   </p:echoInt>\n" +
            "   </soapenv:Body>\n" +
            "</soapenv:Envelope>";


    public static void main(String[] args) throws MqttException {


        MqttClient mqttClient = new MqttClient(SERVER_URL,CLIENT_ID);
        MqttConnectOptions cleintOptions = new MqttConnectOptions(); // lets keep this to default..
        mqttClient.setCallback(new MqttCallback() {
            public void connectionLost(Throwable throwable) {
                System.out.println("Connection lost...");
            }

            public void messageArrived(MqttTopic mqttTopic, MqttMessage mqttMessage) throws Exception {
                System.out.println("Message arrived...");
                System.out.println("Topic : " + mqttTopic.toString());
                System.out.println("Message : " + mqttMessage.toString());
            }

            public void deliveryComplete(MqttDeliveryToken mqttDeliveryToken) {
                System.out.println("Delivery complete....");
                System.out.println("Delivery Token : " + mqttDeliveryToken.toString());
            }
        });
        mqttClient.connect(cleintOptions); // actual connection happens

        if (mqttClient.isConnected()) {
            System.out.println("Mqtt client connected successfully...");
            MqttTopic topic = mqttClient.getTopic("esb.test1");
            MqttMessage message = new MqttMessage();
            message.setPayload(PAYLOAD.getBytes());
            message.setRetained(true);
            topic.publish(message);

        }


        mqttClient.disconnect();

    }


}
