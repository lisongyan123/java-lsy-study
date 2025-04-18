package com.lsy.java.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public  class ResultModel {
    private String string;
    private List<String> strings;
    private List<Integer> integers;
}