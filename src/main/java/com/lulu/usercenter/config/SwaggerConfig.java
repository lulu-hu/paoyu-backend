package com.lulu.usercenter.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

@Configuration
@EnableSwagger2WebMvc // Swagger的开关，表示已经启用Swagger
public class SwaggerConfig {

    @Bean(value = "api")
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
//                .host("localhost:8081")// 不配的话，默认当前项目端口
                .apiInfo(apiInfo())
//                .pathMapping("/")
                .select() // 选择哪些路径和api会生成document
//                .apis(RequestHandlerSelectors.any())// 对所有api进行监控
                .apis(RequestHandlerSelectors.basePackage("com.lulu.usercenter.controller"))// 选择监控的package
//                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))// 只监控有ApiOperation注解的接口
                //不显示错误的接口地址
//                .paths(Predicates.not(PathSelectors.regex("/error.*")))//错误路径不监控
//                .paths(PathSelectors.regex("/.*"))// 对根下所有路径进行监控
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api 信息
     * @return
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("lulu用户中心")
//                .contact(new Contact("Hanstrovsky", "www.hanstrovsky.com", "Hanstrovsky@gmail.com"))
                .description("lulu用户中心接口文档")
                .termsOfServiceUrl("https://github/lulu")
//                .license("The Apache License, Version 2.0")
//                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
//                .version("1.0")
                .build();
    }
}
