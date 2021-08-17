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
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.TopologyBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;

import java.util.ArrayList;
import java.util.List;

public class FakeTopologyProviderGenerator extends GeneratorBase<FakeTopologyProviderGenerator.FakeTopologyProvider> {
    public static class FakeTopologyProvider {
        private List<Link> links = new ArrayList<>();
        private List<Node> nodes = new ArrayList<>();

        public void deactivate() {
            links.clear();
            nodes.clear();
        }

        public Topology getTopology() {
            TopologyBuilder builder = new TopologyBuilder();
            builder.setLink(links);
            builder.setNode(nodes);
            return builder.build();
        }

        public void process(FakeTopologyEventGenerator.FakeTopologyEvent event) {
            if (event.node != null) {
                nodes.add(event.node);
            }
            if (event.link != null) {
                links.add(event.link);
            }
        }
    }


    public FakeTopologyProviderGenerator() {
        super(FakeTopologyProvider.class);
    }

    @Override
    public FakeTopologyProvider generateSingleton(SourceOfRandomness random, GenerationStatus status) {
        FakeTopologyProvider provider = new FakeTopologyProvider();
        generatorManager.registerListener(
                this.getClass(),
                FakeTopologyEventGenerator.class,
                null,
                provider::process
        );
        return provider;
    }
}
