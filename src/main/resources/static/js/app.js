// SIMPLE API HELPER FUNCTION
async function api(url, method = 'POST', body = null, auth = true) {
    console.log("Calling API:", url);
    
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    };
    
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    // Add token if available and auth is true
    const token = localStorage.getItem('ai_token');
    if (auth && token) {
        options.headers['Authorization'] = 'Bearer ' + token;
    }
    
    try {
        const response = await fetch(url, options);
        const data = await response.json();
        
        if (!response.ok) {
            throw new Error(data.error || `HTTP ${response.status}`);
        }
        
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// DASHBOARD FUNCTIONALITY
document.addEventListener('DOMContentLoaded', function() {
    console.log("DOM fully loaded");
    
    // Check if we're on dashboard
    const analyzeBtn = document.getElementById('analyze-btn');
    const logoutBtn = document.getElementById('btn-logout');
    
    if (analyzeBtn) {
        console.log("Found analyze button");
        
        analyzeBtn.addEventListener('click', async function() {
            console.log("Analyze button clicked");
            
            // Get form values
            const language = document.getElementById('language').value.trim();
            const codeSnippet = document.getElementById('codeSnippet').value.trim();
            const context = document.getElementById('context').value.trim();
            const resultsDiv = document.getElementById('results');
            
            // Validate
            if (!language || !codeSnippet) {
                alert("Please enter Language and Code Snippet");
                return;
            }
            
            // Show loading state
            analyzeBtn.disabled = true;
            analyzeBtn.textContent = "Analyzing...";
            resultsDiv.textContent = "Processing your code...";
            
            // Prepare request
            const requestData = {
                language: language,
                codeSnippet: codeSnippet,
                context: context
            };
            
            console.log("Sending:", requestData);
            
            try {
                // Call API
                const response = await api('/api/debug/analyze', 'POST', requestData, true);
                console.log("Response received:", response);
                
                if (response.answer) {
                    resultsDiv.textContent = response.answer;
                } else if (response.error) {
                    resultsDiv.textContent = "Error: " + response.error;
                } else {
                    resultsDiv.textContent = JSON.stringify(response, null, 2);
                }
            } catch (error) {
                console.error("API call failed:", error);
                resultsDiv.textContent = "Submit failed: " + error.message;
                
                // Check if unauthorized
                if (error.message.includes('401') || error.message.includes('Unauthorized')) {
                    localStorage.removeItem('ai_token');
                    window.location.href = '/login';
                }
            } finally {
                // Reset button
                analyzeBtn.disabled = false;
                analyzeBtn.textContent = "Analyze";
            }
        });
    }
    
    // Logout button
    if (logoutBtn) {
        logoutBtn.addEventListener('click', function() {
            if (confirm('Are you sure you want to logout?')) {
                localStorage.removeItem('ai_token');
                window.location.href = '/login';
            }
        });
    }
    
    // Check if logged in
    const token = localStorage.getItem('ai_token');
    if (!token && window.location.pathname.includes('/dashboard')) {
        window.location.href = '/login';
    }
});