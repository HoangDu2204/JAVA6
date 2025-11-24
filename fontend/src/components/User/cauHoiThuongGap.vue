<template>
  <div class="page-lien-he">
    <main>
      <section class="bread-crumb full-width-banner" style="background-image: url(//bizweb.dktcdn.net/100/492/035/themes/919334/assets/breadcrumb.jpg?1735117293436);">
        <div class="banner-overlay"></div>
        <div class="container banner-content">
          <div class="title-bread-crumb">
        Câu hỏi thường gặp
          </div>
          <ul class="breadcrumb">
            <li class="home">
              <a href="/"><span>Trang chủ</span></a>
              <span class="mr_lr">&nbsp;<svg aria-hidden="true" focusable="false" data-prefix="fas" data-icon="chevron-right" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 320 512" class="svg-inline--fa fa-chevron-right fa-w-10"><path fill="currentColor" d="M285.476 272.971L91.132 467.314c-9.373 9.373-24.569 9.373-33.941 0l-22.667-22.667c-9.357-9.357-9.375-24.522-.04-33.901L188.505 256 34.484 101.255c-9.335-9.379-9.317-24.544.04-33.901l22.667-22.667c9.373-9.373 24.569-9.373 33.941 0L285.475 239.03c9.373 9.372 9.373 24.568.001 33.941z" class=""></path></svg>&nbsp;</span>
            </li>
            <li><strong><span>Câu hỏi thường gặp</span></strong></li>
          </ul>
        </div>
      </section>
      <div style="height: 50px;"></div>
      <section class="page page-faq container" style="margin-top: 30px;">
        <div class="pg_page padding-top-15">
          <div class="row">
            <div class="col-lg-8">
              <div class="content-page rte">
                <h3 class="faq-title">Hỏi đáp về tài khoản</h3>
                <div class="faq-accordion">
                  <div class="faq-item" :class="{ active: activeFaq === 'account1' }">
                    <div class="faq-question" @click="toggleFaq('account1')">
                      <h4>1. Làm thế nào để tôi trở thành thành viên của Dola?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'account1' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'account1' }">
                      <p>Quý khách vui lòng nhấn vào nút "Đăng ký" ở thanh menu trên cùng của màn hình (Đối với Desktop) hoặc tại góc trái màn hình, chọn biểu tượng Menu rồi chọn "Đăng ký" (Đối với Mobile).&nbsp;</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'account2' }">
                    <div class="faq-question" @click="toggleFaq('account2')">
                      <h4>2. Tại sao tôi không thể đăng nhập vào tài khoản của tôi?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'account2' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'account2' }">
                      <p>Quý khách vui lòng kiểm tra kỹ về kiểu gõ hoặc phím Caps Lock/ IN HOA trong quá trình điền thông tin đăng nhập thành viên, trường hợp không thể đăng nhập thành công quý khách vui lòng chọn nút "Quên mật khẩu" ngay dưới ô mật khẩu và nhập email&nbsp;đã đăng ký.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'account3' }">
                    <div class="faq-question" @click="toggleFaq('account3')">
                      <h4>3. Tôi có thể sử dụng chung tài khoản với người khác được không?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'account3' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'account3' }">
                      <p>Quý khách nên sử dụng tài khoản cá nhân để đảm bảo độ tin cậy cũng như quyền lợi của bản thân khi mua sắm. Việc sử dụng chung tài khoản có thể dẫn đến những sai sót mà người chịu ảnh hưởng trực tiếp chính là quý khách hàng.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'account4' }">
                    <div class="faq-question" @click="toggleFaq('account4')">
                      <h4>4. Tại sao tôi nên đăng ký thành viên Dola?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'account4' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'account4' }">
                      <p>Việc đăng ký tài khoản là cơ hội giúp quý khách trở thành một trong những khách hàng thân thiết của Dola, được tiếp cận nhanh nhất các chương trình khuyến mãi, thông tin ưu đãi khi mua sắm.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'account5' }">
                    <div class="faq-question" @click="toggleFaq('account5')">
                      <h4>5. Dola có chương trình ưu đãi nào hấp dẫn dành cho khách hàng thân thiết?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'account5' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'account5' }">
                      <p>Khi tổng giá trị đơn hàng của quý khách tích lũy đạt đủ điều kiện của từng mức hạng thành viên, quý khách sẽ nhận được ưu đãi giảm giá cho mọi đơn hàng tương, voucher sinh nhật tương ứng của hạng mức thành viên.</p>
                    </div>
                  </div>
                </div>

                <h3 class="faq-title">Hỏi đáp về đặt hàng</h3>
                <div class="faq-accordion">
                  <div class="faq-item" :class="{ active: activeFaq === 'order1' }">
                    <div class="faq-question" @click="toggleFaq('order1')">
                      <h4>1. Tôi có thể đặt hàng bằng những hình thức nào?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'order1' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'order1' }">
                      <p>Quý khách có thể mua hàng tại Dola bằng những hình thức sau:</p>
                      <ul>
                        <li>Đặt hàng trực tuyến tại website</li>
                        <li>Đặt hàng trực tiếp với tư vấn viên qua&nbsp;Hotline 1900 6750</li>
                        <li>Đặt hàng trực tuyến trên các&nbsp;sàn thương mại điện tử&nbsp;</li>
                        <li>Mua hàng trực tiếp tại các hệ thống cửa hàng</li>
                      </ul>
                      <p>Dola luôn khuyến khích quý khách tạo tài khoản và đặt hàng online để được hưởng các chính sách ưu đãi thành viên tốt hơn.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'order2' }">
                    <div class="faq-question" @click="toggleFaq('order2')">
                      <h4>2. Tôi cần hỗ trợ mua hàng, làm cách nào để liên hệ với tư vấn viên?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'order2' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'order2' }">
                      <p>Để liên hệ với nhân viên tư vấn, quý khách vui lòng liên hệ qua&nbsp;Hotline&nbsp;1900 6750&nbsp;trong thời gian từ 9:00 – 18:00, &nbsp;T2 – T6 hằng tuần.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'order3' }">
                    <div class="faq-question" @click="toggleFaq('order3')">
                      <h4>3. Dola có giới hạn về số lượng sản phẩm khi đặt hàng không?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'order3' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'order3' }">
                      <p>Quý khách có thể đặt hàng với số lượng sản phẩm tùy ý. Đối với các sản phẩm có giới hạn về số lượng (trong các dịp flashsale, sale các dịp lễ Tết), hệ thống sẽ cập nhật số lượng được &nbsp;mua tối đa và chỉ ghi nhận đơn hàng có số lượng đặt mua trong giới hạn.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'order4' }">
                    <div class="faq-question" @click="toggleFaq('order4')">
                      <h4>4. Tôi muốn xem lại lịch sử đơn hàng đã mua?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'order4' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'order4' }">
                      <p>Quý khách vào trang tài khoản bằng cách bấm vào nút "Tài khoản" ở thanh menu trên cùng của màn hình (Đối với Desktop) hoặc tại góc trái màn hình, chọn biểu tượng Menu rồi chọn "Tài khoản" (Đối với Mobile). Sau đó chọn "Đơn hàng của bạn" để kiểm tra lại các sản phẩm đã đặt mua. Hoặc quý khách có thể kiểm tra lại những email Dola thông báo trạng thái đơn hàng.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'order5' }">
                    <div class="faq-question" @click="toggleFaq('order5')">
                      <h4>5. Tôi cần làm gì để thay đổi hoặc hủy bỏ đơn hàng đã đặt?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'order5' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'order5' }">
                      <p>Quý khách vui lòng liên hệ tư vấn viên của Dola Hotline&nbsp;1900 6750 để được hủy hoặc thay đổi sản phẩm trong đơn hàng.</p>
                      <p>Lưu ý: Đơn hàng chỉ được hủy khi đơn hàng của quý khách chưa chuyển trạng thái cho đơn vị vận chuyển.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'order6' }">
                    <div class="faq-question" @click="toggleFaq('order6')">
                      <h4>6. Tôi muốn khiếu nại/ đổi trả hàng, quy trình thực hiện như thế nào?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'order6' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'order6' }">
                      <p>Dola luôn sẵn lòng đón nhận các ý kiến góp ý và phản hồi của quý khách quý khách vui lòng liên hệ tư vấn viên của Dola Hotline&nbsp;1900 6750 để được hủy hoặc thay đổi sản phẩm trong đơn hàng</p>
                    </div>
                  </div>
                </div>

                <h3 class="faq-title">Hỏi đáp về thanh toán</h3>
                <div class="faq-accordion">
                  <div class="faq-item" :class="{ active: activeFaq === 'payment1' }">
                    <div class="faq-question" @click="toggleFaq('payment1')">
                      <h4>Tôi có thể thanh toán đơn hàng bằng những hình thức nào?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'payment1' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'payment1' }">
                      <p>Quý khách có thể thanh toán cho Dola bằng những hình thức sau:</p>
                      <ol>
                        <li>Thanh toán tại chỗ (Ship COD)</li>
                        <li>Chuyển khoản trước:&nbsp;Khách hàng có thể chọn chuyển khoản trước vào tài khoản của Dola:</li>
                      </ol>
                    </div>
                  </div>
                </div>

                <h3 class="faq-title">Hỏi đáp về giao hàng</h3>
                <div class="faq-accordion">
                  <div class="faq-item" :class="{ active: activeFaq === 'shipping1' }">
                    <div class="faq-question" @click="toggleFaq('shipping1')">
                      <h4>1. Tôi không ở Hồ Chí Minh, Dola có hỗ trợ giao hàng không?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'shipping1' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'shipping1' }">
                      <p>Dola áp dụng giao hàng trên toàn quốc cho tất cả giá trị đơn hàng. Phí vận chuyển và thời gian giao hàng sẽ thay đổi tùy thuộc vào giá trị đơn hàng và từng khu vực cụ thể.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'shipping2' }">
                    <div class="faq-question" @click="toggleFaq('shipping2')">
                      <h4>2. Tôi có được hỗ trợ phí vận chuyển không?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'shipping2' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'shipping2' }">
                      <p>Dola&nbsp;hỗ trợ Miễn phí vận chuyển cho khách hàng theo khu vực và giá trị đơn hàng:</p>
                      <ul>
                        <li>
                          <p>TP.HCM:&nbsp;Miễn phí vận chuyển với đơn hàng từ 500,000</p>
                        </li>
                        <li>
                          <p>Toàn quốc: Miễn phí vận chuyển với đơn hàng từ 1,500,000</p>
                        </li>
                      </ul>
                      <p>Đối với đơn hàng chưa đạt điều kiện miễn phí vận chuyển sẽ có phí giao dựa trên ước tính hiển thị trên website. Phí giao nhận tính trên trọng lượng hàng hóa, hình thức chuyển phát nhanh hay thường và vị trí địa lý. Do đó, mức chi phí này phụ thuộc vào từng điều kiện cụ thể.</p>
                    </div>
                  </div>

                  <div class="faq-item" :class="{ active: activeFaq === 'shipping3' }">
                    <div class="faq-question" @click="toggleFaq('shipping3')">
                      <h4>3. Bao lâu thì tôi sẽ nhận được sản phẩm sau khi hoàn tất đặt hàng?</h4>
                      <span class="faq-icon" :class="{ 'rotated': activeFaq === 'shipping3' }">+</span>
                    </div>
                    <div class="faq-answer" :class="{ 'show': activeFaq === 'shipping3' }">
                      <p>Đối với khu vực nội thành Hồ Chí Minh, thời gian giao hàng dao động trong khoảng 1 – 2 ngày làm việc; đối với khu vực ngoại thành và các tỉnh, thời gian trung bình nhận hàng dao động từ 3 – 5 ngày.<br>
                        Lưu ý:</p>
                      <ul>
                        <li>Thời gian giao hàng được tính từ lúc Dola hoàn tất việc xác nhận đơn hàng đến khi nhận được hàng, không kể ngày lễ hay thứ 7 và chủ nhật.</li>
                        <li>Trường hợp khách hàng tại Hồ Chí Minh có nhu cầu nhận hàng hỏa tốc, vui lòng liên hệ trực tiếp tới Hotline&nbsp;1900 6750&nbsp;để được hỗ trợ.</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </div>
            </div>
            <div class="col-lg-4">
              <div class="contact">
                <h4>Giải đáp thắc mắc</h4>
                <span class="content-form">
                  Nếu bạn có thắc mắc gì, có thể gửi yêu cầu cho chúng tôi, và chúng tôi sẽ liên lạc lại với bạn sớm nhất có thể .
                </span>
                <div id="pagelogin">
                  <form method="post" action="/postcontact" id="contact-form-sidebar" accept-charset="UTF-8">
                    <input name="FormType" type="hidden" value="contact">
                    <input name="utf8" type="hidden" value="true">
                    <div class="group_contact">
                      <input placeholder="Họ và tên" type="text" class="form-control form-control-lg" required="" value="" name="contact[Name]">
                      <input placeholder="Email" type="email" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$" required="" class="form-control form-control-lg" value="" name="contact[email]">
                      <input type="number" placeholder="Điện thoại*" name="contact[phone]" class="form-control form-control-lg" required="">
                      <textarea placeholder="Nội dung" name="contact[body]" class="form-control content-area form-control-lg" rows="5" required=""></textarea>
                      <button type="submit" class="btn-lienhe">Gửi thông tin</button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const activeFaq = ref(null)

