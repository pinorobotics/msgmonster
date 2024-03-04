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
 * Generated for ROS msg file: std_msgs/Char
 */

package id.jrosmessages.test_msgs;


import java.util.Objects;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.Array;
import id.xfunction.XJson;
import id.xfunction.Preconditions;

/**
 * Definition for std_msgs/Char
 */
@MessageMetadata(
    name = CharMessage.NAME,
    md5sum = "1bf77f25acecdedba0e224b162199717"
)
public class CharMessage implements Message {
   
   static final String NAME = "std_msgs/Char";

   public byte data;
   
   public CharMessage withData(byte data) {
       this.data = data;
       return this;
   }
   
   @Override
   public int hashCode() {
       return Objects.hash(
           data
       );
   }
   
   @Override
   public boolean equals(Object obj) {
       var other = (CharMessage) obj;
       return
           data == other.data
       ;
   }
   
   @Override
   public String toString() {
       return XJson.asString(
           "data", data
       );
   }
   
}