package com.aidebugger.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DebugRequest {
    @NotBlank(message = "Language is required")
    @Size(min = 2, max = 20, message = "Language must be 2-20 characters")
    private String language;
    
    @NotBlank(message = "Code snippet is required")
    @Size(min = 10, max = 5000, message = "Code snippet must be 10-5000 characters")
    private String codeSnippet;
    
    private String context;
}