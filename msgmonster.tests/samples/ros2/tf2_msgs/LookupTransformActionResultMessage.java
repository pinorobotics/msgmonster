/*
 * Copyright 2024 msgmonster project
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
 * Generated for ROS file: tf2_msgs/LookupTransform
 */
package id.jrosmessages.test_msgs;

import id.jrosmessages.MessageMetadata;
import id.jrosmessages.RosInterfaceType;
import id.xfunction.XJson;
import java.util.Objects;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2ResultMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.StatusType;

/**
 * Definition for tf2_msgs/LookupTransform
 */
@MessageMetadata(
        name = LookupTransformActionResultMessage.NAME,
        fields = {"status", "result"},
        interfaceType = RosInterfaceType.ACTION)
public class LookupTransformActionResultMessage implements Action2ResultMessage<LookupTransformResultMessage> {

    static final String NAME = "tf2_msgs/LookupTransformActionResult";

    public byte status;

    public LookupTransformResultMessage result = new LookupTransformResultMessage();

    public LookupTransformActionResultMessage withStatus(byte status) {
        this.status = status;
        return this;
    }

    public LookupTransformActionResultMessage withResult(LookupTransformResultMessage result) {
        this.result = result;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, result);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LookupTransformActionResultMessage other)
            return Objects.equals(status, other.status) && Objects.equals(result, other.result);
        return false;
    }

    @Override
    public String toString() {
        return XJson.asString(
                "status", status,
                "result", result);
    }

    @Override
    public StatusType getStatus() {
        return StatusType.values()[status];
    }

    @Override
    public LookupTransformResultMessage getResult() {
        return result;
    }
}