const toggleFaq = (faqId) => {
  if (activeFaq.value === faqId) {
    activeFaq.value = null
  } else {
    activeFaq.value = faqId
  }
}
</script>

<style scoped>
.page-faq {
  padding-top: 30px;
  padding-bottom: 30px;
}

.faq-title,
.page-faq .contact h4 {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 25px;
  position: relative;
  display: inline-block;
  padding-bottom: 8px;
  color: #333;
}

.faq-title:before,
.page-faq .contact h4:before {
  background: #cd9b32;
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  height: 3px;
  width: 60px;
}

.faq-accordion {
  margin-bottom: 30px;
}

.faq-item {
  margin-bottom: 10px;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #E3E3E3;
  background: #fff;
  transition: all 0.3s ease;
}

.faq-item:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.faq-item.active {
  border-color: #cd9b32;
}

.faq-question {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #f7d690;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
}

.faq-question:hover {
  background: #f0c870;
}

.faq-item.active .faq-question {
  background: #cd9b32;
  color: #fff;
}

.faq-question h4 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: inherit;
  flex: 1;
  padding-right: 20px;
}

.faq-icon {
  font-size: 24px;
  font-weight: bold;
  color: inherit;
  transition: transform 0.3s ease;
  min-width: 24px;
  text-align: center;
}

