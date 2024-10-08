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
package pinorobotics.msgmonster.tests.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import pinorobotics.msgmonster.utils.MsgMonsterUtils;

/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
public class MsgMonsterUtilsTests {

    @Test
    public void test_no_hangs_on_big_output() {
        Assertions.assertEquals(999999, MsgMonsterUtils.runCommand("seq -w 999999").count());
    }
}
