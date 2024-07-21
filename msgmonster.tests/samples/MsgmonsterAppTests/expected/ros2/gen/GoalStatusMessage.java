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
 * Generated for ROS file: test_msgs/GoalStatus
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
 * Definition for test_msgs/GoalStatus
 */
@MessageMetadata(
    name = GoalStatusMessage.NAME,
    fields = { "goal_id", "status", "text" }
)
public class GoalStatusMessage implements Message {
   
   static final String NAME = "test_msgs/GoalStatus";

   public enum UnknownType {
      /**
       * The goal has yet to be processed by the action server
       */
      PENDING,
      
      /**
       * The goal is currently being processed by the action server
       */
      ACTIVE,
      
      /**
       * The goal received a cancel request after it started executing
       */
      PREEMPTED,
      
      /**
       * and has since completed its execution (Terminal State)
       * The goal was achieved successfully by the action server (Terminal State)
       */
      SUCCEEDED,
      
      /**
       * The goal was aborted during execution by the action server due
       */
      ABORTED,
      
      /**
       * to some failure (Terminal State)
       * The goal was rejected by the action server without being processed,
       */
      REJECTED,
      
      /**
       * because the goal was unattainable or invalid (Terminal State)
       * The goal received a cancel request after it started executing
       */
      PREEMPTING,
      
      /**
       * and has not yet completed execution
       * The goal received a cancel request before it started executing,
       */
      RECALLING,
      
      /**
       * but the action server has not yet confirmed that the goal is canceled
       * The goal received a cancel request before it started executing
       */
      RECALLED,
      
      /**
       * and was successfully cancelled (Terminal State)
       * An action client can determine that a goal is LOST. This should not be
       */
      LOST,
      
      
   }
   
   public GoalIdMessage goal_id = new GoalIdMessage();
   
   public byte status;
   
   /**
    * sent over the wire by an action server
    * Allow for the user to associate a string with GoalStatus for debugging
    */
   public StringMessage text = new StringMessage();
   
   public GoalStatusMessage withGoalId(GoalIdMessage goal_id) {
       this.goal_id = goal_id;
       return this;
   }
   
   public GoalStatusMessage withStatus(byte status) {
       this.status = status;
       return this;
   }
   
   public GoalStatusMessage withText(StringMessage text) {
       this.text = text;
       return this;
   }
   
   @Override
   public int hashCode() {
       return Objects.hash(
           goal_id,
           status,
           text
       );
   }
   
   @Override
   public boolean equals(Object obj) {
       if (obj instanceof GoalStatusMessage other)
           return
               Objects.equals(goal_id, other.goal_id) &&
               status == other.status &&
               Objects.equals(text, other.text)
           ;
       return false;
   }
   
   @Override
   public String toString() {
       return XJson.asString(
           "goal_id", goal_id,
           "status", status,
           "text", text
       );
   }
   
}
