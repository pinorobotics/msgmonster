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
 * Generated for ROS msg file: tf2_msgs/TFMessage
 */

package id.jrosmessages.test_msgs;

import java.util.Objects;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.Array;
import id.xfunction.XJson;
import id.xfunction.Preconditions;

import id.jrosmessages.geometry_msgs.TransformStampedMessage;
import java.util.Arrays;

/**
 * Definition for tf2_msgs/TFMessage
 */
@MessageMetadata(
    name = TFMessageMessage.NAME)
public class TFMessageMessage implements Message {
   
   static final String NAME = "tf2_msgs/TFMessage";

   public TransformStampedMessage[] transforms = new TransformStampedMessage[0];
   
   public TFMessageMessage withTransforms(TransformStampedMessage... transforms) {
       this.transforms = transforms;
       return this;
   }
   
   @Override
   public int hashCode() {
       return Objects.hash(
           Arrays.hashCode(transforms)
       );
   }
   
   @Override
   public boolean equals(Object obj) {
       var other = (TFMessageMessage) obj;
       return
           Arrays.equals(transforms, other.transforms)
       ;
   }
   
   @Override
   public String toString() {
       return XJson.asString(
           "transforms", transforms
       );
   }
   
}
