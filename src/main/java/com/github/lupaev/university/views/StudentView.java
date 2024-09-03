package com.github.lupaev.university.views;

import com.github.lupaev.university.dto.StudentDto;
import com.github.lupaev.university.service.StudentService;
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

import static com.vaadin.flow.component.notification.Notification.Position.MIDDLE;

/**
 * Представление для отображения и управления студентами в группе.
 */
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

    /**
     * Конструктор, инициализирующий представление студентов.
     *
     * @param studentService сервис для управления студентами.
     */
    public StudentView(StudentService studentService) {
        this.studentService = studentService;

        configureGrid();
        configureForm();

        // Добавляем компоненты на страницу
        add(studentGrid, configurePaginationLayout(), fullNameField, addStudentButton, backButton);
    }

    /**
     * Настраивает таблицу для отображения студентов.
     */
    private void configureGrid() {
        studentGrid.removeAllColumns(); // Удаляем все стандартные колонки

        // Добавляем колонки для отображения данных студентов
        studentGrid.addColumn(StudentDto::getAdmissionDate).setHeader("Дата зачисления");
        studentGrid.addColumn(StudentDto::getFullName).setHeader("ФИО студента");

        // Добавляем колонку для действий (удаление студента)
        studentGrid.addComponentColumn(student -> {
            Button deleteButton = new Button("Удалить");
            deleteButton.addClickListener(e -> {
                studentService.deleteStudent(student.getId());
                studentList.remove(student); // Удаляем студента из локального списка
                studentGrid.getDataProvider().refreshAll(); // Обновляем грид без запроса в БД
                Notification.show("Студент удален", 3000, MIDDLE);
            });
            return deleteButton;
        }).setHeader("Действие");

        studentGrid.setItems(studentList);
    }

    /**
     * Настраивает форму для добавления нового студента.
     */
    private void configureForm() {
        // Кнопка добавления нового студента
        addStudentButton.addClickListener(e -> {
            String value = fullNameField.getValue();
            try {
                if (value.isEmpty()) {
                    throw new IllegalArgumentException("ФИО не может быть пустым.");
                }

                StudentDto studentDto = new StudentDto();
                studentDto.setFullName(value);
                studentDto.setAdmissionDate(LocalDate.now().toString());
                studentDto.setGroupId(groupId);

                StudentDto dto = studentService.saveStudent(studentDto);
                studentList.add(dto);
                studentGrid.getDataProvider().refreshAll();
                updatePaginationButtons();
                Notification.show("Студент зачислен", 3000, MIDDLE);
                fullNameField.clear();
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage());
            } catch (Exception ex) {
                Notification.show("Произошла ошибка при добавлении студента.", 3000, MIDDLE);
            }
        });

        // Кнопка возврата к списку групп
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(GroupView.class)));
    }

    /**
     * Настраивает компоненты для управления пагинацией.
     *
     * @return компонент с кнопками пагинации.
     */
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

    /**
     * Обновляет состояние кнопок пагинации.
     */
    private void updatePaginationButtons() {
        // Отключаем кнопку "Предыдущая", если находимся на первой странице
        previousButton.setEnabled(currentPage > 0);
        // Отключаем кнопку "Следующая", если текущая страница заполнена не полностью
        nextButton.setEnabled(studentList.size() >= pageSize);
    }

    /**
     * Обновляет список студентов в таблице.
     */
    private void updateStudentList() {
        try {
            // Получаем список студентов с учетом текущей страницы и размера страницы. Загрузка осуществляется один раз при обновлении страницы
            studentList = new ArrayList<>(studentService.getStudentsByGroup(groupId, currentPage, pageSize));
            //Добавляем полученный список в грид
            studentGrid.setItems(studentList);
            //Проверяем кнопки пагинации
            updatePaginationButtons();
        } catch (Exception ex) {
            Notification.show("Не удалось загрузить список студентов.", 3000, MIDDLE);
        }
    }

    /**
     * Выполняется перед входом в представление.
     * Извлекает идентификатор группы из параметров маршрута и обновляет список студентов.
     *
     * @param event событие перед входом.
     */
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        try {
            //получаем groupId при переходе со страницы группы
            groupId = Long.valueOf(event.getRouteParameters().get("groupId").orElseThrow(() ->
                    new IllegalArgumentException("Идентификатор группы не указан.")));
            // Извлекаем данные группы из сессии
            String selectedGroupName = (String) VaadinSession.getCurrent().getAttribute("selectedGroupName");

            if (selectedGroupName != null && !selectedGroupName.isEmpty()) {
                // Устанавливаем заголовок страницы
                addComponentAsFirst(createTitle(selectedGroupName));
                // Обновляем список студентов
                updateStudentList();
            } else {
                throw new IllegalStateException("Данные группы не найдены.");
            }
        } catch (Exception ex) {
            Notification.show("Ошибка: " + ex.getMessage(), 3000, MIDDLE);
        }
    }

    /**
     * Создает заголовок страницы.
     *
     * @param groupNumber номер группы.
     * @return компонент заголовка.
     */
    private Component createTitle(String groupNumber) {
        return new H1("Группа № " + groupNumber);
    }
}