.faq-icon.rotated {
  transform: rotate(45deg);
}

.faq-answer {
  max-height: 0;
  overflow: hidden;
  transition: all 0.3s ease;
  background: #fff;
}

.faq-answer.show {
  max-height: 500px;
  padding: 20px;
}

.faq-answer p {
  margin-bottom: 15px;
  font-size: 16px;
  line-height: 1.6;
  color: #555;
}

.faq-answer p:last-child {
  margin-bottom: 0;
}

.faq-answer ul, .faq-answer ol {
  padding-left: 20px;
  margin: 15px 0;
}

.faq-answer li {
  margin-bottom: 8px;
  font-size: 16px;
  line-height: 1.5;
}

.page-faq .contact {
  margin-bottom: 20px;
  border-radius: 8px;
  padding: 25px;
  background: #f3e7cd;
  border: none;
}

.page-faq .contact .content-form {
  display: block;
  margin-bottom: 20px;
  font-size: 16px;
  color: #555;
  line-height: 1.5;
}

.page-faq .contact .group_contact {
  padding: 0;
}

.page-faq .contact .group_contact .form-control {
  margin-bottom: 15px;
  border-radius: 5px;
  border: 1px solid #ccc;
  padding: 12px 15px;
  font-size: 16px;
  width: 100%;
}

