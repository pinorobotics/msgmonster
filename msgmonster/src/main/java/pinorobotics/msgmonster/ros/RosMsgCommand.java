/*
 * Copyright 2022 msgmonster project
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
package pinorobotics.msgmonster.ros;

import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public interface RosMsgCommand {

    Stream<Path> listMsgFiles(Path rosPackage);

    default Optional<String> calcMd5Sum(Path msgFile) {
        return Optional.empty();
    }

    Stream<String> lines(Path msgFile);

    RosVersion getRosVersion();
}
