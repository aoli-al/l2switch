package org.opendaylight.l2switch.loopremover.fuzz.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;

public class ConfigurationInstance extends GeneratorBase<Object> {
    public ConfigurationInstance() {
        super(Object.class);
    }

    @Override
    public Object generate(SourceOfRandomness random, GenerationStatus status) {
        generatorManager.executeConfigEvents();
        return null;
    }
}