package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.supplier.onda.OndaPropertyContent;
import com.ota_service.ota_service.dto.supplier.onda.OndaRateplanContent;
import com.ota_service.ota_service.dto.supplier.onda.OndaRoomtypeContent;

public interface OndaSupplierContentClient {

    OndaPropertyContent fetchProperty(String propertyId);

    OndaRoomtypeContent fetchRoomtype(String propertyId, String roomtypeId);

    OndaRateplanContent fetchRateplan(String propertyId, String roomtypeId, String rateplanId);
}
