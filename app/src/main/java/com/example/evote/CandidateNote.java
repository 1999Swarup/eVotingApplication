package com.example.evote;

public class CandidateNote {
    String secretCode;
    String cName;
    String voteCount;
    String post;

    public CandidateNote() {
    }

    public CandidateNote(String secretCode, String cName, String voteCount,String post) {
        this.secretCode = secretCode;
        this.cName = cName;
        this.voteCount = voteCount;
        this.post = post;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getSecretCode() {
        return secretCode;
    }

    public void setSecretCode(String secretCode) {
        this.secretCode = secretCode;
    }

    public String getcName() {
        return cName;
    }

    public void setcName(String cName) {
        this.cName = cName;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }
}
