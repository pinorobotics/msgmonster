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
 * Generated for ROS file: test_msgs/FibonacciGoal
 */

package id.jrosmessages.test_msgs;


import java.util.Objects;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.Array;
import id.xfunction.XJson;
import id.xfunction.Preconditions;

/**
 * Definition for test_msgs/FibonacciGoal
 */
@MessageMetadata(
    name = FibonacciGoalMessage.NAME
)
public class FibonacciGoalMessage implements Message {
   
   static final String NAME = "test_msgs/FibonacciGoal";

   public int order;
   
   public FibonacciGoalMessage withOrder(int order) {
       this.order = order;
       return this;
   }
   
   @Override
   public int hashCode() {
       return Objects.hash(
           order
       );
   }
   
   @Override
   public boolean equals(Object obj) {
       if (obj instanceof FibonacciGoalMessage other)
           return
               order == other.order
           ;
       return false;
   }
   
   @Override
   public String toString() {
       return XJson.asString(
           "order", order
       );
   }
   
}
