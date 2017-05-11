package org.asl19.paskoocheh.pojo;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Action logging Pojo for Amazon Dynamo DB.
 */
@DynamoDBTable(tableName = "action_log")
public class ActionLog {
    private String username;
    private Number actionTime;
    private String actionName;
    private String source;

    @DynamoDBHashKey(attributeName = "user_name")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @DynamoDBRangeKey(attributeName = "action_time")
    public Number getActionTime() {
        return actionTime;
    }

    public void setActionTime(Number actionTime) {
        this.actionTime = actionTime;
    }

    @DynamoDBAttribute(attributeName = "action_name")
    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    @DynamoDBAttribute(attributeName = "source")
    public String getDownloadSource() {
        return source;
    }

    public void setDownloadSource(String source) {
        this.source = source;
    }
}
