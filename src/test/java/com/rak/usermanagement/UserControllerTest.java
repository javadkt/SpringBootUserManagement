package com.rak.usermanagement;

import com.jayway.jsonpath.JsonPath;
import com.rak.usermanagement.common.controller.UserController;
import com.rak.usermanagement.common.model.User;
import com.rak.usermanagement.common.service.UserService;
import com.rak.usermanagement.common.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser_Success() throws Exception {
        User newUser = new User();
        newUser.setLoginId("newUser");
        newUser.setPassword("ValidPassword123");
        newUser.setEmail("newUser@example.com");

        when(userService.getUserByLogin(any())).thenReturn(null);
        when(userService.saveUser(any(User.class))).thenReturn(newUser);
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"loginId\":\"newUser\",\"password\":\"ValidPassword123\",\"email\":\"newUser@example.com\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loginId").value("newUser"))
                .andExpect(jsonPath("$.email").value("newUser@example.com"));
    }

    @Test
    public void testGetUserById_Success() throws Exception {

        String userJson = "{\"loginId\":\"testUser\",\"password\":\"ValidPassword123\",\"email\":\"testUser@example.com\"}";

        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer userIdInteger = JsonPath.parse(response).read("$.id");
        Long userId = userIdInteger.longValue();

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("testUser"))
                .andExpect(jsonPath("$.email").value("testUser@example.com"));
    }


    @Test
    public void testGetAllUsers_Success() throws Exception {

        String userJson1 = "{\"loginId\":\"user1\",\"password\":\"ValidPassword123\",\"email\":\"user1@example.com\"}";
        String userJson2 = "{\"loginId\":\"user2\",\"password\":\"ValidPassword456\",\"email\":\"user2@example.com\"}";

        String response1 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson1))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String response2 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson2))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();


        Integer userId1Integer = JsonPath.parse(response1).read("$.id");
        Integer userId2Integer = JsonPath.parse(response2).read("$.id");

        Long userId1 = userId1Integer.longValue();
        Long userId2 = userId2Integer.longValue();


        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loginId").value("user1"))
                .andExpect(jsonPath("$[1].loginId").value("user2"));


        mockMvc.perform(delete("/users/{id}", userId1))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/users/{id}", userId2))
                .andExpect(status().isNoContent());
    }


    @Test
    public void testUpdateUser_Success() throws Exception {

        String userJson = "{\"loginId\":\"oldUser\",\"password\":\"ValidPassword123\",\"email\":\"oldUser@example.com\"}";


        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());


        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].loginId").value("oldUser"))
                .andExpect(jsonPath("$[0].email").value("oldUser@example.com"))
                .andDo(result -> {

                    String content = result.getResponse().getContentAsString();
                    Integer userIdInteger = JsonPath.parse(content).read("$[0].id");
                    Long userId = userIdInteger.longValue();
                    String updatedUserJson = "{\"loginId\":\"updatedUser\",\"email\":\"updatedUser@example.com\"}";

                    mockMvc.perform(put("/users/{id}", userId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(updatedUserJson))
                            .andExpect(status().isOk())
                            .andExpect(jsonPath("$.loginId").value("updatedUser"))
                            .andExpect(jsonPath("$.email").value("updatedUser@example.com"));

                    mockMvc.perform(delete("/users/{id}", userId))
                            .andExpect(status().isNoContent());
                });
    }


    @Test
    public void testDeleteUser_Success() throws Exception {

        String userJson = "{\"loginId\":\"userToDelete\",\"password\":\"ValidPassword123\",\"email\":\"userToDelete@example.com\"}";
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer userIdInteger = JsonPath.parse(response).read("$.id");
        Long userId = userIdInteger.longValue();
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());
    }


    @Test
    public void testChangePassword_Success() throws Exception {

        String userJson = "{\"loginId\":\"testUser\",\"password\":\"oldPassword\",\"email\":\"testUser@example.com\"}";
        String response = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Integer userIdInt = JsonPath.parse(response).read("$.id");
        Long userId = userIdInt.longValue();

        String changePasswordJson = "{\"oldPassword\":\"oldPassword\",\"newPassword\":\"newPassword123\"}";

        mockMvc.perform(patch("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changePasswordJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Password changed successfully"));

        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

}
