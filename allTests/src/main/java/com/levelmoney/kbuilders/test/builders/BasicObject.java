/*
 * Copyright 2015 Level Money, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.levelmoney.kbuilders.test.builders;

public final class BasicObject {

    public final int value;

    public BasicObject(Builder builder) {
        this.value = builder.value;
    }

    public static class Builder {
        public int value;

        public Builder() {}

        public Builder value(int value) {
            this.value = value;

            Person bilbo = new Person();


            Person frodo = new Person.Builder(bilbo)
                    .firstName("Frodo")
                    .build();

            frodo.getClass();

            return this;
        }

        public BasicObject build() {
            return new BasicObject(this);
        }

    }

}
