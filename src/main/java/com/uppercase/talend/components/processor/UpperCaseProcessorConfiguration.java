package com.uppercase.talend.components.processor;

import java.io.Serializable;

import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.configuration.ui.layout.GridLayout;
import org.talend.sdk.component.api.meta.Documentation;

@GridLayout({
    // the generated layout put one configuration entry per line,
    // customize it as much as needed
    @GridLayout.Row({ "ActiveCamelCase" })
})
@Documentation("UpperCaseProcessorConfiguration")
public class UpperCaseProcessorConfiguration implements Serializable {
    @Option
    @Documentation("ActiveCamelCase")
    private boolean ActiveCamelCase;

    public boolean getActiveCamelCase() {
        return ActiveCamelCase;
    }

    public UpperCaseProcessorConfiguration setActiveCamelCase(boolean ActiveCamelCase) {
        this.ActiveCamelCase = ActiveCamelCase;
        return this;
    }
}