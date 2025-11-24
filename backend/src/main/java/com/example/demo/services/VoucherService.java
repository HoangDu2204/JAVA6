package com.example.demo.services;

import com.example.demo.entity.Voucher;
import com.example.demo.jpas.VoucherJPA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class VoucherService {

    private static final Logger logger = LoggerFactory.getLogger(VoucherService.class);

    @Autowired
    private VoucherJPA vouchersJPA;

    public VoucherService() {
        logger.info("VoucherService initialized. vouchersJPA: {}", (vouchersJPA != null));
    }

    public List<Voucher> getAllVouchers() {
        try {
            if (vouchersJPA == null) {
                logger.error("vouchersJPA is null, cannot fetch vouchers");
                return Collections.emptyList();
            }
            List<Voucher> vouchers = vouchersJPA.findAll();
            logger.info("Fetched {} vouchers", vouchers.size());
            return vouchers;
        } catch (Exception e) {
            logger.error("Failed to fetch vouchers: {}", e.getMessage(), e);
            return Collections.emptyList(); // Trả về danh sách rỗng thay vì ném exception
        }
    }

    public Voucher getVoucherById(Integer id) {
        try {
            if (vouchersJPA == null) {
                logger.error("vouchersJPA is null, cannot fetch voucher with ID: {}", id);
                return null;
            }
            return vouchersJPA.findById(id).orElse(null);
        } catch (Exception e) {
            logger.error("Failed to fetch voucher with ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    public Voucher saveVoucher(Voucher voucher) {
        try {
            if (vouchersJPA == null) {
                logger.error("vouchersJPA is null, cannot save voucher");
                return null;
            }
            Voucher savedVoucher = vouchersJPA.save(voucher);
            logger.info("Saved voucher with ID: {}", savedVoucher.getId());
            return savedVoucher;
        } catch (Exception e) {
            logger.error("Failed to save voucher: {}", e.getMessage(), e);
            return null;
        }
    }

    public void deleteVoucher(Integer id) {
        try {
            if (vouchersJPA == null) {
                logger.error("vouchersJPA is null, cannot delete voucher with ID: {}", id);
                return;
            }
            vouchersJPA.deleteById(id);
            logger.info("Deleted voucher with ID: {}", id);
        } catch (Exception e) {
            logger.error("Failed to delete voucher with ID {}: {}", id, e.getMessage(), e);
        }
    }

    public Voucher findByCode(String code) {
        try {
            if (vouchersJPA == null) {
                logger.error("vouchersJPA is null, cannot find voucher by code: {}", code);
                return null;
            }
            return vouchersJPA.findByCode(code);
        } catch (Exception e) {
            logger.error("Failed to find voucher by code {}: {}", code, e.getMessage(), e);
            return null;
        }
    }
}