package com.github.lupaev.schoolgroups.views;

import com.github.lupaev.schoolgroups.dto.GroupDto;
import com.github.lupaev.schoolgroups.service.GroupService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
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

    public GroupView(GroupService groupService) {
        this.groupService = groupService;

        configureGrid();
        configureForm();

        add(groupGrid, groupNameField, addGroupButton, configurePaginationLayout());
        updateGroupList();
    }

    private void configureGrid() {
        groupGrid.removeAllColumns(); // Удаляем все стандартные колонки

        // Добавляем необходимые колонки
        groupGrid.addColumn(GroupDto::getGroupNumber).setHeader("Номер");
        groupGrid.addColumn(GroupDto::getStudentCount).setHeader("Количество студентов");

        // Добавляем колонку для действий
        groupGrid.addComponentColumn(groupDto -> new Button("Редактировать", click -> {
            VaadinSession.getCurrent().setAttribute("selectedGroup", groupDto);
            getUI().ifPresent(ui -> ui.navigate(StudentView.class, new RouteParameters("groupId", String.valueOf(groupDto.getId()))));
        })).setHeader("Действия");

        groupGrid.addItemDoubleClickListener(event -> {
            VaadinSession.getCurrent().setAttribute("selectedGroup", event.getItem());
            getUI().ifPresent(ui -> ui.navigate(StudentView.class, new RouteParameters("groupId", String.valueOf(event.getItem().getId()))));
        });
    }

    private void configureForm() {
        addGroupButton.addClickListener(e -> {
            GroupDto groupDto = new GroupDto();
            groupDto.setGroupNumber(groupNameField.getValue());
            groupDto.setCreatedAt(LocalDateTime.now());
            GroupDto savedGroup = groupService.saveGroup(groupDto);
            groupGrid.getDataProvider().refreshAll();
            updatePaginationButtons();
            groupNameField.clear();
            // Проверяем, что savedGroup имеет корректный ID
            if (savedGroup != null && savedGroup.getId() != null) {
                getUI().ifPresent(ui -> ui.navigate(StudentView.class, new RouteParameters("groupId", String.valueOf(savedGroup.getId()))));
                VaadinSession.getCurrent().setAttribute("selectedGroup", savedGroup);
            } else {
                Notification.show("Ошибка: группа не была сохранена корректно.");
            }
        });
    }

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

    private void updatePaginationButtons() {
        // Отключаем кнопку "Предыдущая", если находимся на первой странице
        previousButton.setEnabled(currentPage > 0);
        // Отключаем кнопку "Следующая", если текущая страница заполнена не полностью
        nextButton.setEnabled(groups.size() == pageSize);
    }

    private void updateGroupList() {
        // Количество элементов на странице
        groups = new ArrayList<>(groupService.getGroups(currentPage, pageSize));
        groupGrid.setItems(groups);
        updatePaginationButtons();
    }
}
