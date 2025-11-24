<template>
  <div class="chatbot-widget">
    <!-- Chat button -->
    <div
      v-if="!isOpen"
      @click="toggleChat"
      class="chat-button"
      :class="{ 'pulse': hasNewMessage }"
    >
      <div class="ai-logo">🤖</div>
    </div>

    <!-- Chat window -->
    <div v-if="isOpen" class="chat-window">
      <!-- Header -->
      <div class="chat-header">
        <div class="chat-title">
          <div class="ai-logo-small">🤖</div>
          <span>Trợ lý AI Thông Minh BakeryHub</span>
        </div>
        <div class="header-actions">
          <button @click="showResetConfirm" class="new-chat-btn" title="Tạo cuộc trò chuyện mới">
            <i class="bi bi-plus-circle"></i>
          </button>
          <button @click="toggleChat" class="close-btn" title="Đóng chat">
            <i class="bi bi-x-circle"></i>
          </button>
        </div>
      </div>

      <!-- Confirmation Modal -->
      <div v-if="showConfirmModal" class="confirm-modal-overlay" @click="hideResetConfirm">
        <div class="confirm-modal" @click.stop>
          <div class="confirm-icon">🔄</div>
          <h3>Tạo cuộc trò chuyện mới</h3>
          <p>Bạn có chắc chắn muốn tạo cuộc trò chuyện mới? Tất cả tin nhắn hiện tại sẽ bị xóa.</p>
          <div class="confirm-actions">
            <button @click="hideResetConfirm" class="cancel-btn">
              <i class="bi bi-x"></i>
              Hủy bỏ
            </button>
            <button @click="confirmReset" class="confirm-btn">
              <i class="bi bi-check"></i>
              Xác nhận
            </button>
          </div>
        </div>
      </div>

      <!-- Quick Questions -->
      <div v-if="messages.length <= 1" class="quick-questions">
        <div class="quick-questions-title">💡 Gợi ý câu hỏi:</div>
        <div class="quick-questions-grid">
          <button @click="askQuickQuestion('Ứng dụng có những loại bánh gì?')" class="quick-question-btn">
            🍰 Các loại bánh
          </button>
          <button @click="askQuickQuestion('Làm sao để đặt hàng online?')" class="quick-question-btn">
            🛒 Đặt hàng online
          </button>
          <button @click="askQuickQuestion('Phí giao hàng bao nhiêu?')" class="quick-question-btn">
            🚚 Phí giao hàng
          </button>
          <button @click="askQuickQuestion('Có chương trình giảm giá gì không?')" class="quick-question-btn">
            🎫 Giảm giá
          </button>
          <button @click="askQuickQuestion('Làm sao để trở thành đại lý?')" class="quick-question-btn">
            👥 Đại lý
          </button>
          <button @click="askQuickQuestion('Thông tin cửa hàng ở đâu?')" class="quick-question-btn">
            🏪 Thông tin cửa hàng
          </button>
        </div>
      </div>

      <!-- Messages -->
      <div class="chat-messages" ref="messagesContainer">
        <div
          v-for="(message, index) in messages"
          :key="index"
          class="message"
          :class="message.type === 'sent' ? 'message-sent' : 'message-received'"
        >
          <div class="message-avatar">
            <div v-if="message.type === 'sent'" class="user-avatar">👤</div>
            <div v-else class="ai-avatar">🤖</div>
          </div>
          <div class="message-content">
            <div class="message-text" v-html="formatMessage(message.message)"></div>
            <div class="message-time">{{ formatTime(message.timestamp) }}</div>
          </div>
        </div>

        <!-- Loading indicator -->
        <div v-if="isLoading" class="message message-received">
          <div class="message-avatar">
            <div class="ai-avatar">🤖</div>
          </div>
          <div class="message-content">
            <div class="typing-indicator">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        </div>
      </div>

      <!-- Input -->
      <div class="chat-input-container">
        <form @submit.prevent="sendMessage" class="chat-input-form">
          <input
            ref="chatInput"
            v-model="inputMessage"
            type="text"
            placeholder="Hỏi về bánh, đặt hàng, giao hàng..."
            class="chat-input"
            :disabled="isLoading"
            @keydown.enter="sendMessage"
          />
          <button
            type="submit"
            class="send-btn"
            :disabled="!inputMessage.trim() || isLoading"
          >
            <i class="bi bi-send-fill"></i>
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { smartChatbot } from '@/api/smart-chatbot.js';
import { marked } from 'marked';

