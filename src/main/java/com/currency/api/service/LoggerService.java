package com.currency.api.service;

import com.currency.api.model.entity.Action;
import com.currency.api.repository.ActionRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@RequiredArgsConstructor
@Getter
@Setter
public class LoggerService {
    private final ActionRepository actionRepository;

    private Action action;

    public Action log(String actionName) {
        action = new Action();
        action.setName(actionName);
        action.setTime(new Timestamp(System.currentTimeMillis()));
        return actionRepository.save(action);
    }
}
