package ru.unit.techno.device.registration.core.impl.entity;

import lombok.Data;
import ru.unit.techno.device.registration.core.impl.entity.enums.DeviceType;

import javax.persistence.*;

@Data
@Entity
@Table(name = "rfid_devices")
@SequenceGenerator(name = "rfid_devices_id_seq", sequenceName = "rfid_devices_id_seq")
public class RfidDeviceEntity {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rfid_devices_id_seq")
    private Long id;
    @Column(name = "device_id")
    private Long deviceId;
    @Column(name = "type")
    private DeviceType type;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id")
    private GroupsEntity group;
}
