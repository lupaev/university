package com.github.lupaev.schoolgroups.views;

import com.github.lupaev.schoolgroups.dto.GroupDto;
import com.github.lupaev.schoolgroups.service.GroupService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.VaadinSession;

import java.time.LocalDateTime;

@Route("")
@PageTitle("Группы университета")
public class GroupView extends VerticalLayout {

    private final GroupService groupService;
    private final Grid<GroupDto> groupGrid = new Grid<>(GroupDto.class);
    private final TextField groupNameField = new TextField("Номер группы");
    private final Button addGroupButton = new Button("Добавить новую группу");

    public GroupView(GroupService groupService) {
        this.groupService = groupService;

        configureGrid();
        configureForm();

        add(groupGrid, groupNameField, addGroupButton);
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
            updateGroupList();
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

    private void updateGroupList() {
        groupGrid.setItems(groupService.getAllGroups());
    }
}
