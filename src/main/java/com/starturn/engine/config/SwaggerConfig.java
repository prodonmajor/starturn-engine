/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.starturn.engine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import static springfox.documentation.builders.PathSelectors.regex;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 *
 * @author Administrator
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {
	@Bean
    public Docket credentialsApi() {
        /*return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.basePackage("com.easycoop.radicalengineaccount.controller"))
                .paths(PathSelectors.regex("/account/v1/api.*"))
                .build()
                .apiInfo(metaData());*/
        return new Docket(DocumentationType.SWAGGER_2)
                .forCodeGeneration(true)
                .select().apis(RequestHandlerSelectors.basePackage("com.starturn.engine.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
    }
	
    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("Starturn Project API's")
                .description("REST API Request Logics. "
                        + "Refer to use case documentation for more information")
                .termsOfServiceUrl("https://starturntermsofservice.com/")
                .contact(new Contact("Starturn Team", "https://speaknoevilseenoevil/", "Nelson@saveus.com"))
                .license("Proprietary source code")
                .licenseUrl("https://likemypagetogetmoretutorials")
                .build();
    }
}

