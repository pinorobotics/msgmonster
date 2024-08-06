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
import id.xfunction.logging.XLogger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class Ros2MsgCommand implements RosMsgCommand {
    private static final XLogger LOGGER = XLogger.getLogger(Ros2MsgCommand.class);

    private boolean isPackage(Path input) {
        return input.getNameCount() == 1;
    }

    @Override
    public Stream<RosFile> listFiles(Path rosPath) {
        if (isPackage(rosPath)) {
            var exec = new XExec("ros2 interface package " + rosPath);
            LOGGER.fine("Executing command: {0}", Arrays.toString(exec.getCommand()));
            return exec.start()
                    .stderrThrow()
                    .stdoutAsStream()
                    .map(msg -> Paths.get(msg))
                    .map(fileName -> RosFile.create(RosVersion.ros2, fileName))
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        } else {
            return RosFile.create(RosVersion.ros2, rosPath).stream();
        }
    }

    @Override
    public Stream<String> lines(RosFile msgFile) {
        var exec = new XExec("ros2 interface show " + msgFile.name());
        LOGGER.fine("Executing command: {0}", Arrays.toString(exec.getCommand()));
        return exec.start().stderrThrow().stdoutAsStream().filter(s -> !s.startsWith("\t"));
    }

    @Override
    public RosVersion getRosVersion() {
        return RosVersion.ros2;
    }
}
