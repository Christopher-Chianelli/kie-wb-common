/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.variablesEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.Variable;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.VariableRow;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VariablesEditorFieldRendererTest {

    @Mock
    private VariablesEditorWidgetView variablesEditorWidgetView;

    @Mock
    private VariableListItemWidgetView variableListItemWidgetView;

    @Spy
    @InjectMocks
    private VariablesEditorFieldRenderer variablesEditor = new VariablesEditorFieldRenderer(variablesEditorWidgetView);

    @Test
    public void testAddVariable() {
        when(variablesEditorWidgetView.getVariableWidget(anyInt())).thenReturn(variableListItemWidgetView);
        when(variablesEditorWidgetView.getVariableRowsCount()).thenReturn(1);
        variablesEditor.addVariable();
        verify(variablesEditorWidgetView,
               times(1)).setTableDisplayStyle();
        verify(variablesEditorWidgetView,
               times(1)).getVariableRowsCount();
        verify(variablesEditorWidgetView,
               times(1)).getVariableWidget(0);
        when(variablesEditorWidgetView.getVariableRowsCount()).thenReturn(2);
        variablesEditor.addVariable();
        verify(variablesEditorWidgetView,
               times(2)).getVariableRowsCount();
        verify(variablesEditorWidgetView,
               times(1)).getVariableWidget(1);
    }

    @Test
    public void testRemoveVariable() {
        when(variablesEditorWidgetView.getVariableWidget(anyInt())).thenReturn(variableListItemWidgetView);
        when(variablesEditorWidgetView.getVariableRowsCount()).thenReturn(1);
        variablesEditor.addVariable();
        variablesEditor.addVariable();
        variablesEditor.removeVariable(null);
        verify(variablesEditorWidgetView,
               times(3)).getVariableRows();
        verify(variablesEditorWidgetView,
               times(1)).doSave();
        variablesEditor.removeVariable(null);
        verify(variablesEditorWidgetView,
               times(4)).getVariableRows();
        verify(variablesEditorWidgetView,
               times(2)).doSave();
    }

    @Test
    public void testDeserializeVariables() {
        List<String> dataTypes = new ArrayList<String>(Arrays.asList("Boolean",
                                                                     "Float",
                                                                     "Integer",
                                                                     "Object",
                                                                     "org.veg.Potato",
                                                                     "String"));
        List<String> dataTypeDisplayNames = new ArrayList<String>(Arrays.asList("Boolean",
                                                                                "Float",
                                                                                "Integer",
                                                                                "Potato [org.veg]",
                                                                                "Object",
                                                                                "String"));

        variablesEditor.setDataTypes(dataTypes,
                                     dataTypeDisplayNames);
        List<VariableRow> variableRows = variablesEditor.deserializeVariables("var1:String,var2:Integer,var3:org.stuff.Potato");
        assertEquals(3,
                     variableRows.size());
        VariableRow var = variableRows.get(0);
        assertEquals("var1",
                     var.getName());
        assertEquals("String",
                     var.getDataTypeDisplayName());
        assertEquals(Variable.VariableType.PROCESS,
                     var.getVariableType());
        var = variableRows.get(1);
        assertEquals("var2",
                     var.getName());
        assertEquals("Integer",
                     var.getDataTypeDisplayName());
        assertEquals(Variable.VariableType.PROCESS,
                     var.getVariableType());
        var = variableRows.get(2);
        assertEquals("var3",
                     var.getName());
        assertEquals("org.stuff.Potato",
                     var.getCustomDataType());
        assertEquals(Variable.VariableType.PROCESS,
                     var.getVariableType());
    }

    @Test
    public void testSerializeVariables() {
        Map<String, String> mapDataTypeDisplayNamesToNames = new HashMap<String, String>();
        mapDataTypeDisplayNamesToNames.put("String",
                                           "String");
        mapDataTypeDisplayNamesToNames.put("Integer",
                                           "Integer");
        mapDataTypeDisplayNamesToNames.put("Potato [org.veg]",
                                           "org.veg.Potato");
        variablesEditor.mapDataTypeDisplayNamesToNames = mapDataTypeDisplayNamesToNames;

        List<VariableRow> variableRows = new ArrayList<VariableRow>();
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var1",
                                         "String",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var2",
                                         "Integer",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var3",
                                         "org.veg.Potato",
                                         null));
        String s = variablesEditor.serializeVariables(variableRows);
        assertEquals("var1:String,var2:Integer,var3:org.veg.Potato",
                     s);
    }

    @Test
    public void testIsDuplicateName() {
        List<VariableRow> variableRows = new ArrayList<VariableRow>();
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var1",
                                         "String",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var2",
                                         "Integer",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var3",
                                         "org.stuff.Potato",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         "var2",
                                         "Integer",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         null,
                                         "Object",
                                         null));
        variableRows.add(new VariableRow(Variable.VariableType.PROCESS,
                                         null,
                                         null,
                                         null));
        when(variablesEditorWidgetView.getVariableRows()).thenReturn(variableRows);
        assertTrue(variablesEditor.isDuplicateName("var2"));
        assertFalse(variablesEditor.isDuplicateName("var1"));
    }
}
