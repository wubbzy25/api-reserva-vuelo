package com.api.reservavuelos.Services;

import com.api.reservavuelos.Utils.GenerateCodes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceTest {

    @Mock
    GenerateCodes generateCodes;
    @Mock
    RedisTemplate<String, Object> redisTemplate;
    @Mock
    ValueOperations<String, Object> valueOperations;
    @InjectMocks
    ResetPasswordService resetPasswordService;

    @Test
    void setResetCode() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(generateCodes.code()).thenReturn(123456);
        doNothing().when(valueOperations).set(
                eq("test.gmail.com"),
                eq("123456"),
                eq(15L),
                eq(TimeUnit.MINUTES));

        String resetCode = resetPasswordService.SetResetCode("test.gmail.com");
        assertEquals("123456", resetCode);
        verify(redisTemplate, times(1)).opsForValue();
        verify(generateCodes, times(1)).code();
        verify(valueOperations, times(1)).set(anyString(), anyString(), anyLong(), any());
    }

    @Test
    void getData() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("test.gmail.com")).thenReturn("123456");

        String resetCode = resetPasswordService.getData("test.gmail.com");
        assertEquals("123456", resetCode);
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(anyString());
    }

    @Test
    void deleteData() {
        when(redisTemplate.delete("test.gmail.com")).thenReturn(true);
        resetPasswordService.deleteData("test.gmail.com");
        verify(redisTemplate, times(1)).delete(anyString());
    }

    @Test
    void setVerifyStatus() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(
                eq("test.gmail.com"),
                eq("verified"),
                eq(15L),
                eq(TimeUnit.MINUTES));

        resetPasswordService.setVerifyStatus("test.gmail.com");
        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).set(anyString(), anyString(), anyLong(), any());
    }
}