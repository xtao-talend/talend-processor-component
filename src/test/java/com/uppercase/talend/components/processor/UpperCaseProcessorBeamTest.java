package com.uppercase.talend.components.processor;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.io.Serializable;
import java.util.stream.StreamSupport;
import java.util.List;
import java.util.Map;

import org.apache.beam.sdk.PipelineResult;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.values.PCollection;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.Is;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.talend.sdk.component.api.record.Record;
import org.talend.sdk.component.api.service.Service;
import org.talend.sdk.component.api.service.record.RecordBuilderFactory;
import org.talend.sdk.component.junit.JoinInputFactory;
import org.talend.sdk.component.junit.SimpleComponentRule;
import org.talend.sdk.component.junit.beam.Data;
import org.talend.sdk.component.junit5.WithComponents;
import org.talend.sdk.component.junit5.WithMavenServers;
import org.talend.sdk.component.runtime.beam.TalendFn;
import org.talend.sdk.component.runtime.output.Processor;

@WithComponents("com.uppercase.talend.components.processor")
@WithMavenServers
public class UpperCaseProcessorBeamTest implements Serializable {
    @ClassRule
    public static final SimpleComponentRule COMPONENT_FACTORY = new SimpleComponentRule("com.uppercase.talend.components.processor");
    @Rule
    public transient final TestPipeline pipeline = TestPipeline.create();
    @Service
    protected RecordBuilderFactory recordBuilderFactory;
    @Test
    public void processor() {
        // Processor configuration
        // Setup your component configuration for the test here
        final UpperCaseProcessorConfiguration configuration =  new UpperCaseProcessorConfiguration()
                /*  .setActiveCamelCase() */;
        configuration.setActiveCamelCase(true);
        // We create the component processor instance using the configuration filled above
        final Processor processor = COMPONENT_FACTORY.createProcessor(UpperCaseProcessor.class, configuration);
        // The join input factory construct inputs test data for every input branch you have defined for this component
        // Make sure to fil in some test data for the branches you want to test
        // You can also remove the branches that you don't need from the factory below
        final JoinInputFactory joinInputFactory =  new JoinInputFactory()
                .withInput("__default__", asList(new Records("aaa", 22), new Records("camel case", 50)));
        // Convert it to a beam "source"
        final PCollection<Record> inputs =
                pipeline.apply(Data.of(processor.plugin(), joinInputFactory.asInputRecords()));
        // add our processor right after to see each data as configured previously
        final PCollection<Map<String, Record>> outputs = inputs.apply(TalendFn.asFn(processor))
                .apply(Data.map(processor.plugin(), Record.class));

        PAssert.that(outputs).satisfies((SerializableFunction<Iterable<Map<String, Record>>, Void>) input -> {
            final List<Map<String, Record>> result = StreamSupport.stream(input.spliterator(), false).collect(toList());
            result.forEach(r -> {
                MatcherAssert.assertThat(r.get("__default__").getString("name"), AnyOf.anyOf(Is.is("Aaa"), Is.is("CamelCase")));
            });
            return null;
        });


        // run the pipeline and ensure the execution was successful
        assertEquals(PipelineResult.State.DONE, pipeline.run().waitUntilFinish());
    }

    private static void println(Object str) {
        System.out.println(str);
    }


}