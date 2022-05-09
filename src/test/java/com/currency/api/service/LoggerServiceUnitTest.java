package com.currency.api.service;

import com.currency.api.model.entity.Action;
import com.currency.api.repository.ActionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LoggerServiceUnitTest {

    @Mock
    private ActionRepository actionRepository;
    @InjectMocks
    private LoggerService loggerService;
    @BeforeEach
    private void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void logTest_ShouldLogAction() {
        loggerService.log("test");
        Action testAction = loggerService.getAction();
        Assertions.assertNotNull(testAction.getTime());
        Assertions.assertEquals(testAction.getName(), "test");
    }
}