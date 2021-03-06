/*
 * Copyright (c) 2013, 2017 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package io.lighty.codecs.xml;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ValidationException extends Exception {
    private static final long serialVersionUID = -6072893219820274247L;

    private final Map<String/* module name */,
        Map<String/* instance name */,
        ExceptionMessageWithStackTrace>> failedValidations;

    public ValidationException(
            final Map<String /* module name */,
            Map<String /* instance name */,
            ExceptionMessageWithStackTrace>> failedValidations) {
        super(failedValidations.toString());
        this.failedValidations = Collections.unmodifiableMap(failedValidations);
    }

    public static ValidationException createFromCollectedValidationExceptions(
            final List<ValidationException> collectedExceptions) {
        Map<String, Map<String, ExceptionMessageWithStackTrace>> failedValidations = new HashMap<>();
        for (ValidationException ve : collectedExceptions) {
            for (Entry<String, Map<String, ExceptionMessageWithStackTrace>> outerEntry : ve.getFailedValidations()
                    .entrySet()) {
                for (Entry<String, ExceptionMessageWithStackTrace> innerEntry : outerEntry.getValue().entrySet()) {
                    String moduleName = outerEntry.getKey();
                    String instanceName = innerEntry.getKey();
                    ExceptionMessageWithStackTrace ex = innerEntry.getValue();
                    Map<String, ExceptionMessageWithStackTrace> instanceToExMap = failedValidations
                            .computeIfAbsent(moduleName, k -> new HashMap<>());
                    if (instanceToExMap.containsKey(instanceName)) {
                        throw new IllegalArgumentException("Cannot merge with same module name " + moduleName
                                + " and instance name " + instanceName);
                    }
                    instanceToExMap.put(instanceName, ex);
                }
            }
        }
        return new ValidationException(failedValidations);
    }

    public static ValidationException createForSingleException(final ModuleIdentifier moduleIdentifier,
            final Exception exception) {
        Map<String, Map<String, ExceptionMessageWithStackTrace>> failedValidations = new HashMap<>();
        Map<String, ExceptionMessageWithStackTrace> innerMap = new HashMap<>();

        failedValidations.put(moduleIdentifier.getFactoryName(), innerMap);
        innerMap.put(moduleIdentifier.getInstanceName(), new ExceptionMessageWithStackTrace(exception));
        return new ValidationException(failedValidations);
    }

    public Map<String/* module name */,
        Map<String/* instance name */,
        ExceptionMessageWithStackTrace>> getFailedValidations() {
        return failedValidations;
    }

    public static class ExceptionMessageWithStackTrace implements Serializable {
        private static final long serialVersionUID = 1L;

        private String message;
        private String stackTrace;

        public ExceptionMessageWithStackTrace() {
        }

        public ExceptionMessageWithStackTrace(final String message, final String stackTrace) {
            this.message = message;
            this.stackTrace = stackTrace;
        }

        public ExceptionMessageWithStackTrace(final Exception exception) {
            this(exception.getMessage(), Arrays.toString(exception.getStackTrace()));
        }

        public String getMessage() {
            return message;
        }

        public String getTrace() {
            return stackTrace;
        }

        public void setMessage(final String message) {
            this.message = message;
        }

        public void setStackTrace(final String trace) {
            this.stackTrace = trace;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (message == null ? 0 : message.hashCode());
            result = prime * result + (stackTrace == null ? 0 : stackTrace.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            ExceptionMessageWithStackTrace other = (ExceptionMessageWithStackTrace) obj;
            if (message == null) {
                if (other.message != null) {
                    return false;
                }
            } else if (!message.equals(other.message)) {
                return false;
            }
            if (stackTrace == null) {
                if (other.stackTrace != null) {
                    return false;
                }
            } else if (!stackTrace.equals(other.stackTrace)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return message;
        }
    }
}
