package com.vs.service;

import com.vs.entity.Score;
import com.vs.result.Result;
import org.springframework.stereotype.Service;

@Service
public interface ScoreService {
    Result FindScore(Score score);
}
