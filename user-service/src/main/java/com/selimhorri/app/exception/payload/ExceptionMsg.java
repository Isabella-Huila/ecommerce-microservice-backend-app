package com.selimhorri.app.exception.payload;

import java.io.Serializable;
import java.time.ZonedDateTime;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.selimhorri.app.constant.AppConstant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionMsg implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = AppConstant.ZONED_DATE_TIME_FORMAT)
    private ZonedDateTime timestamp;

    private int status;
    private String error;
    private String msg;

    @JsonInclude(Include.NON_NULL)
    private String detalleError;
}









