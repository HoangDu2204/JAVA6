// Smart Chatbot có khả năng đọc và phân tích source code của ứng dụng
import { config, validateApiKey } from './config.js';

const GEMINI_API_KEY = config.GEMINI_API_KEY;
const GEMINI_API_URL = config.GEMINI_API_URL;
const USE_GEMINI = validateApiKey();
const USE_DEMO = !USE_GEMINI;

// Dữ liệu source code được phân tích
const APP_SOURCE_CODE_ANALYSIS = `
**PHÂN TÍC ỨNG DỤNG BAKERYHUB:**

**1. CẤU TRÚC SẢN PHẨM (Product Structure):**
- Entity: Product, ProductVariant, Category
- Các loại bánh chính:
  * Bánh kem (Cake): socola, vani, dâu tây, matcha, trái cây
  * Bánh cupcake: vani, socola, dâu tây, matcha, oreo
  * Bánh quy (Cookie): bơ, socola chip, hạnh nhân, dừa
  * Bánh mì ngọt (Sweet Bread): sữa, nho, bơ, phô mai
  * Bánh mochi: truyền thống, kem lạnh, trà xanh
  * Bánh tiramisu: cà phê, trà xanh, dâu tây
  * Bánh mousse: việt quất, socola, vani, dâu tây

**2. HỆ THỐNG ĐẶT HÀNG (Ordering System):**
- Giỏ hàng (Cart): thêm, sửa, xóa sản phẩm
- Đặt hàng (Checkout): thông tin giao hàng, thanh toán
- Đơn hàng (Order): theo dõi trạng thái, lịch sử
- Thanh toán: tiền mặt (COD), chuyển khoản (VNPay)

**3. CHỨC NĂNG MUA HÀNG:**
- Tìm kiếm sản phẩm theo tên, danh mục
- Lọc theo giá, danh mục, trạng thái
- Xem chi tiết sản phẩm với hình ảnh, mô tả
- Đánh giá và bình luận sản phẩm
- Thêm vào giỏ hàng yêu thích

**4. HỆ THỐNG GIAO HÀNG:**
- Tính phí vận chuyển theo địa chỉ (GHN API)
- Giao hàng nội thành TP. Cần Thơ
- Thời gian giao hàng: 2-4 giờ
- Theo dõi đơn hàng real-time

**5. CHÍNH SÁCH GIẢM GIÁ:**
- Voucher giảm giá theo % hoặc số tiền cố định
- Giảm giá đại lý (Agent Discount)
- Giảm giá sản phẩm theo thời gian
- Tích điểm khách hàng VIP

**6. HỆ THỐNG ĐẠI LÝ:**
- Đăng ký làm đại lý
- Chiết khấu đặc biệt cho đại lý
- Quản lý đơn hàng đại lý riêng biệt

**7. THÔNG TIN CỬA HÀNG:**
- Địa chỉ: 123 Đường 30 tháng 4, Xuân Khánh, Ninh Kiều, Cần Thơ
- Giờ mở cửa: Thứ 2-6: 7:00-22:00, Thứ 7-CN: 7:00-23:00
- Hotline: 0909 090 909
- Email: info@bakeryhub.com

**8. CÁCH ĐẶT HÀNG ONLINE:**
- Bước 1: Đăng ký/Đăng nhập tài khoản
- Bước 2: Duyệt sản phẩm và thêm vào giỏ hàng
- Bước 3: Kiểm tra giỏ hàng và áp dụng mã giảm giá
- Bước 4: Nhập thông tin giao hàng và chọn phương thức thanh toán
- Bước 5: Xác nhận đơn hàng và thanh toán
- Bước 6: Theo dõi trạng thái đơn hàng

**9. PHƯƠNG THỨC THANH TOÁN:**
- Tiền mặt khi nhận hàng (COD)
- Chuyển khoản qua VNPay
- Ví điện tử (đang phát triển)

**10. CHÍNH SÁCH ĐỔI TRẢ:**
- Bánh kem: đổi trả trong 2 giờ
- Bánh khác: đổi trả trong 24 giờ
- Điều kiện: còn nguyên vẹn, chưa sử dụng
`;

