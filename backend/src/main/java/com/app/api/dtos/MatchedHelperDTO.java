package com.app.api.dtos;

/**
 * Data Transfer Object for representing a matched helper.
 */
public class MatchedHelperDTO {

    private int helperId;
    private String helperName;
    private String neighbourhoodZone;
    private boolean skillMatched;
    private int helperXp;
    private String invitationStatus;

    /**
     * Constructs a MatchedHelperDTO with the specified details.
     *
     * @param helperId the unique identifier of the helper
     * @param helperName the name of the helper
     * @param neighbourhoodZone the neighbourhood zone of the helper
     * @param skillMatched indicates if the helper's skills match the requirements
     * @param helperXp the experience points of the helper
     * @param invitationStatus the status of any invitation sent to the helper
     */
    public MatchedHelperDTO(int helperId, String helperName,
            String neighbourhoodZone, boolean skillMatched,
            int helperXp, String invitationStatus) {
        this.helperId = helperId;
        this.helperName = helperName;
        this.neighbourhoodZone = neighbourhoodZone;
        this.skillMatched = skillMatched;
        this.helperXp = helperXp;
        this.invitationStatus = invitationStatus;
    }

    /**
     * Returns the unique identifier of the helper.
     *
     * @return the helper identifier
     */
    public int getHelperId() {
        return helperId;
    }

    /**
     * Returns the name of the helper.
     *
     * @return the helper name
     */
    public String getHelperName() {
        return helperName;
    }

    /**
     * Returns the neighbourhood zone of the helper.
     *
     * @return the neighbourhood zone
     */
    public String getNeighbourhoodZone() {
        return neighbourhoodZone;
    }

    /**
     * Returns whether the helper's skills match the requirements.
     *
     * @return true if the skills match, false otherwise
     */
    public boolean isSkillMatched() {
        return skillMatched;
    }

    /**
     * Returns the experience points of the helper.
     *
     * @return the helper's experience points
     */
    public int getHelperXp() {
        return helperXp;
    }

    /**
     * Returns the status of any invitation sent to the helper.
     *
     * @return the invitation status
     */
    public String getInvitationStatus() {
        return invitationStatus;
    }
}
