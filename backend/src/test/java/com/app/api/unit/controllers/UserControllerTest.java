// package com.app.api.unit.controllers;

// import com.app.api.controllers.UserController;
// import com.app.api.models.User;
// import com.app.api.repositories.UserRepository;
// import com.app.api.security.FirebaseAuthenticationFilter;
// import com.app.api.services.FirebaseAuthService;
// import com.app.api.services.UserService;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
// import org.springframework.boot.test.mock.mockito.MockBean;
// import org.springframework.security.test.context.support.WithMockUser;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.ResultActions;
// import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
// import static org.mockito.Mockito.when;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

// @WebMvcTest(UserController.class)
// @AutoConfigureMockMvc(addFilters = false)
// class UserControllerTest 
// {

//     @Autowired
//     private MockMvc mockMvc;

//     @MockBean
//     private UserService userService;


//     // @Test
//     // @WithMockUser
//     // void getUser_success() throws Exception
//     // {
//     //     User user = new User();
//     //     user.setUserid(101);
//     //     user.setFirstName("John");
//     //     when(userService.getUserById(101)).thenReturn(user);

//     //     ResultActions result = mockMvc.perform(get("/api/users/101"));

//     //     result.andExpect(status().isOk()).andExpect(jsonPath("$.userid").value(101));
//     // }

// @Test
// void getUser_success() throws Exception {
//     User user = new User();
//     user.setUserid(101);
//     user.setFirstName("John");

//     when(userService.getUserById(101)).thenReturn(user);

//     mockMvc.perform(get("/api/users/101"))
//             .andDo(print())
//             .andExpect(status().isOk());
// }

//     @Test
//     void getUser_fail() throws Exception {
//         when(userService.getUserById(99)).thenReturn(null);

//         ResultActions result = mockMvc.perform(get("/api/users/99"));

//         result.andExpect(status().isNotFound());
//     }
// }