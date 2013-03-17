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

public class TopicListener {

        private static String SERVER_URL = "tcp://m2m.eclipse.org:1883";
        private static String CLIENT_ID = "pradeepClientListener";

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
            MqttTopic topic = mqttClient.getTopic("testTopic");
            mqttClient.subscribe("testTopic");

        }

    }
    }

