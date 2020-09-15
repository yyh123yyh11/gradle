/*
 * Copyright 2020 the original author or authors.
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

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.DependencyConstraint;
import org.gradle.api.artifacts.ExcludeRule;
import org.gradle.api.artifacts.ModuleDependency;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.attributes.Usage;
import org.gradle.api.capabilities.Capability;

import java.util.Set;

public class DefaultSoftwareComponentVariant implements UsageContext {

    private final Configuration configuration;

    public DefaultSoftwareComponentVariant(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    @Deprecated
    public Usage getUsage() {
        throw new UnsupportedOperationException("This method has been deprecated, should never be called");
    }

    @Override
    public Set<? extends PublishArtifact> getArtifacts() {
        return configuration.getAllArtifacts();
    }

    @Override
    public Set<? extends ModuleDependency> getDependencies() {
        return configuration.getAllDependencies().withType(ModuleDependency.class);
    }

    @Override
    public Set<? extends DependencyConstraint> getDependencyConstraints() {
        return configuration.getAllDependencyConstraints();
    }

    @Override
    public Set<? extends Capability> getCapabilities() {
        return ImmutableSet.copyOf(configuration.getOutgoing().getCapabilities());
    }

    @Override
    public Set<ExcludeRule> getGlobalExcludes() {
        return getAllExcludeRules(configuration);
    }

    @Override
    public String getName() {
        return configuration.getName();
    }

    @Override
    public AttributeContainer getAttributes() {
        return configuration.getAttributes();
    }

    private Set<ExcludeRule> getAllExcludeRules(Configuration parent) {
        Set<ExcludeRule> result = Sets.newLinkedHashSet();
        result.addAll(parent.getExcludeRules());
        for (Configuration child : parent.getExtendsFrom()) {
            result.addAll(getAllExcludeRules(child));
        }
        return result;
    }
}
