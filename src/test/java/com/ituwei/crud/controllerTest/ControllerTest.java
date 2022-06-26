package com.ituwei.crud.controllerTest;

import com.ituwei.crud.controller.TodoItemController;
import com.ituwei.crud.model.TodoItem;
import com.ituwei.crud.repo.TodoItemRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TodoItemController.class)
public class ControllerTest {

    static final int MOCK_ID = 1234;
    static final String MOCK_DESC = "Mock Item";
    static final Boolean MOCK_COMPLETED = false;
    final Map<Integer, TodoItem> repository = new HashMap<>();
    final TodoItem mockItemA = new TodoItem(MOCK_ID + 555, MOCK_DESC + "-A", true);
    final TodoItem mockItemB = new TodoItem(MOCK_ID + 444, MOCK_DESC + "-B", true);

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoItemRepo todoItemRepository;

    @Test
    public void shouldRenderDefaultTemplate() throws Exception {
        mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void canGetAllTodoItems() throws Exception {
        mockMvc.perform(get("/home")).andDo(print()).andExpect(status().isOk()).andExpect(content()
                .json(String.format("[{\"id\":\"%d\"}, {\"id\":\"%d\"}]", mockItemA.getId(), mockItemB.getId())));
    }

    @Test
    public void canSaveTodoItems() throws Exception {
        final int size = repository.size();
        final TodoItem mockItemC = new TodoItem(555, MOCK_DESC + "-C", true);
        mockMvc.perform(post("/todolist").contentType(MediaType.APPLICATION_JSON_VALUE).content(String
                        .format("{\"description\":\"%s\",\"complete\":\"%b\"}", mockItemC.getDescription(), mockItemC.getComplete())))
                .andDo(print()).andExpect(status().isCreated());
        assertEquals(size + 1, repository.size());
    }

    @Test
    public void canDeleteTodoItems() throws Exception {
        final int size = repository.size();
        mockMvc.perform(delete(String.format("/todolist/%d", mockItemA.getId()))).andDo(print())
                .andExpect(status().isOk());
        assertEquals(size - 1, repository.size());
        assertFalse(repository.containsKey(mockItemA.getId()));
    }

    @Test
    public void canUpdateTodoItems() throws Exception {
        final String newItemJsonString = String.format("{\"id\":\"%d\",\"description\":\"%s\",\"complete\":\"%b\"}",
                mockItemA.getId(), mockItemA.getDescription(), "");
        mockMvc.perform(put("/todolist/{id}").contentType(MediaType.APPLICATION_JSON_VALUE).content(newItemJsonString))
                .andDo(print()).andExpect(status().isOk());
        assertFalse((boolean) repository.get(mockItemA.getId()).getComplete());
    }

    @Test
    public void canNotDeleteNonExistingTodoItems() throws Exception {
        final int size = repository.size();
        mockMvc.perform(delete(String.format("/todolist/%d", "Non-Existing-ID"))).andDo(print())
                .andExpect(status().isNotFound());
        assertEquals(size, repository.size());
    }
}