export default {
  name: 'ChatbotWidget',
  data() {
    return {
      isOpen: false,
      messages: [],
      inputMessage: '',
      isLoading: false,
      hasNewMessage: false,
      showConfirmModal: false
    };
  },
  mounted() {
    this.loadChatHistory();
  },
  methods: {
    toggleChat() {
      this.isOpen = !this.isOpen;
      if (this.isOpen) {
        this.hasNewMessage = false;
        this.$nextTick(() => {
          this.scrollToBottom();
          // Focus vào input khi mở chat
          this.$refs.chatInput.focus();
        });
      }
    },

    async loadChatHistory() {
      // Ưu tiên lấy từ localStorage, nếu không có thì lấy từ API
      const saved = localStorage.getItem('chatbot_history');
      if (saved) {
        try {
          this.messages = JSON.parse(saved);
        } catch (e) {
          console.error('Failed to parse chat history:', e);
          this.messages = [];
        }
      } else {
        // Nếu chưa có, lấy từ API (lần đầu)
        try {
          const history = await smartChatbot.getChatHistory();
          this.messages = history.map(msg => ({
            ...msg,
            timestamp: new Date()
          }));
          this.saveChatHistory();
        } catch (error) {
          console.error('Failed to load chat history:', error);
          // Thêm tin nhắn chào mừng mặc định
          this.messages = [{
            message: 'Xin chào! 👋 Tôi là trợ lý AI thông minh của ứng dụng BakeryHub! Tôi có thể đọc và phân tích source code của ứng dụng để trả lời chính xác các câu hỏi về:\n\n🍰 Các loại bánh có trong ứng dụng\n🛒 Cách mua bánh và đặt hàng online\n🚚 Hệ thống giao hàng và thanh toán\n🎫 Chính sách giảm giá và voucher\n🏪 Thông tin cửa hàng và liên hệ\n👥 Hệ thống đại lý và chiết khấu\n\nBạn có câu hỏi gì không? Tôi sẽ trả lời dựa trên thông tin thực tế từ source code! 🤖✨',
            type: 'received',
            timestamp: new Date()
          }];
          this.saveChatHistory();
        }
      }
    },
    saveChatHistory() {
      localStorage.setItem('chatbot_history', JSON.stringify(this.messages));
    },
    async sendMessage() {
      if (!this.inputMessage.trim() || this.isLoading) return;

      const userMessage = {
        message: this.inputMessage,
        type: 'sent',
        timestamp: new Date().toISOString()
      };

      this.messages.push(userMessage);
      this.saveChatHistory();
      const messageToSend = this.inputMessage;
      this.inputMessage = '';
      this.isLoading = true;

      this.$nextTick(() => {
        this.scrollToBottom();
        // Focus lại vào input sau khi gửi tin nhắn
        this.$refs.chatInput.focus();
      });

      try {
        const response = await smartChatbot.sendMessage(messageToSend);
        const aiMessage = {
          message: response.message,
          type: 'received',
          timestamp: new Date().toISOString()
        };
        this.messages.push(aiMessage);
        this.saveChatHistory();
      } catch (error) {
        console.error('Failed to send message:', error);
        const errorMessage = {
          message: 'Xin lỗi, có lỗi xảy ra. Vui lòng thử lại sau.',
          type: 'received',
          timestamp: new Date().toISOString()
        };
        this.messages.push(errorMessage);
        this.saveChatHistory();
      } finally {
        this.isLoading = false;
        this.$nextTick(() => {
          this.scrollToBottom();
          // Focus lại vào input sau khi nhận phản hồi
          this.$refs.chatInput.focus();
        });
      }
    },

    formatMessage(message) {
      // Sử dụng marked để render markdown
      try {
        return marked(message);
      } catch (error) {
        console.error('Error rendering markdown:', error);
        return message;
      }
    },

    formatTime(timestamp) {
      if (!timestamp) return '';
      return new Date(timestamp).toLocaleTimeString('vi-VN', {
        hour: '2-digit',
        minute: '2-digit'
      });
    },

    scrollToBottom() {
      if (this.$refs.messagesContainer) {
        this.$refs.messagesContainer.scrollTop = this.$refs.messagesContainer.scrollHeight;
      }
    },

    focusInput() {
      if (this.$refs.chatInput) {
        this.$refs.chatInput.focus();
      }
    },

    showResetConfirm() {
      this.showConfirmModal = true;
    },

    hideResetConfirm() {
      this.showConfirmModal = false;
    },

    confirmReset() {
      this.performReset();
      this.hideResetConfirm();
    },

    askQuickQuestion(question) {
      this.inputMessage = question;
      this.sendMessage();
    },

    resetChat() {
      // Hiển thị xác nhận trước khi reset
      if (this.messages.length > 1) {
        this.showResetConfirm();
      } else {
        this.performReset();
      }
    },

    performReset() {
      // Thêm hiệu ứng loading
      this.isLoading = true;

      // Xóa tin nhắn cũ
      this.messages = [];
      localStorage.removeItem('chatbot_history');

      // Reset chatbot API
      smartChatbot.resetChat();

      // Tạo tin nhắn chào mừng mới
      setTimeout(() => {
        this.messages = [{
          message: 'Xin chào! 👋 Tôi là trợ lý AI thông minh của ứng dụng BakeryHub! Tôi có thể đọc và phân tích source code của ứng dụng để trả lời chính xác các câu hỏi về:\n\n🍰 Các loại bánh có trong ứng dụng\n🛒 Cách mua bánh và đặt hàng online\n🚚 Hệ thống giao hàng và thanh toán\n🎫 Chính sách giảm giá và voucher\n🏪 Thông tin cửa hàng và liên hệ\n👥 Hệ thống đại lý và chiết khấu\n\nBạn có câu hỏi gì không? Tôi sẽ trả lời dựa trên thông tin thực tế từ source code! 🤖✨',
          type: 'received',
          timestamp: new Date()
        }];
        this.saveChatHistory();
        this.isLoading = false;

        // Scroll xuống dưới
        this.$nextTick(() => {
          this.scrollToBottom();
          this.$refs.chatInput.focus();
        });
      }, 500);
    }
  }
};
</script>

