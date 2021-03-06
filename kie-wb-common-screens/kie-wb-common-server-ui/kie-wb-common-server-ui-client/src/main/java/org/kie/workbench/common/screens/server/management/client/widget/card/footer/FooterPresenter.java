/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.server.management.client.widget.card.footer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;

@Dependent
public class FooterPresenter {

    public interface View extends IsWidget {

        void setup( final String url,
                    final String version );
    }

    private final View view;

    @Inject
    public FooterPresenter( final View view ) {
        this.view = view;
    }

    public View getView() {
        return view;
    }

    public void setup( final String url,
                       final String version ) {
        view.setup( url, version );
    }

}
