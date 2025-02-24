package com.eureka.spartaonetoone.common.config;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.addSecurityItem(new SecurityRequirement().addList("JWT"))
			.components(new Components().addSecuritySchemes("JWT", createAPIKeyScheme()))
			.info(apiInfo());
	}

	private Info apiInfo() {
		return new Info()
			.title("Sparta OneToOne API")
			.description("Sparta OneToOne API")
			.version("1.0.0");
	}

	private SecurityScheme createAPIKeyScheme() {
		return new SecurityScheme().type(SecurityScheme.Type.HTTP)
			.bearerFormat("JWT")
			.scheme("bearer");
	}

	@Bean
	public OperationCustomizer operationCustomizer() {
		return (operation, handlerMethod) -> {
			this.addResponseBodyWrapperSchemaExample(operation);
			return operation;
		};
	}

	private void addResponseBodyWrapperSchemaExample(Operation operation) {
		final Content content = operation.getResponses().get("200").getContent();
		if (content != null) {
			content.forEach((mediaTypeKey, mediaType) -> {
				Schema<?> originalSchema = mediaType.getSchema();
				Schema<?> wrappedSchema = wrapSchema(originalSchema);
				mediaType.setSchema(wrappedSchema);
			});
		}
	}

	private Schema<?> wrapSchema(Schema<?> originalSchema) {
		final Schema<?> wrapperSchema = new Schema<>();

		wrapperSchema.addProperty("code", new Schema<>().type("string").example("S000"));
		if(originalSchema != null) {
			wrapperSchema.addProperty("data", originalSchema);
		} else {
			wrapperSchema.addProperty("data", new Schema<>().type("null"));
		}
		wrapperSchema.addProperty("message", new Schema<>().type("string").example("성공 메시지"));

		return wrapperSchema;
	}
}
