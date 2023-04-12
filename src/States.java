public enum States {
    DOOR_CLOSED, DOOR_OPEN, IDLE, SCHEDULING, HANDLING_RECEIVED_MESSAGE, SENDING_TASK, RECEIVING_TASK, SENDING_MESSAGE, RECEIVING_MESSAGE, WAITING_FOR_TASK, ADDING_TO_QUEUE, GOING_UP, GOING_DOWN, OUT_OF_SERVICE, UNKNOWN;

    /**
     * Provides a number which signifies a state.
     * @param state
     * @return int
     */
    public static int getStateDatagramValue(States state) {
        if(state == States.IDLE)
            return 0;
        else if(state == States.GOING_UP)
            return 1;
        else if(state == States.GOING_DOWN)
            return 2;
        else if(state == States.DOOR_OPEN)
            return 3;
        else if(state == States.DOOR_CLOSED)
            return 4;
        else if(state == States.OUT_OF_SERVICE)
            return 103;
        else
            return 404;
    }

    /**
     * Provides a state which signifies a datagram state value.
     * @param datagramValue
     * @return States
     */
    public static States getDatagramValueState(int datagramValue) {
        if(datagramValue == 0)
            return IDLE;
        else if(datagramValue == 1)
            return GOING_UP;
        else if(datagramValue == 3)
            return DOOR_OPEN;
        else if(datagramValue == 4)
            return DOOR_CLOSED;
        else if(datagramValue == 103)
            return OUT_OF_SERVICE;
        else
            return UNKNOWN;
    }
}
