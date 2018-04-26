package com.vomozsystems.apps.android.vomoznet.service;

import com.vomozsystems.apps.android.vomoznet.entity.Event;

public class AttendEventRequest {

    private Event event;
    private Long texterCardId;
    private Boolean attended;

    /**
     * @return the event
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @param event the event to set
     */
    public void setEvent(Event event) {
        this.event = event;
    }

    /**
     * @return the texterCardId
     */
    public Long getTexterCardId() {
        return texterCardId;
    }

    /**
     * @param texterCardId the texterCardId to set
     */
    public void setTexterCardId(Long texterCardId) {
        this.texterCardId = texterCardId;
    }

    /**
     * @return the attended
     */
    public Boolean getAttended() {
        return attended;
    }

    /**
     * @param attended the attended to set
     */
    public void setAttended(Boolean attended) {
        this.attended = attended;
    }

}
