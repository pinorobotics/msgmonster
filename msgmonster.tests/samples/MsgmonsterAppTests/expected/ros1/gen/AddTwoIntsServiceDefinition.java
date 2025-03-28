/*
 * Copyright 2021 msgmonster project
 * 
 * Website: https://github.com/pinorobotics/msgmonster
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * msgmonster autogenerated Java class for jrosclient
 * 
 * Generated for ROS file: test_msgs/AddTwoInts
 */

package id.jrosmessages.test_msgs;

import id.jrosmessages.MessageDescriptor;
import pinorobotics.jrosservices.msgs.ServiceDefinition;

/**
 * Definition for test_msgs/AddTwoInts
 */
public class AddTwoIntsServiceDefinition
        implements ServiceDefinition<AddTwoIntsRequestMessage, AddTwoIntsResponseMessage> {
    private static final MessageDescriptor<AddTwoIntsRequestMessage> REQUEST_MESSAGE_DESCRIPTOR =
            new MessageDescriptor<>(AddTwoIntsRequestMessage.class);
    private static final MessageDescriptor<AddTwoIntsResponseMessage> RESPONSE_MESSAGE_DESCRIPTOR =
            new MessageDescriptor<>(AddTwoIntsResponseMessage.class);

    @Override
    public MessageDescriptor<AddTwoIntsRequestMessage> getServiceRequestMessage() {
        return REQUEST_MESSAGE_DESCRIPTOR;
    }

    @Override
    public MessageDescriptor<AddTwoIntsResponseMessage> getServiceResponseMessage() {
        return RESPONSE_MESSAGE_DESCRIPTOR;
    }
}
