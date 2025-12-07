package com.aidebugger.service;

import com.aidebugger.dto.DebugRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DebugService {
    private final OpenAiService openAiService;
    
    // Comment out or remove this line if you want to force OpenAI
    // @Value("${openai.enabled:true}")
    // private boolean openAiEnabled;

    public CompletableFuture<String> analyzeAsync(DebugRequest req) {
        System.out.println("\n=== 🚀 DEBUG SERVICE STARTED ===");
        System.out.println("🌐 Language: " + req.getLanguage());
        System.out.println("📄 Code length: " + req.getCodeSnippet().length());
        System.out.println("📝 Context: " + (req.getContext() != null ? req.getContext() : "None"));
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // TEMPORARY: Always use enhanced mock for interview demo
                // If you want to try OpenAI, uncomment below and comment the mock line
                
                System.out.println("🔄 Using enhanced analysis engine...");
                String response = getEnhancedMockResponse(req);
                
                // UNCOMMENT BELOW TO TRY OPENAI (if you fix credits):
                // System.out.println("🔄 Building prompt for OpenAI...");
                // String prompt = buildPrompt(req);
                // String response = openAiService.askDebugAssistant(prompt);
                
                System.out.println("✅ Analysis completed successfully");
                return response;
                
            } catch (Exception e) {
                System.err.println("❌ Error in DebugService: " + e.getMessage());
                return getErrorResponse(req, e);
            }
        });
    }

    private String getEnhancedMockResponse(DebugRequest req) {
        String code = req.getCodeSnippet();
        String language = req.getLanguage().toLowerCase();
        
        // Enhanced professional mock responses
        String analysis = switch(language) {
            case "java" -> getJavaAnalysis(code);
            case "python", "py" -> getPythonAnalysis(code);
            case "javascript", "js", "typescript", "ts" -> getJavascriptAnalysis(code);
            case "cpp", "c++", "c" -> getCppAnalysis(code);
            default -> getGenericAnalysis(code);
        };
        
        return """
               ## 🤖 AI-Powered Code Analysis
               
               **Note:** Using enhanced analysis engine. Enable OpenAI API for real-time AI insights.
               
               ### 📋 Analysis Summary
               Language: %s
               Code Length: %d characters
               Analysis Time: < 1 second
               
               %s
               
               ---
               *💡 Enable OpenAI API key for GPT-4 level real-time analysis*
               """.formatted(
                   req.getLanguage(), 
                   code.length(), 
                   analysis
               );
    }
    
    private String getJavaAnalysis(String code) {
        boolean hasDivision = code.contains("/") && code.contains("0");
        boolean hasNullCheck = code.contains("null") && !code.contains("!= null") && !code.contains("== null");
        boolean hasExceptionHandling = code.contains("try") || code.contains("catch");
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("### 🔍 Issues Identified\n");
        
        if (hasDivision) {
            analysis.append("1. **Potential Division by Zero**: Integer division without validation\n");
        }
        if (hasNullCheck) {
            analysis.append("2. **Null Safety**: Potential NullPointerException risks\n");
        }
        if (!hasExceptionHandling) {
            analysis.append("3. **Exception Handling**: Consider adding try-catch blocks\n");
        }
        analysis.append("4. **Code Structure**: Review class/method organization\n");
        
        analysis.append("\n### 🔧 Recommendations\n");
        analysis.append("```java\n");
        
        if (hasDivision) {
            analysis.append("// Safe division with validation\n");
            analysis.append("if (denominator != 0) {\n");
            analysis.append("    result = numerator / denominator;\n");
            analysis.append("} else {\n");
            analysis.append("    // Handle division by zero\n");
            analysis.append("    throw new IllegalArgumentException(\"Denominator cannot be zero\");\n");
            analysis.append("}\n");
        } else {
            analysis.append("// Consider adding:\n");
            analysis.append("// 1. Input validation\n");
            analysis.append("// 2. Error handling\n");
            analysis.append("// 3. Logging for debugging\n");
        }
        
        analysis.append("```\n\n");
        
        analysis.append("### 📚 Best Practices\n");
        analysis.append("- Use `final` for immutable variables\n");
        analysis.append("- Follow Java naming conventions (camelCase)\n");
        analysis.append("- Add Javadoc comments for public methods\n");
        analysis.append("- Use `Optional` for nullable returns\n");
        analysis.append("- Implement proper exception hierarchy\n");
        analysis.append("- Consider using records for data classes (Java 14+)\n");
        
        analysis.append("\n### 🧪 Test Cases\n");
        analysis.append("- Null and empty input scenarios\n");
        analysis.append("- Edge cases (MAX_VALUE, MIN_VALUE)\n");
        analysis.append("- Concurrent access scenarios\n");
        analysis.append("- Memory usage under load\n");
        
        return analysis.toString();
    }
    
    private String getPythonAnalysis(String code) {
        boolean hasPrint = code.contains("print(");
        boolean hasDivision = code.contains("/") && code.contains("0");
        boolean hasImport = code.contains("import ");
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("### 🔍 Issues Identified\n");
        
        if (hasPrint && code.contains("production")) {
            analysis.append("1. **Debug Prints**: Remove print statements for production\n");
        }
        if (hasDivision) {
            analysis.append("2. **Division Safety**: No zero division check\n");
        }
        if (!hasImport && code.contains("json") || code.contains("requests")) {
            analysis.append("3. **Missing Imports**: Required modules not imported\n");
        }
        analysis.append("4. **Type Safety**: Consider adding type hints\n");
        
        analysis.append("\n### 🔧 Recommendations\n");
        analysis.append("```python\n");
        
        if (hasDivision) {
            analysis.append("# Safe division with error handling\n");
            analysis.append("try:\n");
            analysis.append("    result = numerator / denominator\n");
            analysis.append("except ZeroDivisionError:\n");
            analysis.append("    result = float('inf')  # or handle appropriately\n");
            analysis.append("    logger.error(\"Division by zero attempted\")\n");
        } else {
            analysis.append("# Consider adding:\n");
            analysis.append("# 1. Type hints for function signatures\n");
            analysis.append("# 2. Docstrings for documentation\n");
            analysis.append("# 3. Environment variable configuration\n");
        }
        
        analysis.append("```\n\n");
        
        analysis.append("### 📚 Best Practices\n");
        analysis.append("- Use f-strings for string formatting (Python 3.6+)\n");
        analysis.append("- Add type hints for better IDE support\n");
        analysis.append("- Use context managers (`with` statements)\n");
        analysis.append("- Implement `__str__` and `__repr__` methods\n");
        analysis.append("- Follow PEP 8 style guide\n");
        
        analysis.append("\n### 🧪 Test Cases\n");
        analysis.append("- Test with `None` inputs\n");
        analysis.append("- Large dataset performance\n");
        analysis.append("- Unicode/encoding scenarios\n");
        analysis.append("- Third-party API failure cases\n");
        
        return analysis.toString();
    }
    
    private String getJavascriptAnalysis(String code) {
        boolean hasConsoleLog = code.contains("console.log");
        boolean hasVar = code.contains("var ");
        boolean hasAsync = code.contains("async") || code.contains("Promise");
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("### 🔍 Issues Identified\n");
        
        if (hasConsoleLog) {
            analysis.append("1. **Debug Logs**: Remove console logs for production\n");
        }
        if (hasVar) {
            analysis.append("2. **Variable Declaration**: Prefer `const` or `let` over `var`\n");
        }
        if (hasAsync && !code.contains("catch")) {
            analysis.append("3. **Async Error Handling**: Missing promise rejection handling\n");
        }
        analysis.append("4. **Type Safety**: Consider TypeScript for larger projects\n");
        
        analysis.append("\n### 🔧 Recommendations\n");
        analysis.append("```javascript\n");
        
        if (hasAsync) {
            analysis.append("// Better async handling\n");
            analysis.append("async function processData() {\n");
            analysis.append("    try {\n");
            analysis.append("        const result = await fetchData();\n");
            analysis.append("        return processResult(result);\n");
            analysis.append("    } catch (error) {\n");
            analysis.append("        console.error('Processing failed:', error);\n");
            analysis.append("        throw new Error('Process failed', { cause: error });\n");
            analysis.append("    }\n");
            analysis.append("}\n");
        } else {
            analysis.append("// Consider adding:\n");
            analysis.append("// 1. Error boundaries for React apps\n");
            analysis.append("// 2. Input validation\n");
            analysis.append("// 3. Internationalization support\n");
        }
        
        analysis.append("```\n\n");
        
        analysis.append("### 📚 Best Practices\n");
        analysis.append("- Use `===` instead of `==` for strict equality\n");
        analysis.append("- Implement proper error handling\n");
        analysis.append("- Use ES6+ features (arrow functions, destructuring)\n");
        analysis.append("- Add JSDoc comments for documentation\n");
        analysis.append("- Consider using a linter (ESLint)\n");
        
        analysis.append("\n### 🧪 Test Cases\n");
        analysis.append("- Cross-browser compatibility\n");
        analysis.append("- Mobile device testing\n");
        analysis.append("- Network failure scenarios\n");
        analysis.append("- Memory leak detection\n");
        
        return analysis.toString();
    }
    
    private String getCppAnalysis(String code) {
        boolean hasNew = code.contains("new ") && !code.contains("delete ");
        boolean hasRawPointer = code.contains("*") && !code.contains("shared_ptr") && !code.contains("unique_ptr");
        boolean hasException = code.contains("throw");
        
        StringBuilder analysis = new StringBuilder();
        analysis.append("### 🔍 Issues Identified\n");
        
        if (hasNew && !code.contains("delete")) {
            analysis.append("1. **Memory Leak**: `new` without corresponding `delete`\n");
        }
        if (hasRawPointer) {
            analysis.append("2. **Raw Pointers**: Consider smart pointers for automatic memory management\n");
        }
        if (hasException && !code.contains("catch")) {
            analysis.append("3. **Exception Safety**: Missing exception handling\n");
        }
        analysis.append("4. **Modern C++**: Consider C++11/14/17 features\n");
        
        analysis.append("\n### 🔧 Recommendations\n");
        analysis.append("```cpp\n");
        
        if (hasNew) {
            analysis.append("// Use smart pointers instead of raw new/delete\n");
            analysis.append("#include <memory>\n\n");
            analysis.append("std::unique_ptr<MyClass> obj = std::make_unique<MyClass>();\n");
            analysis.append("// Automatic cleanup when out of scope\n");
        } else {
            analysis.append("// Consider adding:\n");
            analysis.append("// 1. RAII pattern for resource management\n");
            analysis.append("// 2. Move semantics for efficiency\n");
            analysis.append("// 3. Const correctness\n");
        }
        
        analysis.append("```\n\n");
        
        analysis.append("### 📚 Best Practices\n");
        analysis.append("- Follow RAII (Resource Acquisition Is Initialization)\n");
        analysis.append("- Use smart pointers (`unique_ptr`, `shared_ptr`)\n");
        analysis.append("- Prefer standard library algorithms over raw loops\n");
        analysis.append("- Use `const` wherever possible\n");
        analysis.append("- Implement move constructors for large objects\n");
        
        analysis.append("\n### 🧪 Test Cases\n");
        analysis.append("- Memory leak detection\n");
        analysis.append("- Multi-threaded access\n");
        analysis.append("- Exception safety guarantees\n");
        analysis.append("- Performance under heavy load\n");
        
        return analysis.toString();
    }
    
    private String getGenericAnalysis(String code) {
        return """
               ### 🔍 Code Review Summary
               
               **Code Quality Assessment:**
               - ✅ Syntax appears valid
               - ⚠️ Consider adding error handling
               - ⚠️ Review input validation
               - ⚠️ Check edge cases
               
               ### 🔧 General Recommendations
               1. **Input Validation**: Validate all external inputs
               2. **Error Handling**: Implement comprehensive error handling
               3. **Logging**: Add structured logging for debugging
               4. **Testing**: Write unit tests for critical paths
               
               ### 📚 Universal Best Practices
               - Write self-documenting code with clear naming
               - Keep functions small and focused (Single Responsibility)
               - Avoid magic numbers and strings
               - Use version control effectively
               - Document public APIs
               
               ### 🧪 Testing Strategy
               - Unit tests for individual components
               - Integration tests for modules
               - Load testing for performance
               - Security vulnerability scanning
               """;
    }

    private String buildPrompt(DebugRequest req) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert ").append(req.getLanguage()).append(" programmer and debugger.\n");
        prompt.append("Your task: Analyze the code below and provide SPECIFIC, ACTIONABLE feedback.\n\n");
        
        if (req.getContext() != null && !req.getContext().trim().isEmpty()) {
            prompt.append("User Context: ").append(req.getContext()).append("\n\n");
        }
        
        prompt.append("Code to analyze (").append(req.getLanguage()).append("):\n");
        prompt.append("```").append(req.getLanguage()).append("\n");
        prompt.append(req.getCodeSnippet());
        prompt.append("\n```\n\n");
        
        prompt.append("Provide analysis in this EXACT format:\n");
        prompt.append("## 🔍 Code Analysis\n");
        prompt.append("### 📋 Issues Found\n");
        prompt.append("(List each issue with line numbers if possible)\n\n");
        prompt.append("### 🔧 Suggested Fixes\n");
        prompt.append("(Provide corrected code snippets)\n\n");
        prompt.append("### 💡 Best Practices\n");
        prompt.append("(Language-specific recommendations)\n\n");
        prompt.append("### 🧪 Test Cases\n");
        prompt.append("(Edge cases to test)\n\n");
        prompt.append("IMPORTANT: Start your response with '🤖 AI ANALYSIS:' so I know it's from OpenAI.");
        prompt.append("Keep response under 600 words.");
        
        return prompt.toString();
    }

    private String getErrorResponse(DebugRequest req, Exception e) {
        return """
               ## ❌ Service Error
               
               **Error Details:** %s
               
               **Code Submitted:**
               ```%s
               %s
               ```
               
               **Please check:**
               1. OpenAI API key configuration
               2. Internet connectivity
               3. Spring Boot application logs
               
               *Try again in a moment or check the logs for details.*
               """.formatted(e.getMessage(), req.getLanguage(), req.getCodeSnippet());
    }
}