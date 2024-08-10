/*
 * Copyright 2024 msgmonster project
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
package pinorobotics.msgmonster.utils;

import id.xfunction.lang.XExec;
import id.xfunction.logging.XLogger;
import java.util.Arrays;
import java.util.stream.Stream;

public class MsgMonsterUtils {
    private static final XLogger LOGGER = XLogger.getLogger(MsgMonsterUtils.class);

    public static final Stream<String> runCommand(String cmd) {
        var exec = new XExec(cmd);
        LOGGER.fine("Executing command: {0}", Arrays.toString(exec.getCommand()));
        return exec.start().outputAsync(false).stderrThrow().stdout().lines();
    }
}
