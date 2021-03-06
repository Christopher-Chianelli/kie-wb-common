/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.rule.handler.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.ElementCardinalityContext;
import org.kie.workbench.common.stunner.core.rule.impl.Occurrences;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ElementCardinalityEvaluationHandlerTest extends AbstractGraphRuleHandlerTest {

    private final static Occurrences RULE_NO_LIMIT = new Occurrences("r1",
                                                                     CANDIDATE_ROLE1,
                                                                     0,
                                                                     -1);
    private final static Occurrences RULE_MIN_1 = new Occurrences("r2",
                                                                  CANDIDATE_ROLE1,
                                                                  1,
                                                                  -1);
    private final static Occurrences RULE_MAX_1 = new Occurrences("r3",
                                                                  CANDIDATE_ROLE1,
                                                                  0,
                                                                  1);
    private final static Occurrences RULE_MAX_0 = new Occurrences("r3",
                                                                  CANDIDATE_ROLE1,
                                                                  0,
                                                                  0);

    private final CardinalityEvaluationHandler HANDLER = new CardinalityEvaluationHandler();

    @Mock
    ElementCardinalityContext context;

    private ElementCardinalityEvaluationHandler tested;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        super.setup();
        tested = spy(new ElementCardinalityEvaluationHandler(definitionManager,
                                                             HANDLER));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAcceptsNoOp() {
        when(context.getCandidate()).thenReturn(Optional.empty());
        assertTrue(tested.accepts(RULE_NO_LIMIT,
                                  context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testAccepts() {
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        when(context.getCandidate()).thenReturn(Optional.of(candidate));
        assertTrue(tested.accepts(RULE_NO_LIMIT,
                                  context));
        when(context.getCandidate()).thenReturn(Optional.of(parent));
        assertFalse(tested.accepts(RULE_NO_LIMIT,
                                   context));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateSuccess() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                0);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.of(candidate));
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_NO_LIMIT,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluateSuccessAgain() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                100);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.of(candidate));
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_NO_LIMIT,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMin1EvaluateFailed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                0);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.empty());
        when(context.getOperation()).thenReturn(Optional.empty());
        final RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.WARNING).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMin1AddOk() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                0);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.of(candidate));
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMin1DeleteOk() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                2);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.of(candidate));
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.DELETE));
        final RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMin1DeleteFailed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                1);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.of(candidate));
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.DELETE));
        final RuleViolations violations = tested.evaluate(RULE_MIN_1,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.WARNING).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax1Ok() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                0);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.of(candidate));
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_MAX_1,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax1Failed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(2) {{
            put(CANDIDATE_ROLE1,
                1);
            put(CANDIDATE_ROLE2,
                0);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.of(candidate));
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_MAX_1,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax0Failed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                0);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.of(candidate));
        when(context.getOperation()).thenReturn(Optional.of(CardinalityContext.Operation.ADD));
        final RuleViolations violations = tested.evaluate(RULE_MAX_0,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax0EvaluateSuccess() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                0);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.empty());
        when(context.getOperation()).thenReturn(Optional.empty());
        final RuleViolations violations = tested.evaluate(RULE_MAX_0,
                                                          context);
        assertNotNull(violations);
        assertFalse(violations.violations(RuleViolation.Type.ERROR).iterator().hasNext());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMax0EvaluateFailed() {
        final Map<String, Integer> count = new HashMap<String, Integer>(1) {{
            put(CANDIDATE_ROLE1,
                1);
        }};
        doReturn(count).when(tested).countLabels(any(Graph.class),
                                                 anySet());
        when(context.getCandidate()).thenReturn(Optional.empty());
        when(context.getOperation()).thenReturn(Optional.empty());
        final RuleViolations violations = tested.evaluate(RULE_MAX_0,
                                                          context);
        assertNotNull(violations);
        assertTrue(violations.violations(RuleViolation.Type.WARNING).iterator().hasNext());
    }
}
