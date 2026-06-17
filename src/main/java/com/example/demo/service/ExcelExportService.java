package com.example.demo.service;


import com.example.demo.entity.Role;
import com.example.demo.entity.RoleGroup;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExcelExportService {

    @Autowired
    private UserRepository userRepository;

    public byte[] exportUsers() {

        try (
                Workbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream out = new ByteArrayOutputStream()// Instead of writing to a file, it writes into memory.
        ) {

            Sheet sheet = workbook.createSheet("Users");

            Row headerRow = sheet.createRow(0);

            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Username");
            headerRow.createCell(2).setCellValue("Email");
            headerRow.createCell(3).setCellValue("Active");
            headerRow.createCell(4).setCellValue("Roles");
            headerRow.createCell(5).setCellValue("Role Groups");

            List<User> users = userRepository.findAll();

            int rowNum = 1;

            for (User user : users) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(user.getId());

                row.createCell(1).setCellValue(user.getUsername());

                row.createCell(2).setCellValue(user.getEmail());

                row.createCell(3).setCellValue(user.isActive());

                String roles =
                        user.getRoles()
                                .stream()
                                .map(Role::getName)
                                .collect(Collectors.joining(", "));

                row.createCell(4).setCellValue(roles);

                String groups =
                        user.getRoleGroups()
                                .stream()
                                .map(RoleGroup::getName)
                                .collect(Collectors.joining(", "));

                row.createCell(5).setCellValue(groups);
            }

            for (int i = 0; i < 6; i++) {
                sheet.autoSizeColumn(i);// is an Apache POI feature that automatically adjusts the width of Excel columns.
            }

            workbook.write(out);

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Error exporting Excel",
                    e
            );
        }
    }
}