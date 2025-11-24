// Chatbot sử dụng Google Gemini API với fallback demo
import { config, validateApiKey } from './config.js';

const GEMINI_API_KEY = config.GEMINI_API_KEY;
const GEMINI_API_URL = config.GEMINI_API_URL;
const USE_GEMINI = validateApiKey(); // Sử dụng function validate để kiểm tra API key
const USE_DEMO = !USE_GEMINI;

// Context cho chatbot bánh ngọt
const SYSTEM_PROMPT = `Bạn là trợ lý AI thân thiện của tiệm bánh ngọt "Tiệm Bánh Ngọt Bakery". 

**Thông tin cửa hàng:**
- Địa chỉ: 123 Đường 30 tháng 4, Xuân Khánh, Ninh Kiều, Cần Thơ, Việt Nam
- Giờ mở cửa: Thứ 2-6: 7:00-22:00, Thứ 7-CN: 7:00-23:00
- Hotline: 0909 090 909
- Email: info@banhngotbakery.com

**Sản phẩm chính:**
- Bánh kem: socola, vani, dâu tây, matcha, trái cây
- Bánh cupcake: vani, socola, dâu tây, matcha, oreo
- Bánh quy: bơ, socola chip, hạnh nhân, dừa
- Bánh mì ngọt: sữa, nho, bơ, phô mai

**Giá cả (tham khảo):**
- Bánh kem: 150.000 - 600.000 VNĐ (tùy kích thước)
- Cupcake: 25.000 - 35.000 VNĐ/chiếc
- Bánh quy: 30.000 - 50.000 VNĐ/100g
- Bánh mì ngọt: 15.000 - 25.000 VNĐ/chiếc

**Dịch vụ:**
- Giao hàng miễn phí nội thành TP. Cần Thơ
- Giao hàng 24/7
- Thanh toán: tiền mặt, chuyển khoản, ví điện tử
- Đổi trả trong 24h (bánh kem: 2h)

**Hướng dẫn trả lời:**
1. Luôn thân thiện, nhiệt tình và hữu ích
2. Trả lời bằng tiếng Việt, sử dụng emoji phù hợp
3. Nếu câu hỏi không liên quan đến bánh ngọt, nhẹ nhàng chuyển hướng về chủ đề bánh ngọt
4. Cung cấp thông tin chính xác về cửa hàng và sản phẩm
5. Khuyến khích khách hàng đặt hàng hoặc ghé thăm cửa hàng

**Lưu ý:** Chỉ trả lời về bánh ngọt và dịch vụ của cửa hàng. Nếu câu hỏi về chủ đề khác, nhẹ nhàng từ chối và đề xuất hỏi về bánh ngọt.`;

// Lưu trữ lịch sử chat
let chatHistory = [
  {
    role: 'user',
    parts: [{ text: 'Xin chào! Tôi muốn tìm hiểu về tiệm bánh ngọt của bạn.' }]
  },
  {
    role: 'model',
    parts: [{ text: 'Xin chào! 👋 Rất vui được gặp bạn! Tôi là trợ lý AI của Tiệm Bánh Ngọt Bakery. Tôi có thể giúp bạn tìm hiểu về các loại bánh ngọt thơm ngon, cách đặt hàng, giao hàng và các dịch vụ khác của chúng tôi. Bạn có câu hỏi gì không? 🍰' }]
  }
];

