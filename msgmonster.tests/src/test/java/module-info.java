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
/**
 * @author aeon_flux aeon_flux@eclipso.ch
 */
open module msgmonster.tests {
    requires id.xfunction;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.params;
    requires msgmonster;

    exports pinorobotics.msgmonster.tests.integration;
}
