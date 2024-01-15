package com.api;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Contact;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.info.License;

import jakarta.ws.rs.core.Application;

@OpenAPIDefinition(
  info = @Info(
    title = "API Quarkus Social",
    version = "1.0.0",
    contact = @Contact(
      email = "thiagosf.dev@gmail.com",
      name = "Thiago Soares Figueiredo",
      url = "https://github.com/thiagosf-dev/api-quarkus-social"
    ),
    license = @License (
      name = "MIT"
    )
  )
)
public class QuarkusSocialApplication extends Application {

}
