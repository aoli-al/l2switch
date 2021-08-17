/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.l2switch.loopremover.fuzz.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import edu.cmu.pasta.seal.EventDispatcher;
import edu.cmu.pasta.seal.generator.InternalResult;
import org.onlab.junit.TestUtils;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Link;
import org.opendaylight.yang.gen.v1.urn.tbd.params.xml.ns.yang.network.topology.rev131021.network.topology.topology.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class GeneratorManager {
    private static GeneratorManager instance = null;

    private final Map<Class<?>, InternalResult> cache = new HashMap<>();
    private final Map<Object, Object> dataCache = new HashMap<>();
    private Set<Class<? extends GeneratorBase<?>>> registeredServices = new HashSet<>();
    private List<Class<? extends GeneratorBase<?>>> registeredServiceList = new ArrayList<>();
    private final Map<ConfiguredEvent, Set<Consumer<Object>>> listenerMap = new LinkedHashMap<>();
    private final List<ConfiguredEvent> registeredEvents = new ArrayList<>();
    private Class<?> targetService = null;
    private List<ConfiguredEvent> targetEvents = new ArrayList<>();
    private List<EventDispatcher.Record> records = new ArrayList<>();
    public List<Node> nodes = new ArrayList<>();
    public List<Link> links = new ArrayList<>();

    private GeneratorManager() {
    }

    public boolean containsObject(Class<?> objectClass, GenerationStatus status) {
//        if (currentStatus != status) {
//            currentStatus = status;
//            reset();
//        }
        return cache.containsKey(objectClass);
    }

    public void addConfigEvent(Object event, Consumer<Object> consumer) {
        records.add(new EventDispatcher.Record(event, consumer));
    }

    public void executeConfigEvents() {
        for (EventDispatcher.Record record : records) {
            record.consumer.accept(record.event);
        }
    }

    public void registerServices(Class<? extends GeneratorBase<?>>... services) {
        for (Class<? extends GeneratorBase<?>> service : services) {
            if (!registeredServices.contains(service)) {
                registeredServices.add(service);
                registeredServiceList.add(service);
            }
        }
        registeredServices.addAll(Arrays.asList(services));
    }

    public void setTargetService(Class<?> service) {
        targetService = service;
    }

    public <T> T getObject(Class<?> objectClass) {
        return (T) cache.get(objectClass).obj;
    }

    public List<ConfiguredEvent> getTargetEvents() {
        return targetEvents;
    }

    public Map<ConfiguredEvent, Set<Consumer<Object>>> getListenerMap() {
        return listenerMap;
    }

    public void putResult(Class<?> objectClass, InternalResult result) {
        cache.put(objectClass, result);
    }

    public Void reset() {
        nodes.clear();
        links.clear();
        records.clear();
        listenerMap.clear();
        registeredServices.clear();
        targetEvents.clear();
        cache.entrySet().removeIf(entry -> {
//            if (entry.getKey().getName().contains("Store")) {
            try {
                TestUtils.callMethod(entry.getValue().obj, "deactivate", new Class[0]);
            } catch (TestUtils.TestUtilsException e) {
//                    e.printStackTrace();
            }
            return true;
//            }
//            return false;
        });
        cache.clear();
        dataCache.clear();
        registeredEvents.clear();
        return null;
    }

    public <T> void registerListener(Class<?> from, Class<? extends GeneratorBase<? extends T>> event,
                                     Object config, Consumer<T> consumer) {
        // Only register registered classes.
        if (registeredServices.contains(from)) {
            ConfiguredEvent configuredEvent = new ConfiguredEvent(event, config);
            if (!listenerMap.containsKey(configuredEvent)) {
                registeredEvents.add(configuredEvent);
            }
            listenerMap.computeIfAbsent(configuredEvent, k -> new HashSet<>())
                    .add((Consumer<Object>) consumer);
            if (from == targetService) {
                targetEvents.add(configuredEvent);
            }
        }
    }

    public List<Class<? extends GeneratorBase<?>>> getRegisteredServiceList() {
        return registeredServiceList;
    }

    public List<ConfiguredEvent> getRegisteredEvents() {
        return registeredEvents;
    }


    public static GeneratorManager singleton() {
        if (instance == null) {
            instance = new GeneratorManager();
        }
        return instance;
    }

}
