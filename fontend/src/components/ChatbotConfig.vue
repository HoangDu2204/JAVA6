<template>
  <div class="chatbot-config">
    <div class="config-card">
      <h3>🤖 Cấu hình Chatbot</h3>
      
      <!-- Trạng thái API -->
      <div class="status-section">
        <h4>Trạng thái API</h4>
        <div class="status-indicator" :class="{ 'active': isGeminiActive, 'inactive': !isGeminiActive }">
          <span class="status-dot"></span>
          <span class="status-text">
            {{ isGeminiActive ? 'Gemini API đang hoạt động' : 'Đang sử dụng Demo Mode' }}
          </span>
        </div>
      </div>

      <!-- Cấu hình API Key -->
      <div class="api-section">
        <h4>Google Gemini API Key</h4>
        <div class="input-group">
          <input
            v-model="apiKey"
            type="password"
            placeholder="Nhập API key Gemini..."
            class="api-input"
            :class="{ 'valid': isValidKey, 'invalid': apiKey && !isValidKey }"
          />
          <button @click="toggleKeyVisibility" class="toggle-btn" :title="showKey ? 'Ẩn' : 'Hiện'">
            <i :class="showKey ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
          </button>
        </div>
        
        <div class="key-info">
          <p v-if="apiKey && !isValidKey" class="error-text">
            ❌ API key không hợp lệ. Vui lòng kiểm tra lại.
          </p>
          <p v-else-if="isValidKey" class="success-text">
            ✅ API key hợp lệ
          </p>
          <p v-else class="info-text">
            💡 Lấy API key từ: 
            <a href="https://makersuite.google.com/app/apikey" target="_blank" class="link">
              Google AI Studio
            </a>
          </p>
        </div>

        <div class="action-buttons">
          <button @click="saveApiKey" class="save-btn" :disabled="!isValidKey">
            <i class="bi bi-check-circle"></i>
            Lưu API Key
          </button>
          <button @click="testApiKey" class="test-btn" :disabled="!isValidKey">
            <i class="bi bi-play-circle"></i>
            Test API
          </button>
          <button @click="clearApiKey" class="clear-btn">
            <i class="bi bi-trash"></i>
            Xóa API Key
          </button>
        </div>
      </div>

      <!-- Thông tin API -->
      <div class="info-section">
        <h4>Thông tin API</h4>
        <div class="info-grid">
          <div class="info-item">
            <i class="bi bi-key"></i>
            <span>API Key hiện tại:</span>
            <code>{{ maskedApiKey }}</code>
          </div>
          <div class="info-item">
            <i class="bi bi-gear"></i>
            <span>Chế độ:</span>
            <span :class="{ 'gemini': isGeminiActive, 'demo': !isGeminiActive }">
              {{ isGeminiActive ? 'Gemini AI' : 'Demo Mode' }}
            </span>
          </div>
          <div class="info-item">
            <i class="bi bi-clock"></i>
            <span>Typing delay:</span>
            <span>{{ config.TYPING_DELAY }}ms</span>
          </div>
        </div>
      </div>

      <!-- Test Result -->
      <div v-if="testResult" class="test-result" :class="testResult.type">
        <h4>Kết quả test</h4>
        <div class="result-content">
          <i :class="testResult.type === 'success' ? 'bi bi-check-circle' : 'bi bi-x-circle'"></i>
          <p>{{ testResult.message }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import { config, updateApiKey, validateApiKey } from '@/api/config.js'
import { geminiChatbot } from '@/api/chatbot-gemini.js'

export default {
  name: 'ChatbotConfig',
  setup() {
    const apiKey = ref('')
    const showKey = ref(false)
    const testResult = ref(null)
    const isGeminiActive = ref(false)

    // Computed properties
    const isValidKey = computed(() => {
      return apiKey.value && 
             apiKey.value.startsWith('AIza') && 
             apiKey.value.length > 20
    })

    const maskedApiKey = computed(() => {
      if (!config.GEMINI_API_KEY) return 'Chưa có'
      const key = config.GEMINI_API_KEY
      return key.substring(0, 10) + '...' + key.substring(key.length - 4)
    })

    // Methods
    const toggleKeyVisibility = () => {
      showKey.value = !showKey.value
    }

    const saveApiKey = () => {
      if (isValidKey.value) {
        const success = updateApiKey(apiKey.value)
        if (success) {
          isGeminiActive.value = true
          testResult.value = {
            type: 'success',
            message: 'API key đã được lưu thành công!'
          }
          // Lưu vào localStorage
          localStorage.setItem('gemini_api_key', apiKey.value)
        }
      }
    }

    const testApiKey = async () => {
      if (!isValidKey.value) return

      testResult.value = {
        type: 'info',
        message: 'Đang test API...'
      }

      try {
        // Test với một câu hỏi đơn giản
        const response = await geminiChatbot.sendMessage('Xin chào')
        
        if (response && response.message) {
          testResult.value = {
            type: 'success',
            message: 'API hoạt động tốt! Chatbot có thể trả lời câu hỏi.'
          }
        } else {
          throw new Error('Không nhận được phản hồi từ API')
        }
      } catch (error) {
        console.error('Test API error:', error)
        testResult.value = {
          type: 'error',
          message: `Lỗi khi test API: ${error.message}`
        }
      }
    }

    const clearApiKey = () => {
      apiKey.value = ''
      config.GEMINI_API_KEY = ''
      config.USE_GEMINI = false
      isGeminiActive.value = false
      localStorage.removeItem('gemini_api_key')
      testResult.value = {
        type: 'info',
        message: 'API key đã được xóa. Chatbot sẽ sử dụng Demo Mode.'
      }
    }

    // Lifecycle
    onMounted(() => {
      // Load API key từ localStorage
      const savedKey = localStorage.getItem('gemini_api_key')
      if (savedKey) {
        apiKey.value = savedKey
        updateApiKey(savedKey)
      }
      
      // Kiểm tra trạng thái hiện tại
      isGeminiActive.value = validateApiKey()
    })

    return {
      apiKey,
      showKey,
      testResult,
      isGeminiActive,
      config,
      isValidKey,
      maskedApiKey,
      toggleKeyVisibility,
      saveApiKey,
      testApiKey,
      clearApiKey
    }
  }
}
</script>

<style scoped>
.chatbot-config {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
}

.config-card {
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  border-radius: 15px;
  padding: 30px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border: 1px solid #e9ecef;
}

.config-card h3 {
  color: #333;
  margin-bottom: 25px;
  text-align: center;
  font-size: 24px;
  font-weight: 700;
}

.config-card h4 {
  color: #555;
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: 600;
}

.status-section {
  margin-bottom: 30px;
}

.status-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  border-radius: 10px;
  background: #f8f9fa;
  border: 2px solid #e9ecef;
}

