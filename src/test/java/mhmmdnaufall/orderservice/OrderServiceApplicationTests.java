package mhmmdnaufall.orderservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import mhmmdnaufall.orderservice.dto.OrderLineItemDto;
import mhmmdnaufall.orderservice.dto.OrderRequest;
import mhmmdnaufall.orderservice.entity.Order;
import mhmmdnaufall.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class OrderServiceApplicationTests {

	private static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@DynamicPropertySource
	private static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.driver-class-name", MY_SQL_CONTAINER::getDriverClassName);
		registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);

	}

	@BeforeAll
	static void beforeAll() {
		MY_SQL_CONTAINER.start();
	}

	@AfterAll
	static void afterAll() {
		MY_SQL_CONTAINER.close();
	}

	@AfterEach
	void tearDown() {
		orderRepository.deleteAll();
	}

	@Transactional
	@Test
	void placeOrder() throws Exception {
		final var uuidString = UUID.randomUUID().toString();
		final var uuid = UUID.fromString(uuidString);
		try (final var uuidMock = mockStatic(UUID.class)) {

			uuidMock.when(UUID::randomUUID).thenReturn(uuid);

			final var order = new Order();
			final var request = new OrderRequest(
					List.of(
						new OrderLineItemDto(null, "iphone_13", new BigDecimal("1200"), 1, order),
						new OrderLineItemDto(null, "vivo_v15_pro", new BigDecimal("2000"), 2, order)
					)
			);

			mockMvc
					.perform(
							post("/api/orders")
									.contentType(MediaType.APPLICATION_JSON)
									.content(objectMapper.writeValueAsString(request))
					)
					.andExpect(status().isCreated())
					.andDo(result ->
							assertEquals("Order Placed Successfully", result.getResponse().getContentAsString())
					);

			orderRepository.findAll().forEach(o ->
				assertAll(
						() -> assertEquals(uuidString, o.getOrderNumber()),
						() -> assertTrue(
								o.getOrderLineItemList().stream()
										.anyMatch(orderLineItem -> orderLineItem.getSkuCode().equals("iphone_13"))
						),
						() -> assertTrue(
								o.getOrderLineItemList().stream()
										.anyMatch(orderLineItem -> orderLineItem.getSkuCode().equals("vivo_v15_pro"))
						)
				)
			);
		}
	}

}
