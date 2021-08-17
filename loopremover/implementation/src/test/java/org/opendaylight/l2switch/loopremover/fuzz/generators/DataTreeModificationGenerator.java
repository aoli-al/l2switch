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
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;


public class DataTreeModificationGenerator extends GeneratorBase<DataTreeModification> {
    public DataTreeModificationGenerator() {
        super(DataTreeModification.class);
    }

    public static class FakeDataTreeModification implements DataTreeModification<Link> {
        DataObjectModification obj;

        public FakeDataTreeModification(DataObjectModification obj) {
            this.obj = obj;
        }

        @Override
        public @NonNull DataTreeIdentifier<Link> getRootPath() {
            return null;
        }

        @Override
        public @NonNull DataObjectModification<Link> getRootNode() {
            return obj;
        }
    }

    @Override
    public DataTreeModification generate(SourceOfRandomness random, GenerationStatus status) {
        return new FakeDataTreeModification(gen().make(DataObjectModificationGenerator.class).generate(random, status));
    }
}
