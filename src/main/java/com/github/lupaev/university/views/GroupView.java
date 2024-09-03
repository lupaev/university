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
            VaadinSession.getCurrent().setAttribute("selectedGroupName", groupDto.getGroupNumber());
            getUI().ifPresent(ui -> ui.navigate(StudentView.class, new RouteParameters("groupId", String.valueOf(groupDto.getId()))));
        })).setHeader("Действия");

        // Добавляем обработчик двойного клика по строке
        groupGrid.addItemDoubleClickListener(event -> {
            // Сохраняем выбранную группу в сессии и переходим к просмотру студентов
            VaadinSession.getCurrent().setAttribute("selectedGroupName", event.getItem().getGroupNumber());
            getUI().ifPresent(ui -> ui.navigate(StudentView.class, new RouteParameters("groupId", String.valueOf(event.getItem().getId()))));
        });
    }

    /**
     * Настраивает форму для добавления новой группы.
     */
    private void configureForm() {
        // Кнопка добавления группы
        addGroupButton.addClickListener(e -> {
            try {
                GroupDto groupDto = new GroupDto();
                groupDto.setGroupNumber(groupNameField.getValue());
                groupDto.setCreatedAt(LocalDateTime.now());

                // Сохраняем новую группу
                GroupDto savedGroup = groupService.saveGroup(groupDto);

                // Проверяем, что savedGroup имеет корректный ID
                if (savedGroup != null && savedGroup.getId() != null) {
                    //Обновляем грид без обращения в БД
                    groupGrid.getDataProvider().refreshAll();
                    //Очищаем поле ввода
                    groupNameField.clear();
                    //Добавляем номер группы в сессию для перехода на другую страницу
                    VaadinSession.getCurrent().setAttribute("selectedGroupName", savedGroup.getGroupNumber());
                    getUI().ifPresent(ui -> ui.navigate(
                            StudentView.class,
                            new RouteParameters("groupId", String.valueOf(savedGroup.getId())))
                    );
                }
            } catch (IllegalArgumentException ex) {
                Notification.show(ex.getMessage(), 5000, Notification.Position.MIDDLE);
            } catch (Exception ex) {
                Notification.show("Произошла ошибка при сохранении группы.", 3000, Notification.Position.MIDDLE);
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
        try {
            // Получаем список групп с учетом текущей страницы и размера страницы. Загрузка осуществляется один раз при обновлении страницы
            groups = new ArrayList<>(groupService.getGroups(currentPage, pageSize));
            //Добавляем полученный список в грид
            groupGrid.setItems(groups);
            //Проверяем кнопки пагинации
            updatePaginationButtons();
        } catch (Exception ex) {
            Notification.show("Не удалось загрузить список групп.", 3000, Notification.Position.MIDDLE);
        }
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
