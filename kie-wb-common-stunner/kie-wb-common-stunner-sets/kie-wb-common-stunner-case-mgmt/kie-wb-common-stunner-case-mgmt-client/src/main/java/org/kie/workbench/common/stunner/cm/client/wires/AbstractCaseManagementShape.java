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
package org.kie.workbench.common.stunner.cm.client.wires;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.MultiPath;
import com.ait.lienzo.client.core.shape.wires.WiresShape;
import org.kie.workbench.common.stunner.client.lienzo.shape.view.wires.WiresContainerShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.HasSize;
import org.kie.workbench.common.stunner.core.client.shape.view.event.ViewEventType;

public abstract class AbstractCaseManagementShape<T extends WiresContainerShapeView> extends WiresContainerShapeView<T> implements HasSize<T> {

    public static final ViewEventType[] CM_SHAPE_VIEW_EVENT_TYPES = new ViewEventType[]{
            ViewEventType.MOUSE_CLICK,
            ViewEventType.MOUSE_DBL_CLICK,
            ViewEventType.MOUSE_ENTER,
            ViewEventType.MOUSE_EXIT,
            ViewEventType.TEXT_ENTER,
            ViewEventType.TEXT_EXIT,
            ViewEventType.TEXT_CLICK,
            ViewEventType.TEXT_DBL_CLICK
    };

    private final double minWidth;
    private final double minHeight;
    private final Optional<MultiPath> optDropZone;
    private double currentWidth;
    private double currentHeight;

    public AbstractCaseManagementShape(final ViewEventType[] supportedEventTypes,
                                       final MultiPath path,
                                       final double minWidth,
                                       final double minHeight) {
        super(supportedEventTypes,
              path);
        this.minWidth = minWidth;
        this.minHeight = minHeight;
        this.currentWidth = minWidth;
        this.currentHeight = minHeight;
        this.optDropZone = makeDropZone();
        this.optDropZone.ifPresent((dz) -> dz.setDraggable(false));
    }

    public double getMinWidth() {
        return minWidth;
    }

    public double getWidth() {
        return currentWidth;
    }

    public void setWidth(final double width) {
        this.currentWidth = width;
        setSize(width,
                this.currentHeight);
    }

    public double getMinHeight() {
        return minHeight;
    }

    public double getHeight() {
        return currentHeight;
    }

    public void setHeight(final double height) {
        this.currentHeight = height;
        setSize(this.currentWidth,
                height);
    }

    public void logicallyReplace(final WiresShape original,
                                 final WiresShape replacement) {
        if (original == null) {
            return;
        }
        if (replacement == null) {
            return;
        }
        if (replacement.getParent() == this) {
            return;
        }

        getChildShapes().set(getIndex(original),
                             replacement);
        getContainer().getChildNodes().set(getNodeIndex(original.getGroup()),
                                           replacement.getGroup());

        original.setParent(null);
        replacement.setParent(this);

        if (original.getMagnets() != null) {
            original.getMagnets().shapeMoved();
        }

        if (replacement.getMagnets() != null) {
            replacement.getMagnets().shapeMoved();
        }

        getLayoutHandler().requestLayout(this);
    }

    public void addShape(final WiresShape shape,
                         final int targetIndex) {
        if (shape == null) {
            return;
        }
        if (targetIndex < 0 || targetIndex > getChildShapes().size()) {
            return;
        }
        final List<WiresShape> existingChildShapes = new ArrayList<>();
        existingChildShapes.addAll(getChildShapes().toList());
        existingChildShapes.stream().forEach(WiresShape::removeFromParent);

        existingChildShapes.remove(shape);
        existingChildShapes.add(targetIndex,
                                shape);

        //The call to add(..) causes ILayoutHandler to be invoked.
        existingChildShapes.stream().forEach(this::add);

        if (shape.getMagnets() != null) {
            shape.getMagnets().shapeMoved();
        }
    }

    private int getIndex(final WiresShape shape) {
        return getChildShapes().toList().indexOf(shape);
    }

    private int getNodeIndex(final Group group) {
        return getContainer().getChildNodes().toList().indexOf(group);
    }

    protected Optional<MultiPath> makeDropZone() {
        return Optional.empty();
    }

    public Optional<MultiPath> getDropZone() {
        return optDropZone;
    }

    public abstract AbstractCaseManagementShape getGhost();
}
