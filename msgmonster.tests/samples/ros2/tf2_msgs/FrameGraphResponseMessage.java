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
 * Generated for ROS file: tf2_msgs/FrameGraphResponse
 */

package id.jrosmessages.test_msgs;

import java.util.Objects;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.Array;
import id.xfunction.XJson;
import id.xfunction.Preconditions;

import id.jrosmessages.std_msgs.StringMessage;

/**
 * Definition for tf2_msgs/FrameGraphResponse
 */
@MessageMetadata(
    name = FrameGraphResponseMessage.NAME
)
public class FrameGraphResponseMessage implements Message {
   
   static final String NAME = "tf2_msgs/FrameGraphResponse";

   public StringMessage frame_yaml = new StringMessage();
   
   public FrameGraphResponseMessage withFrameYaml(StringMessage frame_yaml) {
       this.frame_yaml = frame_yaml;
       return this;
   }
   
   @Override
   public int hashCode() {
       return Objects.hash(
           frame_yaml
       );
   }
   
   @Override
   public boolean equals(Object obj) {
       if (obj instanceof FrameGraphResponseMessage other)
           return
               Objects.equals(frame_yaml, other.frame_yaml)
           ;
       return false;
   }
   
   @Override
   public String toString() {
       return XJson.asString(
           "frame_yaml", frame_yaml
       );
   }
   
}