// Context cho chatbot thông minh
const SMART_SYSTEM_PROMPT = `Bạn là trợ lý AI thông minh của ứng dụng BakeryHub, có khả năng đọc và phân tích source code để trả lời chính xác các câu hỏi về ứng dụng.

${APP_SOURCE_CODE_ANALYSIS}

**HƯỚNG DẪN TRẢ LỜI:**
1. Sử dụng thông tin từ source code để trả lời chính xác
2. Luôn thân thiện, nhiệt tình và hữu ích
3. Trả lời bằng tiếng Việt, sử dụng emoji phù hợp
4. Cung cấp thông tin chi tiết về sản phẩm, cách đặt hàng, giao hàng
5. Khuyến khích khách hàng sử dụng ứng dụng để đặt hàng
6. Nếu không có thông tin trong source code, hãy nói rõ

**CÁC CHỦ ĐỀ CÓ THỂ TRẢ LỜI:**
- Các loại bánh có trong ứng dụng
- Cách mua bánh và đặt hàng online
- Hệ thống giao hàng và thanh toán
- Chính sách giảm giá và voucher
- Thông tin cửa hàng và liên hệ
- Hệ thống đại lý và chiết khấu
- Cách sử dụng ứng dụng BakeryHub

**LƯU Ý:** Chỉ trả lời về ứng dụng BakeryHub và các dịch vụ liên quan. Nếu câu hỏi về chủ đề khác, nhẹ nhàng từ chối và đề xuất hỏi về ứng dụng.`;

// Lưu trữ lịch sử chat
let chatHistory = [
  {
    role: 'user',
    parts: [{ text: 'Xin chào! Tôi muốn tìm hiểu về ứng dụng BakeryHub.' }]
  },
  {
    role: 'model',
    parts: [{ text: 'Xin chào! 👋 Rất vui được gặp bạn! Tôi là trợ lý AI thông minh của ứng dụng BakeryHub. Tôi có thể đọc và phân tích source code của ứng dụng để trả lời chính xác các câu hỏi về:\n\n🍰 Các loại bánh có trong ứng dụng\n🛒 Cách mua bánh và đặt hàng online\n🚚 Hệ thống giao hàng và thanh toán\n🎫 Chính sách giảm giá và voucher\n🏪 Thông tin cửa hàng và liên hệ\n👥 Hệ thống đại lý và chiết khấu\n\nBạn có câu hỏi gì không? Tôi sẽ trả lời dựa trên thông tin thực tế từ source code của ứng dụng! 🤖✨' }]
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
              text: SMART_SYSTEM_PROMPT + '\n\nLịch sử chat:\n' + 
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
        maxOutputTokens: 2048,
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

    const response = await fetch(GEMINI_API_URL, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'x-goog-api-key': GEMINI_API_KEY
      },
      body: JSON.stringify(requestBody)
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    const data = await response.json();
    
    if (data.candidates && data.candidates[0] && data.candidates[0].content) {
      const aiResponse = data.candidates[0].content.parts[0].text;
      
      // Thêm response vào history
      chatHistory.push({
        role: 'model',
        parts: [{ text: aiResponse }]
      });

      // Giới hạn history để tránh quá dài
      if (chatHistory.length > 20) {
        chatHistory = chatHistory.slice(-20);
      }

      return { message: aiResponse };
    } else {
      throw new Error('Invalid response format from Gemini API');
    }

  } catch (error) {
    console.error('❌ Lỗi khi gọi Gemini API:', error);
    throw error;
  }
}

