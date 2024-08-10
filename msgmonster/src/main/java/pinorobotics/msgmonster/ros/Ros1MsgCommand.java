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
import id.xfunction.lang.XRE;
import id.xfunction.logging.XLogger;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;
import pinorobotics.msgmonster.utils.MsgMonsterUtils;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class Ros1MsgCommand implements RosMsgCommand {
    private static final XLogger LOGGER = XLogger.getLogger(Ros1MsgCommand.class);

    private boolean isPackage(Path input) {
        return input.getNameCount() == 1;
    }

    @Override
    public Stream<RosFile> listFiles(Path rosPath) {
        if (isPackage(rosPath)) {
            return MsgMonsterUtils.runCommand("rosmsg package " + rosPath)
                    .map(msg -> Paths.get(msg))
                    .map(fileName -> RosFile.create(RosVersion.ros1, fileName))
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        } else {
            return RosFile.create(RosVersion.ros1, rosPath).stream();
        }
    }

    @Override
    public Optional<String> calcMd5Sum(Path msgFile) {
        var cmd = "rosmsg md5 " + msgFile;
        var exec = new XExec(cmd);
        LOGGER.fine("Executing command: {0}", Arrays.toString(exec.getCommand()));
        var proc = exec.start();
        if (proc.await() != 0) throw new XRE("md5sum calc error: " + proc.stderr());
        String md5sum = proc.stdout();
        if (md5sum.isEmpty()) throw new XRE("Command `%s` returned empty MD5", cmd);
        return Optional.of(md5sum);
    }

    @Override
    public Stream<String> lines(RosFile msgFile) {
        return MsgMonsterUtils.runCommand("rosmsg show -r " + msgFile.name());
    }

    @Override
    public RosVersion getRosVersion() {
        return RosVersion.ros1;
    }
}
