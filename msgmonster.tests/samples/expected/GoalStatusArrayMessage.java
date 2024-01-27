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
 * msgmonster autogenerated Message class for jrosclient
 * 
 * Generated for ROS msg file: test_msgs/GoalStatusArray
 */

package id.jrosmessages.test_msgs;

import java.util.Objects;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.Array;
import id.xfunction.XJson;
import id.xfunction.Preconditions;

import id.jrosmessages.std_msgs.HeaderMessage;
import java.util.Arrays;

/**
 * Definition for test_msgs/GoalStatusArray
 * 
 * <p>Stores the statuses for goals that are currently being tracked
 * by an action server
 * 
 * by an action server
 */
@MessageMetadata(
    name = GoalStatusArrayMessage.NAME,
    md5sum = "36d61c7054a071e6c0b3d668d1382f95")
public class GoalStatusArrayMessage implements Message {
   
   static final String NAME = "test_msgs/GoalStatusArray";

   public HeaderMessage header = new HeaderMessage();
   
   public GoalStatusMessage[] status_list = new GoalStatusMessage[0];
   
   public GoalStatusArrayMessage withHeader(HeaderMessage header) {
       this.header = header;
       return this;
   }
   
   public GoalStatusArrayMessage withStatusList(GoalStatusMessage... status_list) {
       this.status_list = status_list;
       return this;
   }
   
   @Override
   public int hashCode() {
       return Objects.hash(
           header,
           Arrays.hashCode(status_list)
       );
   }
   
   @Override
   public boolean equals(Object obj) {
       var other = (GoalStatusArrayMessage) obj;
       return
           Objects.equals(header, other.header) &&
           Arrays.equals(status_list, other.status_list)
       ;
   }
   
   @Override
   public String toString() {
       return XJson.asString(
           "header", header,
           "status_list", status_list
       );
   }
   
}
