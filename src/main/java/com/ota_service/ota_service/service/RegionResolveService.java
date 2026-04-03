package com.ota_service.ota_service.service;

import com.ota_service.ota_service.dto.extranet.accommodation.AddressInfo;
import com.ota_service.ota_service.entity.Region;
import com.ota_service.ota_service.exception.ApiException;
import com.ota_service.ota_service.exception.ExceptionType;
import com.ota_service.ota_service.repository.RegionRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionResolveService {

    private final RegionRepository regionRepository;

    public Region resolve(AddressInfo addressInfo) {
        List<Region> activeRegions = regionRepository.findAllByActiveTrueOrderByDepthDescSortOrderAsc();
        Map<Long, Region> regionById = activeRegions.stream()
                .collect(java.util.stream.Collectors.toMap(Region::getId, Function.identity()));

        return findByStructuredRegion(activeRegions, regionById, addressInfo)
                .or(() -> findByAddress(activeRegions, addressInfo))
                .orElseThrow(() -> new ApiException(ExceptionType.INVALID_INPUT, "주소에 해당하는 지역 정보를 찾을 수 없습니다."));
    }

    public String resolveDisplayAddress(AddressInfo addressInfo) {
        if (hasText(addressInfo.roadAddress())) {
            return addressInfo.roadAddress();
        }
        return addressInfo.address();
    }

    private java.util.Optional<Region> findByStructuredRegion(
            List<Region> activeRegions,
            Map<Long, Region> regionById,
            AddressInfo addressInfo
    ) {
        List<String> regionNames = new ArrayList<>();
        addIfHasText(regionNames, addressInfo.region1DepthName());
        addIfHasText(regionNames, addressInfo.region2DepthName());
        addIfHasText(regionNames, addressInfo.region3DepthName());

        for (int size = regionNames.size(); size > 0; size--) {
            List<String> targetPath = regionNames.subList(0, size);
            java.util.Optional<Region> matchedRegion = activeRegions.stream()
                    .filter(region -> Objects.equals(region.getName(), targetPath.get(targetPath.size() - 1)))
                    .filter(region -> matchesAncestorChain(region, regionById, targetPath))
                    .findFirst();
            if (matchedRegion.isPresent()) {
                return matchedRegion;
            }
        }

        return java.util.Optional.empty();
    }

    private boolean matchesAncestorChain(Region region, Map<Long, Region> regionById, List<String> targetPath) {
        List<String> actualPath = new ArrayList<>();
        Region current = region;

        while (current != null) {
            if (current.getDepth() >= 2) {
                actualPath.add(0, current.getName());
            }
            current = current.getParentRegionId() == null ? null : regionById.get(current.getParentRegionId());
        }

        if (actualPath.size() < targetPath.size()) {
            return false;
        }

        List<String> actualTail = actualPath.subList(actualPath.size() - targetPath.size(), actualPath.size());
        return actualTail.equals(targetPath);
    }

    private java.util.Optional<Region> findByAddress(List<Region> activeRegions, AddressInfo addressInfo) {
        return activeRegions.stream()
                .filter(region -> containsRegionName(addressInfo.roadAddress(), region)
                        || containsRegionName(addressInfo.address(), region))
                .findFirst();
    }

    private boolean containsRegionName(String address, Region region) {
        return hasText(address)
                && (address.contains(region.getName()) || address.contains(region.getFullName()));
    }

    private void addIfHasText(List<String> values, String value) {
        if (hasText(value)) {
            values.add(value.trim());
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