// Function để gọi Gemini API
async function callGeminiAPI(userMessage) {
  try {
    console.log('🔑 Sử dụng Gemini API với key:', GEMINI_API_KEY.substring(0, 10) + '...');
    
    // Thêm message mới vào history
    chatHistory.push({
      role: 'user',
      parts: [{ text: userMessage }]
    });

    const requestBody = {
      contents: [
        {
          role: 'user',
          parts: [
            {
              text: SYSTEM_PROMPT + '\n\nLịch sử chat:\n' + 
                    chatHistory.map(msg => `${msg.role}: ${msg.parts[0].text}`).join('\n') +
                    '\n\nCâu hỏi mới của khách hàng: ' + userMessage
            }
          ]
        }
      ],
      generationConfig: {
        temperature: 0.7,
        topK: 40,
        topP: 0.95,
        maxOutputTokens: 1024,
      },
      safetySettings: [
        {
          category: "HARM_CATEGORY_HARASSMENT",
          threshold: "BLOCK_MEDIUM_AND_ABOVE"
        },
        {
          category: "HARM_CATEGORY_HATE_SPEECH",
          threshold: "BLOCK_MEDIUM_AND_ABOVE"
        },
        {
          category: "HARM_CATEGORY_SEXUALLY_EXPLICIT",
          threshold: "BLOCK_MEDIUM_AND_ABOVE"
        },
        {
          category: "HARM_CATEGORY_DANGEROUS_CONTENT",
          threshold: "BLOCK_MEDIUM_AND_ABOVE"
        }
      ]
    };

    const response = await fetch(`${GEMINI_API_URL}?key=${GEMINI_API_KEY}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      const errorText = await response.text();
      console.error('❌ Gemini API Error:', response.status, errorText);
      throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
    }

    const data = await response.json();
    
    if (data.candidates && data.candidates[0] && data.candidates[0].content) {
      const botResponse = data.candidates[0].content.parts[0].text;
      
      // Thêm response vào history
      chatHistory.push({
        role: 'model',
        parts: [{ text: botResponse }]
      });

      // Giới hạn history để tránh quá dài
      if (chatHistory.length > 10) {
        chatHistory = chatHistory.slice(-10);
      }

      console.log('✅ Gemini API response received');
      return botResponse;
    } else {
      console.error('❌ Invalid response format from Gemini API:', data);
      throw new Error('Invalid response format from Gemini API');
    }

  } catch (error) {
    console.error('❌ Error calling Gemini API:', error);
    
    // Fallback response khi API lỗi
    return `Xin lỗi, tôi đang gặp sự cố kỹ thuật. 😅 

**🍰 Trong khi chờ đợi, bạn có thể:**
- Gọi hotline: 0909 090 909
- Email: info@banhngotbakery.com
- Ghé thăm cửa hàng: 123 Đường 30 tháng 4, Xuân Khánh, Ninh Kiều, Cần Thơ

**⏰ Giờ mở cửa:** Thứ 2-6: 7:00-22:00, Thứ 7-CN: 7:00-23:00

Tôi sẽ sớm khắc phục và phục vụ bạn tốt hơn! 🙏`;
  }
}

// Function để kiểm tra xem có phải câu hỏi ngoài lề không
function isOffTopic(message) {
  const offTopicKeywords = [
    'chính trị', 'politic', 'political', 'bầu cử', 'election', 'đảng', 'party',
    'kinh tế', 'economy', 'economic', 'tài chính', 'finance', 'chứng khoán', 'stock',
    'thể thao', 'sport', 'football', 'bóng đá', 'tennis', 'bóng rổ', 'basketball',
    'giải trí', 'entertainment', 'phim', 'movie', 'nhạc', 'music', 'ca sĩ', 'singer',
    'công nghệ', 'technology', 'máy tính', 'computer', 'điện thoại', 'phone',
    'y tế', 'medical', 'bệnh viện', 'hospital', 'bác sĩ', 'doctor',
    'giáo dục', 'education', 'trường học', 'school', 'đại học', 'university',
    'du lịch', 'travel', 'khách sạn', 'hotel', 'máy bay', 'airplane',
    'thời tiết', 'weather', 'khí hậu', 'climate', 'nhiệt độ', 'temperature',
    'thời sự', 'news', 'tin tức', 'báo chí', 'press', 'media'
  ];

  const lowerMessage = message.toLowerCase();
  return offTopicKeywords.some(keyword => lowerMessage.includes(keyword.toLowerCase()));
}

// Demo chatbot cho fallback
const demoChatbot = {
  getChatHistory: async () => {
    return [
      {
        message: 'Xin chào! 👋 Tôi là trợ lý AI của Tiệm Bánh Ngọt Bakery. Tôi có thể giúp bạn tìm hiểu về các loại bánh ngọt thơm ngon, cách đặt hàng, giao hàng và các dịch vụ khác của chúng tôi. Bạn có câu hỏi gì không? 🍰',
        type: 'received'
      }
    ];
  },
  
  sendMessage: async (message) => {
    const trimmedMessage = message.trim().toLowerCase();
    
    if (!trimmedMessage) {
      return {
        message: 'Xin chào! 👋 Bạn có câu hỏi gì về bánh ngọt không?',
        type: 'received'
      };
    }

    // Demo responses
    if (trimmedMessage.includes('xin chào') || trimmedMessage.includes('hello')) {
      return {
        message: 'Xin chào! 👋 Rất vui được gặp bạn! Tôi là trợ lý AI của Tiệm Bánh Ngọt ABC. Tôi có thể giúp bạn tìm hiểu về các loại bánh ngọt thơm ngon, cách đặt hàng, giao hàng và các dịch vụ khác của chúng tôi. Bạn có câu hỏi gì không? 🍰',
        type: 'received'
      };
    }
    
    if (trimmedMessage.includes('bánh kem') || trimmedMessage.includes('cake')) {
      return {
        message: '🍰 **Bánh kem của chúng tôi có các loại:**\n- Socola: 150.000 - 600.000 VNĐ\n- Vani: 150.000 - 600.000 VNĐ\n- Dâu tây: 150.000 - 600.000 VNĐ\n- Matcha: 150.000 - 600.000 VNĐ\n- Trái cây: 150.000 - 600.000 VNĐ\n\nGiá cả tùy thuộc vào kích thước bánh. Bạn muốn đặt loại nào? 😊',
        type: 'received'
      };
    }
    
    if (trimmedMessage.includes('cupcake')) {
      return {
        message: '🧁 **Cupcake của chúng tôi có các loại:**\n- Vani: 25.000 VNĐ/chiếc\n- Socola: 30.000 VNĐ/chiếc\n- Dâu tây: 30.000 VNĐ/chiếc\n- Matcha: 35.000 VNĐ/chiếc\n- Oreo: 35.000 VNĐ/chiếc\n\nTất cả đều được làm từ nguyên liệu tươi ngon! Bạn thích loại nào? 😋',
        type: 'received'
      };
    }
    
    if (trimmedMessage.includes('địa chỉ') || trimmedMessage.includes('cửa hàng')) {
      return {
        message: '🏪 **Thông tin cửa hàng:**\n- Địa chỉ: 123 Đường ABC, Quận 1, TP. Cần Thơ\n- Giờ mở cửa: Thứ 2-6: 7:00-22:00, Thứ 7-CN: 7:00-23:00\n- Hotline: 0123 456 789\n- Email: info@banhngot.com\n\nGiao hàng miễn phí nội thành TP. Cần Thơ! 🚚',
        type: 'received'
      };
    }
    
    if (trimmedMessage.includes('giá') || trimmedMessage.includes('bao nhiêu')) {
      return {
        message: '💰 **Bảng giá tham khảo:**\n- Bánh kem: 150.000 - 600.000 VNĐ (tùy kích thước)\n- Cupcake: 25.000 - 35.000 VNĐ/chiếc\n- Bánh quy: 30.000 - 50.000 VNĐ/100g\n- Bánh mì ngọt: 15.000 - 25.000 VNĐ/chiếc\n\nĐể biết giá chính xác, bạn có thể gọi hotline: 0123 456 789 📞',
        type: 'received'
      };
    }
    
    if (trimmedMessage.includes('đặt hàng') || trimmedMessage.includes('order')) {
      return {
        message: '📞 **Cách đặt hàng:**\n1. Gọi hotline: 0123 456 789\n2. Email: info@banhngot.com\n3. Ghé thăm cửa hàng: 123 Đường ABC, Quận 1, TP. Cần Thơ\n\n**Thanh toán:** Tiền mặt, chuyển khoản, ví điện tử\n**Giao hàng:** Miễn phí nội thành TP. Cần Thơ, giao hàng 24/7 🚚',
        type: 'received'
      };
    }
    
    // Default response
    return {
      message: 'Cảm ơn bạn đã quan tâm! 😊 Tôi có thể giúp bạn với:\n- Thông tin về các loại bánh ngọt\n- Giá cả sản phẩm\n- Cách đặt hàng và giao hàng\n- Thông tin cửa hàng\n\nHoặc bạn có thể gọi hotline: 0123 456 789 để được tư vấn trực tiếp! 📞',
      type: 'received'
    };
  }
};

// Main chatbot object
export const geminiChatbot = {
  // Lấy lịch sử chat
  getChatHistory: async () => {
    if (USE_GEMINI) {
      return chatHistory.map(msg => ({
        message: msg.parts[0].text,
        type: msg.role === 'user' ? 'sent' : 'received'
      }));
    }
    
    if (USE_DEMO) {
      return await demoChatbot.getChatHistory();
    }
    
    // Fallback to demo
    return await demoChatbot.getChatHistory();
  },

  // Gửi message và nhận response
  sendMessage: async (message) => {
    const trimmedMessage = message.trim();
    
    if (!trimmedMessage) {
      return {
        message: 'Xin chào! 👋 Bạn có câu hỏi gì về bánh ngọt không?',
        type: 'received'
      };
    }

    if (USE_DEMO) {
      return await demoChatbot.sendMessage(message);
    }

    if (USE_GEMINI) {
      // Kiểm tra câu hỏi ngoài lề
      if (isOffTopic(trimmedMessage)) {
        return {
          message: `Xin lỗi, tôi không biết về lĩnh vực đó. 😊 Tôi là trợ lý AI chuyên về tiệm bánh ngọt của chúng tôi.

**🍰 Bạn có thể hỏi tôi về:**
- Thông tin về các loại bánh ngọt
- Giá cả sản phẩm
- Cách đặt hàng và giao hàng
- Thông tin cửa hàng và giờ mở cửa
- Phương thức thanh toán
- Chính sách đổi trả

**🏪 Thông tin cửa hàng:**
- Địa chỉ: 123 Đường ABC, Quận 1, TP. Cần Thơ
- Giờ mở cửa: 7:00 - 22:00 (Thứ 2-6), 7:00 - 23:00 (Thứ 7-CN)
- Hotline: 0123 456 789
- Giao hàng miễn phí nội thành TP. Cần Thơ`,
          type: 'received'
        };
      }

      try {
        // Gọi Gemini API
        const response = await callGeminiAPI(trimmedMessage);
        
        // Simulate typing delay
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        return {
          message: response,
          type: 'received'
        };
        
      } catch (error) {
        console.error('Chatbot error:', error);
        // Fallback to demo
        return await demoChatbot.sendMessage(message);
      }
    }
    
    // Fallback to demo
    return await demoChatbot.sendMessage(message);
  },

  // Reset chat history
  resetChat: () => {
    chatHistory = [
      {
        role: 'user',
        parts: [{ text: 'Xin chào! Tôi muốn tìm hiểu về tiệm bánh ngọt của bạn.' }]
      },
      {
        role: 'model',
        parts: [{ text: 'Xin chào! 👋 Rất vui được gặp bạn! Tôi là trợ lý AI của Tiệm Bánh Ngọt ABC. Tôi có thể giúp bạn tìm hiểu về các loại bánh ngọt thơm ngon, cách đặt hàng, giao hàng và các dịch vụ khác của chúng tôi. Bạn có câu hỏi gì không? 🍰' }]
      }
    ];
  },

  // Cập nhật API key
  updateApiKey: (newApiKey) => {
    if (newApiKey && newApiKey.trim()) {
      config.GEMINI_API_KEY = newApiKey.trim();
      return true;
    }
    return false;
  }
}; 