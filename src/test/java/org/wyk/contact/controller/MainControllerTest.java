package org.wyk.contact.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.wyk.contact.Application;
import org.wyk.contact.dto.CompanyDto;
import org.wyk.contact.dto.CustomerDto;
import org.wyk.contact.dto.PersonDto;
import org.wyk.contact.repository.Company;
import org.wyk.contact.repository.CompanyRepository;
import org.wyk.contact.repository.Supplier;
import org.wyk.contact.repository.SupplierRepository;

import java.io.UnsupportedEncodingException;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class MainControllerTest {

    protected MockMvc mvc;

    ObjectMapper mapper = new ObjectMapper();

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    public  void setUp(){
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @DisplayName("Create a company as a supplier. New Supplier")
    @Test
    public void createCompSupplier() throws Exception {

        String uri = "/company";
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\n" +
                        "  \"name\": \"Evo\",\n" +
                        "  \"registrationNumber\": \"142875x\",\n" +
                        "  \"supplier\": {\n" +
                        "    \"taxNumber\": 987765,\n" +
                        "    \"orderLastTimeDays\": 4\n" +
                        "  }\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.supplier.id").exists())
                .andReturn();


    }

    @DisplayName("Create a company as a customer. Circular fails")
    @Test
    public void createCompCustCircular() throws Exception {

        //Create a company X, is a supplier
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post("/company")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\n" +
                        "  \"name\": \"X\",\n" +
                        "  \"registrationNumber\": \"xxxxxx1\",\n" +
                        "  \"supplier\": {\n" +
                        "    \"taxNumber\": 100000,\n" +
                        "    \"orderLastTimeDays\": 1\n" +
                        "  }\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.supplier.id").exists())
                .andReturn();

       CompanyDto comp = mapper.readValue(mvcResult.getResponse().getContentAsString(), CompanyDto.class);

        //Create customer Y as company X
        mvcResult = mvc.perform(MockMvcRequestBuilders
                .post("/customer")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{ \n" +
                        "  \"customerNumber\":\"Y\",\n" +
                        "  \"lastOrderDate\": \"2020-02-14\",\n" +
                        "  \"companyId\":\"" + comp.getId() + "\"" +
                        "}")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        CustomerDto cust = mapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerDto.class);

        //Create company Z as customer Y
        String uri = "/company?customerId=" + cust.getId();
        mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\n" +
                        "  \"name\": \"Ecor\",\n" +
                        "  \"registrationNumber\": \"87859po\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Customer supplied is also a company"))
                .andReturn();

    }

    @DisplayName("Create a company as a customer. Existing")
    @Test
    public void createCompCust() throws Exception {

        //Create a person X, is a supplier
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders
                .post("/person")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\n" +
                        "  \"name\": \"X\",\n" +
                        "  \"surname\": \"men\",\n" +
                        "  \"supplier\": {\n" +
                        "    \"taxNumber\": 100000,\n" +
                        "    \"orderLastTimeDays\": 1\n" +
                        "  }\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.supplier.id").exists())
                .andReturn();

        PersonDto pers = mapper.readValue(mvcResult.getResponse().getContentAsString(), PersonDto.class);

        //Create customer Y as person X
        mvcResult = mvc.perform(MockMvcRequestBuilders
                .post("/customer")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{ \n" +
                        "  \"customerNumber\":\"Y\",\n" +
                        "  \"lastOrderDate\": \"2020-02-15\",\n" +
                        "  \"personId\":\"" + pers.getId() + "\"" +
                        "}")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        CustomerDto cust = mapper.readValue(mvcResult.getResponse().getContentAsString(), CustomerDto.class);

        //Create company Z as customer Y
        String uri = "/company?customerId=" + cust.getId();
        mvcResult = mvc.perform(MockMvcRequestBuilders
                .post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("{\n" +
                        "  \"name\": \"Ecor\",\n" +
                        "  \"registrationNumber\": \"87859po\",\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON_VALUE))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

    }


}
