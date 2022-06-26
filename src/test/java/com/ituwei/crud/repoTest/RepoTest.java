package com.ituwei.crud.repoTest;

import com.ituwei.crud.controller.TodoItemController;
import com.ituwei.crud.model.TodoItem;
import com.ituwei.crud.repo.TodoItemRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;


@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoItemRepo.class)
public class RepoTest {

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

    @BeforeEach
    public void setUp() {

        given(this.todoItemRepository.save(any(TodoItem.class))).willAnswer((InvocationOnMock invocation) -> {
            final TodoItem item = invocation.getArgument(0);
            if (repository.containsKey(item.getId())) {
                throw new Exception("Conflict.");
            }
            repository.put(item.getId(), item);
            return item;
        });

        given(this.todoItemRepository.findById(any(Integer.class))).willAnswer((InvocationOnMock invocation) -> {
            final String id = invocation.getArgument(0);
            return Optional.of(repository.get(id));
        });

        given(this.todoItemRepository.findAll()).willAnswer((InvocationOnMock invocation) -> {
            return new ArrayList<TodoItem>(repository.values());
        });

        willAnswer((InvocationOnMock invocation) -> {
            final String id = invocation.getArgument(0);
            if (!repository.containsKey(id)) {
                throw new Exception("Not Found.");
            }
            repository.remove(id);
            return null;
        }).given(this.todoItemRepository).deleteById(any(Integer.class));
    }
    @AfterEach
    public void tearDown() {
        repository.clear();
    }
}
