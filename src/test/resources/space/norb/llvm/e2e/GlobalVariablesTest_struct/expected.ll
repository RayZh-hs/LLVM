@global_struct = private global { i32, i64 } zeroinitializer

define ptr @get_global_struct() {
entry:
  ret ptr @global_struct
}