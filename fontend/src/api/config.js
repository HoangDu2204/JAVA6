// Cấu hình cho chatbot
export const config = {
  // Google Gemini API Configuration
  GEMINI_API_KEY: import.meta.env.VITE_GEMINI_API_KEY || 'AIzaSyA3GoSAnjTPtQfsbqO5o7sFIx4h1Y9T-jg',
  GEMINI_API_URL: 'https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent',
  
  // Chatbot Configuration
  USE_GEMINI: import.meta.env.VITE_GEMINI_API_KEY && import.meta.env.VITE_GEMINI_API_KEY !== 'your_gemini_api_key_here', // Chỉ sử dụng Gemini nếu có API key hợp lệ
  TYPING_DELAY: 1000, // Delay khi typing (ms)
  MAX_HISTORY: 10, // Số lượng message tối đa trong history
  
  // Store Information
  STORE_INFO: {
    name: 'Tiệm Bánh Ngọt Bakery',
    address: '123 Đường 30 tháng 4, Xuân Khánh, Ninh Kiều, Cần Thơ',
    phone: '0909 090 909',
    email: 'info@banhngotbakery.com',
    hours: {
      weekdays: '7:00 - 22:00',
      weekends: '7:00 - 23:00'
    }
  },
  
  // Product Information
  PRODUCTS: {
    'Bánh kem': {
      types: ['socola', 'vani', 'dâu tây', 'matcha', 'trái cây'],
      price: '150.000 - 600.000 VNĐ (tùy kích thước)'
    },
    'Bánh cupcake': {
      types: ['vani', 'socola', 'dâu tây', 'matcha', 'oreo'],
      price: '25.000 - 35.000 VNĐ/chiếc'
    },
    'Bánh quy': {
      types: ['bơ', 'socola chip', 'hạnh nhân', 'dừa'],
      price: '30.000 - 50.000 VNĐ/100g'
    },
    'Bánh mì ngọt': {
      types: ['sữa', 'nho', 'bơ', 'phô mai'],
      price: '15.000 - 25.000 VNĐ/chiếc'
    }
  },
  
  // Services
  SERVICES: {
    delivery: 'Giao hàng miễn phí nội thành TP. Cần Thơ',
    hours: 'Giao hàng 24/7',
    payment: ['Tiền mặt', 'Chuyển khoản', 'Ví điện tử'],
    return: 'Đổi trả trong 24h (bánh kem: 2h)'
  }
};

// Function để cập nhật API key
export const updateApiKey = (newApiKey) => {
  if (newApiKey && newApiKey.trim()) {
    config.GEMINI_API_KEY = newApiKey.trim();
    config.USE_GEMINI = true;
    return true;
  }
  return false;
};

// Function để toggle giữa Gemini và Demo
export const toggleChatbotMode = (useGemini) => {
  config.USE_GEMINI = useGemini;
  return config.USE_GEMINI;
};

// Function để kiểm tra API key có hợp lệ không
export const validateApiKey = () => {
  const apiKey = config.GEMINI_API_KEY;
  return apiKey && 
         apiKey !== 'your_gemini_api_key_here' && 
         apiKey.startsWith('AIza') && 
         apiKey.length > 20;
};

// Function để lấy thông tin cửa hàng
export const getStoreInfo = () => {
  return config.STORE_INFO;
};

// Function để lấy thông tin sản phẩm
export const getProductInfo = () => {
  return config.PRODUCTS;
};

// Function để lấy thông tin dịch vụ
export const getServiceInfo = () => {
  return config.SERVICES;
}; 