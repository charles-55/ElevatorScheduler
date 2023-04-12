public enum States {
    IDLE, SCHEDULING, HANDLING_RECEIVED_MESSAGE, SENDING_TASK, RECEIVING_TASK, SENDING_MESSAGE, RECEIVING_MESSAGE, WAITING_FOR_TASK, ADDING_TO_QUEUE, GOING_UP, GOING_DOWN, OUT_OF_SERVICE;

    /**
     * Provides a number which signifies different direction states
     * @return int
     */
    public static int getDatagramStateValue(States state) {
        if(state == States.IDLE)
            return 0;
        else if(state == States.GOING_UP)
            return 1;
        else if(state == States.GOING_DOWN)
            return 2;
        else if(state == States.RECEIVING_TASK)
            return 3;
        else if(state == States.OUT_OF_SERVICE)
            return 503;
        else
            return 404;
    }
}
