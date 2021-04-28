/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// ----------------------------------------------------------------------------
//  This class is largely adapted from "com.google.common.base.Preconditions",
//  which is part of the "Guava" library.
//
//  Because of frequent issues with dependency conflicts, this class was
//  added to the Flink code base to reduce dependency on Guava.
// ----------------------------------------------------------------------------

package my.linkin.lier;

import org.springframework.lang.Nullable;

import static java.lang.String.format;

/**
 * @author linkin
 */
public final class Preconditions {


    public static <T> T checkNotNull(@Nullable T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
    }


    public static <T> T checkNotNull(@Nullable T reference, @Nullable String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(String.valueOf(errorMessage));
        }
        return reference;
    }


    public static <T> T checkNotNull(
            T reference,
            @Nullable String errorMessageTemplate,
            @Nullable Object... errorMessageArgs) {

        if (reference == null) {
            throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
        }
        return reference;
    }


    public static void checkArgument(boolean condition) {
        if (!condition) {
            throw new IllegalArgumentException();
        }
    }


    public static void checkArgument(boolean condition, @Nullable Object errorMessage) {
        if (!condition) {
            throw new IllegalArgumentException(String.valueOf(errorMessage));
        }
    }

    public static void checkArgument(
            boolean condition,
            @Nullable String errorMessageTemplate,
            @Nullable Object... errorMessageArgs) {

        if (!condition) {
            throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
        }
    }

    private Preconditions() {
    }
}
