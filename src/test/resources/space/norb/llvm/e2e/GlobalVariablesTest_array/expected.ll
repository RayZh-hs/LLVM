@global_array = external global [5 x i32] zeroinitializer

define ptr @get_global_array() {
entry:
  ret ptr @global_array
}