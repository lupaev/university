package com.github.lupaev.schoolgroups.views;

import com.github.lupaev.schoolgroups.dto.GroupDto;
import com.github.lupaev.schoolgroups.dto.StudentDto;
import com.github.lupaev.schoolgroups.service.StudentService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Route("students/:groupId")
@PageTitle("Список студентов группы")
public class StudentView extends VerticalLayout implements BeforeEnterObserver {

    private final StudentService studentService;
    private final Grid<StudentDto> studentGrid = new Grid<>(StudentDto.class);
    private final TextField fullNameField = new TextField("", "ФИО");
    private final Button addStudentButton = new Button("Принять нового студента");
    private final Button backButton = new Button("Вернуться к списку групп");
    private Long groupId;
    private GroupDto groupDto;
    private List<StudentDto> studentList = new ArrayList<>();

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
                studentList.remove(student); // Удаляем студента из локального списка
                studentGrid.getDataProvider().refreshAll(); // Обновляем грид
                Notification.show("Студент удален");
            });
            return deleteButton;
        }).setHeader("Действие");

        studentGrid.setItems(studentList);
    }

    private void configureForm() {
        //кнопка добавления нового студента
        addStudentButton.addClickListener(e -> {
            StudentDto studentDto = new StudentDto();
            studentDto.setFullName(fullNameField.getValue());
            studentDto.setDateReceipt(LocalDate.now().toString());
            studentDto.setGroupId(groupId);
            StudentDto dto = studentService.saveStudent(studentDto);
            studentList.add(dto);
            studentGrid.getDataProvider().refreshAll();
            Notification.show("Студент зачислен");
            fullNameField.clear();
        });

        //кнопка удаления студента
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GroupView.class)));
    }

    private void updateStudentList() {
        studentList = studentService.getStudentsByGroup(groupId);
        studentGrid.setItems(studentList);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        groupId = Long.valueOf(event.getRouteParameters().get("groupId").orElse("0"));
        // Извлекаем данные группы из сессии
        groupDto = (GroupDto) VaadinSession.getCurrent().getAttribute("selectedGroup");

        if (groupDto != null) {
            // Устанавливаем заголовок страницы
            UI.getCurrent().getPage().setTitle("Группа № " + groupDto.getGroupNumber());
            // Обновляем список студентов
            updateStudentList();
        } else {
            // Обработка случая, когда данные группы не найдены
            Notification.show("Ошибка: данные группы не найдены.");
        }
    }

}
