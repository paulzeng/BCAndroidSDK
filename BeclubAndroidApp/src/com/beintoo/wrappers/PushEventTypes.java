package com.beintoo.wrappers;

public class PushEventTypes {
    public enum EventType {
        MISSION_COMPLETE("1");


        private final String value;
        private EventType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
