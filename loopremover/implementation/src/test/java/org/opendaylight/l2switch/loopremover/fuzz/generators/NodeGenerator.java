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
import org.opendaylight.yang.gen.v1.urn.ietf.params.xml.ns.yang.ietf.inet.types.rev130715.Uri;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.NodeBuilder;

import static edu.berkeley.cs.jqf.fuzz.JQF.LIST_SIZE_LARGE;

public class NodeGenerator extends GeneratorBase<Node> {
    public NodeGenerator() {
        super(Node.class);
    }

    @Override
    public Node generate(SourceOfRandomness random, GenerationStatus status) {
        NodeBuilder builder = new NodeBuilder();
        builder.setNodeId(generateNodeId(random));
        Node node = builder.build();
        generatorManager.nodes.add(node);
        return node;
    }

    public NodeId generateNodeId(SourceOfRandomness random) {
        return new NodeId(generateUri(random));
    }

    public Uri generateUri(SourceOfRandomness random) {
        if (random.nextBoolean()) {
            return new Uri("of:" + random.nextInt(LIST_SIZE_LARGE));
        } else {
            return new Uri("host:" + random.nextInt(LIST_SIZE_LARGE));
        }
    }
}