package ru.unit.techno.device.registration.core.impl.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import ru.unit.techno.device.registration.core.impl.dto.DeviceDto;
import ru.unit.techno.device.registration.core.impl.dto.RegistrationDto;
import ru.unit.techno.device.registration.core.impl.entity.BarrierEntity;
import ru.unit.techno.device.registration.core.impl.entity.GroupsEntity;
import ru.unit.techno.device.registration.core.impl.entity.RfidDeviceEntity;
import ru.unit.techno.device.registration.core.impl.entity.enums.DeviceType;
import ru.unit.techno.device.registration.core.impl.repository.BarrierRepository;
import ru.unit.techno.device.registration.core.impl.repository.GroupsRepository;
import ru.unit.techno.device.registration.core.impl.repository.RfidDevicesRepository;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final GroupsRepository groupsRepository;
    private final RfidDevicesRepository rfidDevicesRepository;
    private final BarrierRepository barrierRepository;

    public Long registerGroup(RegistrationDto registrationDto) {
        GroupsEntity groupsEntity;
        //TODO Проверить были ли регистрирующиеся устройства в какой либо группе

        if (registrationDto.getGroup() == null) {
            groupsEntity = new GroupsEntity();
            groupsEntity.setGroupId(generateRandomGroupId());
        } else {
            groupsEntity = groupsRepository.findByGroupId(registrationDto.getGroup());
        }

        groupsEntity.setAddress(registrationDto.getAddress());

        if (!CollectionUtils.isEmpty(registrationDto.getGroups())) {
            parseRegistrationRequestAndSaveDevices(registrationDto.getGroups(), groupsEntity);
        } else {
            throw new IllegalArgumentException("Cant register devices if devices count is 0");
        }

        return groupsEntity.getGroupId();
    }

    private Long generateRandomGroupId() {
        Random random = new Random();
        long id = random.nextLong();

        if (groupsRepository.isGroupExist(id)) {
            return generateRandomGroupId();
        } else {
            return id;
        }
    }

    private void parseRegistrationRequestAndSaveDevices(List<DeviceDto> devices, GroupsEntity groupId) {

        for (DeviceDto deviceDto : devices) {
            switch (deviceDto.getType()) {
                case ("RFID"):
                    if (!checkRfidDevice(deviceDto.getId())) {
                        RfidDeviceEntity rfidEntity = new RfidDeviceEntity();
                        rfidEntity.setDeviceId(deviceDto.getId())
                                .setGroup(groupId)
                                .setType(DeviceType.RFID);
                        rfidDevicesRepository.save(rfidEntity);
                    } else {
                        RfidDeviceEntity existedRfidDevice = rfidDevicesRepository.findByDeviceId(deviceDto.getId());
                        existedRfidDevice.setGroup(groupId);
                        rfidDevicesRepository.save(existedRfidDevice);
                    }

                case ("ENTRY"):
                    if (!checkBarrierDevice(deviceDto.getId())) {
                        BarrierEntity barrierEntity = new BarrierEntity();
                        barrierEntity.setDeviceId(deviceDto.getId())
                                .setGroup(groupId)
                                .setType(DeviceType.RFID);
                        barrierRepository.save(barrierEntity);
                    } else {
                        BarrierEntity existedRfidDevice = barrierRepository.findByDeviceId(deviceDto.getId());
                        existedRfidDevice.setGroup(groupId);
                        barrierRepository.save(existedRfidDevice);
                    }
            }
        }
    }

    private boolean checkRfidDevice(Long deviceId) {
        RfidDeviceEntity byDeviceId = rfidDevicesRepository.findByDeviceId(deviceId);
        if (byDeviceId != null) {
            log.info("This device is already registered in service");
            return false;
        }

        return true;
    }

    private boolean checkBarrierDevice(Long deviceId) {
        BarrierEntity byDeviceId = barrierRepository.findByDeviceId(deviceId);
        if (byDeviceId != null) {
            log.info("This device is already registered in service");
            return false;
        }

        return true;
    }
}
