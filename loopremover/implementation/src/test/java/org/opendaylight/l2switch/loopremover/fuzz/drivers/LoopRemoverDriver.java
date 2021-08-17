/*
 * Copyright (c) 2014 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.opendaylight.l2switch.loopremover.fuzz.drivers;

import com.pholser.junit.quickcheck.From;
import edu.berkeley.cs.jqf.fuzz.Fuzz;
import edu.berkeley.cs.jqf.fuzz.JQF;
import edu.cmu.pasta.seal.EventDispatcher;
import edu.cmu.pasta.seal.generator.ConfigurationInstance;
import org.junit.runner.RunWith;
import org.opendaylight.l2switch.loopremover.fuzz.generators.EventDispatcherGenerator;
import org.opendaylight.l2switch.loopremover.fuzz.generators.FakeTopologyProviderGenerator;
import org.opendaylight.l2switch.loopremover.fuzz.generators.TopologyLinkDataChangeHandlerGenerator;

@RunWith(JQF.class)
public class LoopRemoverDriver {

    @Fuzz
    public void zest(
            @EventDispatcherGenerator.RegisteredServices(
                    services = {
                            TopologyLinkDataChangeHandlerGenerator.class,
                            FakeTopologyProviderGenerator.class
                    },
                    target = TopologyLinkDataChangeHandlerGenerator.class
            )
            @From(EventDispatcherGenerator.class) EventDispatcher dispatcher,
            @From(ConfigurationInstance.class) Object instance) {
        dispatcher.execute();
    }

}