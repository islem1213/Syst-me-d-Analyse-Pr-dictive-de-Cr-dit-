const ollamaApiUrl = '/api/ollama';

function initChatbot() {
    const widget = document.getElementById('chatbot-widget');
    widget.innerHTML = '<p>Chatbot disponible via Ollama / Llama 3.</p>';
}

document.addEventListener('DOMContentLoaded', initChatbot);
