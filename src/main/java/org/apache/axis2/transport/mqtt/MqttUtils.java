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

import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;
import org.apache.axis2.Constants;
import org.apache.axis2.builder.Builder;
import org.apache.axis2.builder.BuilderUtil;
import org.apache.axis2.builder.SOAPBuilder;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.format.DataSourceMessageBuilder;
import org.apache.axis2.format.TextMessageBuilder;
import org.apache.axis2.format.TextMessageBuilderAdapter;
import org.apache.axis2.transport.TransportUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.mail.internet.ContentType;
import javax.mail.internet.ParseException;
import java.io.ByteArrayInputStream;

public class MqttUtils {

    private static  Log log = LogFactory.getLog(MqttUtils.class);

    public static void setSOAPEnvelope(MqttMessage mqttMessage, MessageContext msgContext, String contentType)
            throws AxisFault, AxisMqttException {

        if (contentType == null) {
                contentType = "text/xml";         // TODO: hardcoding for the SOAP11 builder
        }

        int index = contentType.indexOf(';');
        String type = index > 0 ? contentType.substring(0, index) : contentType;
        Builder builder = BuilderUtil.getBuilderFromSelector(type, msgContext);
        if (builder == null) {
            if (log.isDebugEnabled()) {
                log.debug("No message builder found for type '" + type + "'. Falling back to SOAP.");
            }
            builder = new SOAPBuilder();
        }
        msgContext.setProperty(Constants.Configuration.CHARACTER_SET_ENCODING, "UTF-8");
        OMElement documentElement = null;
        try {
            byte[] bytes = mqttMessage.getPayload();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            documentElement = builder.processDocument(byteArrayInputStream,contentType,msgContext);
        } catch (MqttException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        msgContext.setEnvelope(TransportUtils.createSOAPEnvelope(documentElement));
    }

}
