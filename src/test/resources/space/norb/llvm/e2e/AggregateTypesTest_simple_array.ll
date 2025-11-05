define ptr @simple_array() {
entry:
  %array = alloca [10 x i32]
  ret ptr %array
}