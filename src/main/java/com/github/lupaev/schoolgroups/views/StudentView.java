package com.github.lupaev.schoolgroups.views;

import com.github.lupaev.schoolgroups.dto.StudentDto;
import com.github.lupaev.schoolgroups.service.StudentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.time.LocalDate;

@Route("students/:groupId")
@PageTitle("Students")
public class StudentView extends VerticalLayout implements BeforeEnterObserver {

    private final StudentService studentService;
    private final Grid<StudentDto> studentGrid = new Grid<>(StudentDto.class);
    private final TextField fullNameField = new TextField("ФИО");
    private final Button addStudentButton = new Button("Принять нового студента");
    private final Button backButton = new Button("Вернуться к списку групп");
    private Long groupId;

    public StudentView(StudentService studentService) {
        this.studentService = studentService;

        configureGrid();
        configureForm();

        add(studentGrid, fullNameField, addStudentButton, backButton);
    }

    private void configureGrid() {
        studentGrid.removeAllColumns();

        studentGrid.addColumn(StudentDto::getDateReceipt).setHeader("Дата зачисления");
        studentGrid.addColumn(StudentDto::getFullName).setHeader("ФИО студента");

        studentGrid.addComponentColumn(student -> {
            Button deleteButton = new Button("Delete");
            deleteButton.addClickListener(e -> {
                studentService.deleteStudent(student.getId());
                updateStudentList();
            });
            return deleteButton;
        });
    }

    private void configureForm() {
        addStudentButton.addClickListener(e -> {
            StudentDto studentDto = new StudentDto();
            studentDto.setFullName(fullNameField.getValue());
            studentDto.setDateReceipt(LocalDate.now());
            studentDto.setGroupId(groupId);
            studentService.saveStudent(studentDto);
            updateStudentList();
            fullNameField.clear();
        });

        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GroupView.class)));
    }

    private void updateStudentList() {
        studentGrid.setItems(studentService.getStudentsByGroup(groupId));
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        groupId = Long.valueOf(event.getRouteParameters().get("groupId").orElse("0"));
        updateStudentList();
    }

}
