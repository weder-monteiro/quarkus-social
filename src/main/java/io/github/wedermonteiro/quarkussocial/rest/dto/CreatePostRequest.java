package io.github.wedermonteiro.quarkussocial.rest.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "Text is Required")
    private String text;

}
