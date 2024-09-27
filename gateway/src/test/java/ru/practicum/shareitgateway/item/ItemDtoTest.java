package ru.practicum.shareitgateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareitgateway.ShareItGateway;
import ru.practicum.shareitgateway.item.dto.ItemDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDtoTest {

    final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("ItemDto_serializeJson")
    void serializeJsonTest() throws Exception {

        final ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1);

        String json = objectMapper.writeValueAsString(itemDto);
        assertThat(json).contains("\"name\":\"name\"");
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("ItemDto_deserializeJson")
    void deserializeJsonTest() throws Exception {

        String json = "{\"id\":1,\"name\":\"Table\",\"description\":\"wood\",\"available\":true,\"requestId\":1}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);
        assertThat(itemDto.getName()).isEqualTo("Table");
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("ItemDto_validation")
    void validationTest() {

        final ItemDto itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("good name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(1);

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertThat(violations).isEmpty();
    }
}
