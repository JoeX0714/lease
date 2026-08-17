package com.atguigu.lease.web.app.service.ai;

import com.atguigu.lease.common.exception.LeaseException;
import com.atguigu.lease.common.login.LoginUser;
import com.atguigu.lease.common.login.LoginUserHolder;
import com.atguigu.lease.common.result.ResultCodeEnum;
import com.atguigu.lease.model.entity.UserInfo;
import com.atguigu.lease.model.entity.ViewAppointment;
import com.atguigu.lease.web.app.service.RoomInfoService;
import com.atguigu.lease.web.app.service.UserInfoService;
import com.atguigu.lease.web.app.service.ViewAppointmentService;
import com.atguigu.lease.web.app.vo.appointment.AppointmentItemVo;
import com.atguigu.lease.web.app.vo.room.RoomDetailVo;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class AppointmentTools {

    @Autowired
    private ViewAppointmentService viewAppointmentService;

    @Autowired
    private RoomInfoService roomInfoService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private AiConversationContext conversationContext;

    @Tool(name = "getMyAppointments", description = "查询当前登录用户的真实看房预约列表。不接收 userId，用户身份必须来自后端登录上下文。")
    public List<AppointmentItemVo> getMyAppointments() {
        Long userId = currentUserId();
        return viewAppointmentService.listItemByUserId(userId);
    }

    @Tool(name = "createViewingAppointment", description = "为当前登录用户创建真实看房预约。必须已确定具体 roomId、日期和时间后才能调用。")
    public CreateAppointmentResult createViewingAppointment(
            @ToolParam(description = "房源 roomId", required = true) Long roomId,
            @ToolParam(description = "预约日期，格式 yyyy-MM-dd", required = true) String appointmentDate,
            @ToolParam(description = "预约时间，格式 HH:mm，必须是明确时间", required = true) String appointmentTime) {
        if (roomId == null) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "roomId不能为空");
        }
        Date appointmentDateTime = parseDateTime(appointmentDate, appointmentTime);
        RoomDetailVo roomDetail = roomInfoService.getDetailById(roomId);
        if (roomDetail == null) {
            throw new LeaseException(ResultCodeEnum.DATA_ERROR.getCode(), "房源不存在");
        }
        conversationContext.saveCurrentRoomId(roomId);
        Long userId = currentUserId();
        UserInfo userInfo = userInfoService.getById(userId);

        ViewAppointment appointment = new ViewAppointment();
        appointment.setUserId(userId);
        appointment.setRoomId(roomId);
        appointment.setApartmentId(roomDetail.getApartmentId());
        appointment.setAppointmentTime(appointmentDateTime);
        if (userInfo != null) {
            appointment.setName(userInfo.getNickname());
            appointment.setPhone(userInfo.getPhone());
        }
        boolean success = viewAppointmentService.saveOrUpdate(appointment);
        return new CreateAppointmentResult(success, appointment.getId(), roomId, roomDetail.getApartmentId(),
                appointmentDateTime);
    }

    private Long currentUserId() {
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        if (loginUser == null || loginUser.getUserId() == null) {
            throw new LeaseException(ResultCodeEnum.ADMIN_LOGIN_AUTH);
        }
        return loginUser.getUserId();
    }

    private Date parseDateTime(String appointmentDate, String appointmentTime) {
        if (appointmentDate == null || appointmentDate.trim().isEmpty()
                || appointmentTime == null || appointmentTime.trim().isEmpty()) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "预约日期和时间不能为空");
        }
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            formatter.setLenient(false);
            return formatter.parse(appointmentDate.trim() + " " + appointmentTime.trim());
        } catch (ParseException e) {
            throw new LeaseException(ResultCodeEnum.PARAM_ERROR.getCode(), "预约时间格式应为 yyyy-MM-dd HH:mm");
        }
    }

    public record CreateAppointmentResult(boolean success, Long appointmentId, Long roomId, Long apartmentId,
                                          Date appointmentTime) {
    }
}
