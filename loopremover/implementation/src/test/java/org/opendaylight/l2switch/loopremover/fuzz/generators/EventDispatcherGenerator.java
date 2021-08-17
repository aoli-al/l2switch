package org.opendaylight.l2switch.loopremover.fuzz.generators;

import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Generator;
import com.pholser.junit.quickcheck.generator.GeneratorConfiguration;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.berkeley.cs.jqf.fuzz.junit.quickcheck.FastSourceOfRandomness;
import edu.cmu.pasta.seal.EventDispatcher;
import org.onlab.junit.TestUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static edu.berkeley.cs.jqf.fuzz.JQF.LIST_SIZE_LARGE;
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;

public class EventDispatcherGenerator extends GeneratorBase<EventDispatcher> {

    //    protected static Map<Class, Class> providerMapping = Map.of(
//            HostServiceGenerator.class, HostProviderGenerator.class,
//            TopologyServiceGenerator.class, DefaultTopologyProviderGenerator.class,
//            LinkServiceGenerator.class, FakeLinkProviderGenerator.class,
//            DeviceServiceGenerator.class, FakeDeviceProviderGenerator.class
//    );
    private Map<Class, Class> providerMapping = new HashMap<>();



    @Target({PARAMETER, FIELD, ANNOTATION_TYPE, TYPE_USE})
    @Retention(RetentionPolicy.RUNTIME)
    @GeneratorConfiguration
    public @interface RegisteredServices {
        Class<? extends GeneratorBase<?>>[] services();
        Class<? extends GeneratorBase<?>> target();
        boolean repro() default false;
    }

    private Class<? extends GeneratorBase<?>>[] registeredServices = new Class[0];
    private Class<? extends GeneratorBase<?>> targetService;
    private boolean repro;


    public EventDispatcherGenerator() {
        super(EventDispatcher.class);
    }

    @Override
    public EventDispatcher generate(SourceOfRandomness random, GenerationStatus status) {
        generatorManager.reset();
        JQF.resetCallback = generatorManager::reset;
        generatorManager.registerServices(registeredServices);
        generatorManager.setTargetService(targetService);

        for (Class<? extends GeneratorBase<?>> registeredService : registeredServices) {
            if (providerMapping.containsKey(registeredService)) {
                generatorManager.registerServices(providerMapping.get(registeredService));
            }
        }

        for (Class<? extends GeneratorBase<?>> registeredService : generatorManager.getRegisteredServiceList()) {
            GeneratorBase<?> generator = gen().make(registeredService);
            generator.generate(random, status);
        }


        EventDispatcher dispatcher = new EventDispatcher();
        int size = ((FastSourceOfRandomness) random).getInterestingField("num-of-events", LIST_SIZE_LARGE);
        for (int i = 0; i < size; i++) {
            ConfiguredEvent configuredEvent = random.choose(generatorManager.getRegisteredEvents());
            Object event = generateConfiguredObject(configuredEvent, random, status);
            for (Consumer<Object> consumer : generatorManager.getListenerMap().get(configuredEvent)) {
                if (repro) {
                    dispatcher.addNewEvent(event, consumer);
                } else {
                    generatorManager.addConfigEvent(event, consumer);
                }
            }
        }

        for (ConfiguredEvent targetEvent : generatorManager.getTargetEvents()) {
            Object event = generateConfiguredObject(targetEvent, random, status);
            for (Consumer<Object> consumer: generatorManager.getListenerMap().get(targetEvent)) {
                dispatcher.addNewEvent(event, consumer);
            }
        }
        return dispatcher;
    }


    private Object generateConfiguredObject(ConfiguredEvent event, SourceOfRandomness random, GenerationStatus status) {
        Generator<?> generator = gen().make(event.getEventGenerator());
        Object config = event.getConfig();
        if (config != null) {
            TestUtils.callMethod(generator, "configure", config.getClass(), config);
        }
        return generator.generate(random, status);
    }

    public void configure(EventDispatcherGenerator.RegisteredServices services) {
        registeredServices = services.services();
        targetService = services.target();
        repro = services.repro();
    }
}