<style scoped>
.chatbot-widget {
  position: fixed;
  bottom: 30px;
  right: 30px;
  z-index: 1000;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.chat-button {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #8B4513 0%, #D2691E 50%, #FFD700 100%);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 8px 30px rgba(139, 69, 19, 0.3);
  transition: all 0.3s ease;
  color: white;
  font-weight: bold;
  position: relative;
  overflow: hidden;
  border: 2px solid #FFD700;
}

.chat-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.2) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.chat-button:hover::before {
  transform: translateX(100%);
}

.chat-button::after {
  content: '✨';
  position: absolute;
  top: -5px;
  right: -5px;
  font-size: 16px;
  animation: bounce 2s infinite;
  opacity: 0.8;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

.chat-button:hover {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.3);
}

.chat-button.pulse {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.05); }
  100% { transform: scale(1); }
}

.chat-window {
  width: 420px;
  height: 600px;
  background: linear-gradient(135deg, #FFF8DC 0%, #F5F5DC 100%);
  border-radius: 25px;
  box-shadow: 0 20px 60px rgba(139, 69, 19, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  backdrop-filter: blur(10px);
  border: 2px solid #D2691E;
  animation: slideInUp 0.3s ease-out;
}

@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.chat-header {
  background: linear-gradient(135deg, #8B4513 0%, #D2691E 50%, #CD853F 100%);
  color: white;
  padding: 20px 25px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.chat-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.15"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.15"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.15"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.15"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.15"/><circle cx="30" cy="80" r="0.8" fill="white" opacity="0.1"/><circle cx="80" cy="20" r="0.8" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.4;
}

.chat-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-weight: 700;
  font-size: 18px;
  position: relative;
  z-index: 1;
}

.ai-logo {
  font-size: 32px;
  font-weight: bold;
  color: white;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.ai-logo-small {
  font-size: 20px;
  font-weight: bold;
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 12px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
}

.close-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  cursor: pointer;
  font-size: 20px;
  padding: 8px;
  border-radius: 50%;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 1;
}

.close-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
  transform: scale(1.1);
}

.header-actions {
  display: flex;
  gap: 12px;
  position: relative;
  z-index: 1;
}

.new-chat-btn, .close-btn {
  background: rgba(255, 255, 255, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.2);
  color: white;
  cursor: pointer;
  font-size: 18px;
  padding: 10px;
  border-radius: 12px;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  flex-shrink: 0;
  position: relative;
  overflow: hidden;
  min-width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.new-chat-btn::before, .close-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.3) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.new-chat-btn:hover::before, .close-btn:hover::before {
  transform: translateX(100%);
}

.new-chat-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.4);
  transform: scale(1.05) rotate(90deg);
  box-shadow: 0 6px 20px rgba(255, 255, 255, 0.2);
}

