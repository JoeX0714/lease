package com.atguigu.lease.web.app.service.ai;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.ApartmentInfo;
import com.atguigu.lease.model.entity.FacilityInfo;
import com.atguigu.lease.model.entity.LabelInfo;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.vo.attr.AttrValueVo;
import com.atguigu.lease.web.app.vo.fee.FeeValueVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import com.atguigu.lease.web.app.vo.room.RoomItemVo;
import com.atguigu.lease.web.app.vo.room.RoomQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class RoomTools {

    @Autowired
    private RoomInfoService roomInfoService;

    @Autowired
    private AiConversationContext conversationContext;

    @Tool(name = "searchRooms", description = "根据区域、预算、户型、关键词、设施等条件查询真实可租房源列表。必须用这个工具获取房源列表。")
    public RoomSearchResult searchRooms(
            @ToolParam(description = "区域名称，例如昌平区；商圈、地标、公寓名等位置词请优先放到 keyword", required = false) String districtName,
            @ToolParam(description = "最低月租金", required = false) BigDecimal minRent,
            @ToolParam(description = "最高月租金", required = false) BigDecimal maxRent,
            @ToolParam(description = "户型或房间属性，例如一居室、两室一厅、朝南", required = false) String attrName,
            @ToolParam(description = "关键词，可包含公寓名、地址、商圈、地标、房间号等", required = false) String keyword,
            @ToolParam(description = "设施名称列表，例如空调、洗衣机、独立卫生间", required = false) List<String> facilityNames,
            @ToolParam(description = "标签名称列表，例如近地铁、精装修", required = false) List<String> labelNames) {

        RoomQueryVo queryVo = new RoomQueryVo();
        queryVo.setDistrictName(blankToNull(districtName));
        queryVo.setKeyword(blankToNull(keyword));
        queryVo.setAttrName(normalizeAttrName(attrName));
        queryVo.setMinRent(minRent);
        queryVo.setMaxRent(maxRent);
        queryVo.setFacilityNames(emptyToNull(facilityNames));
        queryVo.setLabelNames(emptyToNull(labelNames));
        queryVo.setOrderType("asc");

        IPage<RoomItemVo> page = roomInfoService.pageItem(new Page<>(1, 6), queryVo);
        List<RoomSummary> rooms = page.getRecords().stream()
                .map(room -> toSummary(room, page.getRecords().indexOf(room) + 1))
                .toList();
        Map<Integer, Long> indexToRoomId = new LinkedHashMap<>();
        rooms.forEach(room -> indexToRoomId.put(room.index(), room.roomId()));
        conversationContext.saveSearchResults(indexToRoomId);
        return new RoomSearchResult(page.getTotal(), rooms, queryVo.getAttrName(), indexToRoomId);
    }

    @Tool(name = "resolveRoomReference", description = "根据当前会话最近一次房源搜索结果，将“第二套”“这套”“刚才那个”等引用解析为真实 roomId。解析后再调用 getRoomDetail 或 createViewingAppointment。")
    public RoomReferenceResult resolveRoomReference(
            @ToolParam(description = "房源序号，例如用户说第二套时传 2；用户说这套、刚才那个时可不传", required = false) Integer index,
            @ToolParam(description = "是否解析当前正在讨论的房源，例如这套、刚才那个、它", required = false) Boolean current) {
        Long roomId = conversationContext.resolveRoomId(index, current);
        if (roomId == null) {
            return new RoomReferenceResult(false, null, conversationContext.getCurrentIndexToRoomId(), "没有找到当前会话中的房源引用，请先让用户重新选择具体房源。");
        }
        conversationContext.saveCurrentRoomId(roomId);
        return new RoomReferenceResult(true, roomId, conversationContext.getCurrentIndexToRoomId(), null);
    }

    @Tool(name = "getRoomDetail", description = "根据 roomId 查询真实房源详情。用户询问某套房源详情、位置、设施、价格、租期、支付方式时调用。")
    public RoomDetailResult getRoomDetail(@ToolParam(description = "房源 roomId", required = true) Long roomId) {
        if (roomId == null) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "roomId不能为空");
        }
        RoomDetailVo detail = roomInfoService.getDetailById(roomId);
        if (detail == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR.getCode(), "房源不存在");
        }
        conversationContext.saveCurrentRoomId(roomId);
        ApartmentInfo apartment = detail.getApartmentItemVo();
        List<String> facilities = detail.getFacilityInfoList() == null ? Collections.emptyList()
                : detail.getFacilityInfoList().stream().map(FacilityInfo::getName).filter(Objects::nonNull).toList();
        List<String> labels = detail.getLabelInfoList() == null ? Collections.emptyList()
                : detail.getLabelInfoList().stream().map(LabelInfo::getName).filter(Objects::nonNull).toList();
        List<String> attrs = detail.getAttrValueVoList() == null ? Collections.emptyList()
                : detail.getAttrValueVoList().stream().map(AttrValueVo::getName).filter(Objects::nonNull).toList();
        List<String> fees = detail.getFeeValueVoList() == null ? Collections.emptyList()
                : detail.getFeeValueVoList().stream().map(FeeValueVo::getName).filter(Objects::nonNull).toList();
        return new RoomDetailResult(
                detail.getId(),
                detail.getRoomNumber(),
                detail.getRent(),
                detail.getApartmentId(),
                apartment == null ? null : apartment.getName(),
                apartment == null ? null : apartment.getDistrictName(),
                apartment == null ? null : apartment.getAddressDetail(),
                apartment == null ? null : apartment.getPhone(),
                attrs,
                facilities,
                labels,
                fees);
    }

    private RoomSummary toSummary(RoomItemVo room, int index) {
        ApartmentInfo apartment = room.getApartmentInfo();
        List<String> labels = room.getLabelInfoList() == null ? Collections.emptyList()
                : room.getLabelInfoList().stream().map(LabelInfo::getName).filter(Objects::nonNull).toList();
        return new RoomSummary(
                index,
                room.getId(),
                room.getRoomNumber(),
                room.getRent(),
                apartment == null ? null : apartment.getName(),
                apartment == null ? null : apartment.getDistrictName(),
                apartment == null ? null : apartment.getAddressDetail(),
                labels);
    }

    private String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private String normalizeAttrName(String value) {
        String attrName = blankToNull(value);
        if (attrName == null) {
            return null;
        }
        if ("一居室".equals(attrName) || "一居".equals(attrName) || "单居室".equals(attrName)) {
            return "一室一厅";
        }
        if ("两居室".equals(attrName) || "二居室".equals(attrName) || "两居".equals(attrName) || "二居".equals(attrName)) {
            return "两室一厅";
        }
        if ("三居室".equals(attrName) || "三居".equals(attrName)) {
            return "三室一厅";
        }
        return attrName;
    }

    private List<String> emptyToNull(List<String> values) {
        if (values == null) {
            return null;
        }
        List<String> filtered = values.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
        return filtered.isEmpty() ? null : filtered;
    }

    public record RoomSearchResult(long total, List<RoomSummary> rooms, String normalizedAttrName,
                                   Map<Integer, Long> indexToRoomId) {
    }

    public record RoomReferenceResult(boolean found, Long roomId, Map<Integer, Long> indexToRoomId, String message) {
    }

    public record RoomSummary(int index, Long roomId, String roomNumber, BigDecimal rent, String apartmentName,
                              String districtName, String addressDetail, List<String> labels) {
    }

    public record RoomDetailResult(Long roomId, String roomNumber, BigDecimal rent, Long apartmentId,
                                   String apartmentName, String districtName, String addressDetail, String phone,
                                   List<String> attrs, List<String> facilities, List<String> labels, List<String> fees) {
    }
}
