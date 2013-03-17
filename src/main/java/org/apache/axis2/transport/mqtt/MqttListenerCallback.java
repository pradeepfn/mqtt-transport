package org.apache.axis2.transport.mqtt;/*
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

import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.AxisEngine;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class MqttListenerCallback implements MqttCallback{

    private  ConfigurationContext configurationContext;

    public MqttListenerCallback(ConfigurationContext configurationContext) {
        this.configurationContext = configurationContext;
    }

    public void connectionLost(Throwable throwable) {
        // lets ignore this for the moment, till we get proper exception handling in place..
    }

    public void messageArrived(MqttTopic mqttTopic, MqttMessage mqttMessage) throws Exception {
        //build the message and hand it over to axisEngine
        MessageContext messageContext = configurationContext.createMessageContext();
        MqttUtils.setSOAPEnvelope(mqttMessage,messageContext,null);
        AxisEngine.receive(messageContext);

    }

    public void deliveryComplete(MqttDeliveryToken mqttDeliveryToken) {
       throw new IllegalStateException();
    }
}
