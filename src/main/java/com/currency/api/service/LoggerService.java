package com.currency.api.service;

import com.currency.api.model.entity.Action;
import com.currency.api.repository.ActionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

@Service
@AllArgsConstructor
public class LoggerService {
    private final ActionRepository actionRepository;

    public void log(String action){
        Action act = new Action();
        act.setAction(action);
        act.setTime(new Timestamp(System.currentTimeMillis()));
        actionRepository.save(act);
    }
}