.close-btn:hover {
  background: rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.4);
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.2);
}

.new-chat-btn:active, .close-btn:active {
  transform: scale(0.95);
}

.chat-messages {
  flex: 1;
  padding: 25px;
  overflow-y: auto;
  background: linear-gradient(135deg, #fff5f7 0%, #ffeef2 100%);
  scroll-behavior: smooth;
}

/* Quick Questions */
.quick-questions {
  padding: 1rem;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.quick-questions-title {
  font-size: 0.9rem;
  font-weight: 600;
  color: #6c757d;
  margin-bottom: 0.75rem;
  text-align: center;
}

.quick-questions-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 0.5rem;
}

.quick-question-btn {
  background: white;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 0.5rem;
  font-size: 0.8rem;
  color: #495057;
  cursor: pointer;
  transition: all 0.2s ease;
  text-align: center;
  line-height: 1.2;
}

.quick-question-btn:hover {
  background: #0d6efd;
  color: white;
  border-color: #0d6efd;
  transform: translateY(-1px);
}

.quick-question-btn:active {
  transform: translateY(0);
}

.chat-messages::-webkit-scrollbar {
  width: 6px;
}

.chat-messages::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 10px;
}

.chat-messages::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 10px;
}

.chat-messages::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.message {
  display: flex;
  margin-bottom: 20px;
  gap: 12px;
  animation: fadeInUp 0.3s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-sent {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
  flex-shrink: 0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.user-avatar {
  font-size: 20px;
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(74, 144, 226, 0.3);
}

.ai-avatar {
  font-size: 20px;
  font-weight: bold;
  background: linear-gradient(135deg, #8B4513 0%, #D2691E 100%);
  color: white;
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.3);
  border: 2px solid #FFD700;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.message-content {
  flex: 1;
  min-width: 0;
}

.message-text {
  background: white;
  padding: 15px 20px;
  border-radius: 18px;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.1);
  line-height: 1.5;
  font-size: 0.95rem;
  color: #333;
  word-wrap: break-word;
  max-width: 85%;
  border: 1px solid rgba(139, 69, 19, 0.1);
}

.message-sent .message-text {
  background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  color: white;
  border-bottom-right-radius: 5px;
  margin-left: auto;
}

.message-received .message-text {
  background: linear-gradient(135deg, #FFF8DC 0%, #F5F5DC 100%);
  border-bottom-left-radius: 5px;
  border: 1px solid #D2691E;
}

.message-text :deep(h1),
.message-text :deep(h2),
.message-text :deep(h3),
.message-text :deep(h4),
.message-text :deep(h5),
.message-text :deep(h6) {
  margin-top: 0;
  margin-bottom: 12px;
  color: inherit;
  font-size: 1.1em;
}

.message-text :deep(ul),
.message-text :deep(ol) {
  margin-bottom: 12px;
  padding-left: 18px;
}

.message-text :deep(li) {
  margin-bottom: 6px;
}

.message-text :deep(strong) {
  font-weight: 700;
  color: inherit;
}

.message-time {
  font-size: 0.8rem;
  color: #888;
  margin-top: 6px;
  text-align: right;
}

.message-sent .message-time {
  text-align: left;
}

.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 15px 20px;
  background: white;
  border-radius: 18px;
  box-shadow: 0 4px 15px rgba(255, 107, 157, 0.1);
  border-bottom-left-radius: 5px;
  max-width: 85%;
  border: 1px solid rgba(255, 107, 157, 0.1);
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ccc;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-indicator span:nth-child(1) { animation-delay: -0.32s; }
.typing-indicator span:nth-child(2) { animation-delay: -0.16s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

.chat-input-container {
  padding: 20px 25px;
  background: white;
  border-top: 1px solid #e9ecef;
}

.chat-input-form {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.chat-input {
  flex: 1;
  border: 2px solid #ffeef2;
  border-radius: 25px;
  padding: 12px 20px;
  font-size: 0.95rem;
  outline: none;
  transition: all 0.3s ease;
  resize: none;
  min-height: 45px;
  max-height: 100px;
  font-family: inherit;
  background: white;
  color: #333;
  box-shadow: 0 2px 10px rgba(255, 107, 157, 0.05);
}

.chat-input:focus {
  border-color: #ff6b9d;
  box-shadow: 0 0 0 3px rgba(255, 107, 157, 0.1);
}

.chat-input:disabled {
  background: #f5f5f5;
  cursor: not-allowed;
  opacity: 0.6;
}

.chat-input::placeholder {
  color: #999;
  opacity: 1;
}

.send-btn {
  background: linear-gradient(135deg, #8B4513 0%, #D2691E 100%);
  color: white;
  border: none;
  border-radius: 50%;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1.1rem;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.3);
  flex-shrink: 0;
  border: 2px solid #FFD700;
}

.send-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(139, 69, 19, 0.4);
}

.send-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* Confirmation Modal Styles */
.confirm-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001;
}

.confirm-modal {
  background: linear-gradient(135deg, #fff 0%, #f8f9fa 100%);
  border-radius: 20px;
  padding: 35px 30px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  text-align: center;
  max-width: 420px;
  width: 90%;
  position: relative;
  animation: scaleIn 0.3s ease-out;
  border: 1px solid rgba(139, 69, 19, 0.1);
}

.confirm-modal::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(139, 69, 19, 0.05) 0%, transparent 100%);
  border-radius: 20px;
  pointer-events: none;
}

@keyframes scaleIn {
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.confirm-icon {
  font-size: 70px;
  color: #FFD700;
  margin-bottom: 25px;
  animation: pulseIcon 2s infinite;
  filter: drop-shadow(0 4px 8px rgba(255, 215, 0, 0.3));
}

@keyframes pulseIcon {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.confirm-modal h3 {
  color: #333;
  margin-bottom: 18px;
  font-size: 24px;
  font-weight: 700;
  position: relative;
  z-index: 1;
}

.confirm-modal p {
  color: #666;
  margin-bottom: 35px;
  font-size: 16px;
  line-height: 1.6;
  position: relative;
  z-index: 1;
}

.confirm-actions {
  display: flex;
  justify-content: space-around;
  gap: 20px;
  position: relative;
  z-index: 1;
}

.confirm-btn, .cancel-btn {
  flex: 1;
  padding: 15px 25px;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  position: relative;
  overflow: hidden;
}

.confirm-btn::before, .cancel-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255, 255, 255, 0.3) 50%, transparent 70%);
  transform: translateX(-100%);
  transition: transform 0.6s ease;
}