.page-faq .contact .group_contact textarea.form-control {
  min-height: 100px;
  resize: vertical;
}

.page-faq .contact .group_contact .btn-lienhe {
  background: #cd9b32;
  color: #fff;
  border: none;
  padding: 12px 25px;
  font-size: 18px;
  font-weight: 600;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  width: 100%;
}

.page-faq .contact .group_contact .btn-lienhe:hover {
  background: #a88a2a;
}

/* Responsive Design */
@media (max-width: 991px) {
  .page-lien-he .container {
    padding: 0 20px;
  }

  .faq-question h4 {
    font-size: 16px;
  }

  .faq-question {
    padding: 15px;
  }

  .faq-answer.show {
    padding: 15px;
  }

  .faq-title,
  .page-faq .contact h4 {
    font-size: 24px;
  }
}

@media (max-width: 767px) {
  .page-lien-he .container {
    padding: 0 15px;
  }

  .page-faq {
    padding-top: 20px;
    padding-bottom: 20px;
  }

  .faq-question h4 {
    font-size: 15px;
    padding-right: 15px;
  }

  .faq-question {
    padding: 12px;
  }

  .faq-answer.show {
    padding: 12px;
  }

  .faq-answer p,
  .faq-answer li {
    font-size: 14px;
  }

  .faq-title,
  .page-faq .contact h4 {
    font-size: 20px;
    margin-bottom: 20px;
  }

  .page-faq .contact {
    padding: 20px;
    margin-top: 20px;
  }

  .page-faq .contact .group_contact .form-control {
    font-size: 14px;
    padding: 10px 12px;
  }

  .page-faq .contact .group_contact .btn-lienhe {
    font-size: 16px;
    padding: 10px 20px;
  }
}

