package com.example.superroutes.interfaces;

import com.example.superroutes.R;
import com.example.superroutes.model.Difficulty;

public interface DiffcultyInterface {
    default int getDifficultyDrawable(String difficulty) {
        switch (difficulty) {
            case "EASY":
                return R.drawable.difficulty_easy;
            case "MEDIUM":
                return R.drawable.difficulty_moderate;
            case "HARD":
                return R.drawable.difficulty_hard;
            case "EXPERIENCED":
                return R.drawable.difficulty_expert;
            default:
                return 0;
        }
    }
}