.confirm-btn:hover::before, .cancel-btn:hover::before {
  transform: translateX(100%);
}

.confirm-btn {
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.3);
  border: 2px solid #4CAF50;
}

.confirm-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(76, 175, 80, 0.4);
}

.cancel-btn {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  color: #495057;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  border: 2px solid #dee2e6;
}

.cancel-btn:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.confirm-btn:active, .cancel-btn:active {
  transform: translateY(-1px);
}

/* Responsive */
@media (max-width: 768px) {
  .chatbot-widget {
    bottom: 20px;
    right: 20px;
  }

  .chat-button {
    width: 60px;
    height: 60px;
    font-size: 24px;
  }

  .chat-window {
    width: 350px;
    height: 500px;
    right: 0;
    bottom: 80px;
  }

  .chat-header {
    padding: 15px 20px;
  }

  .chat-title {
    font-size: 16px;
    gap: 10px;
  }

  .header-actions {
    gap: 8px;
  }

  .new-chat-btn,
  .close-btn {
    font-size: 16px;
    padding: 8px;
    min-width: 40px;
    height: 40px;
    border-radius: 10px;
  }

  .chat-messages {
    padding: 20px;
  }

  .message {
    gap: 10px;
  }

  .message-avatar {
    width: 40px;
    height: 40px;
    font-size: 1rem;
  }

  .message-text {
    padding: 12px 18px;
    font-size: 0.9rem;
  }

  .chat-input-container {
    padding: 15px 20px;
  }

  .send-btn {
    width: 45px;
    height: 45px;
    font-size: 1rem;
  }
}

@media (max-width: 480px) {
  .chat-window {
    width: calc(100vw - 40px);
    height: 450px;
    right: 20px;
    left: 20px;
  }
}
</style>
