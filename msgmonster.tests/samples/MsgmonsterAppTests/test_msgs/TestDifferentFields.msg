# field id
int64 id

# this value should lie in the range [0-1].
float64 score

# Test primitive types
Header header1
std_msgs/Header header2
Duration timeout1
builtin_interfaces/Duration timeout2

Time stamp1
builtin_interfaces/Time stamp2

# Comment for HEADER_FIELD_SET
# on multiple lines
uint16 HEADER_FIELD_SET=1
uint16 IMAGE_RESPONSE_FIELD_SET=4
# raandom comment for OTHER_DATA_FIELD_SET
uint16 OTHER_DATA_FIELD_SET=32
uint16 CUSTOM_PARAM_ERROR_FIELD_SET=128
uint16 ALERT_DATA_FIELD_SET=256

uint8 REQUEST_HEADER_FIELD_SET=1
uint8 REQUEST_RECEIVED_TIMESTAMP_FIELD_SET=2
uint8 RESPONSE_TIMESTAMP_FIELD_SET=4
uint8 ERROR_FIELD_SET=8
# comment for last REQUEST_FIELD_SET
uint8 REQUEST_FIELD_SET=16

int8 DRAWABLE_NOT_SET=0
int8 DRAWABLE_FRAME_SET=1
int8 DRAWABLE_SPHERE_SET=2
int8 DRAWABLE_BOX_SET=3
int8 DRAWABLE_ARROW_SET=4
int8 DRAWABLE_CAPSULE_SET=5
int8 DRAWABLE_CYLINDER_SET=6
int8 DRAWABLE_LINESTRIP_SET=7
int8 DRAWABLE_POINTS_SET=8

int32 STATUS_UNKNOWN=0
int32 STATUS_OK=1
int32 STATUS_AMBIGUOUS=2
int32 STATUS_HIGH_ERROR=3
			