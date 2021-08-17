/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.l2switch.loopremover.fuzz.generators;

import java.util.Objects;

public class ConfiguredEvent {
    private Class<? extends GeneratorBase<?>> eventGenerator;
    private Object config;

    public ConfiguredEvent(Class<? extends GeneratorBase<?>> eventGenerator, Object config) {
        this.eventGenerator = eventGenerator;
        this.config = config;
    }

    public Class<? extends GeneratorBase<?>> getEventGenerator() {
        return eventGenerator;
    }

    public Object getConfig() {
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfiguredEvent that = (ConfiguredEvent) o;
        return Objects.equals(eventGenerator, that.eventGenerator) && Objects.equals(config, that.config);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventGenerator, config);
    }
}
