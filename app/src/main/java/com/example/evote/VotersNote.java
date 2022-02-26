package com.example.evote;

import android.widget.EditText;

public class VotersNote {

    String voterName;
    String votersCompanyId;
    String votersCompanyName;
    String voterSecretCode;
    String voterEmployeeId;
    String voted;

    public VotersNote() {
    }

    public VotersNote(String voterName, String votersCompanyId, String votersCompanyName, String voterSecretCode, String voterEmployeeId, String voted) {
        this.voterName = voterName;
        this.votersCompanyId = votersCompanyId;
        this.votersCompanyName = votersCompanyName;
        this.voterSecretCode = voterSecretCode;
        this.voterEmployeeId = voterEmployeeId;
        this.voted=voted;
    }

    public String getVoted() {
        return voted;
    }

    public void setVoted(String voted) {
        this.voted = voted;
    }

    public String getVoterName() {
        return voterName;
    }

    public void setVoterName(String voterName) {
        this.voterName = voterName;
    }

    public String getVotersCompanyId() {
        return votersCompanyId;
    }

    public void setVotersCompanyId(String votersCompanyId) {
        this.votersCompanyId = votersCompanyId;
    }

    public String getVotersCompanyName() {
        return votersCompanyName;
    }

    public void setVotersCompanyName(String votersCompanyName) {
        this.votersCompanyName = votersCompanyName;
    }

    public String getVoterSecretCode() {
        return voterSecretCode;
    }

    public void setVoterSecretCode(String voterSecretCode) {
        this.voterSecretCode = voterSecretCode;
    }

    public String getVoterEmployeeId() {
        return voterEmployeeId;
    }

    public void setVoterEmployeeId(String voterEmployeeId) {
        this.voterEmployeeId = voterEmployeeId;
    }
}
