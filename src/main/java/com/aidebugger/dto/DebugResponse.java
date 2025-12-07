package com.aidebugger.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class DebugResponse {
    private String id;
    private String answer;
    private boolean cached;
}
