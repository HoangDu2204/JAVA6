package com.example.demo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GHNService {

    private final RestTemplate restTemplate;

    @Value("${ghn.token}")
    private String ghnToken;

    @Value("${ghn.shopId}")
    private Long shopId;

    private static final int FROM_DISTRICT_ID = 1442; // Quận Ninh Kiều, Cần Thơ (GHN yêu cầu)

    public GHNService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // =================== LẤY TỈNH ===================
    public List<Map<String, Object>> getProvinces() {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/province";

        HttpHeaders headers = defaultHeaders();
        Map<String, Object> emptyBody = new HashMap<>();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emptyBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && ((Integer) body.get("code")) == 200) {
                return (List<Map<String, Object>>) body.get("data");
            } else {
                System.err.println("GHN lỗi khi lấy tỉnh: " + body);
                throw new RuntimeException("Lỗi lấy danh sách tỉnh từ GHN");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gọi API lấy danh sách tỉnh GHN: " + e.getMessage(), e);
        }
    }


    // =================== LẤY HUYỆN ===================
    public List<Map<String, Object>> getDistricts(int provinceId) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/district";

        HttpHeaders headers = defaultHeaders();
        Map<String, Integer> requestBody = Map.of("province_id", provinceId);
        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && ((Integer) body.get("code")) == 200) {
                return (List<Map<String, Object>>) body.get("data");
            } else {
                System.err.println("GHN lỗi khi lấy huyện: " + body);
                throw new RuntimeException("Lỗi lấy danh sách quận/huyện từ GHN");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gọi API lấy danh sách quận/huyện GHN: " + e.getMessage(), e);
        }
    }


    // =================== LẤY XÃ ===================
    public List<Map<String, Object>> getWards(int districtId) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward";

        HttpHeaders headers = defaultHeaders();
        Map<String, Integer> requestBody = Map.of("district_id", districtId);
        HttpEntity<Map<String, Integer>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && ((Integer) body.get("code")) == 200) {
                return (List<Map<String, Object>>) body.get("data");
            } else {
                System.err.println("GHN lỗi khi lấy xã: " + body);
                throw new RuntimeException("Lỗi lấy danh sách phường/xã từ GHN");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gọi API lấy danh sách phường/xã GHN: " + e.getMessage(), e);
        }
    }


    // =================== TÍNH PHÍ SHIP ===================
    public BigDecimal calculateShippingFee(int toDistrictId, String toWardCode, int weight, int insuranceValue) {
        String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.set("ShopId", String.valueOf(shopId));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("service_type_id", 2);
        requestBody.put("insurance_value", insuranceValue);
        requestBody.put("from_district_id", FROM_DISTRICT_ID);
        requestBody.put("to_district_id", toDistrictId);
        requestBody.put("to_ward_code", toWardCode);
        requestBody.put("height", 15);
        requestBody.put("length", 15);
        requestBody.put("weight", weight);
        requestBody.put("width", 15);

        // Log request để debug
        System.out.println("🚚 GHN Request - URL: " + url);
        System.out.println("🚚 GHN Request - Headers: Token=" + ghnToken + ", ShopId=" + shopId);
        System.out.println("🚚 GHN Request - Body: " + requestBody);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map<String, Object> body = response.getBody();

            // Log response để debug
            System.out.println("🚚 GHN Response - Status: " + response.getStatusCode());
            System.out.println("🚚 GHN Response - Body: " + body);

            if (body == null) {
                System.err.println("❌ GHN không trả về response body");
                throw new RuntimeException("Không có dữ liệu trả về từ GHN");
            }

            // Kiểm tra response code từ GHN
            Object codeObj = body.get("code");
            if (codeObj != null) {
                int code = ((Number) codeObj).intValue();
                if (code != 200) {
                    String message = body.containsKey("message") ? body.get("message").toString() : "Lỗi không xác định";
                    String codeMessage = body.containsKey("code_message") ? body.get("code_message").toString() : "";
                    System.err.println("❌ GHN API trả về lỗi - Code: " + code + ", Message: " + message + ", CodeMessage: " + codeMessage);
                    throw new RuntimeException("GHN API lỗi: " + message + (codeMessage.isEmpty() ? "" : " (" + codeMessage + ")"));
                }
            }

            if (!body.containsKey("data")) {
                System.err.println("❌ GHN không trả về data: " + body);
                throw new RuntimeException("Không có dữ liệu trả về từ GHN");
            }

            Map<String, Object> data = (Map<String, Object>) body.get("data");
            Object total = data.get("total");

            if (total instanceof Number) {
                BigDecimal fee = BigDecimal.valueOf(((Number) total).doubleValue());
                System.out.println("✅ GHN tính phí thành công: " + fee);
                return fee;
            } else {
                System.err.println("❌ GHN trả về phí sai định dạng: " + data);
                throw new RuntimeException("Không thể đọc được phí vận chuyển từ GHN");
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("❌ GHN HTTP Error - Status: " + e.getStatusCode() + ", Body: " + e.getResponseBodyAsString());
            e.printStackTrace();
            throw new RuntimeException("Lỗi HTTP khi gọi API GHN: " + e.getMessage(), e);
        } catch (org.springframework.web.client.ResourceAccessException e) {
            System.err.println("❌ GHN Connection Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể kết nối đến GHN API. Vui lòng kiểm tra kết nối mạng.", e);
        } catch (Exception e) {
            System.err.println("❌ GHN Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gọi API tính phí vận chuyển GHN: " + e.getMessage(), e);
        }
    }

    // =================== HEADER DÙNG CHUNG ===================
    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
     //   System.out.println("GHN TOKEN = " + ghnToken);
        return headers;
    }
}
