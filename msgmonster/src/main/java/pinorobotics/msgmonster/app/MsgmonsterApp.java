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
package pinorobotics.msgmonster.app;

import id.xfunction.ResourceUtils;
import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.cli.SmartArgs;
import id.xfunction.logging.XLogger;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import pinorobotics.msgmonster.Msgmonster;
import pinorobotics.msgmonster.ros.RosVersion;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class MsgmonsterApp {
    private static final XLogger LOGGER = XLogger.getLogger(MsgmonsterApp.class);
    private static final ResourceUtils resourceUtils = new ResourceUtils();

    private static void usage() {
        resourceUtils.readResourceAsStream("README-msgmonster.md").forEach(System.out::println);
    }

    public static void main(String[] args) throws Exception {
        try {
            XLogger.load("logging-msgmonster.properties");
            var app = new Msgmonster();
            Map<String, Consumer<String>> handlers =
                    Map.of(
                            "-exclude",
                            val -> {
                                app.setExcludePatterns(
                                        Arrays.stream(val.split(","))
                                                .map(Pattern::compile)
                                                .toList());
                            },
                            "-import",
                            val -> {
                                app.setUserImports(Arrays.stream(val.split(",")).toList());
                            });
            var positionalArgs = new ArrayList<String>();
            Function<String, Boolean> defaultHandler =
                    arg -> {
                        switch (arg) {
                            case "-d":
                                {
                                    XLogger.load("logging-debug-msgmonster.properties");
                                    return true;
                                }
                            default:
                                {
                                    positionalArgs.add(arg);
                                    return true;
                                }
                        }
                    };
            new SmartArgs(handlers, defaultHandler).parse(args);
            if (positionalArgs.size() < 4) {
                usage();
                return;
            }
            var rosVersion = RosVersion.valueOf(positionalArgs.get(0));
            var packageName = Paths.get(positionalArgs.get(1));
            var outputFolder = Paths.get(positionalArgs.get(3));
            outputFolder.toFile().mkdirs();
            LOGGER.info("Output folder {0}", outputFolder);
            var input = Paths.get(positionalArgs.get(2));
            app.run(rosVersion, packageName, input, outputFolder);
        } catch (ArgumentParsingException e) {
            usage();
        }
    }
}
