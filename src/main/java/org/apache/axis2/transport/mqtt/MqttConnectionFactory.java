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
import org.apache.axis2.description.Parameter;
import org.apache.axis2.description.ParameterIncludeImpl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.util.Hashtable;

public class MqttConnectionFactory {

    private static final Log log = LogFactory.getLog(MqttConnectionFactory.class);

    private String name;

    private Hashtable<String, String> parameters = new Hashtable<String, String>();

    public MqttConnectionFactory(Parameter passedInParameter) {
        this.name = passedInParameter.getName();
        ParameterIncludeImpl parameterInclude = new ParameterIncludeImpl();

        try {
            parameterInclude.deserializeParameters((OMElement) passedInParameter.getValue());
        } catch (AxisFault axisFault) {
           log.error("Error while reading properties for MQTT Connection Factory " + name , axisFault);
           throw new AxisMqttException(axisFault);
        }

        for (Object object : parameterInclude.getParameters()) {
            Parameter parameter = (Parameter) object;
            parameters.put(parameter.getName(), (String) parameter.getValue());
        }
    }

    public MqttConnectionFactory(Hashtable<String,String> parameters) {
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public MqttClient getMqttClient(){
        return createMqttClient();
    }

    private MqttClient createMqttClient() {
        String mqttEndpointURL= "tcp://" + parameters.get(MqttConstants.MQTT_SERVER_HOST_NAME) + ":" +
                parameters.get(MqttConstants.MQTT_SERVER_PORT);
        String uniqueClientId = parameters.get(MqttConstants.MQTT_CLIENT_ID);
        MqttClient mqttClient = null;
        try {
            mqttClient = new MqttClient(mqttEndpointURL,uniqueClientId);
        } catch (MqttException e) {
            log.error("Error while creating the MQTT client...", e);
        }

        return mqttClient;
    }

    public String getTopic(){
        return parameters.get(MqttConstants.MQTT_TOPIC_NAME);
    }
}
