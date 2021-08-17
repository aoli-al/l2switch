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
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.Identifier;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import java.util.Collection;

public class DataObjectModificationGenerator extends GeneratorBase<DataObjectModification> {

    public DataObjectModificationGenerator() {
        super(DataObjectModification.class);
    }

    @Override
    public DataObjectModification generate(SourceOfRandomness random, GenerationStatus status) {
        return new DataObjectModification() {
            ModificationType modificationType = random.choose(ModificationType.values());
            Link dataBefore = gen().make(LinkGenerator.class).generate(random, status);
            Link dataAfter = gen().make(LinkGenerator.class).generate(random, status);

            @Override
            public InstanceIdentifier.PathArgument getIdentifier() {
                return null;
            }

            @Override
            public @NonNull Class getDataType() {
                return null;
            }

            @Override
            public @NonNull ModificationType getModificationType() {
                return modificationType;
            }

            @Nullable
            @Override
            public DataObject getDataBefore() {
                return dataBefore;
            }

            @Nullable
            @Override
            public DataObject getDataAfter() {
                return dataAfter;
            }

            @Override
            public @NonNull Collection<? extends DataObjectModification<? extends DataObject>> getModifiedChildren() {
                return null;
            }

            @Override
            public @Nullable DataObjectModification getModifiedChildContainer(@NonNull Class child) {
                return null;
            }

            @Override
            public @Nullable DataObjectModification getModifiedAugmentation(@NonNull Class augmentation) {
                return null;
            }

            @Override
            public DataObjectModification getModifiedChildListItem(@NonNull Class listItem,
                                                                   @NonNull Identifier listKey) {
                return null;
            }

            @Override
            public @Nullable DataObjectModification<? extends DataObject> getModifiedChild(
                    InstanceIdentifier.PathArgument childArgument) {
                return null;
            }
        };
    }
}
