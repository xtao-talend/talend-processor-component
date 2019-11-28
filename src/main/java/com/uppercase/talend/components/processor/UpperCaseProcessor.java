package com.uppercase.talend.components.processor;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.text.CaseUtils;
import org.talend.sdk.component.api.component.Icon;
import org.talend.sdk.component.api.component.Version;
import org.talend.sdk.component.api.configuration.Option;
import org.talend.sdk.component.api.meta.Documentation;
import org.talend.sdk.component.api.processor.AfterGroup;
import org.talend.sdk.component.api.processor.BeforeGroup;
import org.talend.sdk.component.api.processor.ElementListener;
import org.talend.sdk.component.api.processor.Input;
import org.talend.sdk.component.api.processor.Output;
import org.talend.sdk.component.api.processor.OutputEmitter;
import org.talend.sdk.component.api.processor.Processor;
import org.talend.sdk.component.api.record.Record;

import org.talend.sdk.component.api.record.Schema;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;

@Version(1) // default version is 1, if some configuration changes happen between 2 versions you can add a migrationHandler
@Icon(Icon.IconType.STAR) // you can use a custom one using @Icon(value=CUSTOM, custom="filename") and adding icons/filename.svg in resources
@Processor(name = "UpperCaseProcessor")
@Documentation("UpperCaseProcessor")
public class UpperCaseProcessor implements Serializable {
    private final UpperCaseProcessorConfiguration configuration;

    @Service
    private final RecordBuilderFactory recordBuilderFactory;

    public UpperCaseProcessor(@Option("configuration") final UpperCaseProcessorConfiguration configuration, RecordBuilderFactory recordBuilderFactory) {
        this.configuration = configuration;
        this.recordBuilderFactory = recordBuilderFactory;
    }

    @PostConstruct
    public void init() {
        // this method will be executed once for the whole component execution,
        // this is where you can establish a connection for instance
        // Note: if you don't need it you can delete it
    }

    @BeforeGroup
    public void beforeGroup() {
        // if the environment supports chunking this method is called at the beginning if a chunk
        // it can be used to start a local transaction specific to the backend you use
        // Note: if you don't need it you can delete it
    }

    @ElementListener
    public void onNext(
            @Input final Record defaultInput,
            @Output final OutputEmitter<Record> defaultOutput) {
        // this is the method allowing you to handle the input(s) and emit the output(s)
        // after some custom logic you put here, to send a value to next element you can use an
        // output parameter and call emit(value).
        println("[UPPERCASE-PROCESSOR : ] UPPERCASE-PROCESSOR Begin!!!!!!!!!!!!!!!!!!!!");
        Schema schema = defaultInput.getSchema();
        println("[UPPERCASE-PROCESSOR : ] Origin record is <" + defaultInput + ">");
        Record.Builder recordBuilder = recordBuilderFactory.newRecordBuilder();
        List<Pair<String, Schema.Entry>> valueEntry = schema
                .getEntries()
                .stream()
                .filter(v -> Schema.Type.STRING.equals(v.getType()))
                .map(v -> {
                    if (configuration.getActiveCamelCase()) {
                        String newValue = CaseUtils.toCamelCase(defaultInput.getString(v.getName()), true, null);
                        return new Pair<>(newValue, v);
                    } else {
                        String newValue = defaultInput.getString(v.getName()).toUpperCase();
                        return new Pair<>(newValue, v);
                    }
                })
                .collect(Collectors.toList());

        for (Pair<String, Schema.Entry> ve : valueEntry) {
            recordBuilder.withString(ve.getRight(), ve.getLeft());
        }

        Record newRecord = recordBuilder.build();
        println("[UPPERCASE-PROCESSOR : ] New record is <" + newRecord + ">");

        defaultOutput
                .emit(newRecord);
    }

    @AfterGroup
    public void afterGroup() {
        // symmetric method of the beforeGroup() executed after the chunk processing
        // Note: if you don't need it you can delete it
    }

    @PreDestroy
    public void release() {
        // this is the symmetric method of the init() one,
        // release potential connections you created or data you cached
        // Note: if you don't need it you can delete it
    }

    private static void println(Object str) {
        System.out.println(str);
    }


}