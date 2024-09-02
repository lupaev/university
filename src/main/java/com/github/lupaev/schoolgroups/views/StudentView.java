package com.github.lupaev.schoolgroups.views;

import com.github.lupaev.schoolgroups.dto.GroupDto;
import com.github.lupaev.schoolgroups.dto.StudentDto;
import com.github.lupaev.schoolgroups.service.StudentService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
    private List<StudentDto> studentList = new ArrayList<>();
    private Button previousButton;
    private Button nextButton;
    // Параметры пагинации
    private int currentPage = 0;
    private final int pageSize = 20;

    public StudentView(StudentService studentService) {
        this.studentService = studentService;

        configureGrid();
        configureForm();

        add(studentGrid, configurePaginationLayout(), fullNameField, addStudentButton, backButton);
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
            updatePaginationButtons();
            Notification.show("Студент зачислен");
            fullNameField.clear();
        });

        //кнопка удаления студента
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GroupView.class)));
    }

    private Component configurePaginationLayout() {
        previousButton = new Button("Предыдущая", e -> {
            if (currentPage > 0) {
                currentPage--;
                updateStudentList();
            }
        });

        nextButton = new Button("Следующая", e -> {
            currentPage++;
            updateStudentList();
        });

        return new HorizontalLayout(previousButton, nextButton);
    }

    private void updatePaginationButtons() {
        // Отключаем кнопку "Предыдущая", если находимся на первой странице
        previousButton.setEnabled(currentPage > 0);
        // Отключаем кнопку "Следующая", если текущая страница заполнена не полностью
        nextButton.setEnabled(studentList.size() == pageSize);
    }

    private void updateStudentList() {
        // Количество элементов на странице
        studentList = new ArrayList<>(studentService.getStudentsByGroup(groupId, currentPage, pageSize));
        studentGrid.setItems(studentList);
        updatePaginationButtons();
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        groupId = Long.valueOf(event.getRouteParameters().get("groupId").orElse("0"));
        // Извлекаем данные группы из сессии
        GroupDto groupDto = (GroupDto) VaadinSession.getCurrent().getAttribute("selectedGroup");

        if (groupDto != null) {
            // Устанавливаем заголовок страницы
            addComponentAsFirst(createTitle(groupDto.getGroupNumber()));
            // Обновляем список студентов
            updateStudentList();
        } else {
            // Обработка случая, когда данные группы не найдены
            Notification.show("Ошибка: данные группы не найдены.");
        }
    }

    private Component createTitle(String groupNumber) {
        return new H1("Группа № " + groupNumber);
    }

}
