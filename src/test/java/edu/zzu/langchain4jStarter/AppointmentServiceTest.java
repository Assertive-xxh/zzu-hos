package edu.zzu.langchain4jStarter;

import edu.zzu.langchain4jStarter.entity.AppointmentRecord;
import edu.zzu.langchain4jStarter.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional; // 用于回滚测试数据

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AppointmentServiceTest {

    @Autowired
    private AppointmentService appointmentService; // 注入您的Service接口

    private AppointmentRecord sampleRecord;

    @BeforeEach
    void setUp() {
        sampleRecord = new AppointmentRecord();
        sampleRecord.setUsername("测试用户");
        sampleRecord.setIdCard("10000000000000000X");
        sampleRecord.setDepartmentName("测试科室");
        sampleRecord.setAppointmentDate(LocalDate.of(2025, 12, 1));
        sampleRecord.setAppointmentTimeSlot("上午");
        sampleRecord.setPhoneNumber("13600000000");
        sampleRecord.setAppointmentStatus("PENDING");
    }

    @Test
    @DisplayName("测试保存挂号记录 (Create)")
    void testSaveAppointment() {
        boolean saved = appointmentService.save(sampleRecord); // IService 提供的 save 方法
        assertTrue(saved, "记录应该成功保存");
        assertNotNull(sampleRecord.getId(), "保存后记录应该有ID");

        AppointmentRecord fetchedRecord = appointmentService.getById(sampleRecord.getId()); // IService 提供的 getById 方法
        assertNotNull(fetchedRecord, "应该能从数据库中查询到保存的记录");
        assertEquals(sampleRecord.getUsername(), fetchedRecord.getUsername());
    }

    @Test
    @DisplayName("测试根据ID查询挂号记录 (Read)")
    void testGetAppointmentById() {
        appointmentService.save(sampleRecord); // 先保存一条记录
        assertNotNull(sampleRecord.getId(), "ID should be generated after save");

        AppointmentRecord fetchedRecord = appointmentService.getById(sampleRecord.getId());
        assertNotNull(fetchedRecord, "应该能根据ID查询到记录");
        assertEquals(sampleRecord.getIdCard(), fetchedRecord.getIdCard());
    }

    @Test
    @DisplayName("测试根据ID查询挂号记录 - ID不存在")
    void testGetAppointmentById_NotFound() {
        AppointmentRecord fetchedRecord = appointmentService.getById(-999L); // 一个不存在的ID
        assertNull(fetchedRecord, "查询不存在的ID时应返回null");
    }

    @Test
    @DisplayName("测试更新挂号记录 (Update)")
    void testUpdateAppointment() {
        appointmentService.save(sampleRecord);
        assertNotNull(sampleRecord.getId(), "ID should be generated after save");

        AppointmentRecord recordToUpdate = appointmentService.getById(sampleRecord.getId());
        assertNotNull(recordToUpdate, "更新前应能获取到记录");

        String newStatus = "CONFIRMED";
        String newPhoneNumber = "13711112222";
        recordToUpdate.setAppointmentStatus(newStatus);
        recordToUpdate.setPhoneNumber(newPhoneNumber);

        boolean updated = appointmentService.updateById(recordToUpdate); // IService 提供的 updateById 方法
        assertTrue(updated, "记录应该成功更新");

        AppointmentRecord updatedRecordFromDB = appointmentService.getById(sampleRecord.getId());
        assertNotNull(updatedRecordFromDB, "更新后应能获取到记录");
        assertEquals(newStatus, updatedRecordFromDB.getAppointmentStatus());
        assertEquals(newPhoneNumber, updatedRecordFromDB.getPhoneNumber());
    }

    @Test
    @DisplayName("测试根据ID删除挂号记录 (Delete)")
    void testDeleteAppointmentById() {
        appointmentService.save(sampleRecord);
        assertNotNull(sampleRecord.getId(), "ID should be generated after save");
        Long recordId = sampleRecord.getId();

        boolean deleted = appointmentService.removeById(recordId); // IService 提供的 removeById 方法
        assertTrue(deleted, "记录应该成功删除");

        AppointmentRecord deletedRecordFromDB = appointmentService.getById(recordId);
        assertNull(deletedRecordFromDB, "删除后不应该能从数据库中查询到该记录");
    }

    @Test
    @DisplayName("测试 getOne 方法 - 记录存在")
    void testGetOne_RecordExists() {
        // 先确保数据库中有一条与sampleRecord完全一致的记录
        appointmentService.save(sampleRecord);
        assertNotNull(sampleRecord.getId(), "Test setup: Record should have an ID after saving.");

        // 使用与sampleRecord相同的条件进行查询
        AppointmentRecord queryCriteria = new AppointmentRecord();
        queryCriteria.setUsername(sampleRecord.getUsername());
        queryCriteria.setIdCard(sampleRecord.getIdCard());
        queryCriteria.setDepartmentName(sampleRecord.getDepartmentName());
        queryCriteria.setAppointmentDate(sampleRecord.getAppointmentDate());
        queryCriteria.setAppointmentTimeSlot(sampleRecord.getAppointmentTimeSlot());
        // 如果getOne方法还考虑了phoneNumber或doctorName等，也需要在此设置

        // 假设您的AppointmentService接口中定义了 getOne(AppointmentRecord record)
        // 并且其实现与之前讨论的 getOneByDetails 逻辑类似
        AppointmentRecord foundRecord = appointmentService.getOne(queryCriteria);

        assertNotNull(foundRecord, "应该能通过getOne方法找到匹配的记录");
        assertEquals(sampleRecord.getId(), foundRecord.getId(), "找到的记录ID应该与保存的记录ID一致");
        assertEquals(sampleRecord.getUsername(), foundRecord.getUsername());
    }

    @Test
    @DisplayName("测试 getOne 方法 - 记录不存在")
    void testGetOne_RecordNotExists() {
        AppointmentRecord queryCriteria = new AppointmentRecord();
        queryCriteria.setUsername("不存在的用户GetOne");
        queryCriteria.setIdCard("000000000000000000");
        queryCriteria.setDepartmentName("不存在的科室GetOne");
        queryCriteria.setAppointmentDate(LocalDate.of(2000, 1, 1));
        queryCriteria.setAppointmentTimeSlot("任意时间GetOne");

        AppointmentRecord foundRecord = appointmentService.getOne(queryCriteria);
        assertNull(foundRecord, "通过getOne方法查询不存在的记录时应返回null");
    }

    @Test
    @DisplayName("测试 getOne 方法 - 关键查询条件部分缺失")
    void testGetOne_MissingCrucialCriteria() {
        appointmentService.save(sampleRecord); // 保存一条记录以供对比

        AppointmentRecord queryCriteria = new AppointmentRecord();
        queryCriteria.setUsername(sampleRecord.getUsername());
        queryCriteria.setIdCard(sampleRecord.getIdCard());
        // 故意不设置 departmentName, appointmentDate, appointmentTimeSlot

        // 假设 getOne 方法在关键条件不足时返回 null
        AppointmentRecord foundRecord = appointmentService.getOne(queryCriteria);
        assertNull(foundRecord, "当getOne方法的关键查询条件不足时，应返回null");
    }
}