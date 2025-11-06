define ptr @bitcast(ptr %arg0) {
entry:
  %result = bitcast ptr %arg0 to ptr
  ret ptr %result
}