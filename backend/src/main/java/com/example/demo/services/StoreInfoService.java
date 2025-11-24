package com.example.demo.services;

import com.example.demo.dto.StoreInfoDTO;
import com.example.demo.entity.StoreInfo;
import com.example.demo.jpas.StoreInfoJPA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class StoreInfoService {
    @Autowired
    private StoreInfoJPA storeInfoJPA;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public StoreInfoDTO getStoreInfo() {
        Optional<StoreInfo> storeOpt = storeInfoJPA.findAll().stream().findFirst();
        return storeOpt.map(this::toDTO).orElse(null);
    }

    public StoreInfoDTO updateStoreInfo(StoreInfoDTO dto) {
        Optional<StoreInfo> storeOpt = storeInfoJPA.findById(dto.getId());
        if (storeOpt.isPresent()) {
            StoreInfo store = storeOpt.get();
            store.setStoreName(dto.getStoreName());
            store.setEmail(dto.getEmail());
            store.setPhone(dto.getPhone());
            store.setAddress(dto.getAddress());
            store.setLogoUrl(dto.getLogoUrl());
            store.setFacebookUrl(dto.getFacebookUrl());
            store.setZaloUrl(dto.getZaloUrl());
            store.setInstagramUrl(dto.getInstagramUrl());
            store.setDescription(dto.getDescription());
            store.setOpenTime(dto.getOpenTime());
            store.setCloseTime(dto.getCloseTime());
            store.setIsActive(dto.getIsActive());
            StoreInfo saved = storeInfoJPA.save(store);
            return toDTO(saved);
        }
        return null;
    }

    public StoreInfoDTO createStoreInfo(StoreInfoDTO dto) {
        StoreInfo store = new StoreInfo();
        store.setStoreName(dto.getStoreName());
        store.setEmail(dto.getEmail());
        store.setPhone(dto.getPhone());
        store.setAddress(dto.getAddress());
        store.setLogoUrl(dto.getLogoUrl());
        store.setFacebookUrl(dto.getFacebookUrl());
        store.setZaloUrl(dto.getZaloUrl());
        store.setInstagramUrl(dto.getInstagramUrl());
        store.setDescription(dto.getDescription());
        store.setOpenTime(dto.getOpenTime());
        store.setCloseTime(dto.getCloseTime());
        store.setIsActive(dto.getIsActive());
        StoreInfo saved = storeInfoJPA.save(store);
        return toDTO(saved);
    }

    public StoreInfoDTO toDTO(StoreInfo store) {
        StoreInfoDTO dto = new StoreInfoDTO();
        dto.setId(store.getId());
        dto.setStoreName(store.getStoreName());
        dto.setEmail(store.getEmail());
        dto.setPhone(store.getPhone());
        dto.setAddress(store.getAddress());
        dto.setLogoUrl(store.getLogoUrl());
        dto.setFacebookUrl(store.getFacebookUrl());
        dto.setZaloUrl(store.getZaloUrl());
        dto.setInstagramUrl(store.getInstagramUrl());
        dto.setDescription(store.getDescription());
        dto.setOpenTime(store.getOpenTime());
        dto.setCloseTime(store.getCloseTime());
        dto.setIsActive(store.getIsActive());
        dto.setCreatedAt(store.getCreatedAt() != null ? store.getCreatedAt().format(formatter) : null);
        return dto;
    }
} 