/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.l2switch.loopremover.fuzz.generators;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.FluentFuture;
import com.google.common.util.concurrent.Futures;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import edu.cmu.pasta.seal.generator.object.GeneratorBase;
import org.eclipse.jdt.annotation.NonNull;
import org.opendaylight.controller.md.sal.binding.api.BindingTransactionChain;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.ReadOnlyTransaction;
import org.opendaylight.controller.md.sal.binding.api.ReadWriteTransaction;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.ReadFailedException;
import org.opendaylight.controller.md.sal.common.api.data.TransactionChainListener;
import org.opendaylight.mdsal.common.api.CommitInfo;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.Topology;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DataBrokerGenerator extends GeneratorBase<DataBroker> {
    public DataBrokerGenerator() {
        super(DataBroker.class);
    }

    @Override
    public DataBroker generateSingleton(SourceOfRandomness random, GenerationStatus status) {
        TestBroker dataBroker =
                new TestBroker(gen().make(FakeTopologyProviderGenerator.class).generate(random, status));
        return dataBroker;
    }

    public static class TestBroker implements DataBroker {

        private FakeTopologyProviderGenerator.FakeTopologyProvider provider;

        public TestBroker(FakeTopologyProviderGenerator.FakeTopologyProvider provider) {
            this.provider = provider;
        }

        @Override
        public ReadOnlyTransaction newReadOnlyTransaction() {
            return new ReadOnlyTransaction() {
                @Override
                public <T extends DataObject> CheckedFuture<Optional<T>, ReadFailedException> read(
                        LogicalDatastoreType store, InstanceIdentifier<T> path) {
                    return Futures.immediateCheckedFuture(Optional.of((T) provider.getTopology()));
                }

                @Override
                public void close() {

                }

                @Override
                public Object getIdentifier() {
                    return null;
                }
            };
        }

        @Override
        public ReadWriteTransaction newReadWriteTransaction() {
            return new ReadWriteTransaction() {
                @Override
                public <T extends DataObject> CheckedFuture<Optional<T>, ReadFailedException> read(
                        LogicalDatastoreType store, InstanceIdentifier<T> path) {
                    return null;
                }

                @Override
                public <T extends DataObject> void put(LogicalDatastoreType store, InstanceIdentifier<T> path, T data) {

                }

                @Override
                public <T extends DataObject> void put(LogicalDatastoreType store, InstanceIdentifier<T> path, T data,
                                                       boolean createMissingParents) {

                }

                @Override
                public <T extends DataObject> void merge(LogicalDatastoreType store, InstanceIdentifier<T> path,
                                                         T data) {

                }

                @Override
                public <T extends DataObject> void merge(LogicalDatastoreType store, InstanceIdentifier<T> path, T data,
                                                         boolean createMissingParents) {

                }

                @Override
                public void delete(LogicalDatastoreType store, InstanceIdentifier<?> path) {

                }

                @Override
                public boolean cancel() {
                    return false;
                }

                @Override
                public @NonNull FluentFuture<? extends @NonNull CommitInfo> commit() {
                    return null;
                }

                @Override
                public Object getIdentifier() {
                    return null;
                }
            };
        }

        @Override
        public WriteTransaction newWriteOnlyTransaction() {
            return null;
        }

        @Override
        public BindingTransactionChain createTransactionChain(TransactionChainListener listener) {
            return null;
        }

        @Override
        public @NonNull <T extends DataObject, L extends DataTreeChangeListener<T>> ListenerRegistration<L> registerDataTreeChangeListener(
                @NonNull DataTreeIdentifier<T> treeId, @NonNull L listener) {
            return null;
        }
    }
}
