define ptr @bitcast_int_to_ptr(i64 %arg0) {
entry:
  %result = bitcast i64 %arg0 to ptr
  ret ptr %result
}