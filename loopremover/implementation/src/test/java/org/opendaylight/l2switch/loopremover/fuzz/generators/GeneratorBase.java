/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.l2switch.loopremover.fuzz.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import edu.cmu.pasta.seal.generator.InternalResult;
import org.onlab.junit.TestUtils;

import java.lang.reflect.Field;
import java.util.function.Consumer;

import static edu.berkeley.cs.jqf.fuzz.JQF.LIST_SIZE_SMALL;

public abstract class GeneratorBase<T> extends Generator<T> {

    protected GeneratorManager generatorManager = GeneratorManager.singleton();
    protected Class<T> type;

    protected GeneratorBase(Class<T> type) {
        super(type);
        this.type = type;
    }

    @Override
    public T generate(SourceOfRandomness random, GenerationStatus status) {
        if (!generatorManager.containsObject(type, status)) {
            generatorManager.putResult(type, new InternalResult(generateSingleton(random, status), false));
        }
        return generatorManager.getObject(type);
    }

    protected void generateServices(Object obj, SourceOfRandomness random, GenerationStatus status) {
        Class<?> currentClass = obj.getClass();
        while (currentClass != null) {
            for (Field field : currentClass.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (field.get(obj) == null) {
                        field.set(obj, gen().type(field.getType()).generate(random, status));
                    }
                } catch (IllegalStateException | IllegalAccessException | IllegalArgumentException | UnsupportedOperationException ignored) {
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }

    public T generateSingleton(SourceOfRandomness random, GenerationStatus status) {
        return null;
    }

    protected void setInternal(Object object,
                               Class<? extends edu.cmu.pasta.seal.generator.object.GeneratorBase<?>> type,
                               String name, SourceOfRandomness random, GenerationStatus status) {
        Object field = gen().make(type).generate(random, status);
        TestUtils.setField(object, name, field);
    }

    protected <F> Generator<F> type(Class<F> aClass, Class<?>... classes) {
        return gen().type(aClass, classes);
    }


    protected <F> void fillConfigurations(
            Class<? extends edu.cmu.pasta.seal.generator.object.GeneratorBase<F>> configClass, Consumer<F> consumer,
            SourceOfRandomness random, GenerationStatus status) {
        int size = random.nextInt(LIST_SIZE_SMALL) + 1;
        for (int i = 0; i < size; i++) {
            F obj = gen().make(configClass).generate(random, status);
            consumer.accept(obj);
        }
    }
}
