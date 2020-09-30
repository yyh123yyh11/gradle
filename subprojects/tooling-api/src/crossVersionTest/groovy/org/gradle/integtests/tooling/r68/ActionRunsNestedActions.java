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

package org.gradle.integtests.tooling.r68;

import org.gradle.tooling.BuildAction;
import org.gradle.tooling.BuildController;
import org.gradle.tooling.model.ProjectModel;
import org.gradle.tooling.model.gradle.BasicGradleProject;
import org.gradle.tooling.model.gradle.GradleBuild;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ActionRunsNestedActions implements BuildAction<ActionRunsNestedActions.Models> {
    @Override
    public Models execute(BuildController controller) {
        GradleBuild buildModel = controller.getBuildModel();
        List<GetProjectModel> projectActions = new ArrayList<GetProjectModel>();
        for (BasicGradleProject project : buildModel.getProjects()) {
            projectActions.add(new GetProjectModel(project));
        }
        List<String> results = controller.run(projectActions);
        return new Models(results);
    }

    private static class GetProjectModel implements BuildAction<String> {
        private final ProjectModel project;

        public GetProjectModel(ProjectModel project) {
            this.project = project;
        }

        @Override
        public String execute(BuildController controller) {
            return project.getProjectIdentifier().getProjectPath();
        }
    }

    public static class Models implements Serializable {
        private final List<String> projectPaths;

        public Models(Collection<String> projectPaths) {
            this.projectPaths = new ArrayList<String>(projectPaths);
        }

        public List<String> getProjectPaths() {
            return projectPaths;
        }
    }
}