@media (max-width: 575px) {
  .page-lien-he .container {
    padding: 0 10px;
  }

  .faq-question h4 {
    font-size: 14px;
    padding-right: 10px;
  }

  .faq-question {
    padding: 10px;
  }

  .faq-answer.show {
    padding: 10px;
  }

  .faq-icon {
    font-size: 20px;
    min-width: 20px;
  }

  .faq-title,
  .page-faq .contact h4 {
    font-size: 18px;
  }

  .page-faq .contact {
    padding: 15px;
  }
}

/* Banner styles */
.full-width-banner {
  width: 100vw;
  position: relative;
  left: 50%;
  transform: translateX(-50%);
  min-height: 300px;
  background-size: cover;
  background-position: center center;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 0 50px;
  text-align: center;
  margin: 0;
  box-sizing: border-box;
}

.banner-overlay {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1;
}

.banner-content {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
  text-align: center;
}

.full-width-banner .title-bread-crumb {
  font-size: 55px;
  color: #cd9b32;
  font-weight: normal;
  margin-bottom: 5px;
  font-family: 'Playball', cursive;
  text-align: center;
  display: block;
}

.full-width-banner .breadcrumb {
  font-size: 20px;
  padding: 0;
  width: 100%;
  text-align: center;
  display: block;
  background: none;
  margin: 0;
  list-style: none;
}

.full-width-banner .breadcrumb li {
  display: inline-block;
  margin: 0 3px;
  text-align: center;
}

.full-width-banner .breadcrumb li a {
  font-size: 20px;
  color: #eee;
  text-decoration: none;
  text-align: center;
}

.full-width-banner .breadcrumb li span {
  font-size: 18px;
  color: #eee;
  text-align: center;
}

.full-width-banner .breadcrumb li .mr_lr svg {
  width: 7px;
  height: 7px;
  margin: 0 5px;
  fill: #eee;
  vertical-align: middle;
}

.full-width-banner .breadcrumb li strong span {
  color: #cd9b32;
  font-weight: 600;
  text-align: center;
}

.full-width-banner .breadcrumb li a:hover span {
  color: #fff;
}

/* Mobile banner adjustments */
@media (max-width: 767px) {
  .full-width-banner {
    min-height: 200px;
    padding: 60px 0 30px;
  }

  .banner-content {
    padding: 0 10px;
  }

  .full-width-banner .title-bread-crumb {
    font-size: 36px;
    text-align: center;
  }

  .full-width-banner .breadcrumb {
    font-size: 16px;
    text-align: center;
  }

  .full-width-banner .breadcrumb li a,
  .full-width-banner .breadcrumb li span {
    font-size: 14px;
    text-align: center;
  }
}

@media (max-width: 575px) {
  .full-width-banner {
    min-height: 150px;
    padding: 40px 0 20px;
  }

  .banner-content {
    padding: 0 8px;
  }

  .full-width-banner .title-bread-crumb {
    font-size: 28px;
    text-align: center;
  }

  .full-width-banner .breadcrumb {
    font-size: 14px;
    text-align: center;
  }

  .full-width-banner .breadcrumb li a,
  .full-width-banner .breadcrumb li span {
    font-size: 12px;
    text-align: center;
  }
}

.page-lien-he {
  width: 100%;
  overflow-x: hidden;
}

.page-lien-he main {
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.page-lien-he .container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 15px;
  box-sizing: border-box;
}
.full-width-banner .title-bread-crumb[data-v-6c280968] {
    font-size: 55px;
    color: #cd9b32;
    font-weight: normal;
    margin-bottom: 5px;
   font-family: 'Playball';
    text-align: center;
    display: block;
}
</style>

