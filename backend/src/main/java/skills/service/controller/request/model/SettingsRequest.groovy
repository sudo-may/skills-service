package skills.service.controller.request.model

class SettingsRequest {

    Integer id
    //nullable
    String settingGroup
    //nullable
    String projectId
    //non-null
    String setting
    //non-null
    String value

    //convenience method for settings that are in an either on or off state as opposed to containing a meaningful value
    boolean isEnabled(){
        return Boolean.valueOf(value) || value.toLowerCase() == "enabled" || value.toLowerCase() == "enable" || value.toLowerCase() == "on"
    }
}
