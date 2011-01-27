/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.felix.dm.test.bundle.annotation.extraproperties;

import java.util.HashMap;
import java.util.Map;

import org.apache.felix.dm.annotation.api.AspectService;
import org.apache.felix.dm.annotation.api.Component;
import org.apache.felix.dm.annotation.api.Property;
import org.apache.felix.dm.annotation.api.ServiceDependency;
import org.apache.felix.dm.annotation.api.Start;
import org.apache.felix.dm.test.bundle.annotation.sequencer.Sequencer;

/**
 * This test validates that an adapter Service may specify some extra service properties
 * from it's start callback
 */
public class ExtraAspectServiceProperties
{
    public interface Provider
    {
    }
    
    @Component(properties={@Property(name="foo", value="bar")})
    public static class ProviderImpl implements Provider
    {
    }
    
    @AspectService(ranking=10, properties={@Property(name="foo2", value="bar2")})
    public static class ProviderAspectImpl implements Provider
    {        
        @Start
        Map<String, String> start()
        {
            return new HashMap<String, String>() {{ put("foo3", "aspect"); }};
        }
    }
    
    @Component
    public static class Consumer
    {
        @ServiceDependency(filter="(test=ExtraAspectServiceProperties)")
        Sequencer m_sequencer;

        private Map m_properties;

        @ServiceDependency
        void bind(Map properties, Provider provider)
        {
            m_properties = properties;
        }
        
        @Start
        void start() 
        {
            System.out.println("provider aspect service properties: " + m_properties);
            if ("bar".equals(m_properties.get("foo"))) 
            {
                m_sequencer.step(1);
            }
            
            if ("bar2".equals(m_properties.get("foo2"))) 
            {
                m_sequencer.step(2);
            }         
            
            if ("aspect".equals(m_properties.get("foo3"))) 
            {
                m_sequencer.step(3);
            }            
        }
    }
}
