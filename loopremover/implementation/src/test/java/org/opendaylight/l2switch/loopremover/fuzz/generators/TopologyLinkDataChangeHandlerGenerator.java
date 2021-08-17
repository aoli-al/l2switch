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
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.l2switch.loopremover.topology.TopologyLinkDataChangeHandler;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;

import java.util.ArrayList;

public class TopologyLinkDataChangeHandlerGenerator extends GeneratorBase<TopologyLinkDataChangeHandler> {
    public TopologyLinkDataChangeHandlerGenerator() {
        super(TopologyLinkDataChangeHandler.class);
    }

    @Override
    public TopologyLinkDataChangeHandler generateSingleton(SourceOfRandomness random, GenerationStatus status) {
        TopologyLinkDataChangeHandler handler = new TopologyLinkDataChangeHandler(
                gen().make(DataBrokerGenerator.class).generate(random, status),
                gen().make(NetworkGraphServiceGenerator.class).generate(random, status)
        );

        generatorManager.registerListener(
                this.getClass(),
                DataTreeModificationGenerator.class,
                null,
                dataTreeModification -> {
                    ArrayList<DataTreeModification<Link>> array = new ArrayList<>();
                    array.add(dataTreeModification);
                    handler.onDataTreeChanged(array);
                }
        );

        return handler;
    }
}
