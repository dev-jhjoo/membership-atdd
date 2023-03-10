package com.jhjoo.atdd.membership.controller;

import com.google.gson.Gson;
import com.jhjoo.atdd.membership.dto.MembershipDetailResponse;
import com.jhjoo.atdd.membership.dto.MembershipResponse;
import com.jhjoo.atdd.membership.exception.GlobalExceptionHandler;
import com.jhjoo.atdd.membership.exception.MembershipErrorResult;
import com.jhjoo.atdd.membership.exception.MembershipException;
import com.jhjoo.atdd.membership.service.MembershipService;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import com.jhjoo.atdd.membership.enums.MembershipType;
import com.jhjoo.atdd.membership.dto.MembershipRequest;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.jhjoo.atdd.membership.enums.MembershipConstants.USER_ID_HEADER;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MembershipControllerTest {

    @InjectMocks
    private MembershipController target;
    @Mock
    private MembershipService membershipService;

    private MockMvc mockMvc;
    private Gson gson;


    @BeforeEach
    public void init(){
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(target)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    @Test
    public void 멤버십등록실패_사용자식별값이헤더에없음() throws Exception{
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_포인트Null() throws Exception{
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "12345")
                        .content(gson.toJson(membershipRequest(null, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_포인트가음수() throws Exception{
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "1234")
                        .content(gson.toJson(membershipRequest(-1, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_멤버십타입Null() throws Exception{
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "1234")
                        .content(gson.toJson(membershipRequest(10000, null)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_Service에서errorThrow() throws Exception{
        // given
        final String url = "/api/v1/memberships";
        doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
                .when(membershipService)
                .createMembership("312", MembershipType.NAVER, 10000);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"312")
                        .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록성공() throws Exception{
        // given
        final String url="/api/v1/memberships";
        final MembershipResponse membershipResponse = MembershipResponse.builder()
                .id(-1L)
                .membershipType(MembershipType.NAVER)
                .build();
        doReturn(membershipResponse).when(membershipService).createMembership("123", MembershipType.NAVER, 10000);


        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER, "123")
                        .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );


        // then
        resultActions.andExpect(status().isCreated());

        final MembershipResponse response = gson.fromJson(
                resultActions
                        .andReturn()
                        .getResponse()
                        .getContentAsString(StandardCharsets.UTF_8),
                MembershipResponse.class
        );

        System.out.println("response: " + response.toString());

        assertThat(response.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(response.getId()).isNotNull();
    }

    @Test
    public void 멤버십목록조회실패_사용자식별값이헤더에없음() throws Exception{
        // given
        final String url = "/api/v1/memberships";

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        // then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십목록조회성공() throws Exception{
        // given
        final String url = "/api/v1/memberships";
        doReturn(Arrays.asList(
                MembershipDetailResponse.builder().build(),
                MembershipDetailResponse.builder().build(),
                MembershipDetailResponse.builder().build()
        )).when(membershipService).membershipListByUserId("1234");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "1234")
        );

        // then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 멤버십삭제실패_사용자식별값이헤더에없음() throws Exception{
        // given
        final String url = "/api/v1/memverships/-1";

        // when
        final ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
        );

        // then
        actions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십삭제성공() throws Exception{
        // given
        final String url = "/api/v1/memverships/-1";

        // when
        final ResultActions actions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
                        .header(USER_ID_HEADER, "userId")
        );

        // then
        actions.andExpect(status().isNoContent());
    }


    private MembershipRequest membershipRequest(final Integer point, final MembershipType membershipType) {
        return MembershipRequest.builder().point(point).membershipType(membershipType).build();
    }


}