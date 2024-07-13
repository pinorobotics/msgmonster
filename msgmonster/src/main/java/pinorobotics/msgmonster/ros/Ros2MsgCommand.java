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

import id.xfunction.lang.XExec;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class Ros2MsgCommand implements RosMsgCommand {

    private boolean isPackage(Path input) {
        return new XExec("ros2 interface packages")
                .start()
                .stderrThrow()
                .stdoutAsStream()
                .filter(Predicate.isEqual(input.toString()))
                .findFirst()
                .isPresent();
    }

    @Override
    public Stream<RosFile> listFiles(Path rosPath) {
        if (isPackage(rosPath)) {
            return new XExec("ros2 interface package " + rosPath)
                    .start()
                    .stderrThrow()
                    .stdoutAsStream()
                    .map(msg -> Paths.get(msg))
                    .map(RosFile::create)
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        } else {
            return RosFile.create(rosPath).stream();
        }
    }

    @Override
    public Stream<String> lines(RosFile msgFile) {
        return new XExec("ros2 interface show " + msgFile.name())
                .start()
                .stderrThrow()
                .stdoutAsStream()
                .filter(s -> !s.startsWith("\t"));
    }

    @Override
    public RosVersion getRosVersion() {
        return RosVersion.ros2;
    }
}
