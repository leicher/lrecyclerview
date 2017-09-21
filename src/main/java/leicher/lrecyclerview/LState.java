package leicher.lrecyclerview;


interface LState {

    /*
        The state of normal
     */
    int IDLE = 0xfa;

    /*
        when pulling
     */
    int SLIDING = 0xfb;

    /*
        Release the finger to refresh
     */
    int RELEASE_TO_REFRESH = 0xfc;

    /*
        when refreshing
     */
    int REFRESHING = 0xfd;

    /*
        load or refresh fail
     */
    int FAIL = 0xff;

    void setCurrentState(int state);

    int getCurrentState();

}
