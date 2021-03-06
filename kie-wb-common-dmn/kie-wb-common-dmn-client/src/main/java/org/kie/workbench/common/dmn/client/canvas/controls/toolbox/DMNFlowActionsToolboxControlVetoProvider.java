/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.dmn.client.canvas.controls.toolbox;

import java.util.HashSet;
import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import org.kie.workbench.common.dmn.api.DMNDefinitionSet;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.FlowActionsToolboxControlProvider;
import org.kie.workbench.common.stunner.core.client.canvas.controls.toolbox.command.ToolboxCommandFactory;
import org.kie.workbench.common.stunner.core.client.components.toolbox.ToolboxFactory;
import org.kie.workbench.common.stunner.core.lookup.util.CommonLookups;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * A DMN-specific implementation of {@see FlowActionsToolboxControlProvider} that
 * prevents the default behaviour applying for DMN DefinitionSets.
 */
@Dependent
@Specializes
public class DMNFlowActionsToolboxControlVetoProvider extends FlowActionsToolboxControlProvider {

    private final DefinitionManager definitionManager;
    private final Set<String> dmnDefinitionIds = new HashSet<>();

    protected DMNFlowActionsToolboxControlVetoProvider() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public DMNFlowActionsToolboxControlVetoProvider(final ToolboxFactory toolboxFactory,
                                                    final DefinitionUtils definitionUtils,
                                                    final ToolboxCommandFactory defaultToolboxCommandFactory,
                                                    final CommonLookups commonLookups) {
        super(toolboxFactory,
              definitionUtils,
              defaultToolboxCommandFactory,
              commonLookups);
        this.definitionManager = definitionUtils.getDefinitionManager();
        final DMNDefinitionSet definitionSet = (DMNDefinitionSet) definitionManager.definitionSets().getDefinitionSetByType(DMNDefinitionSet.class);
        this.dmnDefinitionIds.addAll(definitionManager.adapters().forDefinitionSet().getDefinitions(definitionSet));
    }

    @Override
    public boolean supports(final Object definition) {
        final String definitionId = definitionManager.adapters().forDefinition().getId(definition);
        return !dmnDefinitionIds.contains(definitionId);
    }
}
