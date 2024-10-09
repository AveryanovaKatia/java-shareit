package ru.practicum.shareitgateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareitgateway.item.dto.CommentDto;
import ru.practicum.shareitgateway.request.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentDtoTest {

    ObjectMapper objectMapper;

    Validator validator;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("CommentDtoTest_serializeJson")
    public void serializeJsonTest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Bla-bla-bla");

        String jsonContent = objectMapper.writeValueAsString(commentDto);

        assertThat(jsonContent).contains("\"text\":\"Bla-bla-bla\"");
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("CommentDtoTest_validation")
    public void validationTest() {
        ItemRequestDto validRequest = new ItemRequestDto();
        validRequest.setDescription("Blaaaaa");

        ItemRequestDto invalidRequest = new ItemRequestDto();
        invalidRequest.setDescription("");

        var validConstraints = validator.validate(validRequest);
        var invalidConstraints = validator.validate(invalidRequest);

        assertThat(validConstraints).isEmpty();
        assertThat(invalidConstraints).isNotEmpty();
    }
}
