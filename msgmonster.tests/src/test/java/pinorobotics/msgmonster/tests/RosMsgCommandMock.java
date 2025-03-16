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
package pinorobotics.msgmonster.tests;

import id.xfunction.Checksum;
import id.xfunction.function.Unchecked;
import id.xfunction.nio.file.XPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;
import pinorobotics.msgmonster.ros.RosFile;
import pinorobotics.msgmonster.ros.RosInterfaceType;
import pinorobotics.msgmonster.ros.RosMsgCommand;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class RosMsgCommandMock implements RosMsgCommand {

    private Path folder;
    private RosVersion rosVersion;

    public RosMsgCommandMock(RosVersion rosVersion, Path folder) {
        this.rosVersion = rosVersion;
        this.folder = folder;
    }

    @Override
    public Optional<String> calcMd5Sum(Path msgFile) {
        return Optional.of(Unchecked.get(() -> Checksum.md5(msgFile.toString())));
    }

    @Override
    public Stream<String> lines(RosFile msgFile) {
        return Unchecked.get(() -> Files.lines(toFilePath(msgFile)));
    }

    @Override
    public RosVersion getRosVersion() {
        return rosVersion;
    }

    @Override
    public Stream<RosFile> listFiles(Path rosPackage) {
        return Unchecked.get(() -> Files.list(folder.resolve(rosPackage)).map(Path::getFileName))
                .map(msgName -> rosPackage.resolve(msgName))
                .peek(System.out::println)
                .sorted()
                .map(this::fromFilePath);
    }

    private Path toFilePath(RosFile msgFile) {
        var path = msgFile.name();
        if (path.getNameCount() == 3)
            path = path.getParent().getParent().resolve(path.getFileName());
        path =
                switch (msgFile.type()) {
                    case SERVICE -> folder.resolve(path + ".srv");
                    case ACTION -> folder.resolve(path + ".action");
                    default -> folder.resolve(path + ".msg");
                };
        return path;
    }

    private RosFile fromFilePath(Path filePath) {
        var tokens = XPaths.splitFileName(filePath.getFileName().toString());
        var rosName =
                switch (rosVersion) {
                    case ros2 -> filePath.resolveSibling(tokens[1]).resolve(tokens[0]);
                    default -> filePath.resolveSibling(tokens[0]);
                };
        var type =
                switch (tokens[1]) {
                    case "srv" -> RosInterfaceType.SERVICE;
                    case "action" -> RosInterfaceType.ACTION;
                    default -> RosInterfaceType.MESSAGE;
                };
        return new RosFile(rosName, type);
    }
}
