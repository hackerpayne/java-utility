package com.lingdonge.spring.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Api(tags = "用户API")
public class IndexController {

    @GetMapping("/")
    @ApiOperation(value = "用户新增")
    public String index() {

        return "test";
    }

    @GetMapping("/get/{id}")
    @ApiOperation(value = "用户查询(ID)")
    @ApiImplicitParam(name = "id", value = "查询ID", required = true)
    public String postUser(@PathVariable("id") String id) {

        return "Test" + id;
    }
}
