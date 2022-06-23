package com.ituwei.crud;

import com.ituwei.crud.controller.TodoItemController;
import com.ituwei.crud.model.TodoItem;
import com.ituwei.crud.repo.TodoItemRepo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = TodoItemController.class)
 class CrudApplicationTests {
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
		repository.clear();
		repository.put(mockItemA.getId(), mockItemA);
		repository.put(mockItemB.getId(), mockItemB);

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


	@Test
	public void idempotenceOfPut() throws Exception {
		final String newItemJsonString = String.format("{\"id\":\"%d\",\"description\":\"%s\",\"complete\":\"%b\"}",
				mockItemA.getId(), mockItemA.getDescription(), "complete");
		mockMvc.perform(put("/todolist").contentType(MediaType.APPLICATION_JSON_VALUE).content(newItemJsonString))
				.andDo(print()).andExpect(status().isOk());
		final TodoItem firstRes = repository.get(mockItemA.getId());
		mockMvc.perform(put("/todolist").contentType(MediaType.APPLICATION_JSON_VALUE).content(newItemJsonString))
				.andDo(print()).andExpect(status().isOk());
		final TodoItem secondRes = repository.get(mockItemA.getId());
		assertEquals(firstRes, secondRes);
	}
}
