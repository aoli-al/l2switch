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
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.LinkId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.NodeId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.TpId;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.Destination;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.DestinationBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.Source;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.link.attributes.SourceBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.LinkBuilder;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.LinkKey;

import static edu.berkeley.cs.jqf.fuzz.JQF.LIST_SIZE_LARGE;

public class LinkGenerator extends GeneratorBase<Link> {
    public LinkGenerator() {
        super(Link.class);
    }

    @Override
    public Link generate(SourceOfRandomness random, GenerationStatus status) {
        LinkBuilder builder = new LinkBuilder();
        builder.withKey(generateLinkKey(random));
        builder.setDestination(generateDestination(random, status));
        builder.setSource(generateSource(random, status));
        return builder.build();
    }

    public Destination generateDestination(SourceOfRandomness random, GenerationStatus status) {
        DestinationBuilder builder = new DestinationBuilder();
        NodeId nodeId;
        if (generatorManager.nodes.isEmpty()) {
            nodeId = gen().make(NodeGenerator.class).generate(random, status).getNodeId();
        } else {
            nodeId = random.choose(generatorManager.nodes).getNodeId();
        }
        builder.setDestNode(nodeId);
        builder.setDestTp(new TpId(nodeId.getValue()));
        return builder.build();
    }

    public Source generateSource(SourceOfRandomness random, GenerationStatus status) {
        SourceBuilder builder = new SourceBuilder();
        NodeId nodeId;
        if (generatorManager.nodes.isEmpty()) {
            nodeId = gen().make(NodeGenerator.class).generate(random, status).getNodeId();
        } else {
            nodeId = random.choose(generatorManager.nodes).getNodeId();
        }
        builder.setSourceNode(nodeId);
        builder.setSourceTp(new TpId(nodeId.getValue()));
        return builder.build();
    }

    public LinkKey generateLinkKey(SourceOfRandomness random) {
        return new LinkKey(new LinkId(generateUri(random)));
    }

    public Uri generateUri(SourceOfRandomness random) {
        return new Uri("link:" + random.nextInt(LIST_SIZE_LARGE));
    }
}