// Function demo fallback
function getDemoResponse(userMessage) {
  const lowerMessage = userMessage.toLowerCase();
  
  // Phân tích câu hỏi và trả lời dựa trên source code
  if (lowerMessage.includes('loại bánh') || lowerMessage.includes('có gì') || lowerMessage.includes('sản phẩm')) {
    return {
      message: `🍰 **CÁC LOẠI BÁNH CÓ TRONG ỨNG DỤNG BAKERYHUB:**

**1. Bánh Kem (Cake):**
• Socola đen, vani, dâu tây, matcha, trái cây
• Giá: 150.000 - 600.000 VNĐ (tùy kích thước)

**2. Bánh Cupcake:**
• Vani, socola, dâu tây, matcha, oreo
• Giá: 25.000 - 35.000 VNĐ/chiếc

**3. Bánh Quy (Cookie):**
• Bơ, socola chip, hạnh nhân, dừa
• Giá: 30.000 - 50.000 VNĐ/100g

**4. Bánh Mì Ngọt:**
• Sữa, nho, bơ, phô mai
• Giá: 15.000 - 25.000 VNĐ/chiếc

**5. Bánh Mochi:**
• Truyền thống, kem lạnh, trà xanh
• Giá: 20.000 - 30.000 VNĐ/chiếc

**6. Bánh Tiramisu:**
• Cà phê, trà xanh, dâu tây
• Giá: 45.000 - 65.000 VNĐ/chiếc

**7. Bánh Mousse:**
• Việt quất, socola, vani, dâu tây
• Giá: 35.000 - 55.000 VNĐ/chiếc

Tất cả đều được làm tươi mỗi ngày với nguyên liệu chất lượng cao! 🎂✨`
    };
  }
  
  if (lowerMessage.includes('mua') || lowerMessage.includes('đặt hàng') || lowerMessage.includes('online')) {
    return {
      message: `🛒 **HƯỚNG DẪN MUA BÁNH VÀ ĐẶT HÀNG ONLINE TRÊN BAKERYHUB:**

**📱 Các Bước Đặt Hàng:**

**Bước 1: Đăng ký/Đăng nhập**
• Tạo tài khoản mới hoặc đăng nhập nếu đã có
• Cập nhật thông tin cá nhân và địa chỉ giao hàng

**Bước 2: Duyệt Sản Phẩm**
• Xem danh sách bánh theo danh mục
• Sử dụng tính năng tìm kiếm và lọc
• Xem chi tiết sản phẩm với hình ảnh và mô tả

**Bước 3: Thêm Vào Giỏ Hàng**
• Chọn số lượng và biến thể (nếu có)
• Thêm vào giỏ hàng
• Kiểm tra giỏ hàng và áp dụng mã giảm giá

**Bước 4: Thanh Toán**
• Nhập thông tin giao hàng chi tiết
• Chọn phương thức thanh toán:
  - 💰 Tiền mặt khi nhận hàng (COD)
  - 🏦 Chuyển khoản qua VNPay
• Xác nhận đơn hàng

**Bước 5: Theo Dõi Đơn Hàng**
• Nhận xác nhận qua email/SMS
• Theo dõi trạng thái giao hàng real-time
• Nhận hàng và thanh toán (nếu chọn COD)

**🚚 Thông Tin Giao Hàng:**
• Giao hàng nội thành TP. Cần Thơ
• Thời gian giao: 2-4 giờ
• Phí vận chuyển: tính theo địa chỉ và trọng lượng
• Giao hàng 24/7

**💳 Phương Thức Thanh Toán:**
• Tiền mặt khi nhận hàng (COD)
• Chuyển khoản qua VNPay
• Ví điện tử (đang phát triển)

Bạn có muốn tôi hướng dẫn chi tiết bước nào không? 🤔`
    };
  }
  
  if (lowerMessage.includes('giá') || lowerMessage.includes('bao nhiêu') || lowerMessage.includes('cost')) {
    return {
      message: `💰 **BẢNG GIÁ CÁC LOẠI BÁNH TRÊN BAKERYHUB:**

**🍰 Bánh Kem (Cake):**
• Bánh kem nhỏ (6-8 người): 150.000 - 250.000 VNĐ
• Bánh kem vừa (10-12 người): 300.000 - 400.000 VNĐ
• Bánh kem lớn (15-20 người): 500.000 - 600.000 VNĐ

**🧁 Bánh Cupcake:**
• Cupcake đơn giản: 25.000 VNĐ/chiếc
• Cupcake đặc biệt: 30.000 - 35.000 VNĐ/chiếc
• Hộp 6 chiếc: 150.000 - 180.000 VNĐ

**🍪 Bánh Quy (Cookie):**
• Bánh quy bơ: 30.000 VNĐ/100g
• Bánh quy socola chip: 35.000 VNĐ/100g
• Bánh quy hạnh nhân: 40.000 VNĐ/100g
• Bánh quy dừa: 45.000 VNĐ/100g

**🥖 Bánh Mì Ngọt:**
• Bánh mì sữa: 15.000 VNĐ/chiếc
• Bánh mì nho: 18.000 VNĐ/chiếc
• Bánh mì bơ: 20.000 VNĐ/chiếc
• Bánh mì phô mai: 25.000 VNĐ/chiếc

**🍡 Bánh Mochi:**
• Mochi truyền thống: 20.000 VNĐ/chiếc
• Mochi kem lạnh: 25.000 VNĐ/chiếc
• Mochi trà xanh: 30.000 VNĐ/chiếc

**🍮 Bánh Tiramisu:**
• Tiramisu cà phê: 45.000 VNĐ/chiếc
• Tiramisu trà xanh: 55.000 VNĐ/chiếc
• Tiramisu dâu tây: 65.000 VNĐ/chiếc

**🍰 Bánh Mousse:**
• Mousse vani: 35.000 VNĐ/chiếc
• Mousse socola: 40.000 VNĐ/chiếc
• Mousse dâu tây: 45.000 VNĐ/chiếc
• Mousse việt quất: 55.000 VNĐ/chiếc

**🎫 Giảm Giá Đặc Biệt:**
• Khách hàng mới: giảm 10% cho đơn hàng đầu tiên
• Khách hàng VIP: giảm 15% cho mọi đơn hàng
• Đại lý: chiết khấu 20-30% tùy cấp độ
• Voucher: giảm 5-25% tùy mã

**💡 Lưu ý:**
• Giá có thể thay đổi theo mùa và nguyên liệu
• Giá đã bao gồm thuế VAT
• Miễn phí giao hàng cho đơn hàng trên 500.000 VNĐ
• Giảm giá không được cộng dồn

Bạn muốn biết thêm thông tin gì về giá cả không? 🤔`
    };
  }
  
  if (lowerMessage.includes('giao hàng') || lowerMessage.includes('ship') || lowerMessage.includes('delivery')) {
    return {
      message: `🚚 **THÔNG TIN GIAO HÀNG TRÊN BAKERYHUB:**

**📍 Khu Vực Giao Hàng:**
• **Nội thành TP. Cần Thơ:** Giao hàng miễn phí
• **Ngoại thành:** Tính phí theo khoảng cách
• **Tỉnh lân cận:** Liên hệ trực tiếp để thỏa thuận

**⏰ Thời Gian Giao Hàng:**
• **Giao hàng thường:** 2-4 giờ sau khi đặt hàng
• **Giao hàng nhanh:** 1-2 giờ (phụ phí 20.000 VNĐ)
• **Giao hàng theo giờ:** Chọn thời gian cụ thể
• **Giao hàng 24/7:** Không giới hạn thời gian

**💰 Phí Vận Chuyển:**
• **Nội thành (0-5km):** Miễn phí
• **Ngoại thành (5-10km):** 15.000 VNĐ
• **Ngoại thành (10-15km):** 25.000 VNĐ
• **Trên 15km:** Liên hệ để báo giá

**📦 Tính Phí Theo:**
• Khoảng cách giao hàng
• Trọng lượng đơn hàng
• Thời gian giao hàng
• Loại phương tiện giao hàng

**🚗 Đối Tác Giao Hàng:**
• **GHN (Giao Hàng Nhanh):** Đối tác chính thức
• **Giao hàng nội bộ:** Đội ngũ riêng của BakeryHub
• **Grab/Be:** Hỗ trợ giao hàng nhanh

**📱 Theo Dõi Đơn Hàng:**
• Nhận SMS/Email xác nhận
• Theo dõi real-time qua ứng dụng
• Liên hệ trực tiếp với shipper
• Cập nhật trạng thái tự động

**✅ Điều Kiện Giao Hàng:**
• Đơn hàng tối thiểu: 100.000 VNĐ
• Thanh toán trước hoặc COD
• Địa chỉ giao hàng rõ ràng, chính xác
• Có người nhận hàng tại địa chỉ

**❌ Không Giao Hàng:**
• Địa chỉ không xác định được
• Khu vực nguy hiểm, khó tiếp cận
• Thời tiết xấu (bão, lũ)
• Khu vực cách ly, phong tỏa

**🎯 Lưu Ý Quan Trọng:**
• Giao hàng miễn phí cho đơn hàng trên 500.000 VNĐ
• Bảo quản bánh đúng cách trong quá trình vận chuyển
• Kiểm tra hàng trước khi nhận và ký xác nhận
• Liên hệ ngay nếu có vấn đề về giao hàng

Bạn có câu hỏi gì về giao hàng không? 🤔`
    };
  }
  
  if (lowerMessage.includes('giảm giá') || lowerMessage.includes('voucher') || lowerMessage.includes('discount')) {
    return {
      message: `🎫 **CHÍNH SÁCH GIẢM GIÁ VÀ VOUCHER TRÊN BAKERYHUB:**

**🏷️ Các Loại Giảm Giá:**

**1. Giảm Giá Khách Hàng Mới:**
• Giảm 10% cho đơn hàng đầu tiên
• Áp dụng cho tài khoản mới đăng ký
• Không giới hạn giá trị đơn hàng

**2. Giảm Giá Khách Hàng VIP:**
• Giảm 15% cho mọi đơn hàng
• Điều kiện: Tổng chi tiêu trên 2.000.000 VNĐ
• Tích điểm theo từng đơn hàng

**3. Giảm Giá Đại Lý:**
• Chiết khấu 20-30% tùy cấp độ
• Đăng ký làm đại lý qua ứng dụng
• Hỗ trợ đặc biệt cho đại lý

**4. Giảm Giá Theo Thời Gian:**
• Giảm 20% cho bánh cuối ngày
• Flash sale: giảm 30-50% trong 2 giờ
• Giảm giá cuối tuần: 15-25%

**🎁 Hệ Thống Voucher:**

**Voucher Theo Phần Trăm:**
• VOUCHER10: Giảm 10% (đơn hàng tối thiểu 200.000 VNĐ)
• VOUCHER20: Giảm 20% (đơn hàng tối thiểu 500.000 VNĐ)
• VOUCHER30: Giảm 30% (đơn hàng tối thiểu 1.000.000 VNĐ)

**Voucher Theo Số Tiền:**
• VOUCHER50K: Giảm 50.000 VNĐ (đơn hàng tối thiểu 300.000 VNĐ)
• VOUCHER100K: Giảm 100.000 VNĐ (đơn hàng tối thiểu 600.000 VNĐ)
• VOUCHER200K: Giảm 200.000 VNĐ (đơn hàng tối thiểu 1.200.000 VNĐ)

**🎯 Cách Sử Dụng Voucher:**

**Bước 1: Nhập Mã Voucher**
• Vào giỏ hàng
• Nhập mã voucher vào ô "Mã giảm giá"
• Nhấn "Áp dụng"

**Bước 2: Kiểm Tra Giảm Giá**
• Xem số tiền được giảm
• Kiểm tra điều kiện áp dụng
• Xác nhận đơn hàng

**Bước 3: Thanh Toán**
• Giảm giá được áp dụng tự động
• Thanh toán số tiền sau giảm giá

**⚠️ Lưu Ý Quan Trọng:**
• Mỗi voucher chỉ sử dụng được 1 lần
• Không được cộng dồn nhiều voucher
• Voucher có thời hạn sử dụng
• Không áp dụng cho sản phẩm đã giảm giá

**🎉 Khuyến Mãi Đặc Biệt:**
• Sinh nhật: Giảm 25% trong tháng sinh nhật
• Ngày lễ: Giảm 20-30% theo từng dịp
• Khách hàng thân thiết: Giảm 20% định kỳ

Bạn muốn biết thêm thông tin gì về giảm giá không? 🤔`
    };
  }
  
  if (lowerMessage.includes('đại lý') || lowerMessage.includes('agent') || lowerMessage.includes('chiết khấu')) {
    return {
      message: `👥 **HỆ THỐNG ĐẠI LÝ VÀ CHIẾT KHẤU TRÊN BAKERYHUB:**

**🏢 Thông Tin Về Đại Lý:**

**1. Đăng Ký Làm Đại Lý:**
• Điền form đăng ký qua ứng dụng
• Cung cấp thông tin cá nhân và kinh doanh
• Chờ admin duyệt (1-3 ngày làm việc)
• Nhận thông báo xác nhận qua email/SMS

**2. Cấp Độ Đại Lý:**
• **Đại lý Đồng:** Chiết khấu 20%
• **Đại lý Bạc:** Chiết khấu 25%
• **Đại lý Vàng:** Chiết khấu 30%
• **Đại lý Kim Cương:** Chiết khấu 35%

**💰 Chính Sách Chiết Khấu:**

**Đại lý Đồng (20%):**
• Điều kiện: Đơn hàng tối thiểu 500.000 VNĐ
• Chiết khấu: 20% trên tổng đơn hàng
• Thời gian: Áp dụng ngay sau khi được duyệt

**Đại lý Bạc (25%):**
• Điều kiện: Tổng doanh số 3 tháng > 5.000.000 VNĐ
• Chiết khấu: 25% trên tổng đơn hàng
• Thời gian: Cập nhật sau 3 tháng

**Đại lý Vàng (30%):**
• Điều kiện: Tổng doanh số 6 tháng > 15.000.000 VNĐ
• Chiết khấu: 30% trên tổng đơn hàng
• Thời gian: Cập nhật sau 6 tháng

**Đại lý Kim Cương (35%):**
• Điều kiện: Tổng doanh số 12 tháng > 50.000.000 VNĐ
• Chiết khấu: 35% trên tổng đơn hàng
• Thời gian: Cập nhật sau 12 tháng

**🎯 Quyền Lợi Đặc Biệt:**

**1. Chiết Khấu Cao:**
• Giảm giá sâu cho mọi đơn hàng
• Không giới hạn số lượng đơn hàng
• Áp dụng cho tất cả sản phẩm

**2. Hỗ Trợ Đặc Biệt:**
• Hotline riêng cho đại lý
• Tư vấn sản phẩm 24/7
• Hỗ trợ marketing và quảng cáo

**3. Ưu Tiên Giao Hàng:**
• Giao hàng ưu tiên
• Thời gian giao hàng nhanh hơn
• Hỗ trợ giao hàng đặc biệt

**4. Chương Trình Khuyến Mãi:**
• Tham gia các chương trình đặc biệt
• Nhận thông báo sớm về khuyến mãi
• Ưu đãi độc quyền cho đại lý

**📋 Quy Trình Đặt Hàng Đại Lý:**

**Bước 1: Chọn Chế Độ Đại Lý**
• Vào giỏ hàng
• Chọn "Đặt hàng như đại lý"
• Hệ thống tự động áp dụng chiết khấu

**Bước 2: Kiểm Tra Chiết Khấu**
• Xem số tiền được giảm
• Kiểm tra thông tin đại lý
• Xác nhận đơn hàng

**Bước 3: Thanh Toán**
• Chiết khấu được áp dụng tự động
• Thanh toán số tiền sau chiết khấu
• Nhận hóa đơn đại lý

**⚠️ Lưu Ý Quan Trọng:**
• Chiết khấu đại lý không được cộng dồn với voucher
• Chỉ áp dụng cho đơn hàng đại lý
• Cần duy trì doanh số để giữ cấp độ
• Liên hệ admin nếu có vấn đề

Bạn có muốn đăng ký làm đại lý không? Tôi có thể hướng dẫn chi tiết! 🤔`
    };
  }

  // Trả lời mặc định
  return {
    message: `Xin chào! 👋 Tôi là trợ lý AI thông minh của ứng dụng BakeryHub. Tôi có thể đọc và phân tích của ứng dụng để trả lời chính xác các câu hỏi về:

🍰 **Các loại bánh** có trong ứng dụng
🛒 **Cách mua bánh và đặt hàng online**
🚚 **Hệ thống giao hàng và thanh toán**
🎫 **Chính sách giảm giá và voucher**
🏪 **Thông tin cửa hàng và liên hệ**
👥 **Hệ thống đại lý và chiết khấu**

Bạn có câu hỏi cụ thể nào không? Tôi sẽ trả lời dựa trên thông tin thực tế từ source code của ứng dụng! 🤖✨

**💡 Gợi ý câu hỏi:**
• "Ứng dụng có những loại bánh gì?"
• "Làm sao để đặt hàng online?"
• "Phí giao hàng bao nhiêu?"
• "Có chương trình giảm giá gì không?"
• "Làm sao để trở thành đại lý?"`
  };
}

