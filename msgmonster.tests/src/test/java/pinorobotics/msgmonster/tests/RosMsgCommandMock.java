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

import id.xfunction.XUtils;
import id.xfunction.function.Unchecked;
import id.xfunction.nio.file.XPaths;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import pinorobotics.msgmonster.app.RosMsgCommand;

public class RosMsgCommandMock extends RosMsgCommand {

    private Path folder;

    public RosMsgCommandMock(Path folder) {
        this.folder = folder;
    }

    @Override
    public Stream<Path> listMessageFiles(Path rosPackage) {
        return Unchecked.get(
                        () ->
                                Files.list(folder.resolve(rosPackage))
                                        .map(
                                                p ->
                                                        XPaths.splitFileName(
                                                                p.getFileName().toString())[0]))
                .map(msgName -> rosPackage.resolve(msgName))
                .peek(System.out::println);
    }

    @Override
    public String calcMd5Sum(Path msgFile) {
        return Unchecked.get(() -> XUtils.md5Sum(msgFile.toString()));
    }

    @Override
    public Stream<String> lines(Path msgFile) {
        return Unchecked.get(() -> Files.lines(folder.resolve(msgFile.toString() + ".msg")));
    }

    public boolean isPackage(Path input) {
        return true;
    }
}
