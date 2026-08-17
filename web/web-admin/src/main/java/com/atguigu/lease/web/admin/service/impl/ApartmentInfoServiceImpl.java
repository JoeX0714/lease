package com.atguigu.lease.web.admin.service.impl;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.*;
import com.atguigu.lease.model.enums.ItemType;
import com.atguigu.lease.web.admin.mapper.*;
import com.atguigu.lease.web.admin.service.*;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentDetailVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentItemVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentQueryVo;
import com.atguigu.lease.web.admin.vo.apartment.ApartmentSubmitVo;
import com.atguigu.lease.web.admin.vo.fee.FeeValueVo;
import com.atguigu.lease.web.admin.vo.graph.GraphVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author liubo
 * @description 针对表【apartment_info(公寓信息表)】的数据库操作Service实现
 * @createDate 2023-07-24 15:48:00
 */
@Service
public class ApartmentInfoServiceImpl extends ServiceImpl<ApartmentInfoMapper, ApartmentInfo>
        implements ApartmentInfoService {


    @Autowired
    private GraphInfoService graphInfoService;

    @Autowired
    private ApartmentFacilityService  apartmentFacilityService;

    @Autowired
    private ApartmentLabelService apartmentLabelService;

    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private LabelInfoMapper labelInfoMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;

    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Override
    public void saveOrUpdateApartment(ApartmentSubmitVo apartmentSubmitVo) {
        boolean isUpdate=apartmentSubmitVo.getId()!=null;//要先判断，如果判断放后面，saveOrUpdate在前，那无论如何传入的apartmentSubmitVo id都不为空
        super.saveOrUpdate(apartmentSubmitVo);/*ApartmentInfoServiceImpl继承了通用Service：ServiceImpl，
          IService
    ├─ 声明 saveOrUpdate
    │
    ├─ ApartmentInfoService extends IService
    │    └─ 继承“方法声明”
    │
    └─ ServiceImpl implements IService   ServiceImpl实现了IService接口
         └─ 提供“方法实现”
               ↑
   ApartmentInfoServiceImpl extends ServiceImpl
         └─ 在这里用 super.saveOrUpdate(...)*/

        if(isUpdate){
            //1.删除图片列表
            LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
            graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
            graphQueryWrapper.eq(GraphInfo::getItemId, apartmentSubmitVo.getId());
            graphInfoService.remove(graphQueryWrapper);

            //2.删除配套列表,注意是删除公寓和配套之间的关系，不是删配套表里的内容，因为一个配套是多个公寓共用的
            LambdaQueryWrapper<ApartmentFacility> apartmentFacilityQueryWrapper = new LambdaQueryWrapper<>();
            apartmentFacilityQueryWrapper.eq(ApartmentFacility::getApartmentId,
                    apartmentSubmitVo.getId());
            apartmentFacilityService.remove(apartmentFacilityQueryWrapper);

            //3.删除标签列表
            LambdaQueryWrapper<ApartmentLabel> apartmentLabelQueryWrapper = new LambdaQueryWrapper<>();
            apartmentLabelQueryWrapper.eq(ApartmentLabel::getApartmentId,
                    apartmentSubmitVo.getId());
            apartmentLabelService.remove(apartmentLabelQueryWrapper);

            //4.删除杂费列表
            LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueQueryWrapper = new LambdaQueryWrapper<>();
            apartmentFeeValueQueryWrapper.eq(ApartmentFeeValue::getApartmentId,
                    apartmentSubmitVo.getId());
            apartmentFeeValueService.remove(apartmentFeeValueQueryWrapper);
        }

        //1.插入图片列表
        List<GraphVo> graphVoList = apartmentSubmitVo.getGraphVoList();
        if(!graphVoList.isEmpty()){
            ArrayList<GraphInfo> graphInfoList = new ArrayList<>();
            for(GraphVo graphVo:graphVoList){
                GraphInfo graphInfo=new GraphInfo();
                graphInfo.setItemType(ItemType.APARTMENT);
                graphInfo.setItemId(apartmentSubmitVo.getId());
                graphInfo.setName(graphVo.getName());
                graphInfo.setUrl(graphVo.getUrl());
                graphInfoList.add(graphInfo);
            }
            graphInfoService.saveBatch(graphInfoList);/*saveBatch 是 MyBatis-Plus 通用 Service 提供的批量保存方法
              把 graphInfoList 里的多条 GraphInfo 对象一次批量写入数据库。可以理解为循环执行多条新增，只是 MyBatis-Plus 帮你封装了批量处理逻辑*/
        }


        //2.插入配套列表
        List<Long> facilityInfoIdsList = apartmentSubmitVo.getFacilityInfoIds();
        if(!facilityInfoIdsList.isEmpty()){
            ArrayList<ApartmentFacility> apartmentFacilityList = new ArrayList<>();
            for(Long id:facilityInfoIdsList){
                ApartmentFacility apartmentFacility = new ApartmentFacility();
                apartmentFacility.setApartmentId(apartmentSubmitVo.getId());
                apartmentFacility.setFacilityId(id);
                apartmentFacilityList.add(apartmentFacility);
            }
            apartmentFacilityService.saveBatch(apartmentFacilityList);
        }

        //3.插入标签列表
        List<Long> labelIds = apartmentSubmitVo.getLabelIds();
        if(!labelIds.isEmpty()){
            ArrayList<ApartmentLabel>  apartmentLabelList= new ArrayList<>();
            for(Long id:labelIds){
                ApartmentLabel apartmentLabel = new ApartmentLabel();
                apartmentLabel.setApartmentId(apartmentSubmitVo.getId());
                apartmentLabel.setLabelId(id);
                apartmentLabelList.add(apartmentLabel);
            }
            apartmentLabelService.saveBatch(apartmentLabelList);
        }

        //4.插入杂费列表
        List<Long> feeValueIds = apartmentSubmitVo.getFeeValueIds();
        if(!feeValueIds.isEmpty()){
            ArrayList<ApartmentFeeValue> apartmentFeeValueList = new ArrayList<>();
            for(Long id:feeValueIds){
                ApartmentFeeValue apartmentFeeValue = new ApartmentFeeValue();
                apartmentFeeValue.setApartmentId(apartmentSubmitVo.getId());
                apartmentFeeValue.setFeeValueId(id);
                apartmentFeeValueList.add(apartmentFeeValue);
            }
            apartmentFeeValueService.saveBatch(apartmentFeeValueList);
        }
    }

    @Override
    public IPage<ApartmentItemVo> pageItem(IPage<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        return apartmentInfoMapper.pageItem(page,queryVo);
    }

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        //1.查询公寓列表
        ApartmentInfo apartmentInfo = apartmentInfoMapper.selectById(id);
        if (apartmentInfo == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR);
        }

        //2.查询图片列表
        //Controller层传过来的ApartmentDetailVo中图片列表是List<GraphVo>，而数据库中只有GraphInfo表
        LambdaQueryWrapper<GraphInfo> graphInfoQueryWrapper = new LambdaQueryWrapper<>();
        graphInfoQueryWrapper.eq(GraphInfo::getItemId,id);
        graphInfoQueryWrapper.eq(GraphInfo::getItemType,ItemType.APARTMENT);
        List<GraphInfo> graphInfoList = graphInfoService.list(graphInfoQueryWrapper);

        //类型转化这里可以自定义sql/for循环/BeanUtils.copyProperties/stream流的map方法
        /*for循环写法
        List<GraphVo> graphVoList = new ArrayList<>();
        for(GraphInfo graphInfo:graphInfolist){
            GraphVo graphVo = new GraphVo();
            graphVo.setName(graphInfo.getName());
            graphVo.setUrl(graphInfo.getUrl());
            graphVoList.add(graphVo);
        }

        BeanUtils.copyProperties写法
        List<GraphVo> graphVoList = new ArrayList<>();
        for(GraphInfo graphInfo:graphInfolist){
            GraphVo graphVo = new GraphVo();
            BeanUtils.copyProperties(graphInfo,graphVo)两个对象中属性名相同、类型兼容才能复制
            graphVoList.add(graphVo);
        }

        stream流的map方法
        Stream<GraphInfo> graphInfostream = graphInfoList.stream();
        Stream<GraphVo> graphVoStream=graphInfostream.map(graphInfo -> {
            GraphVo graphVo = new GraphVo();
            BeanUtils.copyProperties(graphInfo,graphVo);
            return graphVo;
        });
        List<GraphVo> graphVoList = graphVoStream.toList();*/
        List<GraphVo>graphVoList=graphInfoMapper.selectListByItemTypeAndId(ItemType.APARTMENT,id);

        //3.查询标签列表
        List<LabelInfo> labelInfoList=labelInfoMapper.selectListByApartmentId(id);

        //4.查询配套列表
        List<FacilityInfo> facilityInfoList=facilityInfoMapper.selectListByApartmentId(id);

        //5.查询杂费列表
        List<FeeValueVo> feeValueVoList=feeValueMapper.selectListByApartmentId(id);

        //组装结果
        ApartmentDetailVo apartmentDetailVo = new ApartmentDetailVo();
        BeanUtils.copyProperties(apartmentInfo,apartmentDetailVo);
        apartmentDetailVo.setGraphVoList(graphVoList);
        apartmentDetailVo.setLabelInfoList(labelInfoList);
        apartmentDetailVo.setFacilityInfoList(facilityInfoList);
        apartmentDetailVo.setFeeValueVoList(feeValueVoList);

        return apartmentDetailVo;
    }

    @Override
    public void removeApartmentById(Long id) {
        //判断公寓下是否有房间
        LambdaQueryWrapper<RoomInfo> roomInfoQueryWrapper = new LambdaQueryWrapper<>();
        roomInfoQueryWrapper.eq(RoomInfo::getApartmentId,id);
        Long count = roomInfoMapper.selectCount(roomInfoQueryWrapper);

        if(count>0){
            //终止删除并响应提示信息
            throw new LeaseException(ResultCodeEnum.ADMIN_APARTMENT_DELETE_ERROR);
        }
        super.removeById(id);
        //1.删除图片列表
        LambdaQueryWrapper<GraphInfo> graphQueryWrapper = new LambdaQueryWrapper<>();
        graphQueryWrapper.eq(GraphInfo::getItemType, ItemType.APARTMENT);
        graphQueryWrapper.eq(GraphInfo::getItemId, id);
        graphInfoService.remove(graphQueryWrapper);

        //2.删除配套列表,注意是删除公寓和配套之间的关系，不是删配套表里的内容，因为一个配套是多个公寓共用的
        LambdaQueryWrapper<ApartmentFacility> apartmentFacilityQueryWrapper = new LambdaQueryWrapper<>();
        apartmentFacilityQueryWrapper.eq(ApartmentFacility::getApartmentId,id);
        apartmentFacilityService.remove(apartmentFacilityQueryWrapper);

        //3.删除标签列表
        LambdaQueryWrapper<ApartmentLabel> apartmentLabelQueryWrapper = new LambdaQueryWrapper<>();
        apartmentLabelQueryWrapper.eq(ApartmentLabel::getApartmentId,id);
        apartmentLabelService.remove(apartmentLabelQueryWrapper);

        //4.删除杂费列表
        LambdaQueryWrapper<ApartmentFeeValue> apartmentFeeValueQueryWrapper = new LambdaQueryWrapper<>();
        apartmentFeeValueQueryWrapper.eq(ApartmentFeeValue::getApartmentId,id);
        apartmentFeeValueService.remove(apartmentFeeValueQueryWrapper);
    }
}




