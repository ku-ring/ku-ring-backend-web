package com.kustacks.kuring.service;

import com.kustacks.kuring.CategoryName;
import com.kustacks.kuring.error.ErrorCode;
import com.kustacks.kuring.error.InternalLogicException;
import com.kustacks.kuring.mq.MQUpdaterProducer;
import com.kustacks.kuring.mq.dto.NoticeUpdateRequestMQMessageDTO;
import com.kustacks.kuring.mq.dto.StaffUpdateRequestMQMessageDTO;
import com.kustacks.kuring.mq.dto.UserUpdateRequestMQMessageDTO;
import com.kustacks.kuring.persistence.admin.Admin;
import com.kustacks.kuring.persistence.admin.AdminRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class TriggerServiceImpl implements TriggerService {

    private final AdminRepository adminRepository;
    private final MQUpdaterProducer<NoticeUpdateRequestMQMessageDTO> mqNoticeUpdaterProducer;
    private final MQUpdaterProducer<StaffUpdateRequestMQMessageDTO> mqStaffUpdaterProducer;
    private final MQUpdaterProducer<UserUpdateRequestMQMessageDTO> mqUserUpdaterProducer;

    public TriggerServiceImpl(AdminRepository adminRepository,
                              MQUpdaterProducer<NoticeUpdateRequestMQMessageDTO> rabbitMQNoticeUpdaterProducer,
                              MQUpdaterProducer<StaffUpdateRequestMQMessageDTO> rabbitMQStaffUpdaterProducer,
                              MQUpdaterProducer<UserUpdateRequestMQMessageDTO> rabbitMQUserUpdaterProducer
    ) {
        this.adminRepository = adminRepository;
        this.mqNoticeUpdaterProducer = rabbitMQNoticeUpdaterProducer;
        this.mqStaffUpdaterProducer = rabbitMQStaffUpdaterProducer;
        this.mqUserUpdaterProducer = rabbitMQUserUpdaterProducer;
    }

    public boolean checkAuth(String token) {
        Admin admin = adminRepository.findByToken(token);
        return admin != null;
    }

    public void triggerNoticeWorker(String type) {
        CategoryName[] categoryNames = CategoryName.values();
        List<NoticeUpdateRequestMQMessageDTO> messageDTOList = new ArrayList<>(categoryNames.length);
        for (CategoryName categoryName : categoryNames) {
            messageDTOList.add(NoticeUpdateRequestMQMessageDTO.builder()
                    .type(type)
                    .category(categoryName.getName())
                    .build());
        }
        try {
            mqNoticeUpdaterProducer.publish(messageDTOList);
        } catch (IOException | TimeoutException e) {
            throw new InternalLogicException(ErrorCode.MQ_CANNOT_PUBLISH);
        }
    }

    public void triggerStaffWorker(String type) {
        CategoryName[] categoryNames = CategoryName.values();
        List<StaffUpdateRequestMQMessageDTO> messageDTOList = new ArrayList<>(categoryNames.length);
        for (CategoryName categoryName : categoryNames) {
            if(CategoryName.BACHELOR.equals(categoryName) ||
                    CategoryName.SCHOLARSHIP.equals(categoryName) ||
                    CategoryName.EMPLOYMENT.equals(categoryName) ||
                    CategoryName.NATIONAL.equals(categoryName) ||
                    CategoryName.STUDENT.equals(categoryName) ||
                    CategoryName.INDUSTRY_UNIV.equals(categoryName) ||
                    CategoryName.NORMAL.equals(categoryName) ||
                    CategoryName.LIBRARY.equals(categoryName)
            ) {
            } else {
                messageDTOList.add(StaffUpdateRequestMQMessageDTO.builder()
                        .type(type)
                        .category(categoryName.getName())
                        .build());
            }
        }
        try {
            mqStaffUpdaterProducer.publish(messageDTOList);
        } catch (IOException | TimeoutException e) {
            throw new InternalLogicException(ErrorCode.MQ_CANNOT_PUBLISH);
        }
    }

    public void triggerUserWorker(String type) {
        UserUpdateRequestMQMessageDTO messageDTO = UserUpdateRequestMQMessageDTO.builder()
                .type(type)
                .build();

        try {
            mqUserUpdaterProducer.publish(messageDTO);
        } catch (IOException | TimeoutException e) {
            throw new InternalLogicException(ErrorCode.MQ_CANNOT_PUBLISH);
        }
    }
}
