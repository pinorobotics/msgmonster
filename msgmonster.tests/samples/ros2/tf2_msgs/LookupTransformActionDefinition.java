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
 * Generated for ROS file: tf2_msgs/LookupTransform
 */

package id.jrosmessages.test_msgs;

import pinorobotics.jros2actionlib.actionlib_msgs.Action2Definition;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GetResultRequestMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2GoalMessage;
import pinorobotics.jros2actionlib.actionlib_msgs.Action2ResultMessage;

/**
 * Definition for tf2_msgs/action/LookupTransform
 */
public class LookupTransformActionDefinition
        implements Action2Definition<LookupTransformGoalMessage, LookupTransformResultMessage> {

    @Override
    public Class<? extends Action2GoalMessage<LookupTransformGoalMessage>> getActionGoalMessage() {
        return LookupTransformActionGoalMessage.class;
    }

    @Override
    public Class<? extends Action2ResultMessage<LookupTransformResultMessage>> getActionResultMessage() {
        return LookupTransformActionResultMessage.class;
    }

    @Override
    public Class<? extends Action2GetResultRequestMessage> getActionResultRequestMessage() {
        return LookupTransformActionGetResultRequestMessage.class;
    }
}