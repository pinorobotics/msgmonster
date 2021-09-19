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
 * Authors:
 * - aeon_flux <aeon_flux@eclipso.ch>
 */
package pinorobotics.msgmonster.app;

import java.util.ArrayList;
import java.util.List;

public class MessageDefinition {

    private List<Field> fields = new ArrayList<>();
    private String comment;
    
    public MessageDefinition(String comment) {
        this.comment = comment;
    }

    public void addField(String type, String name, String comment) {
        fields.add(new Field(name, type, comment));
    }
    
    public String getComment() {
        return comment;
    }
    
//    public int getFieldsCount() {
//        return fields.size();
//    }
    
    public String toString() {
        var buf = new StringBuilder();
        buf.append(comment + "\n");
        for (int i = 0; i < fields.size(); i++) {
            buf.append(fields.get(i));
        }
        return buf.toString();
    }

    public List<Field> getFields() {
        return fields;
    }
}
