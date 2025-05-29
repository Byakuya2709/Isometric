package ctu.game.isometric.model.entity;

public class NPCs {
    int npcID;
    String npcName;
    String npcDescription;
    String arcId;
    String sceneId;


    public NPCs() {
    }


    public void interact() {
        // Logic for interaction with the NPC
    }

    public NPCs(int npcID, String npcName, String npcDescription, String arcId, String sceneId) {
        this.npcID = npcID;
        this.npcName = npcName;
        this.npcDescription = npcDescription;
        this.arcId = arcId;
        this.sceneId = sceneId;
    }

    public int getNpcID() {
        return npcID;
    }

    public void setNpcID(int npcID) {
        this.npcID = npcID;
    }

    public String getNpcName() {
        return npcName;
    }

    public void setNpcName(String npcName) {
        this.npcName = npcName;
    }

    public String getNpcDescription() {
        return npcDescription;
    }

    public void setNpcDescription(String npcDescription) {
        this.npcDescription = npcDescription;
    }

    public String getArcId() {
        return arcId;
    }

    public void setArcId(String arcId) {
        this.arcId = arcId;
    }

    public String getSceneId() {
        return sceneId;
    }

    public void setSceneId(String sceneId) {
        this.sceneId = sceneId;
    }
}
