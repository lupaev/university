package com.github.lupaev.university.views;

import com.github.lupaev.university.dto.GroupDto;
import com.github.lupaev.university.service.GroupService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Представление для отображения и управления группами университета.
 */
@Route("")
@PageTitle("Группы университета")
public class GroupView extends VerticalLayout {

    private final GroupService groupService;
    private final Grid<GroupDto> groupGrid = new Grid<>(GroupDto.class);
    private final TextField groupNameField = new TextField("Номер группы");
    private final Button addGroupButton = new Button("Добавить новую группу");
    private List<GroupDto> groups = new ArrayList<>();
    private Button previousButton;
    private Button nextButton;

    // Параметры пагинации
    private int currentPage = 0;
    private final int pageSize = 10;

    /**
     * Конструктор, инициализирующий представление групп.
     *
     * @param groupService сервис для управления группами.
     */
    public GroupView(GroupService groupService) {
        this.groupService = groupService;

        configureGrid();
        configureForm();

        // Добавляем компоненты на страницу
        add(createTitle(), groupGrid, groupNameField, addGroupButton, configurePaginationLayout());
        updateGroupList();
    }

    /**
     * Настраивает таблицу для отображения групп.
     */
    private void configureGrid() {
        groupGrid.removeAllColumns(); // Удаляем все стандартные колонки

        // Добавляем необходимые колонки
        groupGrid.addColumn(GroupDto::getGroupNumber).setHeader("Номер");
        groupGrid.addColumn(GroupDto::getStudentCount).setHeader("Количество студентов");

        // Добавляем колонку для действий
        groupGrid.addComponentColumn(groupDto -> new Button("Редактировать", click -> {
            // Сохраняем выбранную группу в сессии и переходим к просмотру студентов
            VaadinSession.getCurrent().setAttribute("selectedGroup", groupDto);
            getUI().ifPresent(ui -> ui.navigate(StudentView.class, new RouteParameters("groupId", String.valueOf(groupDto.getId()))));
        })).setHeader("Действия");

        // Добавляем обработчик двойного клика по строке
        groupGrid.addItemDoubleClickListener(event -> {
            VaadinSession.getCurrent().setAttribute("selectedGroup", event.getItem());
            getUI().ifPresent(ui -> ui.navigate(StudentView.class, new RouteParameters("groupId", String.valueOf(event.getItem().getId()))));
        });
    }

    /**
     * Настраивает форму для добавления новой группы.
     */
    private void configureForm() {
        addGroupButton.addClickListener(e -> {
            GroupDto groupDto = new GroupDto();
            groupDto.setGroupNumber(groupNameField.getValue());
            groupDto.setCreatedAt(LocalDateTime.now());
            //Сохраняем новую группу
            GroupDto savedGroup = groupService.saveGroup(groupDto);
            //Обновляем состояние грида без запроса в БД
            groupGrid.getDataProvider().refreshAll();
            // обновляем состояние кнопок пагинации
            updatePaginationButtons();
            //очищаем поле ввода после добавления группы
            groupNameField.clear();

            // Проверяем, что savedGroup имеет корректный ID
            if (savedGroup != null && savedGroup.getId() != null) {
                // Сохраняем выбранную группу в сессии и переходим к просмотру студентов
                getUI().ifPresent(ui -> ui.navigate(StudentView.class, new RouteParameters("groupId", String.valueOf(savedGroup.getId()))));
                VaadinSession.getCurrent().setAttribute("selectedGroup", savedGroup);
            } else {
                Notification.show("Ошибка: группа не была сохранена корректно.");
            }
        });
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
                updateGroupList();
            }
        });

        nextButton = new Button("Следующая", e -> {
            currentPage++;
            updateGroupList();
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
        nextButton.setEnabled(groups.size() == pageSize);
    }

    /**
     * Обновляет список групп в таблице.
     */
    private void updateGroupList() {
        // Получаем список групп с учетом текущей страницы и размера страницы. Загрузка осуществляется один раз при обновлении страницы
        groups = new ArrayList<>(groupService.getGroups(currentPage, pageSize));
        //Добавляем полученный список в грид
        groupGrid.setItems(groups);
        //Проверяем кнопки пагинации
        updatePaginationButtons();
    }

    /**
     * Создает заголовок страницы.
     *
     * @return компонент заголовка.
     */
    private Component createTitle() {
        return new H1("Группы университета");
    }
}
