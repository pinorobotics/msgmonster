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
 * Generated for ROS file: test_msgs/TestDifferentFields
 */

package id.jrosmessages.test_msgs;

import java.util.Objects;

import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.jrosmessages.Array;
import id.xfunction.XJson;
import id.xfunction.Preconditions;

import id.jros2messages.std_msgs.HeaderMessage;
import id.jrosmessages.primitives.Duration;
import id.jrosmessages.primitives.Time;

/**
 * Definition for test_msgs/TestDifferentFields
 */
@MessageMetadata(
    name = TestDifferentFieldsMessage.NAME,
    fields = { "id", "score", "header1", "header2", "timeout1", "timeout2", "stamp1", "stamp2" }
)
public class TestDifferentFieldsMessage implements Message {
   
   static final String NAME = "test_msgs/TestDifferentFields";

   public enum UnknownType {
      DRAWABLE_NOT_SET,
      
      DRAWABLE_FRAME_SET,
      
      DRAWABLE_SPHERE_SET,
      
      DRAWABLE_BOX_SET,
      
      DRAWABLE_ARROW_SET,
      
      DRAWABLE_CAPSULE_SET,
      
      DRAWABLE_CYLINDER_SET,
      
      DRAWABLE_LINESTRIP_SET,
      
      DRAWABLE_POINTS_SET,
      
      
   }
   
   public enum UnknownType {
      STATUS_UNKNOWN,
      
      STATUS_OK,
      
      STATUS_AMBIGUOUS,
      
      STATUS_HIGH_ERROR,
      
      
   }
   
   /**
    * Comment for HEADER_FIELD_SET
    * on multiple lines
    */
   public static final short HEADER_FIELD_SET = 1;
   
   public static final short IMAGE_RESPONSE_FIELD_SET = 4;
   
   /**
    * raandom comment for OTHER_DATA_FIELD_SET
    */
   public static final short OTHER_DATA_FIELD_SET = 32;
   
   public static final short CUSTOM_PARAM_ERROR_FIELD_SET = 128;
   
   public static final short ALERT_DATA_FIELD_SET = 256;
   
   public static final byte REQUEST_HEADER_FIELD_SET = 1;
   
   public static final byte REQUEST_RECEIVED_TIMESTAMP_FIELD_SET = 2;
   
   public static final byte RESPONSE_TIMESTAMP_FIELD_SET = 4;
   
   public static final byte ERROR_FIELD_SET = 8;
   
   /**
    * comment for last REQUEST_FIELD_SET
    */
   public static final byte REQUEST_FIELD_SET = 16;
   
   /**
    * field id
    */
   public long id;
   
   /**
    * this value should lie in the range [0-1].
    */
   public double score;
   
   /**
    * Test primitive types
    */
   public HeaderMessage header1 = new HeaderMessage();
   
   public HeaderMessage header2 = new HeaderMessage();
   
   public Duration timeout1 = new Duration();
   
   public Duration timeout2 = new Duration();
   
   public Time stamp1 = new Time();
   
   public Time stamp2 = new Time();
   
   public TestDifferentFieldsMessage withId(long id) {
       this.id = id;
       return this;
   }
   
   public TestDifferentFieldsMessage withScore(double score) {
       this.score = score;
       return this;
   }
   
   public TestDifferentFieldsMessage withHeader1(HeaderMessage header1) {
       this.header1 = header1;
       return this;
   }
   
   public TestDifferentFieldsMessage withHeader2(HeaderMessage header2) {
       this.header2 = header2;
       return this;
   }
   
   public TestDifferentFieldsMessage withTimeout1(Duration timeout1) {
       this.timeout1 = timeout1;
       return this;
   }
   
   public TestDifferentFieldsMessage withTimeout2(Duration timeout2) {
       this.timeout2 = timeout2;
       return this;
   }
   
   public TestDifferentFieldsMessage withStamp1(Time stamp1) {
       this.stamp1 = stamp1;
       return this;
   }
   
   public TestDifferentFieldsMessage withStamp2(Time stamp2) {
       this.stamp2 = stamp2;
       return this;
   }
   
   @Override
   public int hashCode() {
       return Objects.hash(
           id,
           score,
           header1,
           header2,
           timeout1,
           timeout2,
           stamp1,
           stamp2
       );
   }
   
   @Override
   public boolean equals(Object obj) {
       if (obj instanceof TestDifferentFieldsMessage other)
           return
               id == other.id &&
               score == other.score &&
               Objects.equals(header1, other.header1) &&
               Objects.equals(header2, other.header2) &&
               Objects.equals(timeout1, other.timeout1) &&
               Objects.equals(timeout2, other.timeout2) &&
               Objects.equals(stamp1, other.stamp1) &&
               Objects.equals(stamp2, other.stamp2)
           ;
       return false;
   }
   
   @Override
   public String toString() {
       return XJson.asString(
           "id", id,
           "score", score,
           "header1", header1,
           "header2", header2,
           "timeout1", timeout1,
           "timeout2", timeout2,
           "stamp1", stamp1,
           "stamp2", stamp2
       );
   }
   
}
