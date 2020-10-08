package org.appenders.log4j2.elasticsearch.hc;

import org.appenders.core.logging.InternalLogging;
import org.appenders.core.logging.Logger;
import org.appenders.log4j2.elasticsearch.ByteBufItemSourceTest;
import org.appenders.log4j2.elasticsearch.ItemSource;
import org.appenders.log4j2.elasticsearch.Result;
import org.appenders.log4j2.elasticsearch.SetupContext;
import org.junit.After;
import org.junit.Test;

import static org.appenders.core.logging.InternalLoggingTest.mockTestLogger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PutIndexTemplateTest {

    public static final String TEST_TEMPLATE_NAME = "testRolloverAlias";
    private static final ItemSource TEST_SOURCE = ByteBufItemSourceTest.createTestItemSource();

    @After
    public void tearDown() {
        InternalLogging.setLogger(null);
    }

    @Test
    public void doesNotExecuteOnFailure() {

        // given
        PutIndexTemplate setupStep = new PutIndexTemplate(TEST_TEMPLATE_NAME, TEST_SOURCE);
        SetupContext setupContext = new SetupContext(Result.FAILURE);

        // when
        boolean result = setupStep.shouldProcess(setupContext);

        // then
        assertFalse(result);

    }

    @Test
    public void executesOnSuccess() {

        // given
        PutIndexTemplate setupStep = new PutIndexTemplate(TEST_TEMPLATE_NAME, TEST_SOURCE);
        SetupContext setupContext = new SetupContext(Result.SUCCESS);

        // when
        boolean result = setupStep.shouldProcess(setupContext);

        // then
        assertTrue(result);

    }

    @Test
    public void executesOnSkip() {

        // given
        PutIndexTemplate setupStep = new PutIndexTemplate(TEST_TEMPLATE_NAME, TEST_SOURCE);
        SetupContext setupContext = new SetupContext(Result.SKIP);

        // when
        boolean result = setupStep.shouldProcess(setupContext);

        // then
        assertTrue(result);

    }

    @Test
    public void onResponseLogsOnSuccess() {

        // given
        PutIndexTemplate setupStep = new PutIndexTemplate(TEST_TEMPLATE_NAME, TEST_SOURCE);

        Response response = mock(Response.class);
        when(response.getResponseCode()).thenReturn(200);

        Logger logger = mockTestLogger();

        // when
        Result result = setupStep.onResponse(response);

        // then
        assertEquals(Result.SUCCESS, result);
        verify(logger).info(
                "{}: Index template {} updated",
                PutIndexTemplate.class.getSimpleName(),
                TEST_TEMPLATE_NAME);

    }

    @Test
    public void onResponseLogsOnNonSuccess() {

        // given
        PutIndexTemplate setupStep = new PutIndexTemplate(TEST_TEMPLATE_NAME, TEST_SOURCE);

        Response response = mock(Response.class);
        when(response.isSucceeded()).thenReturn(false);
        String error = "test index template creation error";
        when(response.getErrorMessage()).thenReturn(error);

        Logger logger = mockTestLogger();

        // when
        Result result = setupStep.onResponse(response);

        // then
        assertEquals(Result.FAILURE, result);
        verify(logger).error(
                "{}: Unable to update index template: {}",
                PutIndexTemplate.class.getSimpleName(),
                error);

    }

    @Test
    public void createsActualRequest() {

        // given
        PutIndexTemplate setupStep = new PutIndexTemplate(TEST_TEMPLATE_NAME, TEST_SOURCE);

        // when
        Request request = setupStep.createRequest();

        // then
        assertEquals("PUT", request.getHttpMethodName());
        assertEquals("_template/" + TEST_TEMPLATE_NAME, request.getURI());

    }

}