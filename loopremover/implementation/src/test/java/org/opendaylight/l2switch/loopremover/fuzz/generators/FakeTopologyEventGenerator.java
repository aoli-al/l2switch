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
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;

public class FakeTopologyEventGenerator extends GeneratorBase<FakeTopologyEventGenerator.FakeTopologyEvent> {
    public static class FakeTopologyEvent {
        public Link link;
        public Node node;

        public FakeTopologyEvent(Link link, Node node) {
            this.link = link;
            this.node = node;
        }
    }

    public FakeTopologyEventGenerator() {
        super(FakeTopologyEvent.class);
    }

    @Override
    public FakeTopologyEvent generate(SourceOfRandomness random, GenerationStatus status) {
        Link link = null;
        Node node = null;
        if (generatorManager.nodes.isEmpty() || random.nextBoolean()) {
            node = gen().make(NodeGenerator.class).generate(random, status);
        } else {
            link = gen().make(LinkGenerator.class).generate(random, status);
        }
        return new FakeTopologyEvent(link, node);
    }
}
