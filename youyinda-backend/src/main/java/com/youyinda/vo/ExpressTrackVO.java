package com.youyinda.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class ExpressTrackVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String trackingNo;

    private String expressName;

    private String logisticsStatus;

    private List<TrackInfo> trackList;

    @Data
    public static class TrackInfo implements Serializable {
        private Date time;
        private String status;
        private String description;
        private String location;
    }
}
