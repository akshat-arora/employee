package com.initializer.Employeedetails;
import org.aspectj.weaver.ast.And;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeDetailsApplicationTest {
    @Autowired
    MockMvc mvc;
    String str="/rest/employees/";
    @Test
    public void findallApi() throws Exception {
        mvc.perform(get(str+"get"))
        .andDo(print())
        .andExpect(jsonPath("$").isArray())
        .andExpect(status().isOk());
    }
    @Test
    public void getOneApi() throws Exception{
        mvc.perform(get(str+"get/{id}",2))
                .andDo(print())
                .andExpect(jsonPath("$").isMap())
                .andExpect(status().isOk());
    }
    @Test
    public void getOnebrApi() throws Exception{
        mvc.perform(get(str+"get/{id}",16))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void getOnenegApi() throws Exception{
        mvc.perform(get(str+"get/{id}",-1))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    public void getOnezeroApi() throws Exception{
        mvc.perform(get(str+"get/{id}",0))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    public void deleteDirApi() throws Exception{
        mvc.perform(delete(str+"delete/{id}",1))
                .andExpect(status().isBadRequest());
    }
    @Test
    public void deletezeroApi() throws Exception{
        mvc.perform(delete(str+"delete/{id}",0))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    public void deleteApi() throws Exception{
        mvc.perform(delete(str+"delete/{id}",2))
                .andExpect(status().isOk());
    }
    @Test
    public void deletenegApi() throws Exception{
        mvc.perform(delete(str+"delete/{id}",-1))
                .andExpect(status().isNotAcceptable());
    }
    @Test
    public void deleteStrApi() throws Exception{
        mvc.perform(delete(str+"delete/{id}","hello"))
                .andExpect(status().isBadRequest());
    }

}