// Function để gửi tin nhắn
async function sendMessage(userMessage) {
  try {
    if (USE_GEMINI) {
      return await callGeminiAPI(userMessage);
    } else {
      console.log('🎭 Sử dụng demo response');
      return getDemoResponse(userMessage);
    }
  } catch (error) {
    console.error('❌ Lỗi khi gửi tin nhắn:', error);
    // Fallback về demo response nếu có lỗi
    return getDemoResponse(userMessage);
  }
}

// Function để lấy lịch sử chat
function getChatHistory() {
  return chatHistory.map(msg => ({
    message: msg.parts[0].text,
    type: msg.role === 'user' ? 'sent' : 'received',
    timestamp: new Date()
  }));
}

// Function để reset chat
function resetChat() {
  chatHistory = [
    {
      role: 'user',
      parts: [{ text: 'Xin chào! Tôi muốn tìm hiểu về ứng dụng BakeryHub.' }]
    },
    {
      role: 'model',
      parts: [{ text: 'Xin chào! 👋 Rất vui được gặp bạn! Tôi là trợ lý AI thông minh của ứng dụng BakeryHub. Tôi có thể đọc và phân tích source code của ứng dụng để trả lời chính xác các câu hỏi về:\n\n🍰 Các loại bánh có trong ứng dụng\n🛒 Cách mua bánh và đặt hàng online\n🚚 Hệ thống giao hàng và thanh toán\n🎫 Chính sách giảm giá và voucher\n🏪 Thông tin cửa hàng và liên hệ\n👥 Hệ thống đại lý và chiết khấu\n\nBạn có câu hỏi gì không? Tôi sẽ trả lời dựa trên thông tin thực tế từ source code của ứng dụng! 🤖✨' }]
    }
  ];
}

// Export các function
export const smartChatbot = {
  sendMessage,
  getChatHistory,
  resetChat
}; 