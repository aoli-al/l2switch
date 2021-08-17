/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.l2switch.loopremover.fuzz.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import edu.cmu.pasta.seal.generator.object.GeneratorBase;
import org.opendaylight.l2switch.loopremover.topology.NetworkGraphImpl;
import org.opendaylight.l2switch.loopremover.topology.NetworkGraphService;

public class NetworkGraphServiceGenerator extends GeneratorBase<NetworkGraphService> {
    public NetworkGraphServiceGenerator() {
        super(NetworkGraphService.class);
    }

    @Override
    public NetworkGraphService generateSingleton(SourceOfRandomness random, GenerationStatus status) {
        NetworkGraphImpl impl = new NetworkGraphImpl();
        return impl;
    }
}
