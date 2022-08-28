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
package pinorobotics.msgmonster.app;

import id.xfunction.lang.XExec;
import id.xfunction.lang.XRE;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Ros1MsgCommand implements RosMsgCommand {

    @Override
    public boolean isPackage(Path input) {
        return new XExec("rosmsg packages")
                .run()
                .stderrThrow()
                .stdoutAsStream()
                .filter(Predicate.isEqual(input.toString()))
                .findFirst()
                .isPresent();
    }

    @Override
    public Stream<Path> listMessageFiles(Path rosPackage) {
        return new XExec("rosmsg package " + rosPackage)
                .run()
                .stderrThrow()
                .stdoutAsStream()
                .map(msg -> Paths.get(msg));
    }

    @Override
    public Optional<String> calcMd5Sum(Path msgFile) {
        var cmd = "rosmsg md5 " + msgFile;
        var proc = new XExec(cmd).run();
        if (proc.await() != 0) throw new XRE("md5sum calc error: " + proc.stderr());
        String md5sum = proc.stdout();
        if (md5sum.isEmpty()) throw new XRE("Command `%s` returned empty MD5", cmd);
        return Optional.of(md5sum);
    }

    @Override
    public Stream<String> lines(Path msgFile) {
        return new XExec("rosmsg show -r " + msgFile).run().stderrThrow().stdoutAsStream();
    }
}