.status-indicator.active {
  background: #d4edda;
  border-color: #c3e6cb;
}

.status-indicator.inactive {
  background: #f8d7da;
  border-color: #f5c6cb;
}

.status-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #28a745;
}

.status-indicator.inactive .status-dot {
  background: #dc3545;
}

.status-text {
  font-weight: 600;
  color: #333;
}

.api-section {
  margin-bottom: 30px;
}

.input-group {
  display: flex;
  gap: 10px;
  margin-bottom: 15px;
}

.api-input {
  flex: 1;
  padding: 12px 15px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.api-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.api-input.valid {
  border-color: #28a745;
}

.api-input.invalid {
  border-color: #dc3545;
}

.toggle-btn {
  padding: 12px 15px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.toggle-btn:hover {
  background: #5a6268;
}

.key-info {
  margin-bottom: 20px;
}

.error-text {
  color: #dc3545;
  font-size: 14px;
  margin: 0;
}

.success-text {
  color: #28a745;
  font-size: 14px;
  margin: 0;
}

.info-text {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
}

.link {
  color: #007bff;
  text-decoration: none;
}

.link:hover {
  text-decoration: underline;
}

.action-buttons {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.save-btn, .test-btn, .clear-btn {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s ease;
}

.save-btn {
  background: #28a745;
  color: white;
}

.save-btn:hover:not(:disabled) {
  background: #218838;
}

.save-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.test-btn {
  background: #007bff;
  color: white;
}

.test-btn:hover:not(:disabled) {
  background: #0056b3;
}

.test-btn:disabled {
  background: #6c757d;
  cursor: not-allowed;
}

.clear-btn {
  background: #dc3545;
  color: white;
}

.clear-btn:hover {
  background: #c82333;
}

.info-section {
  margin-bottom: 30px;
}

.info-grid {
  display: grid;
  gap: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.info-item i {
  color: #6c757d;
  font-size: 18px;
}

.info-item span:first-of-type {
  font-weight: 600;
  color: #333;
  min-width: 120px;
}

.info-item code {
  background: #e9ecef;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: monospace;
  font-size: 12px;
}

.gemini {
  color: #28a745;
  font-weight: 600;
}

.demo {
  color: #dc3545;
  font-weight: 600;
}

.test-result {
  padding: 20px;
  border-radius: 10px;
  border: 2px solid;
}

.test-result.success {
  background: #d4edda;
  border-color: #c3e6cb;
}

.test-result.error {
  background: #f8d7da;
  border-color: #f5c6cb;
}

.test-result.info {
  background: #d1ecf1;
  border-color: #bee5eb;
}

.result-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.result-content i {
  font-size: 20px;
}

.result-content.success i {
  color: #28a745;
}

.result-content.error i {
  color: #dc3545;
}

.result-content.info i {
  color: #17a2b8;
}

.result-content p {
  margin: 0;
  font-weight: 600;
}

@media (max-width: 768px) {
  .chatbot-config {
    padding: 15px;
  }
  
  .config-card {
    padding: 20px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .info-item {
    flex-direction: column;
    text-align: center;
    gap: 8px;
  }
  
  .info-item span:first-of-type {
    min-width: auto;
  }
}
</style>