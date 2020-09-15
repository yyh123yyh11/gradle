/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.component;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.component.SoftwareComponentContainer;
import org.gradle.api.internal.DefaultNamedDomainObjectSet;
import org.gradle.api.internal.CollectionCallbackActionDecorator;
import org.gradle.api.model.ObjectFactory;
import org.gradle.internal.reflect.Instantiator;

public class DefaultSoftwareComponentContainer extends DefaultNamedDomainObjectSet<SoftwareComponent> implements SoftwareComponentContainer {
    private ConfigurationContainer configurations;
    private final ObjectFactory objects;

    public DefaultSoftwareComponentContainer(Instantiator instantiator, CollectionCallbackActionDecorator decorator, ObjectFactory objects) {
        super(SoftwareComponentInternal.class, instantiator, decorator);
        this.objects = objects;
    }

    public void setConfigurations(ConfigurationContainer configurations) {
        this.configurations = configurations;
    }

    @Override
    public void register(String name, Action<? super SoftwareComponent> action) {
        SoftwareComponent component = findByName(name);
        if (component == null) {
            component = new DefaultSoftwareComponent(name, configurations, objects);
            add(component);
        }
        action.execute(component);
    }
}
