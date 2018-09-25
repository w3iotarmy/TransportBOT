package org.droidplanner.android.constant.code;

/**
 * Created by Borhan Uddin on 3/29/2018.
 */

public class ConstantCode {

    //public static String USER="ground";
    public static String USER = "g";
    //public static String USER_EAGLE_EYE="eagle";
    public static String USER_EAGLE_EYE = "e";
    public static String USERNAME_KEY = "u";
    public static String ENABLE_DISABLE_KEY = "enable_disable";
    public static String CONTROL_TYPE = "control_type";
    public static String LAT = "136";  //"lat";
    public static String LNG = "137"; //"lon";
    public static String ALT = "138"; //"alt";
    public static String BATTERY_VOLTAGE = "139"; //"voltage";
    public static String BATTERY_CURRENT = "140"; //"current";
    public static String BATTERY_LEVEL = "141"; //"level";
    public static String AIR_SPEED = "160"; //"air";
    public static String GROUND_SPEED = "159"; //"ground";

    //GPS Info
    public static String GPS_FIXED = "143"; //"fix";
    public static String SAT_NUM = "144"; //"sat_num";

    //Ultrasonic type
    public static int CONTROL_TYPE_RADAR = 1;
    public static int SENSOR_ENABLE = 1;
    public static int SENSOR_DISABLE = 2;
    // Default PWM Type
    public static int CONTROL_TYPE_ENABLE_PWM = 2;
    public static int CONTROL_TYPE_ENABLE_ALT = 3;
    public static int CONTROL_TYPE_ENABLE_LOITER = 4;

    // Default MENUAL CONTROL
    public static int CONTROL_TYPE_MUNUAL = 3;


    public static String VARIABLE_ARRAY_LEFT_OR_RIGHT = "variable_array_left_or_right";
    public static String VARIABLE_ARRAY_PWM = "variable_array_pwm";
    public static String VARIABLE_ARRAY_TIME = "variable_array";
    public static String VARIABLE_ARRAY_KEY = "variable_array";
    public static String VARIABLE_LAT = "lat";
    public static String VARIABLE_LNG = "lng";
    public static String SOCKET_URL = "http://184.72.95.87:3000/";


    // pwm signal enabled
    public static int DRONE_LEFT_RIGHT_CONTROL = 2001;
    public static int PWM_ENABLED = 1001;
    // pwm signal enabled
    public static int DRONE_LAT_LNG_RESPONSE = 4002;
    public static int DRONE_LAT_LNG_REQUEST = 4001;
    public static int CONTROL_TYPE_OBSTACLE_DETECT = 5001;

    public static String WAYPOINT_DIRECTORY = "waypoint";
    public static String WAYPOINT_DIRECTORY_NAMEE = "Eagle";


    ///######### Ground code start
    public static String ACTION_TYPE = "135";  //"action";
    public static String ACTION_BATTERY = "111"; //   "battery";
    public static String START_OBJ = "156"; // "obj_started";
    public static String STOP_OBJ = "157"; // "obj_stop";
    public static String ACTION_DRONE_MOVEMENT = "drone_movement";
    public static String FACE_CAPTURE_RES = "131"; // "face_capture_res";
    public static String MOVEMENT_DIRECTION = "move";
    ////##### Location
    public static String ACTION_LOCATION = "147"; //"location";
    public static String ACTION_ABC = "ABC";
    public static String ACTION_WP_SCHEDULE = "116"; //"wp_schedule";
    public static String ACTION_GYROSCOPE = "155"; //"gyroscope";
    public static String ACTION_RES_SCHEDULE = "152"; //"res_schedule";
    public static String ACTION_DEL_WP_ID_RES = "153"; //"dl_wp_by_id_res";
    public static String ACTION_DEL_SCH_ID_RES = "dl_schedule_by_id_res";
    ////##### GPS_Info
    public static String ACTION_GPSINFO = "149"; //"gps_info";
    ////###$Guided Mode
    public static String ACTION_MODE_GUIDED = "GUIDED";
    public static String ACTION_ARMED = "104"; //"arm";
    public static String ACTION_DISARMED = "105"; //"disarm";
    public static String ACTION_TAKEOFF = "107"; //"takeoff";
    public static String ACTION_TIMEROFF = "timer_off";
    public static String DATA_STRING = "142"; //"data";
    public static String MODE_CHANGE = "106"; //"mode";
    public static String WAY_RES = "150";   //"wp_res";
    public static String HOME_LOCATION = "101";  //"home_location";
    public static String MANUAL_FLY = "113"; //"manual_fly";
    public static String IS_ARMED = "104";  //"arm";
    public static String NO_WAYPOINT = "163";  //"no_waypoint";
    public static String ACTION_ALL_SCHEDULE_RES = "122"; //"all_schedule_res";
    public static String ARRAY_SCH = "146"; //"array";
    public static String ARRAY_SCH_2 = "146"; // "array";
    public static String S_NAME = "sname";
    public static String S_NAME_2 = "s_name";
    public static String ID = "id";
    public static String ID_2 = "id";
    public static String DRONE_POWER_ON = "drone_power_on";
    public static String FLIGHT_START = "flight_start";
    public static String PITCH = "162"; //"pitch";
    public static String ROLL = "148"; //"roll";
    public static String YAW = "161";  //"yaw";
    public static String FACE_DETECT_START = "165";
    public static String FACE_CAPTURE_START = "166";
    public static String FACE_CAPTURE_RES_JOHN = "167";
    public static String FACE_NONE = "168";

    public static String CHANNEL_0 = "channel_0";
    public static String CHANNEL_1 = "channel_1";
    public static String CHANNEL_2 = "channel_2";
    public static String CHANNEL_3 = "channel_3";
    public static String CHANNEL_4 = "channel_4";
    public static String CHANNEL_5 = "channel_5";
    public static String CHANNEL_6 = "channel_6";
    public static String CHANNEL_7 = "channel_7";


}
