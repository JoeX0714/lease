package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.constant.RedisConstant;
import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.attr.AttrValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.atguigu.lease.web.admin.vo.room.RoomDetailVo;
import com.atguigu.lease.web.admin.vo.room.RoomItemVo;
import com.atguigu.lease.web.admin.vo.room.RoomQueryVo;
import com.atguigu.lease.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liubo
 * @description 针对表【room_info(房间信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class RoomInfoServiceImpl extends ServiceImpl<RoomInfoMapper, RoomInfo>
        implements RoomInfoService {

    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private RoomAttrValueService roomAttrValueService;

    @Autowired
    private RoomFacilityService roomFacilityService;

    @Autowired
    private RoomLabelService roomLabelService;

    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Autowired
    private RoomLeaseTermService roomLeaseTermService;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @Autowired
    private AttrValueMapper attrValueMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseTermMapper leaseTermMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void saveOrUpdateRoom(RoomSubmitVo roomSubmitVo) {
        boolean isNull = roomSubmitVo.getId() != null;
        super.saveOrUpdate(roomSubmitVo);
        if(isNull){
            //删除图片列表
            LambdaQueryWrapper<GraphInfo> graphVoQueryWrapper = new LambdaQueryWrapper<>();
            graphVoQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
            graphVoQueryWrapper.eq(GraphInfo::getItemId,roomSubmitVo.getId());
            graphInfoService.remove(graphVoQueryWrapper);

            //删除属性信息列表
            LambdaQueryWrapper<RoomAttrValue> roomAttrValueQueryWrapper = new LambdaQueryWrapper<>();
            roomAttrValueQueryWrapper.eq(RoomAttrValue::getRoomId,roomSubmitVo.getId());
            roomAttrValueService.remove(roomAttrValueQueryWrapper);

            //删除配套信息列表
            LambdaQueryWrapper<RoomFacility> roomFacilityQueryWrapper = new LambdaQueryWrapper<>();
            roomFacilityQueryWrapper.eq(RoomFacility::getRoomId,roomSubmitVo.getId());
            roomFacilityService.remove(roomFacilityQueryWrapper);

            //删除标签信息列表
            LambdaQueryWrapper<RoomLabel> roomLabelQueryWrapper = new LambdaQueryWrapper<>();
            roomLabelQueryWrapper.eq(RoomLabel::getRoomId,roomSubmitVo.getId());
            roomLabelService.remove(roomLabelQueryWrapper);

            //删除支付方式列表
            LambdaQueryWrapper<RoomPaymentType> roomPaymentTypeQueryWrapper = new LambdaQueryWrapper<>();
            roomPaymentTypeQueryWrapper.eq(RoomPaymentType::getRoomId,roomSubmitVo.getId());
            roomPaymentTypeService.remove(roomPaymentTypeQueryWrapper);

            //删除可选租期列表
            LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermQueryWrapper = new LambdaQueryWrapper<>();
            roomLeaseTermQueryWrapper.eq(RoomLeaseTerm::getRoomId,roomSubmitVo.getId());
            roomLeaseTermService.remove(roomLeaseTermQueryWrapper);

            //删除缓存
            String key = RedisConstant.APP_ROOM_PREFIX + roomSubmitVo.getId();
            redisTemplate.delete(key);
        }

        //插入图片列表
        List<GraphVo> graphVoList = roomSubmitVo.getGraphVoList();
        if(!graphVoList.isEmpty()){
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
            for(GraphVo graphVo:graphVoList){
                GraphInfo graphInfo = new GraphInfo();
                BeanUtils.copyProperties(graphVo,graphInfo);
                graphInfo.setItemType(ItemType.ROOM);
                graphInfo.setItemId(roomSubmitVo.getId());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);
        }

        //插入属性信息列表
        List<Long> attrValueList=roomSubmitVo.getAttrValueIds();
        if(!attrValueList.isEmpty()){
            ArrayList<RoomAttrValue> roomAttrValueList = new ArrayList<>();
            for(Long attrValueId:attrValueList){
                RoomAttrValue roomAttrValue = new RoomAttrValue();
                roomAttrValue.setRoomId(roomSubmitVo.getId());
                roomAttrValue.setAttrValueId(attrValueId);
                roomAttrValueList.add(roomAttrValue);
            }
            roomAttrValueService.saveBatch(roomAttrValueList);
        }

        //插入配套信息列表
        List<Long> facilityList=roomSubmitVo.getFacilityInfoIds();
        if(!facilityList.isEmpty()){
            ArrayList<RoomFacility> roomFacilityList = new ArrayList<>();
            for(Long facilityListId:facilityList){
                RoomFacility roomFacility = new RoomFacility();
                roomFacility.setRoomId(roomSubmitVo.getId());
                roomFacility.setFacilityId(facilityListId);
                roomFacilityList.add(roomFacility);
            }
            roomFacilityService.saveBatch(roomFacilityList);
        }

        //插入标签信息列表
        List<Long> labelList=roomSubmitVo.getLabelInfoIds();
        if(!labelList.isEmpty()){
            ArrayList<RoomLabel> roomLabelList = new ArrayList<>();
            for(Long labelListId:labelList){
                RoomLabel roomLabel = new RoomLabel();
                roomLabel.setRoomId(roomSubmitVo.getId());
                roomLabel.setLabelId(labelListId);
                roomLabelList.add(roomLabel);
            }
            roomLabelService.saveBatch(roomLabelList);
        }

        //插入支付方式列表
        List<Long> paymentTypeList=roomSubmitVo.getPaymentTypeIds();
        if(!paymentTypeList.isEmpty()){
            ArrayList<RoomPaymentType> roomPaymentTypesList = new ArrayList<>();
            for(Long paymentTypeId:paymentTypeList){
                RoomPaymentType roomPaymentType = new RoomPaymentType();
                roomPaymentType.setRoomId(roomSubmitVo.getId());
                roomPaymentType.setPaymentTypeId(paymentTypeId);
                roomPaymentTypesList.add(roomPaymentType);
            }
            roomPaymentTypeService.saveBatch(roomPaymentTypesList);
        }

        //插入可选租期列表
        List<Long> LeaseTermList=roomSubmitVo.getLeaseTermIds();
        if(!LeaseTermList.isEmpty()){
            ArrayList<RoomLeaseTerm> roomLeaseTermList = new ArrayList<>();
            for(Long leaseTermid:LeaseTermList){
                RoomLeaseTerm roomLeaseTerm = new RoomLeaseTerm();
                roomLeaseTerm.setRoomId(roomSubmitVo.getId());
                roomLeaseTerm.setLeaseTermId(leaseTermid);
                roomLeaseTermList.add(roomLeaseTerm);
            }
            roomLeaseTermService.saveBatch(roomLeaseTermList);
        }
    }

    @Override
    public IPage<RoomItemVo> pageItem(IPage<RoomItemVo> page, RoomQueryVo queryVo){
        return roomInfoMapper.pageItem(page,queryVo);
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        //查询roomInfo信息
        RoomInfo roomInfo = roomInfoMapper.selectById(id);
        if (roomInfo == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }

        //查询公寓信息
        LambdaQueryWrapper<ApartmentInfo> apartmentInfoQueryWrapper = new LambdaQueryWrapper<>();
        ApartmentInfo apartmentInfo = apartmentInfoService.getById(roomInfo.getApartmentId());

        //查询图片列表
        LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
        graphInfoQueryWrapper.eq(GraphInfo::getItemId,id);
        graphInfoQueryWrapper.eq(GraphInfo::getItemType,ItemType.ROOM);
        List<GraphInfo> graphInfoList = graphInfoService.list(graphInfoQueryWrapper);

        ArrayList<GraphVo> graphVoList = new ArrayList<>();
        if(!graphInfoList.isEmpty()){
            for(GraphInfo graphInfo:graphInfoList){
                GraphVo graphVo = new GraphVo();
                BeanUtils.copyProperties(graphInfo,graphVo);
                graphVoList.add(graphVo);
            }
        }

        //查询属性信息列表
        List<AttrValueVo> attrValueVoList=attrValueMapper.selectListByRoomId(id);

        //查询配套信息列表
        List<FacilityInfo> facilityInfoList=facilityInfoMapper.selectListByRoomId(id);

        //查询标签信息列表
        List<LabelInfo> labelInfoList=labelInfoMapper.selectListByRoomId(id);

        //查询支付方式列表
        List<PaymentType> paymentTypeList=paymentTypeMapper.selectListByRoomId(id);

        //查询可选租期列表
        List<LeaseTerm> leaseTermList=leaseTermMapper.selectListByRoomId(id);

        //组装结果
        RoomDetailVo roomDetailVo = new RoomDetailVo();
        BeanUtils.copyProperties(roomInfo, roomDetailVo);
        roomDetailVo.setApartmentInfo(apartmentInfo);
        roomDetailVo.setGraphVoList(graphVoList);
        roomDetailVo.setAttrValueVoList(attrValueVoList);
        roomDetailVo.setFacilityInfoList(facilityInfoList);
        roomDetailVo.setLabelInfoList(labelInfoList);
        roomDetailVo.setPaymentTypeList(paymentTypeList);
        roomDetailVo.setLeaseTermList(leaseTermList);

        return roomDetailVo;
    }

    @Override
    public void removeRoomById(Long id) {
        super.removeById(id);

        //删除图片列表
        LambdaQueryWrapper<GraphInfo> graphVoQueryWrapper = new LambdaQueryWrapper<>();
        graphVoQueryWrapper.eq(GraphInfo::getItemType, ItemType.ROOM);
        graphVoQueryWrapper.eq(GraphInfo::getItemId,id);
        graphInfoService.remove(graphVoQueryWrapper);

        //删除属性信息列表
        LambdaQueryWrapper<RoomAttrValue> roomAttrValueQueryWrapper = new LambdaQueryWrapper<>();
        roomAttrValueQueryWrapper.eq(RoomAttrValue::getRoomId,id);
        roomAttrValueService.remove(roomAttrValueQueryWrapper);

        //删除配套信息列表
        LambdaQueryWrapper<RoomFacility> roomFacilityQueryWrapper = new LambdaQueryWrapper<>();
        roomFacilityQueryWrapper.eq(RoomFacility::getRoomId,id);
        roomFacilityService.remove(roomFacilityQueryWrapper);

        //删除标签信息列表
        LambdaQueryWrapper<RoomLabel> roomLabelQueryWrapper = new LambdaQueryWrapper<>();
        roomLabelQueryWrapper.eq(RoomLabel::getRoomId,id);
        roomLabelService.remove(roomLabelQueryWrapper);

        //删除支付方式列表
        LambdaQueryWrapper<RoomPaymentType> roomPaymentTypeQueryWrapper = new LambdaQueryWrapper<>();
        roomPaymentTypeQueryWrapper.eq(RoomPaymentType::getRoomId,id);
        roomPaymentTypeService.remove(roomPaymentTypeQueryWrapper);

        //删除可选租期列表
        LambdaQueryWrapper<RoomLeaseTerm> roomLeaseTermQueryWrapper = new LambdaQueryWrapper<>();
        roomLeaseTermQueryWrapper.eq(RoomLeaseTerm::getRoomId,id);
        roomLeaseTermService.remove(roomLeaseTermQueryWrapper);

        //删除缓存
        String key = RedisConstant.APP_ROOM_PREFIX + id;
        redisTemplate.delete(key);
    }
}




