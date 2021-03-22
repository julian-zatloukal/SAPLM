package com.example.SAPLM.commandActivity;

import com.example.SAPLM.R;
import com.example.SAPLM.bluetoothActivities.UnifiedTransmissionProtocol;

import java.util.List;

public class CommandItem {
    public enum ItemType { CHILD, PARENT}
    public enum BoardGroupState {FOLDED, UNFOLDED}

    private String itemName;
    public ItemType itemType = ItemType.CHILD;
    private int deviceStateIconID;
    private int deviceStateInteger = 0;
    private int moduleIndex = 0;
    private int deviceIndex = 0;
    private List<String> childInternalID;
    private BoardGroupState groupState = BoardGroupState.UNFOLDED;



    public CommandItem( int moduleIndex, int deviceIndex ){
        this.itemName = UnifiedTransmissionProtocol.modulesList.get(moduleIndex).getDevicesList().get(deviceIndex).getName();
        this.itemType = ItemType.CHILD;
        this.moduleIndex = moduleIndex;
        this.deviceIndex = deviceIndex;
        this.deviceStateIconID = UnifiedTransmissionProtocol.modulesList.get(moduleIndex).getDevicesList().get(deviceIndex).getIconResourceId();
    }

    public CommandItem(int boardInternalId){
        this.itemName = UnifiedTransmissionProtocol.modulesList.get(boardInternalId).getModuleName();
        this.itemType = ItemType.PARENT;
        this.deviceStateIconID = UnifiedTransmissionProtocol.modulesList.get(boardInternalId).getIconResourceId();
    }

    public void onClickHandler(){
        UnifiedTransmissionProtocol.modulesList.get(moduleIndex).getDevicesList().get(deviceIndex).deviceOnClickHandler();
    }


    //Getters

    public BoardGroupState getGroupState (){
        return groupState;
    }

    public int getDeviceStateIconID() {
        return deviceStateIconID;
    }

    public int getDeviceStateInteger() {
        return deviceStateInteger;
    }

    public int getDeviceIndex() {
        return deviceIndex;
    }

    public String getItemName() {
        return itemName;
    }

    public int getModuleIndex(){
        return moduleIndex;
    }

    public List<String> getChildInternalID() {
        return childInternalID;
    }

    public ItemType getItemType() {
        return itemType;
    }


    public String getDeviceStateSubtitle(){
        return UnifiedTransmissionProtocol.modulesList.get(moduleIndex).getDevicesList().get(deviceIndex).getDataAsString();
    }

    public int getProgressBarValue(){
        return UnifiedTransmissionProtocol.modulesList.get(moduleIndex).getDevicesList().get(deviceIndex).getProgressBarValue();
    }

    // Setters
    public void setChildInternalID(List<String> childInternalID) {
        this.childInternalID = childInternalID;
    }


    public void setDeviceStateIconID(int deviceStateIconID) {
        this.deviceStateIconID = deviceStateIconID;
    }

    public void setDeviceStateInteger(int deviceStateInteger) {
        this.deviceStateInteger = deviceStateInteger;
    }

    public void setModuleIndex(int moduleIndex) {
        this.moduleIndex = moduleIndex;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public void setDeviceIndex(int deviceIndex) {
        this.deviceIndex = deviceIndex;
    }

    public void setGroupState (BoardGroupState groupState){
        this.groupState = groupState;
    }

}
