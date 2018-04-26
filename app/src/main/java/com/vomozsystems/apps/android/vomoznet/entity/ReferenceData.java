package com.vomozsystems.apps.android.vomoznet.entity;

import java.util.List;

/**
 * Created by leksrej on 7/13/16.
 */
public class ReferenceData {
    private List<Data> careerTypes;
    private List<ResetPasswordQuestion> group1Questions;
    private List<ResetPasswordQuestion> group2Questions;

    public List<Data> getCareerTypes() {
        return careerTypes;
    }

    public void setCareerTypes(List<Data> careerTypes) {
        this.careerTypes = careerTypes;
    }

    public List<ResetPasswordQuestion> getGroup1Questions() {
        return group1Questions;
    }

    public void setGroup1Questions(List<ResetPasswordQuestion> group1Questions) {
        this.group1Questions = group1Questions;
    }

    public List<ResetPasswordQuestion> getGroup2Questions() {
        return group2Questions;
    }

    public void setGroup2Questions(List<ResetPasswordQuestion> group2Questions) {
        this.group2Questions = group2Questions;
    }
}
