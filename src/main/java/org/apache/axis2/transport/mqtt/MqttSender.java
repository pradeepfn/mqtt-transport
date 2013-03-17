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

import org.apache.axiom.om.OMOutputFormat;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.description.TransportOutDescription;
import org.apache.axis2.transport.MessageFormatter;
import org.apache.axis2.transport.OutTransportInfo;
import org.apache.axis2.transport.TransportUtils;
import org.apache.axis2.transport.base.AbstractTransportSender;
import org.apache.axis2.transport.base.BaseUtils;
import org.apache.commons.io.output.WriterOutputStream;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Hashtable;

public class MqttSender extends AbstractTransportSender {

    private MqttConnectionFactoryManager connectionFactoryManager;
    private Hashtable<String,String> properties = new Hashtable<String, String>();
    private String targetEPR;

    @Override
    public void init(ConfigurationContext cfgCtx, TransportOutDescription transportOutDescription) throws AxisFault {
        super.init(cfgCtx, transportOutDescription);
        connectionFactoryManager = new MqttConnectionFactoryManager(transportOutDescription);
        log.info("Mqtt transport sender initialized....");
    }

    @Override
    public void sendMessage(MessageContext messageContext, String targetEPR, OutTransportInfo outTransportInfo) throws AxisFault {
        properties = BaseUtils.getEPRProperties(targetEPR);
        MqttConnectionFactory mqttConnectionFactory = new MqttConnectionFactory(properties);
        MqttClient mqttClient = mqttConnectionFactory.getMqttClient();
        try {
            mqttClient.setCallback(new MqttPublisherCallback());
            mqttClient.connect();

            if(mqttClient.isConnected()){
               MqttTopic mqttTopic = mqttClient.getTopic(mqttConnectionFactory.getTopic());
               MqttMessage mqttMessage = createMqttMessage(messageContext);
               mqttTopic.publish(mqttMessage);
            }
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private MqttMessage createMqttMessage(MessageContext messageContext) {
        OMOutputFormat format = BaseUtils.getOMOutputFormat(messageContext);
        MessageFormatter messageFormatter = null;
        try {
            messageFormatter = TransportUtils.getMessageFormatter(messageContext);
        } catch (AxisFault axisFault) {
            throw new AxisMqttException("Unable to get the message formatter to use");
        }

        String contentType = messageFormatter.getContentType(
                messageContext, format, messageContext.getSoapAction());

        OutputStream out;
        StringWriter sw = new StringWriter();
        try {
            out = new WriterOutputStream(sw, format.getCharSetEncoding());
        } catch (UnsupportedCharsetException ex) {
            throw  new AxisMqttException("Unsupported encoding " + format.getCharSetEncoding(), ex);
        }

        try {
            messageFormatter.writeTo(messageContext, format, out, true);
            out.close();
        } catch (IOException e) {
            throw new AxisMqttException("IO Error while creating BytesMessage", e);
        }

        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(sw.toString().getBytes());

        return mqttMessage;
    }
}
